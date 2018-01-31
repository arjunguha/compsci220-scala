import * as dockerPool from './dockerPool';
import * as compiler from './compiler';
import * as commander from 'commander';
import * as storage from '@google-cloud/storage';
import * as Docker from 'dockerode';
import * as util from './util';

const sto = storage();

function zipToJarName(name: string) {
    return name.slice(0, name.length - 4) + '.jar';
}

function main(bucketName: string, bucketDir: string,
  dockerHosts: [Docker.DockerOptions, number][])  {
  const pool = new dockerPool.DockerPool(dockerHosts);

  function compileZip(zipPath: string): Promise<void> {
    const metadata = {
      src: zipPath,
      dest: zipToJarName(zipPath),
      bucket: bucketName
    };

    return  pool.acquireDocker()
      .then(docker => 
        util.promiseFinally(
          compiler.main(docker, metadata),
          () => pool.releaseDocker(docker)))
  }

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
    .then(zipFiles =>  Promise.all(zipFiles.map(compileZip)))
    .catch(reason => {
      console.error(reason);
      process.exit(1);
    });
}

main('compsci220-grading', '2018S-list-processing',
  [ [{ host: "10.200.0.1",  port: 2376 }, 10] ]);
