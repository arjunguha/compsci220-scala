import * as storage from '@google-cloud/storage';
import * as datastore from '@google-cloud/datastore';
import * as fs from 'fs-extra';
import * as config from './config';
import * as util from './util';
import * as path from 'path';
import { format } from 'url';
import { testJar } from './testRunner';

const ds = new datastore({});
const sto = storage();

const dsKind = 'compsci220-test-runner';


async function main() {
  const [r] = await ds.createQuery('testName')
    .filter('bucket', '=', config.bucket)
    .run();
  console.log("Filtering bogus tests...");
  const results = (r as util.TestResult[]).filter(r => config.tests.includes(r.testName));
  console.log("Grouping tests...");
  const groupedResults = util.groupBy<util.TestResult>((x, y) => x.jar === y.jar, results);
  console.log("Dumping ...");
  const numTests = config.tests.length;
  for (const group of groupedResults) {
    const jarName = path.basename(group[0].jar);
    const tests = group.filter(test => test.exitCode === 0);

    const dir = jarName.slice(0, jarName.length - 4);
    if (tests.length < numTests) {
      console.log(`${dir}: ${tests.length} / ${config.tests.length} tests complete`);
    }

    if (fs.existsSync(dir) && fs.statSync(dir).isDirectory) {

      // stdout prints the test name, OK / Error, and a newline.
      let description = tests.map(x => x.stdout).join('');

      const completedTestNames = tests.map(x => x.testName);
      let timeoutTests = config.tests
        .filter(x => !completedTestNames.includes(x))
        .map(x => x + ': Timeout / Infinite loop').join('\n');
      description = description + timeoutTests;

      const numPassed = tests.filter(x => x.stdout.endsWith('OK\n')).length;
      const summary = `Passed ${numPassed} out of ${numTests} tests.\n`;
      const grade = Math.ceil(numPassed / numTests * 100);
      await fs.writeFile(`${dir}/report.txt`,
        `Grade: ${grade}%\n` + summary + description);
    }
    else {
      console.error(`Nowhere to put results for ${dir}`);
    }
  }
}


main()
  .catch(reason => {
    console.error(reason);
    process.exit(1);
  });