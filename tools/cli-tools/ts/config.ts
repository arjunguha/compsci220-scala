import * as fs from 'fs-extra';
import * as util from './util';

function init() {
  var jsonConfig: any;
  try {
    const rawConfig = fs.readFileSync('grading.json', { encoding: 'utf-8' });
    jsonConfig = JSON.parse(rawConfig);
  }
  catch (exn) {
    util.assertExit(false, './grading.json must be a JSON file');
  }
  util.assertExit(typeof jsonConfig.bucket === 'string',
    'bucket property must be a string');
  util.assertExit(typeof jsonConfig['bucket-directory'] === 'string',
    'bucket-directory property must be a string');
  util.assertExit(jsonConfig.tests instanceof Array,
    'tests property must be an array of test names');
  jsonConfig.tests.forEach((test: any) =>
    util.assertExit(typeof test === 'string',
    'tests property must be an array of test names'));
  return {
    bucket: jsonConfig.bucket as string,
    bucketDir: jsonConfig['bucket-directory'] as string,
    tests: jsonConfig.tests as string[]
  }
}

export const { bucket, bucketDir, tests } = init();