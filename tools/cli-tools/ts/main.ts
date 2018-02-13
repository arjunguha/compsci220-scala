import * as commander from 'commander';
import * as fillMoodleCsv from './fillMoodleCsv';
import * as extract from './extract';
import * as overlay from './overlay';
import * as uploadZips from './uploadZips';

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

commander.command('fill-moodle-csv')
  .action(() => fillMoodleCsv.main());

const opts = commander.parse(process.argv);
