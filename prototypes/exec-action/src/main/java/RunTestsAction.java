import java.io.File;
import java.io.InputStream;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;

import com.google.gson.JsonObject;

public class RunTestsAction {
    
    private static final String ENDPOINT = "https://s3.us-south.objectstorage.softlayer.net";
    private static final String ENDPOINT_REGION = "us-south";

    public static JsonObject main(JsonObject args) {
        // extract relevant information from input json
        String accessKey = "";
        String secretKey = "";
        String sourceBucket = "";
        String sourceKey = "";
        String dest = "";

        // pull jar from s3 storage and run tests
        pullJar(accessKey, secretKey, sourceBucket, sourceKey, dest);
        return runTests(dest);
    }

    private static void pullJar(String accessKey,
                                String secretKey,
                                String sourceBucket,
                                String sourceKey,
                                String dest) {
        AWSCredentialsProvider credentials = new AWSStaticCredentialsProvider(
            new BasicAWSCredentials(accessKey, secretKey));
        EndpointConfiguration endpoint = new EndpointConfiguration(ENDPOINT, ENDPOINT_REGION);

        // Construct the S3 client with configuration details
        AmazonS3 cos = AmazonS3Client.builder()
            .withCredentials(credentials)
            .withEndpointConfiguration(endpoint)
            .build();

        // Create an execute request for file
        GetObjectRequest request = new GetObjectRequest(
            sourceBucket, // bucket name
            sourceKey     // object name
        );
        cos.getObject(
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
    
}