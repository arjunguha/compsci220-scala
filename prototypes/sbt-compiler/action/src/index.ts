/*
 * IBM cloud function that takes a .tar.gz file with a scala sbt project.
 * Compiles the project with sbt, extracts a built jar and uploads it
 * to {@param bucket}.
 */

import * as express from 'express'
import * as bodyParser from 'body-parser';
import * as AWS from 'ibm-cos-sdk'
import * as util from 'util';

//const cp = require('child_process');

const app = express();
const jsonParser = bodyParser.json()

const LOG_SENTINEL = 'XXX_THE_END_OF_A_WHISK_ACTIVATION_XXX\n';

function complete() {
  process.stdout.write(LOG_SENTINEL);
  process.stderr.write(LOG_SENTINEL);
}

// This handler receives executable source code if an action is a ZIP file
// based on this image. But, we are never going to do that.
app.post('/init', function(req, resp) {
  resp.status(200).send();
});

app.post('/run', jsonParser, function(req, resp) {

  let { submission, apiKey, serviceInstance } = req.body.value;

  if (typeof submission !== 'string') {
    resp.status(404).send(JSON.stringify({
      error: `expected submission parameter`
    }));
    complete();
  }

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
      Key: submission
    }).promise()
  }

  return doGetObject()
    .then(function (data) {
      resp.status(200).send(
        JSON.stringify({ data: 'Successfully received the object' }))
    })
    .catch(function (err) {
      resp.status(200).send(JSON.stringify({ error: util.inspect(err) }))
    })

});

app.listen(8080);
