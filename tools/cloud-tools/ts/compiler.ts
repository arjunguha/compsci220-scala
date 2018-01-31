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

// Function that takes a docker host and a tar src file containing scala project.
// Compiles the project in a docker container and returns a destination path
// of the jar file along with console output of the container.
function runContainer(connector: { host: string, port: number }, srcFile: Buffer):
  Promise<{stdout: string, stderr: string, status: number, destFile: string | null}> {

  const { host, port } = connector;
  const docker = new Docker({ host, port })

  return docker.createContainer({
    Image: "rachitnigam/compsci220",
    AttachStderr: true,
    AttachStdout: true
  })
  .then(container => {
    return promiseFinally(container.putArchive(srcFile, {
      // NOTE(rachit): This hardcoded path is based on Dockerfile in docker-sbt-compiler
      path: '/home/student/hw',
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
          const jarFile = path.join(outputDir, '/project.jar')
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

const testingConn = {
  host: "10.200.0.1",
  port: 2376
}

function main(metadata: MD) {
  const { bucket, src, dest } = metadata
  downloadFile(metadata)
    .then(srcPath => runContainer(testingConn, srcPath))
    .then(data => {
      const { status, stdout, stderr, destFile } = data;
      console.log(destFile)
      const up = {
        src, dest, status, stdout, stderr, metadata: {}, timestamp: Date.now()
      }
      uploadMetadata(up)
      .then(_ => {
        if(status === 0 && destFile !== null) {
          return uploadFile(destFile, metadata)
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