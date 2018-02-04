import * as Docker from 'dockerode';
import * as fs from 'fs-extra';
import * as path from 'path';
import * as storage from '@google-cloud/storage';
import * as datastore from '@google-cloud/datastore';
import * as tmp from 'tmp';
import * as commander from 'commander';
import * as archiver from 'archiver';
import * as StreamBuffers from 'stream-buffers';
import * as tar2 from 'tar';
import * as util from './util';

const sto = storage();
const ds = new datastore({});

type MD = {
  src: string,
  dest: string,
  bucket: string
}

function uploadFile(filepath: string, metadata: MD) {
  const { dest, bucket } = metadata;
  const bucketObj = sto.bucket(bucket)
  const data = fs.readFileSync(filepath)
  return bucketObj.file(dest).save(data)
    .then(_ => console.info(`uploaded ${filepath}`))
}

function downloadFile(metadata: MD): Promise<Buffer> {
  const { bucket, src } = metadata
  const bucketObj = sto.bucket(bucket)
  return bucketObj.file(src).get()
    .then(([file]) => file.download())
    .then(([buf]) => {
      const archive = archiver('tar');
      const stream = new StreamBuffers.WritableStreamBuffer();
      archive.append(buf, { name: 'solution.zip' }).pipe(stream);
      return archive.finalize()
        .then(() => stream.getContents());
    });
}

type CompileResult = {
  stdout: string,
  stderr: string,
  statusCode: number,
  jarFile?: string
}

// Function that takes a docker host and a tar src file containing scala project.
// Compiles the project in a docker container and returns a destination path
// of the jar file along with console output of the container.
async function runContainer(docker: Docker, srcFile: Buffer): Promise<CompileResult> {
  const createContainerOpts = {
    Image: "rachitnigam/compsci220",
    AttachStderr: true,
    AttachStdout: true
  };

  const container = await docker.createContainer(createContainerOpts);
  try {
    const putArchiveOpts = {
      // NOTE(rachit): This hardcoded path is based on Dockerfile in docker-sbt-compiler
      path: '/home/student/hw',
    };
    await container.putArchive(srcFile, putArchiveOpts);
    await container.start();
    const containerIO = await util.attachIO(container);
    const status: { Error: object, StatusCode: number } = await container.wait();
    let jarFile: string | undefined;
    if (status.StatusCode === 0) {
      const stream = await container.getArchive({ path: '/home/student/hw/project.jar' });
      const outputDir = tmp.dirSync().name;
      jarFile = path.join(outputDir, '/project.jar');
      await util.onStreamClose(stream.pipe(tar2.x({ cwd: outputDir },
        [ 'project.jar' ])));
    }

    return {
      stdout: containerIO.stdout(),
      stderr: containerIO.stderr(),
      statusCode: status.StatusCode,
      jarFile: jarFile
    };
  }
  finally {
    await container.remove({ force: true });
  }
}

export async function main(docker: Docker, bucket: string, src: string,
  dst: string) {
  const srcPath = await downloadFile({ bucket, src, dest: dst });
  const data = await runContainer(docker, srcPath);
  if (data.statusCode === 0 && data.jarFile) {
    await uploadFile(data.jarFile , { bucket, src, dest: dst })
  }
  else {
    console.error(`Failed to compile gs://${bucket}/${src} (exit code ${data.statusCode})`);
  }
  await ds.upsert({
    key: ds.key([ 'sbt-compiler', 'default', 'bucket', bucket, 'zip', src ]),
    excludeFromIndexes: [ 'stdout', 'stderr' ],
    data: {
      statusCode: data.statusCode,
      stdout: data.stdout,
      stderr: data.stderr,
      zipFile: src,
      jarFile: data.jarFile,
      zipDir: path.dirname(src)
    }
  });
}



// function mainmain() {

//   commander.option('--bucket <BUCKET>',
//     'name of bucket on Google Cloud Storage');
//   commander.option('--src <src.tar.gz>',
//     'tar file containing the project to be compiled')
//   commander.option('--dest <dest.jar>',
//     'name of the jar file to be uploaded to GCS')

//   const args = commander.parse(process.argv);

//   if (args.bucket && args.src && args.dest) {
//     main(new Docker(testingConn), { bucket: args.bucket, src: args.src, dest: args.dest })
//   }
//   else {
//     console.error('Incorrect args')
//     commander.outputHelp()
//   }
// }