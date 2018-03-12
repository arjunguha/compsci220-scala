import * as storage from '@google-cloud/storage';
import * as datastore from '@google-cloud/datastore';
import * as fs from 'fs-extra';
import * as tmp from 'tmp';
import * as cp from 'child_process';
import * as path from 'path';
import { Readable } from 'stream';
import { SpawnSyncReturns } from 'child_process';
import { DatastoreTransaction } from '@google-cloud/datastore/transaction';
import { read } from 'fs-extra';

const dsKind = 'compsci220-test-runner';
const javaPath = path.resolve(__dirname + '/bin/java');

export function sleep(ms: number): Promise<null> {
  return new Promise((resolve, reject) => {
    setTimeout(() => resolve(null), ms);
  });
}

export function onExit(proc: cp.ChildProcess): Promise<number> {
  return new Promise((resolve, reject) => {
    proc.once('exit', (code) => resolve(code));
  });
}

type RequestData = {
  bucket: string,
  jar: string,
  testName: string
}

const sto = storage();
const ds = new datastore({});

function requestKey(reqData: RequestData) {
  const { bucket, jar, testName } = reqData;
  return ds.key([ dsKind, 'default', 'bucket', bucket, 'jar', jar,
    'testName', testName ]);
}

function readIO(readable: Readable): { get: () => string } {
  let buf = '';
  readable.on('data', (message: string) => {
    buf = buf + message;
  });
  return {
    get() { return buf }
  };
}

export async function main(reqData: RequestData) {
  const reqKey = requestKey(reqData);
  console.info(`(v3) Test ${reqData.testName} for ${reqData.jar}`);

  const bucket = sto.bucket(reqData.bucket);
  const [file] = await bucket.file(reqData.jar).get();
  const [buf] = await file.download();
  const jarPath = tmp.fileSync({ postfix: '.jar'}).name;
  try {
    await fs.writeFile(jarPath, buf);
    const args = ['-jar', jarPath, 'run-test', reqData.testName];
    const spawnOpts = {
      stdio: [ 'ignore', 'pipe', 'pipe' ],
      encoding: 'utf8',
    };
    const child = cp.spawn(javaPath, args, spawnOpts);
    const stdout = readIO(child.stdout);
    const stderr = readIO(child.stderr);
    const result = await Promise.race([onExit(child), sleep(1 * 60 * 1000)]);
    if (result === null) {
      child.kill('SIGKILL');
    }

    //const result = cp.spawnSync(javaPath, args, spawnOpts);
    console.info(`exited with code ${result}`);

    return ds.upsert({
      key: reqKey,
      excludeFromIndexes: [ 'stdout', 'stderr' ],
      data: {
        stdout: stdout.get(),
        stderr: stderr.get(),
        dirname: path.dirname(reqData.jar),
        basename: path.basename(reqData.jar),
        exitCode: result,
        bucket: reqData.bucket,
        jar: reqData.jar,
        testName: reqData.testName
      }
    });
  }
  finally {
    await fs.unlink(jarPath);
  }
}

export function jarRunner(event: any, callback: any) {
  const reqData: RequestData = JSON.parse(
    Buffer.from(event.data.data, 'base64').toString());
  main(reqData)
    .then(() => callback())
    .catch(reason => {
      console.error(reason);
      console.error(`Request key is ${JSON.stringify(requestKey(reqData))}`);
      callback(reason);
    });
}
