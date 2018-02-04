import * as stream from 'stream';
import * as Docker from 'dockerode';
import { DockerOptions } from 'dockerode';
import * as Storage from '@google-cloud/storage';

export type TestResult = {
  jar: string,
  testName: string,
  exitCode: 0,
  stdout: string,
  stderr: string
};

export function toPromise<T>(
  f: (cb: (err: any, result: T) => void) => void): Promise<T> {
  return new Promise((resolve, reject) =>
    f((err, result) => err === null ? resolve(result) : reject(err)));
}

export function assertExit(bool: boolean, message: string): void {
  if (!bool) {
    console.error(message);
    process.exit(1);
  }
}

export async function cloudTimestamp(file: Storage.File): Promise<number> {
  const [meta] = await file.getMetadata();
  const ts = meta.metadata && meta.metadata.timestamp;
  if (typeof ts === 'string') {
    const n = Number(ts);
    if (isNaN(n)) {
      throw new Error(`Invalid timestamp for ${file.name}`);
    }
    return n;
  }
  else {
    throw new Error(`No timestamp for ${file.name}`);
  }
}


const MemoryStream = require('memorystream');

export function onStreamClose(stream: stream.Writable): Promise<void> {
  return new Promise((resolve, reject) => {
    stream.once('error', (err) =>  reject(err));
    stream.once('close', () => resolve());
  });
}


export interface ContainerIO {
  stdout(): string,
  stderr(): string
}

export function attachIO(container: Docker.Container): Promise<ContainerIO> {
  return container.attach({ stream: true, stdout: true, stderr: true})
    .then(stream => {
      let out: string = "";
      let err: string = "";
      const outStream = new MemoryStream()
      const errStream = new MemoryStream()

      outStream.on('data', (data: string) => out += data.toString());
      errStream.on('data', (data: string) => err += data.toString());
      container.modem.demuxStream(stream, outStream, errStream)
      return {
        stdout() { return out },
        stderr() { return err }
      };
    });
};

// Type faccade for function
export function promiseFinally<T>(promise: Promise<T>,
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


// Copied from Stopify
export function groupBy<T>(inGroup: (x: T, y: T) => boolean, arr: T[]): T[][] {
  if (arr.length === 0) {
    return [];
  }

  const groups = [[arr[0]]];
  let currentGroup = groups[0];
  let last = arr[0];
  for (let i = 1; i < arr.length; i++) {
    const current = arr[i];
    if (inGroup(last, current)) {
      currentGroup.push(current);
      last = current;
    }
    else {
      currentGroup = [current];
      groups.push(currentGroup);
      last = current;
    }
  }
  return groups;
}
