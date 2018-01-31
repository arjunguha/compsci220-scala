#!/usr/bin/env node
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

const sto = storage()

type MD = {
  src: string,
  dest: string,
  bucket: string
}

type UploadData = {
  src: string,
  dest: string | null,
  status: number,
  stdout: string,
  stderr: string,
  metadata: object,
  timestamp: number
}

class CompilerError {
  data: {stdout: string, stderr: string, status: number}

  constructor(data: {stdout: string, stderr: string, status: number}) {
    this.data = data
  }
}

// Type faccade for function
function promiseFinally<T>(promise: Promise<T>,
  finallyHandler: () => void): Promise<T> {
  return promise.then(result => {
    finallyHandler();
    return result;
  })
  .catch(reason => {
    finallyHandler();
    throw reason;
  });
};

// Upload metadata from compilation to google datastore
function uploadMetadata(data: UploadData) {
  const ds = new datastore({})
  return ds.save({ key: { kind: 'sbt-compiler' }, data })
}

function uploadFile(filepath: string, metadata: MD) {
  const { dest, bucket } = metadata;
  const bucketObj = sto.bucket(bucket)
  const data = fs.readFileSync(filepath)
  return bucketObj.file(dest).save(data)
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
function runContainer(docker: Docker, srcFile: Buffer): Promise<CompileResult> {
  const createContainerOpts = {
    Image: "rachitnigam/compsci220",
    AttachStderr: true,
    AttachStdout: true
  };

  function runMain(container: Docker.Container): Promise<CompileResult> {
    const putArchiveOpts = {
      // NOTE(rachit): This hardcoded path is based on Dockerfile in docker-sbt-compiler
      path: '/home/student/hw',
    };

    return container.putArchive(srcFile, putArchiveOpts)
    .then(_ => container.start())
    .then(_ => util.attachIO(container))
    .then(containerIO => {
      return container.wait()
        .then((status: { Error: object, StatusCode: number }) => {
        if (status.StatusCode !== 0) {
          return Promise.resolve({
            stdout: containerIO.stdout(),
            stderr: containerIO.stderr(),
            statusCode: status.StatusCode,
          });
        }
        return container.getArchive({
          // NOTE(rachit): This hardcoded path is based on build.sbt in docker-sbt-compiler
          path: '/home/student/hw/project.jar'
        })
        .then(stream => {
          const outputDir = tmp.dirSync().name
          const jarFile = path.join(outputDir, '/project.jar');
          return  util.onStreamClose(stream.pipe(tar2.x({ cwd: outputDir }, [ 'project.jar' ])))
            .then(() => ({
                stdout: containerIO.stdout(),
                stderr: containerIO.stderr(),
                statusCode: status.StatusCode,
                jarFile: jarFile
              }))
            });
          });
        })
    }

  return docker.createContainer(createContainerOpts)
    .then(container =>
      promiseFinally(
        runMain(container),
        () => container.remove({ force: true })));
}

const testingConn = {
  host: "10.200.0.1",
  port: 2376
}

function main(metadata: MD) {
  const { bucket, src, dest } = metadata
  downloadFile(metadata)
    .then(srcPath => runContainer(new Docker(testingConn), srcPath))
    .then(data => {

      const { statusCode, stdout, stderr, jarFile } = data;
      console.log(jarFile);
        const up = {
        src, dest: jarFile || null, status: statusCode, stdout, stderr, metadata: {}, timestamp: Date.now()
      }
      uploadMetadata(up)
      .then(_ => {
        if(statusCode === 0 && jarFile) {
          return uploadFile(jarFile, metadata)
        } else {
          return Promise.resolve()
        }
      })
      .catch(err => {
        throw err
      })
    })
    .catch(err => {
      throw err
    })
}

commander.option('--bucket <BUCKET>',
  'name of bucket on Google Cloud Storage');
commander.option('--src <src.tar.gz>',
  'tar file containing the project to be compiled')
commander.option('--dest <dest.jar>',
  'name of the jar file to be uploaded to GCS')

const args = commander.parse(process.argv);

if (args.bucket && args.src && args.dest) {
  main({ bucket: args.bucket, src: args.src, dest: args.dest })
}
else {
  console.error('Incorrect args')
  commander.outputHelp()
}