import * as fs from 'fs-extra';
import * as assert from 'assert';
import * as cp from 'child_process';
import * as unzip from 'unzip';
import * as tar from 'tar';
import * as config from './config';

const moodleSubmissionRegex = /^(?:[^_]*)_(\d+)_.*$/;

export function main(dir: string) {
  fs.ensureDirSync(dir);
  const overlay = processOverlays(config.overlay);

  for (const p of fs.readdirSync(dir)) {
    const submissionDir = `${dir}/${p}`;
    if (fs.statSync(submissionDir).isDirectory()) {
      overlay(submissionDir);
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
    if (!fs.existsSync(`${dstDir}/${dst}`)) {
      fs.copySync(srcPath, `${dstDir}/${dst}`);
    }
  }
}

function processOverlays(overlays: any[]) {
  var result = function(dir: string) { };
  for (const overlay of overlays) {
    if (overlay.type === 'overlay') {
      result = seqCommand(result, overlayCommand(overlay.src));
    }
    else if (overlay.type === 'copy') {
      result = seqCommand(result, copyCommand(overlay.src, overlay.dst));
    }
    else {
      throw 'bad';
    }
  }
  return result;
}
