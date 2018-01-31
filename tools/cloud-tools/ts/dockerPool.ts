import * as Docker from 'dockerode';


class DockerPool {

  private available: Docker[];
  private pending: ((docker: Docker) => void)[];

  constructor(private remotes: [Docker.DockerOptions, number][]) {
    for (const [opts, maxContainers] of remotes) {
      const docker = new Docker(opts);
    }

  }

  public acquireDocker(): Promise<Docker> {
    if (this.available.length > 0) {
      return Promise.resolve(this.available.pop()!);
    }
    else {
      return new Promise((resolve, reject) => {
        this.pending.push(resolve);
      });
    }
  }

  public releaseDocker(docker: Docker) {
    if (this.pending.length === 0) {
      this.available.push(docker);
    }
    else {
      this.pending.pop()!(docker);
    }
  }
}