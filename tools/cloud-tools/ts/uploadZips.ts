import * as fs from 'fs-extra';
import * as assert from 'assert';
import * as archiver from 'archiver';
import * as Storage from '@google-cloud/storage';
import * as commander from 'commander';

function main(projectsPath: string, bucketName: string, dest: string) {
  assert(fs.statSync(projectsPath).isDirectory, `${projectsPath} must be a directory`);

  // init GCS stuff
  const storage = Storage();
  const bucket = storage.bucket(bucketName);

  // iterate through all submissions
  fs.readdir(projectsPath, (err, files) => {
    files.forEach(file => {
      // establish destination on google cloud
      const destFile = bucket.file(dest + '/' + file + '.zip');
      destFile.exists().then(([doesExist]) => {
        if (doesExist) {
          return;
        }

        console.info(`Uploading ${projectsPath}/${file} to gs://${bucketName}/${destFile.name}`);
        const stream = destFile.createWriteStream({
          metadata: {
            contentType: "text/plain"
          }
        });

        // zip up and pipe to google cloud
        const archive = archiver('zip');
        archive.directory(projectsPath + '/' + file, false); // puts dir contents at root of zip
        archive.pipe(stream);
        archive.finalize();
      });
    });
  });
}

commander.option('--src <DIR>',
  'local directory with all projects',
  '.');
commander.option('--bucket <BUCKET>',
  'name of bucket on Google Cloud Storage',
  'compsci220-grading');
commander.option('--bucket-dir <PATH>',
  'name of directory in <BUCKET> that will hold the .zip files');

const args = commander.parse(process.argv);

main(args.src, args.bucket, args.bucketDir);