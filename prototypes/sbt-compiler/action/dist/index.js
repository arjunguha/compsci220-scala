"use strict";
/*
 * IBM cloud function that takes a .tar.gz file with a scala sbt project.
 * Compiles the project with sbt, extracts a built jar and uploads it
 * to {@param bucket}.
 */
Object.defineProperty(exports, "__esModule", { value: true });
var express = require("express");
var bodyParser = require("body-parser");
var AWS = require("ibm-cos-sdk");
var util = require("util");
//const cp = require('child_process');
var app = express();
var jsonParser = bodyParser.json();
var LOG_SENTINEL = 'XXX_THE_END_OF_A_WHISK_ACTIVATION_XXX\n';
function complete() {
    process.stdout.write(LOG_SENTINEL);
    process.stderr.write(LOG_SENTINEL);
}
// This handler receives executable source code if an action is a ZIP file
// based on this image. But, we are never going to do that.
app.post('/init', function (req, resp) {
    resp.status(200).send();
});
app.post('/run', jsonParser, function (req, resp) {
    var _a = req.body.value, submission = _a.submission, apiKey = _a.apiKey, serviceInstance = _a.serviceInstance;
    if (typeof submission !== 'string') {
        resp.status(404).send(JSON.stringify({
            error: "expected submission parameter"
        }));
        complete();
    }
    var config = {
        endpoint: 'https://s3.us-south.objectstorage.softlayer.net',
        apiKeyId: "zyMKlXb7JP4GqMZzLfyukohohZsSVW-FYcznW_XNdkXy",
        ibmAuthEndpoint: 'https://iam.ng.bluemix.net/oidc/token',
        serviceInstanceId: "crn:v1:bluemix:public:cloud-object-storage:global:a/26e037c79ccb86eff52eb0bbcd4a8e8d:b72d161d-e0d2-491e-9832-19823b07f77c::"
    };
    var cos = new AWS.S3(config);
    function doGetObject() {
        console.log('Retreiving object....');
        return cos.getObject({
            Bucket: 'compsci220',
            Key: submission
        }).promise();
    }
    return doGetObject()
        .then(function (data) {
        resp.status(200).send(JSON.stringify({ data: 'Successfully received the object' }));
    })
        .catch(function (err) {
        resp.status(200).send(JSON.stringify({ error: util.inspect(err) }));
    });
});
app.listen(8080);
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiaW5kZXguanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi9zcmMvaW5kZXgudHMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IjtBQUFBOzs7O0dBSUc7O0FBRUgsaUNBQWtDO0FBQ2xDLHdDQUEwQztBQUMxQyxpQ0FBa0M7QUFDbEMsMkJBQTZCO0FBRTdCLHNDQUFzQztBQUV0QyxJQUFNLEdBQUcsR0FBRyxPQUFPLEVBQUUsQ0FBQztBQUN0QixJQUFNLFVBQVUsR0FBRyxVQUFVLENBQUMsSUFBSSxFQUFFLENBQUE7QUFFcEMsSUFBTSxZQUFZLEdBQUcseUNBQXlDLENBQUM7QUFFL0Q7SUFDRSxPQUFPLENBQUMsTUFBTSxDQUFDLEtBQUssQ0FBQyxZQUFZLENBQUMsQ0FBQztJQUNuQyxPQUFPLENBQUMsTUFBTSxDQUFDLEtBQUssQ0FBQyxZQUFZLENBQUMsQ0FBQztBQUNyQyxDQUFDO0FBRUQsMEVBQTBFO0FBQzFFLDJEQUEyRDtBQUMzRCxHQUFHLENBQUMsSUFBSSxDQUFDLE9BQU8sRUFBRSxVQUFTLEdBQUcsRUFBRSxJQUFJO0lBQ2xDLElBQUksQ0FBQyxNQUFNLENBQUMsR0FBRyxDQUFDLENBQUMsSUFBSSxFQUFFLENBQUM7QUFDMUIsQ0FBQyxDQUFDLENBQUM7QUFFSCxHQUFHLENBQUMsSUFBSSxDQUFDLE1BQU0sRUFBRSxVQUFVLEVBQUUsVUFBUyxHQUFHLEVBQUUsSUFBSTtJQUV6QyxJQUFBLG1CQUF3RCxFQUF0RCwwQkFBVSxFQUFFLGtCQUFNLEVBQUUsb0NBQWUsQ0FBb0I7SUFFN0QsRUFBRSxDQUFDLENBQUMsT0FBTyxVQUFVLEtBQUssUUFBUSxDQUFDLENBQUMsQ0FBQztRQUNuQyxJQUFJLENBQUMsTUFBTSxDQUFDLEdBQUcsQ0FBQyxDQUFDLElBQUksQ0FBQyxJQUFJLENBQUMsU0FBUyxDQUFDO1lBQ25DLEtBQUssRUFBRSwrQkFBK0I7U0FDdkMsQ0FBQyxDQUFDLENBQUM7UUFDSixRQUFRLEVBQUUsQ0FBQztJQUNiLENBQUM7SUFFRCxJQUFNLE1BQU0sR0FBRztRQUNiLFFBQVEsRUFBRSxpREFBaUQ7UUFDM0QsUUFBUSxFQUFFLDhDQUE4QztRQUN4RCxlQUFlLEVBQUUsdUNBQXVDO1FBQ3hELGlCQUFpQixFQUFFLDZIQUE2SDtLQUNqSixDQUFBO0lBRUQsSUFBTSxHQUFHLEdBQUcsSUFBSSxHQUFHLENBQUMsRUFBRSxDQUFDLE1BQU0sQ0FBQyxDQUFDO0lBRS9CO1FBQ0UsT0FBTyxDQUFDLEdBQUcsQ0FBQyx1QkFBdUIsQ0FBQyxDQUFBO1FBQ3BDLE1BQU0sQ0FBQyxHQUFHLENBQUMsU0FBUyxDQUFDO1lBQ25CLE1BQU0sRUFBRSxZQUFZO1lBQ3BCLEdBQUcsRUFBRSxVQUFVO1NBQ2hCLENBQUMsQ0FBQyxPQUFPLEVBQUUsQ0FBQTtJQUNkLENBQUM7SUFFRCxNQUFNLENBQUMsV0FBVyxFQUFFO1NBQ2pCLElBQUksQ0FBQyxVQUFVLElBQUk7UUFDbEIsSUFBSSxDQUFDLE1BQU0sQ0FBQyxHQUFHLENBQUMsQ0FBQyxJQUFJLENBQ25CLElBQUksQ0FBQyxTQUFTLENBQUMsRUFBRSxJQUFJLEVBQUUsa0NBQWtDLEVBQUUsQ0FBQyxDQUFDLENBQUE7SUFDakUsQ0FBQyxDQUFDO1NBQ0QsS0FBSyxDQUFDLFVBQVUsR0FBRztRQUNsQixJQUFJLENBQUMsTUFBTSxDQUFDLEdBQUcsQ0FBQyxDQUFDLElBQUksQ0FBQyxJQUFJLENBQUMsU0FBUyxDQUFDLEVBQUUsS0FBSyxFQUFFLElBQUksQ0FBQyxPQUFPLENBQUMsR0FBRyxDQUFDLEVBQUUsQ0FBQyxDQUFDLENBQUE7SUFDckUsQ0FBQyxDQUFDLENBQUE7QUFFTixDQUFDLENBQUMsQ0FBQztBQUVILEdBQUcsQ0FBQyxNQUFNLENBQUMsSUFBSSxDQUFDLENBQUMifQ==