import * as csvParse from 'csv-parse';
import * as csvStringify from 'csv-stringify';

import * as util from './util';
import * as fs from 'fs-extra';
import * as glob from 'glob';
const header = [
  'Identifier', "Full name", "Email address", 'Status', 'Grade',
  "Maximum Grade", "Grade can be changed", "Last modified (submission)",
  "Last modified (grade)", "Feedback comments"
];

type Row = { 'Email address': string, Status: string, Grade: string, 'Feedback comments': string };
type Penalty = Map<string, { path: string, lateDays: number }[]>;

const lateRegex = /Submitted for grading - ((\d+) day(?:s)?)?( ?(\d+) hour(?:s)?)?( ?(\d+) min(?:s)?)?(?: ?\d+ sec(?:s)?)? late - Graded/;

function getPenaltiesOf(penalty: Penalty,
  identifier: string): { path: string, lateDays: number }[] {
  let r = penalty.get(identifier);
  if (r === undefined) {
    r = [ ];
    penalty.set(identifier, r);
  }
  return r;
}

function calcLateDays(penalty: Penalty, path: string, sheet: Row[]) {
  for (const row of sheet) {
    const status = row.Status;
    if (status.startsWith('No submission')) {
      continue;
    }
    else if (status === 'Submitted for grading - Graded') {
      continue;
    }
    else if (status.startsWith('Submitted for grading - Graded - Extension granted until')) {
      // Student was given an extension and handed it in on time
      continue;
    }
    else if (status.startsWith('Submitted for grading - Extension granted until:')) {
      console.warn(`Did not grade ${path}/${row["Email address"]} `);
      continue;
    }
    const match = lateRegex.exec(status);
    if (match === null) {
      console.warn(`Unknown status for ${path}/${row["Email address"]} ${status}`);
      continue;
    }

    const daysLate = match[2] === undefined ? 0 : Number(match[2]);
    const hoursLate = match[4] === undefined ? 0 : Number(match[4]);
    const minsLate = match[6] === undefined ? 0 : Number(match[6]);

    if (daysLate === 0 && hoursLate === 0 && minsLate < 15) {
      continue;
    }

    const lateness = daysLate + (hoursLate > 0 || minsLate > 0 ? 1 : 0);
    getPenaltiesOf(penalty, row["Email address"]).push({ path, lateDays: lateness });
  }
}

function aggregateLateDays(
  sheets: Map<string, Row[]>, 
  penalty: Penalty,
  lateFile: Row[]) {
  for (const id of penalty.keys()) {
    let remaining = 4;
    let text: string[] = [];
    for (const { path,lateDays } of penalty.get(id)!) {

      if (id === 'yanjiechen@umass.edu' && path === '05-Tic Tac Toe.csv') {
        continue;
      }

      const nextRemaining = remaining - lateDays;
      if (nextRemaining >= 0) {
        text.push(`${path} was ${lateDays} day(s) late. ${nextRemaining} late days remain.`);
        remaining = nextRemaining;
        continue;
      }

      const sheet = sheets.get(path)!;
      const row = sheet.findIndex((row) => row["Email address"] === id);

      const penalty = remaining > 0 && nextRemaining < 0 ? -nextRemaining * 10 : lateDays * 10;
      const grade = Math.max(Number(sheet[row].Grade) - penalty, 0);

      text.push(`${path} was ${lateDays} day(s) late. ${Math.max(0, nextRemaining)} late days remain. Penalty of ${penalty} applied. Grade is now ${grade}.`);
      sheet[row].Grade = String(grade);
      sheet[row]["Feedback comments"] = sheet[row]["Feedback comments"] + '<br>' + text[text.length - 1];
      remaining = nextRemaining;
    }
    const lateRow  = lateFile.findIndex((row) => {
      // console.log(row["Email address"], id);
      return row["Email address"] === id;
    });
    if (lateRow < 0) {
      console.log(`Row not found for ${id} in penalty file`);
      continue;
    }
    if (text.some(line => line.indexOf('Penalty') >= 0)) {
      lateFile[lateRow]["Feedback comments"] = text.join('<br>');
      console.log(`${id}:\n  - ${text.filter(line => line.indexOf('Penalty') >= 0).join('\n  - ')}`);
    }
  }
}

async function readCSVFile(path: string) {
  const str = await fs.readFile(path, { encoding: 'utf-8' });
  return await util.toPromise(cb => csvParse(str, { columns: true }, cb));
}

async function mainAsync() {
  const penalty: Penalty = new Map();
  const sheets = new Map<string, Row[]>();
  const late = await readCSVFile('../lates.csv') as Row[];
  // Read in all CSV files and add to the sheets array
  for (const path of glob.sync('*.csv').sort()) {
    const sheet = await readCSVFile(path) as Row[];
    sheets.set(path, sheet);
    calcLateDays(penalty, path, sheet);
  }
  aggregateLateDays(sheets, penalty, late);

  const updatedCsv = await util.toPromise<string>(cb =>
    csvStringify(late, { columns: header, header: true }, cb));
  await fs.writeFile('../filled-late.csv', updatedCsv);

  for (const path of sheets.keys()) {
    const updatedCsv = await util.toPromise<string>(cb => 
      csvStringify(sheets.get(path)!, { columns: header, header: true }, cb));
    await fs.writeFile(path, updatedCsv);
  }
}


export function main() {
  mainAsync().catch(reason => {
    console.error(reason);
    console.error('Aborted with an error');
  });
}