import * as fs from 'fs-extra';
import * as assert from 'assert';
import * as cp from 'child_process';
// import * as unzip from 'unzip';
import * as tar from 'tar';
const yauzl = require('yauzl'); // supports timestamps

const moodleSubmissionRegex = /^(?:[^_]*)_(\d+)_.*$/;

export function main(src: string, dst: string) {
  assert(fs.statSync(src).isFile, `${src} must be a file`);
  assert(fs.statSync(dst).isDirectory, `${dst} must be a directory`);

  function handleEntry(zipFile: any, entry: any) {
    // Directory names end with /
    if (/\/$/.test(entry.fileName)) {
      zipFile.readEntry();
      return;
    }

    const m = moodleSubmissionRegex.exec(entry.fileName);
    if (m === null) {
      console.log(`Ignoring file ${entry.fileName}`);
      zipFile.readEntry();
      return;
    }

    const outPath = `${dst}/${m[1]}`;
    if (fs.existsSync(outPath)) {
      const localTime = fs.statSync(outPath).mtime;
      const moodleTime = entry.getLastModDate();
      if (localTime.valueOf() < moodleTime.valueOf()) {
        console.info(`${outPath} was updated on Moodle at ${moodleTime}.`);
      }
      zipFile.readEntry();
      return;
    }

    fs.mkdirSync(outPath);

    const outStream = tar.extract({ cwd: outPath });
    zipFile.openReadStream(entry, (err: null, readStream: any) => {
      if (err !== null) {
        console.error(err);
        zipFile.readEntry();
        return;
      }
      readStream.pipe(outStream);
      outStream.once('close', () => {
        if (fs.existsSync(`${outPath}/.metadata`) === false) {
          console.error(`Not found: ${outPath}/.metadata`);
          fs.removeSync(outPath);
        }
        zipFile.readEntry();
      });
    });
  }

  yauzl.open(src, { lazyEntries: true }, (err: any, zipFile: any) => {
    if (err !== null) {
      console.error(err);
      process.exit(1);
    }
    zipFile.readEntry();
    zipFile.on('entry', (entry: any) => handleEntry(zipFile, entry));
  });

}
