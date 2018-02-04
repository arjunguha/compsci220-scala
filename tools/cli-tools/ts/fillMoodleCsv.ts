import * as fs from 'fs-extra';
import * as config from './config';
import * as util from './util';
import * as csvParse from 'csv-parse';
import * as csvStringify from 'csv-stringify';
import { update } from 'tar';

type MoodleRow = {
  Identifier: string,
  'Full name': string,
  'Grade': string,
  'Feedback comments': string
}
const header = [
  'Identifier', "Full name", "Email address", 'Status', 'Grade',
  "Maximum Grade", "Grade can be changed", "Last modified (submission)",
  "Last modified (grade)", "Feedback comments"
];

async function fillGrade(row: MoodleRow): Promise<MoodleRow> {
  const mId = /^Participant (\d+)$/.exec(row.Identifier);
  if (mId === null) {
    throw new Error(`Bad sheet: could not match ${row.Identifier}`);
  }

  const dir: string = mId[1];
  if (!(await fs.pathExists(dir) && (await fs.statSync(dir)).isDirectory)) {
    console.log(`No submission from ${row.Identifier} (${row['Full name']})`);
    return row;
  }

  const reportPath = `${dir}/report.txt`;
  if (!(await fs.pathExists(reportPath) &&
       (await fs.statSync(reportPath)).isFile)) {
    console.log(`${reportPath} not found`);
    return row;
  }

  const report = await fs.readFile(reportPath,'utf-8');
  const mReport = /^Grade: (\d+)%\n/.exec(report);
  if (mReport === null) {
    console.log(`Could not parse ${reportPath}`);
    return row;
  }

  row.Grade = mReport[1];
  row['Feedback comments'] = report.split('\n').join('<br>');
  return row;
}
async function asyncMain() {
  const csvString = await fs.readFile('moodle.csv', { encoding: 'utf-8' });
  const grades = (await util.toPromise(cb =>
    csvParse(csvString, { columns: true }, cb))) as MoodleRow[];


  const updatedGrades = await Promise.all( grades.map(fillGrade));
  const updatedCsv = await util.toPromise<string>(cb =>
    csvStringify(updatedGrades, { columns: header, header: true }, cb));
  await fs.writeFile('./graded.csv', updatedCsv);
}

export function main() {
  asyncMain().catch(reason => {
    console.log(reason);
    process.exit(1);
  });
}