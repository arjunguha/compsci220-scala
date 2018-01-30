import * as storage from '@google-cloud/storage';
import * as cp from 'child_process';
import * as path from 'path';
import * as commander from 'commander';

const sto = storage();

function zipToJarName(name: string) {
  return name.slice(0, name.length - 4) + '.jar';
}

function main(bucketName: string,bucketDir: string, sshloginfile: string) {
  const bucket = sto.bucket(bucketName);
  bucket.getFiles({ prefix: bucketDir })
    .then(([files]) => {
       // Produce only the .zip files that are not yet compiled.
       const zipFiles = files.filter(f => f.name.endsWith('.zip'))
         .map(f => f.name);
       const jarFiles = files.filter(f => f.name.endsWith('.jar'))
         .map(f => f.name);
       return zipFiles.filter(f => !jarFiles.includes(zipToJarName(f)));
    })
    .then(zipFiles => {
      const jarFiles = zipFiles.map(zipToJarName);
      const result = cp.spawnSync('parallel',
        [ '--sshloginfile', sshloginfile, 'compsci220-cloud-compiler',
          '--bucket', bucketName, ':::', ...zipFiles, ':::', ...jarFiles ],
        { stdio: [ 'none', 'inherit', 'inherit' ] });
      if (result.status !== 0) {
        console.error(`GNU Parallel exited with code ${result.status}`);
        process.exit(1);
      }
    })
    .catch(reason => {
      console.error(reason);
      process.exit(1);
    });
}

commander.option('--bucket <BUCKET>',
  'name of bucket on Google Cloud Storage');
commander.option('--dir <DIR>',
  'name of directory in <BUCKET> that will hold the .zip and .jar files');
commander.option('--sshloginfile <FILE>',
  'path to an sshloginfile');

const args = commander.parse(process.argv);
main(args.bucket, args.dir, args.sshloginfile);