import * as dockerPool from './dockerPool';
import * as compiler from './compiler';
import * as datastore from '@google-cloud/datastore';
import * as commander from 'commander';
import * as storage from '@google-cloud/storage';
import * as Docker from 'dockerode';
import * as util from './util';

const ds = new datastore({});
const sto = storage();

function zipToJarName(name: string) {
    return name.slice(0, name.length - 4) + '.jar';
}

async function getAlreadyCompiled(bucketDir: string): Promise<string[]> {
  const [results] = await ds.createQuery('zip')
    .filter('zipDir', '=', bucketDir)
    .run();
  return results.map((x: any) => x.zipFile);
}

async function main(bucketName: string, bucketDir: string,
  dockerHosts: [Docker.DockerOptions, number][])  {
  const pool = new dockerPool.DockerPool(dockerHosts);

  async function compileZip(zipPath: string): Promise<void> {
    const docker = await pool.acquireDocker();
    try {
      await compiler.main(docker, bucketName, zipPath, zipToJarName(zipPath));
      return;
    }
    finally {
       pool.releaseDocker(docker);
    }
  }

  const alreadyCompiled = await getAlreadyCompiled(bucketDir);

  const bucket = sto.bucket(bucketName);
  const [files] = await bucket.getFiles({ prefix: bucketDir });
  const zipFiles = files
    .filter(f => f.name.endsWith('.zip'))
    .map(f => f.name)
    .filter(f => !alreadyCompiled.includes(f));

  return Promise.all(zipFiles.map(compileZip));
}

main('compsci220-grading', '2018S-list-processing',
  [
    [{ host: "10.200.0.1",  port: 2376 }, 10],
    [{ host: "10.200.0.6",  port: 2376 }, 10],
    [{ host: "10.200.0.11",  port: 2376 }, 10]
  ]);