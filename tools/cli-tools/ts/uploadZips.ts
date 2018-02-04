import * as fs from 'fs-extra';
import * as assert from 'assert';
import * as archiver from 'archiver';
import * as Storage from '@google-cloud/storage';
import * as glob from 'glob';
import { close } from 'fs';
import * as util from './util';
import * as Datastore from '@google-cloud/datastore';
import * as config from './config';

const storage = Storage();
const ds =  new Datastore({});

function globPromise(globString: string): Promise<string[]> {
  return new Promise((resolve, reject) => {
    glob(globString, (err, matches) => {
      if (err !== null) {
        return reject(err);
      }
      return resolve(matches);
    });
  });
}

async function latestScalaTimestamp(projectPath: string) {
  const matches = await globPromise(`${projectPath}/**/*.scala`);
  async function timestamp(path: string) {

    const t = await fs.stat(path);
    return t.mtime.valueOf();
  }
  const dirTime = await timestamp(projectPath);
  const times = await Promise.all(matches.map(timestamp));
  return Math.max(dirTime, ...times);
}

async function mainAsync(projectsPath: string, bucketName: string, dest: string) {
  assert(fs.statSync(projectsPath).isDirectory, `${projectsPath} must be a directory`);

  // init GCS stuff
  const bucket = storage.bucket(bucketName);

  // iterate through all submissions
  const files = (await fs.readdir(projectsPath))
    .filter(f => fs.statSync(`${projectsPath}/${f}`).isDirectory());

  for (const file of files) {
    // establish destination on google cloud
    const destFile = bucket.file(dest + '/' + file + '.zip');
    const localTimestamp = await latestScalaTimestamp(file);
    const [doesExist] = await destFile.exists();
    if (!doesExist || localTimestamp > await util.cloudTimestamp(destFile)) {
      console.info(`Uploading ${destFile.name}`);
      const stream = destFile.createWriteStream({
        metadata: {
          contentType: "text/plain",
          metadata: {
            timestamp: localTimestamp
          }
        }
      });
      // zip up and pipe to google cloud
      const archive = archiver('zip');
      archive.directory(projectsPath + '/' + file, false); // puts dir contents at root of zip
      archive.pipe(stream);
      await ds.delete(ds.key([ 'sbt-compiler', 'default', 'bucket', bucketName, 'zip', destFile.name ]));

      archive.finalize();
    }
  }
}

function main(projectsPath: string, bucketName: string, dest: string) {
  mainAsync(projectsPath, bucketName, dest)
    .catch(reason => {
      console.error(reason);
      process.exit(1);
    });
}

main('.', config.bucket, config.bucketDir);