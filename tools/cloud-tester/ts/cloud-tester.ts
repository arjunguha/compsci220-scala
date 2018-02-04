import * as Storage from '@google-cloud/storage';

function main(bucketName: string, projectsPath: string) {
  // init GCS stuff
  const storage = Storage();
  const bucket = storage.bucket(bucketName);

  bucket.getFiles({prefix: projectsPath})
    .then(([files]) => {
      files.forEach(file => {
        console.log(file);
      })
    });
}

main(process.argv[2], process.argv[3]);
