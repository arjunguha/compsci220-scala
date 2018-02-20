import * as datastore from '@google-cloud/datastore';
import * as config from './config';

const ds = new datastore({});


function jarName(path: string): string {
  const m = /(\d+).jar/.exec(path);
  if (m === null) {
    throw new Error(`Did not match ${path}`);
  }
  return m[1];
}

async function testAll(names: string[], bucketName: string, bucketDir: string) {
  const [completedTests] = await ds.createQuery('testName')
    .filter('bucket', '=', bucketName).run();
  let toForget = completedTests.filter((obj: any) => obj.jar.startsWith(bucketDir))
    .filter((obj: any) => names.length === 0 || names.includes(jarName(obj.jar)))
    .map((obj: any) => obj[ds.KEY]);
  
  while (toForget.length > 0) {
    console.log(`will delete ${toForget.length}`);
    await ds.delete(toForget.slice(0, 500));
    toForget = toForget.slice(500);
  }
}

export function main(args: string[]) {
  testAll(args, config.bucket, config.bucketDir)
  .catch(reason => {
    console.error(reason);
    process.exit(1);
  });
}