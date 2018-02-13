import * as storage from '@google-cloud/storage';
import * as pubsub from '@google-cloud/pubsub';
import * as datastore from '@google-cloud/datastore';
import * as fs from 'fs-extra';
import * as process from 'process';
import * as config from './config';

const ds = new datastore({});
const sto = storage();
const ps = pubsub();

const dsKind = 'compsci220-test-runner';

const jarRunner = ps.topic('jarRunner').publisher();

export function sleep(ms: number): Promise<void> {
  return new Promise((resolve, reject) => {
    setTimeout(resolve, ms);
  });
}

export async function testSingle(bucket: string, jar: string, testName: string) {
  const data = Buffer.from(JSON.stringify({ bucket, jar, testName }));
  await jarRunner.publish(data);
  return;
}

export async function testJar(bucket: string, jar: string) {
  const [completedTests] = await ds.createQuery('testName')
    .filter('bucket', '=', bucket)
    .filter('jar', '=', jar)
    .run();
  const completedTestNames = completedTests.map((obj: any) => obj.testName);
  const remainingTestNames = config.tests
    .filter(x => !completedTestNames.includes(x));
  await Promise.all(remainingTestNames.map(testName => testSingle(bucket, jar, testName)));
  return remainingTestNames.length;
}

async function testAll(bucketName: string, bucketDir: string) {
  const bucket = sto.bucket(bucketName);
  const [allFiles] = await bucket.getFiles({ prefix: bucketDir });
  const jarFiles = allFiles.filter(x => x.name.endsWith('.jar'));
  console.log(`${jarFiles.length} submissions`);

  let count = 0;
  let lastSleep = 0;
  for (const jar of jarFiles) {
    count += await testJar(bucketName, jar.name);
    if (lastSleep + 200 < count) {
      console.log("Sleeping ...");
      lastSleep = count;
      await sleep(3000);

    }
  }
  console.log(`Started ${count} test cases`);
}

export function main() {
  testAll(config.bucket, config.bucketDir)
  .catch(reason => {
    console.error(reason);
    process.exit(1);
  });
}

