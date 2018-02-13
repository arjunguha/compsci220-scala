import * as datastore from '@google-cloud/datastore';
import * as commander from 'commander';
import * as util from './util';
import * as fs from 'fs-extra';
import * as path from 'path';
import * as config from './config';

const ds = new datastore({});

async function mainAsync(bucketName: string, bucketDir: string) {
  const [results] = await ds.createQuery('zip')
    .filter('zipDir', '=', bucketDir)
    .run();
  for (const result of results) {
    const r: any = result;
    if (r.statusCode !== 0) {
      const zipFile = path.basename(r.zipFile);
      const submissionDir = zipFile.slice(0, zipFile.length - 4);
      if ((await fs.stat(submissionDir)).isDirectory()) {
        await fs.writeFile(`${submissionDir}/compile-error.txt`,
          r.stdout + '\n' + r.stderr);
        console.log(`${submissionDir}/compile-error.txt`);
      }
      else {
        console.log(`Compile failed on ${r.zipFile} but no directory found`);
      }
    }
  }
}

export function main() {
  mainAsync(config.bucket, config.bucketDir)
    .catch(reason => {
      console.error(reason);
      process.exit(1);
    });
  }