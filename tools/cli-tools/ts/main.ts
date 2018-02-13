import * as commander from 'commander';
import * as fillMoodleCsv from './fillMoodleCsv';
import * as extract from './extract';
import * as overlay from './overlay';
import * as uploadZips from './uploadZips';
import * as compileAll from './compileAll';
import * as compileFailures from './compileFailures';
import * as testRunner from './testRunner';
import * as testResults from './testResults';

commander.command('extract')
  .option('--src <FILE>', '.zip file from Moodle')
  .action((...args) => {
    const opts = args[args.length - 1];
    extract.main(opts.src, '.');
  });

commander.command('overlay')
  .action((...args) => {
    overlay.main('.');
  });

commander.command('upload-zips')
  .action(() => uploadZips.main());

commander.command('compile-all')
  .action(() => compileAll.main());

commander.command('compile-failures')
  .action(() => compileFailures.main());

commander.command('test-all')
  .action(() => testRunner.main());

  commander.command('test-results')
  .action(() => testResults.main());

commander.command('fill-moodle-csv')
  .action(() => fillMoodleCsv.main());

const opts = commander.parse(process.argv);
