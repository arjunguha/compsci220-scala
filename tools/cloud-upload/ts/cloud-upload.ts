import * as fs from 'fs-extra';
import * as assert from 'assert';
import * as archiver from 'archiver';
import * as Storage from '@google-cloud/storage';

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
}

main(process.argv[2], process.argv[3], process.argv[4]);
