import * as datastore from '@google-cloud/datastore';
import * as commander from 'commander';
import * as util from './util';
import * as fs from 'fs-extra';
import * as path from 'path';

const ds = new datastore({});

export async function main(bucketName: string, bucketDir: string) {
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
      }
      else {
        console.log(`Compile failed on ${r.zipFile} but no directory found`);
      }
    }
  }
}

commander.option('--bucket <BUCKET>',
  'name of bucket on Google Cloud Storage',
  'compsci220-grading');
commander.option('--bucket-dir <PATH>',
  'name of directory in <BUCKET> that will hold the .zip files');

const args = commander.parse(process.argv);

util.assertExit(typeof args.bucketDir === 'string', '--bucket-dir expected');


main(args.bucket, args.bucketDir)
  .catch(reason => {
    console.error(reason);
    process.exit(1);
  });