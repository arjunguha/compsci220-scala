import * as Docker from 'dockerode';
import * as fs from 'fs';
import * as path from 'path'

const testingConn = {
  host: "10.200.0.1",
  port: 2376
}

function promiseFinally<T>(promise: Promise<T>,
  finallyHandler: () => void): Promise<T> {
  return promise.then(result => {
    finallyHandler();
    return result;
  })
  .catch(reason => {
    finallyHandler();
    throw reason;
  });
};

function runContainer(connector: {host: string, port: number}, src: string, dest: string) {

  const { host, port } = connector;
  const docker = new Docker({ host, port })

  let container: Docker.Container;
  debugger;

  return promiseFinally(docker.createContainer({
    Image: "rachitnigam/compsci220",
    AttachStderr: true,
    AttachStdout: true
  })
  .then(_container => {
    container = _container
    const projectPath = path.join(__dirname, src)
    const file = fs.createReadStream(projectPath)
    return container.putArchive(file, {
      path: '/home/student/hw'
    })
  })
  .then(_ => container.start())
  .then(_ => container.wait())
  .then(_ => {
    return container.getArchive({
      path: '/home/student/hw/project.jar'
    })
  })
  .then(stream => {
    const file = fs.createWriteStream(path.join(__dirname, dest))
    stream.pipe(file)
  }),
  () => {
     container.remove({ force: true })
  })
  .catch(err => console.log("Containe error:", err))
}

runContainer(testingConn, "../project.tar.gz", "../output.tar")