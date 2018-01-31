import * as Docker from 'dockerode';
import * as fs from 'fs-extra';
import * as path from 'path';
import * as storage from '@google-cloud/storage';
import * as datastore from '@google-cloud/datastore';
import * as tmp from 'tmp';

const tar = require('tar-fs')
const MemoryStream = require('memorystream');

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

function uploadFile(filepath: string, metadata: MD, bucket: string) {
  const { dest } = metadata;
  const bucketObj = sto.bucket(bucket)
  return bucketObj.file(dest).save(path.join(__dirname, filepath))
}

function downloadFile(bucket: string, metadata: MD): Promise<string> {
  const { src } = metadata
  const bucketObj = sto.bucket(bucket)
  return bucketObj.file(src).get()
    .then(([file]) => file.download())
    .then(([buf]) => {
      const projectTar = tmp.fileSync({ postfix: '.tar.gz' }).name
      return fs.writeFile(projectTar, buf)
        .then(() => projectTar)
    })
}

// Function that takes a docker host and a tar src file containing scala project.
// Compiles the project in a docker container and returns a destination path
// of the jar file along with console output of the container.
function runContainer(connector: { host: string, port: number }, srcFile: string): 
  Promise<{stdout: string, stderr: string, status: number, destFile: string | null}> {

  const { host, port } = connector;
  const docker = new Docker({ host, port })

  return docker.createContainer({
    Image: "rachitnigam/compsci220",
    AttachStderr: true,
    AttachStdout: true
  })
  .then(container => {
    const projectPath = srcFile
    const file = fs.createReadStream(projectPath)

    return promiseFinally(container.putArchive(file, {
      // NOTE(rachit): This hardcoded path is based on Dockerfile in docker-sbt-compiler
      path: '/home/student/hw'
    })
    .then(_ => container.start())
    .then(_ => container.attach({ stream: true, stdout: true, stderr: true}))
    .then(stream => {
      let out: string = "";
      let err: string = "";

      // Need seperate streams for demuxing data.
      let outStream = new MemoryStream()
      let errStream = new MemoryStream()

      outStream.on('data', (data: string) => out += data.toString()) 
      errStream.on('data', (data: string) => err += data.toString()) 

      container.modem.demuxStream(stream, outStream, errStream)

      return container.wait()
      .then((status: { Error: object, StatusCode: number }) => {
        if (status.StatusCode !== 0) {
          throw new CompilerError({
            stdout: out,
            stderr: err,
            status: status.StatusCode,
          })
        }
        return container.getArchive({
          // NOTE(rachit): This hardcoded path is based on build.sbt in docker-sbt-compiler
          path: '/home/student/hw/project.jar'
        })
        .then(stream => {
          const outputDir = tmp.dirSync().name
          const jarFile = path.join(outputDir, '/project.tar')
          stream.pipe(tar.extract(outputDir, {
            entries: ['project.jar']
          }))
          return {
            stdout: out,
            stderr: err,
            status: status.StatusCode,
            destFile: jarFile
          }
        })
      })
    }),
    () => {
      container.remove({ force: true })
    })

  })
  .catch(err => {
    if (err instanceof CompilerError) {
      return Promise.resolve({...err.data, destFile: null})
    }
    throw ("Container error: " + err)
  })
}

const testingData = {
  src: "../failed.tar.gz", 
  dest: "../output.tar"
}

const testingConn = {
  host: "10.200.0.1",
  port: 2376
}

runContainer(testingConn, path.join(__dirname, "../failed.tar.gz"))
  .then(data => console.log(data))