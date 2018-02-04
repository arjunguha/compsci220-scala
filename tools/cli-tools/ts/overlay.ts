import * as fs from 'fs-extra';
import * as assert from 'assert';
import * as cp from 'child_process';
import * as unzip from 'unzip';
import * as tar from 'tar';

const moodleSubmissionRegex = /^(?:[^_]*)_(\d+)_.*$/;

function main(dir: string, prepare: (dir: string) => void) {
  fs.ensureDirSync(dir);
  overlays.forEach(x => fs.ensureDirSync(x));

  for (const p of fs.readdirSync(dir)) {
    const submissionDir = `${dir}/${p}`;
    if (fs.statSync(submissionDir)) {
      prepare(submissionDir);
    }
  }
}

function seqCommand(cmd1: (dir: string) => void, cmd2: (dir: string) => void) {
  return (dir: string) => {
    cmd1(dir);
    cmd2(dir);
  }
}

function overlayCommand(overlayDir: string) {
  return (dstDir: string) => {
    fs.copySync(overlayDir, dstDir);
  }
}

function copyCommand(srcPath: string, dst: string) {
  return (dstDir: string) => {
    fs.copySync(srcPath, `${dstDir}/${dst}`);
  }
}

function processOverlays(commands: string[]) {
  var result = function(dir: string) { };
  while(commands.length > 0) {
    const typ = commands.shift();
    if (typ === 'overlay') {
      const path = commands.shift()!;
      result = seqCommand(result, overlayCommand(path));
    }
    else if (typ === 'copy') {
      const src = commands.shift()!;
      const dst = commands.shift()!;
      result = seqCommand(result, copyCommand(src, dst));
    }
    else {
      throw 'bad';
    }
  }
  assert(commands.length === 0);
  return result;
}

const [ _, __, submissions, testFile ] = process.argv;

const overlays = [ 'overlay', __dirname + '/../data/scala-grading-overlay',
  'copy', testFile, 'src/main/scala/GradingTests.scala' ];

main(submissions, processOverlays(overlays));
