import * as commander from 'commander';
import * as fillMoodleCsv from './fillMoodleCsv';

commander.command('fill-moodle-csv')
  .action(() => fillMoodleCsv.main());

commander.parse(process.argv);