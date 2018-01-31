import * as stream from 'stream';
import * as Docker from 'dockerode';
import { DockerOptions } from 'dockerode';

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