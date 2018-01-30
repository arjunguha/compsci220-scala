import * as storage from '@google-cloud/storage';
import * as datastore from '@google-cloud/datastore';
import * as fs from 'fs-extra';
import * as tmp from 'tmp';
import * as cp from 'child_process';
import * as path from 'path';

const sto = storage();
const ds = new datastore({});

const javaPath = path.resolve(__dirname + '/bin/java');

function promiseFinally<T>(promise: Promise<T>,
   finallyHandler: () => void): Promise<T> {
  return promise.then(result =>  {
    finallyHandler();
    return result;
  })
  .catch(reason => {
    finallyHandler();
    throw reason;
  });
};

export function jarRunner(event: any, callback: any) {
  const data: { bucket: string, jar: string, args: string[], metadata: { [key: string]: any } } =
    JSON.parse(Buffer.from(event.data.data, 'base64').toString());
  const { bucket, jar, args, metadata } = data;
  const bucketObj = sto.bucket(bucket);
  return bucketObj.file(jar).get()
    .then(([file]) => file.download())
    .then(([buf]) => {
      const jarPath = tmp.fileSync({ postfix: '.jar'}).name;
      return promiseFinally(fs.writeFile(jarPath, buf)
        .then(() => {
          const { status, stdout, stderr } = cp.spawnSync(
            javaPath, ['-jar', jarPath, ...args],
            { stdio: [ 'ignore', 'pipe', 'pipe' ], encoding: 'utf8' });
          console.info(`java -jar ${jarPath} ${args.join(' ')} exited with code ${status}`);
          return { status, stdout, stderr };
        }), () => fs.unlinkSync(jarPath));
    })
    .then(result => {
      const data = { ...result, ...metadata };
      return ds.save({ key: { kind: 'compsci220' }, data });
    })
    .then(() => callback())
    .catch(reason => {
      callback(reason);
    });
}