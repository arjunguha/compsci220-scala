/*
 * IBM cloud function that takes a .tar.gz file with a scala sbt project.
 * Compiles the project with sbt, extracts a built jar and uploads it
 * to {@param bucket}.
 */

const express = require('express');
const bodyParser = require('body-parser');
const cp = require('child_process');
const AWS = require('ibm-cos-sdk');
const util = require('util');

const app = express();
const jsonParser = bodyParser.json()

const LOG_SENTINEL = 'XXX_THE_END_OF_A_WHISK_ACTIVATION_XXX\n';

function complete() {
  process.stdout.write(LOG_SENTINEL);
  process.stderr.write(LOG_SENTINEL);
}

// TODO(rachit): Re-write this nonsense in TS
function hasType(resp, value, valueName, type) {
  if (typeof value !== type) {
    resp.status(404).send(JSON.stringify({
      error: `expected ${valueName} parameter of type ${type}`
    }));
    complete();
    return;
  }
  else {
    return value
  }
}

// This handler receives executable source code if an action is a ZIP file
// based on this image. But, we are never going to do that.
app.post('/init', function(req, resp) {
  resp.status(200).send();
});

app.post('/run', jsonParser, function(req, resp) {

  let { project, apiKey, serviceInstance } = req.body.value;

  project = hasType(resp, project, 'project', 'string')

  // TODO(rachit): Dont harcode these.
  //apiKey = hasType(apiKey, 'apiKey', 'string')
  //serviceInstance = hasType(serviceInstance, 'serviceInstance', 'string')

  const config = {
    endpoint: 'https://s3.us-south.objectstorage.softlayer.net',
    apiKeyId: "zyMKlXb7JP4GqMZzLfyukohohZsSVW-FYcznW_XNdkXy",
    ibmAuthEndpoint: 'https://iam.ng.bluemix.net/oidc/token',
    serviceInstanceId: "crn:v1:bluemix:public:cloud-object-storage:global:a/26e037c79ccb86eff52eb0bbcd4a8e8d:b72d161d-e0d2-491e-9832-19823b07f77c::"
  }

  const cos = new AWS.S3(config);

  function doGetObject() {
    console.log('Retreiving object....')
    return cos.getObject({
      Bucket: 'compsci220',
      Key: project
    }).promise()
  }

  return doGetObject()
    .then(function (data) {
      resp.status(200).send(JSON.stringify({ data: 'Successfully received the object' }))
    })
    .catch(function (err) {
      resp.status(200).send(JSON.stringify({ error: util.inspect(err) }))
    })

  /*cp.exec(cmd,
    { encoding: 'utf8' },
    function(err, stdout, stderr) {
      if (err !== null) {
        resp.status(404).send(JSON.stringify({
          error: {
            message: `error from ${cmd}`,
            stdout: stdout,
            stderr: stderr
          }
        }));
        complete();
        return;
      }

      // Drop the trailing newline or we get an empty string in the file list.
      var files = stdout.slice(0, stdout.length - 1) // drop trailing newline
        .split('\n') // one file per line
        .map(x => x.split(' ', 3)[1]); // 2nd column, space delimited
      resp.status(200).send(JSON.stringify({
        files: files
      }));
      complete();
    });*/

});

app.listen(8080);
