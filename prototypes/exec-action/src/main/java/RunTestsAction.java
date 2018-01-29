import java.io.File;
import java.io.InputStream;

import java.util.List;

import com.ibm.cloud.objectstorage.ClientConfiguration;
import com.ibm.cloud.objectstorage.SDKGlobalConfiguration;
import com.ibm.cloud.objectstorage.auth.AWSCredentials;
import com.ibm.cloud.objectstorage.auth.AWSCredentialsProvider;
import com.ibm.cloud.objectstorage.auth.AWSStaticCredentialsProvider;
import com.ibm.cloud.objectstorage.auth.BasicAWSCredentials;
import com.ibm.cloud.objectstorage.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3Client;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3ClientBuilder;
import com.ibm.cloud.objectstorage.services.s3.model.Bucket;
import com.ibm.cloud.objectstorage.services.s3.model.ListObjectsRequest;
import com.ibm.cloud.objectstorage.services.s3.model.GetObjectRequest;
import com.ibm.cloud.objectstorage.services.s3.model.ObjectListing;
import com.ibm.cloud.objectstorage.services.s3.model.S3ObjectSummary;
import com.ibm.cloud.objectstorage.oauth.BasicIBMOAuthCredentials;

import com.google.gson.JsonObject;

public class RunTestsAction {
    
    private static final String ENDPOINT = "https://s3-api.us-geo.objectstorage.softlayer.net";
    private static final String ENDPOINT_REGION = "us";

    public static void main(String[] args) {
        // for testing purposes
        main(new JsonObject());
    }

    public static JsonObject main(JsonObject args) {
        // extract relevant information from input json
        String accessKey = "_ixYyqdFstiD7AAXizRcMtQZHrP9A8chQseJk";
        String secretKey = "crn:v1:bluemix:public:cloud-object-storage:global:a/26e037c79ccb86eff52eb0bbcd4a8e8d:b72d161d-e0d2-491e-9832-19823b07f77c::";
        String sourceBucket = "plasma-research";
        String sourceKey = "";
        String dest = "";

        AmazonS3 client = getClient(accessKey, secretKey);
        // pullJar(client, sourceBucket, sourceKey, dest);
        // return runTests(dest);

        listBuckets(client);
        return new JsonObject();
    }

    private static AmazonS3 getClient(String accessKey, String secretKey) {
        SDKGlobalConfiguration.IAM_ENDPOINT = "https://iam.bluemix.net/oidc/token";

        AWSCredentialsProvider credentials = new AWSStaticCredentialsProvider(
            new BasicIBMOAuthCredentials(accessKey, secretKey));
        EndpointConfiguration endpoint = new EndpointConfiguration(ENDPOINT, ENDPOINT_REGION);
        ClientConfiguration clientConfig = new ClientConfiguration()
            .withRequestTimeout(5000)
            .withTcpKeepAlive(true);

        // Construct the S3 client with configuration details
        return AmazonS3Client.builder()
            .withCredentials(credentials)
            .withEndpointConfiguration(endpoint)
            .withPathStyleAccessEnabled(true)
            .withClientConfiguration(clientConfig)
            .build();
    }

    private static void pullJar(AmazonS3 client,
                                String sourceBucket,
                                String sourceKey,
                                String dest) {
        // Create an execute request for file
        GetObjectRequest request = new GetObjectRequest(
            sourceBucket, // bucket name
            sourceKey     // object name
        );
        client.getObject(
            request,
            new File(dest) // destination file
        );
    }
    
    private static JsonObject runTests(String jarPath) {
        try {
            Process proc = Runtime.getRuntime().exec("java -jar " + jarPath);
            int code = proc.waitFor();
            InputStream in = proc.getInputStream();
            InputStream err = proc.getErrorStream();
    
            String stdLog, errLog;
            byte b[]=new byte[in.available()];
            in.read(b,0,b.length);
            stdLog = new String(b);
            byte c[]=new byte[err.available()];
            err.read(c,0,c.length);
            errLog = new String(c);
            
            return formatResults(code, stdLog, errLog);
        } catch(Exception e) {
            return formatResults(1, "", "Test execution failed");
        }
    }
        
    private static JsonObject formatResults(int resultCode, String std, String err) {
        JsonObject response = new JsonObject();
        response.addProperty("result", resultCode);
        response.addProperty("std", std);
        response.addProperty("err", err);
        return response;
    }

    /**
     * @param bucketName
     * @param s3Client
     */
    public static void listObjects(String bucketName, AmazonS3 s3Client)
    {
        System.out.println("Listing objects in bucket " + bucketName);
        ObjectListing objectListing = s3Client.listObjects(new ListObjectsRequest().withBucketName(bucketName));
        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            System.out.println(" - " + objectSummary.getKey() + "  " + "(size = " + objectSummary.getSize() + ")");
        }
        System.out.println();
    }

    /**
     * @param s3Client
     */
    public static void listBuckets(AmazonS3 s3Client)
    {
        System.out.println("Listing buckets");
        final List<Bucket> bucketList = s3Client.listBuckets();
        for (final Bucket bucket : bucketList) {
            System.out.println(bucket.getName());
        }
        System.out.println();
    }

    
}