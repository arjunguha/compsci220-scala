import * as fs from 'fs-extra';
import * as assert from 'assert';
import * as cp from 'child_process';
import * as unzip from 'unzip';
import * as tar from 'tar';

const moodleSubmissionRegex = /^(?:[^_]*)_(\d+)_.*$/;

function main(src: string, dst: string) {
  assert(fs.statSync(src).isFile, `${src} must be a file`);
  assert(fs.statSync(dst).isDirectory, `${dst} must be a directory`);

  function handleEntry(entry: unzip.Entry) {
    if (entry.type !== 'File') {
      entry.autodrain();
      return;
    }

    const m = moodleSubmissionRegex.exec(entry.path);
    if (m === null) {
      entry.autodrain();
      return;
    }

    const outPath = `${dst}/${m[1]}`;
    if (fs.existsSync(outPath)) {
      console.info(`${outPath} already exists, ignoring`);
      entry.autodrain();
      return;
    }

    fs.mkdirSync(outPath);

    const outStream = tar.extract({ cwd: outPath });
    entry.pipe(outStream);
    outStream.once('close', () => {
      if (fs.existsSync(`${outPath}/.metadata`) === false) {
        console.error(`Not found: ${outPath}/.metadata`);
        fs.removeSync(outPath);
      }
    });
  }

  fs.createReadStream(src)
    .pipe(unzip.Parse())
    .on('entry', handleEntry);
}

main(process.argv[2], process.argv[3])
