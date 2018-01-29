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

  private static AmazonS3 _s3Client;

  public static void main(String[] args)
  {

    SDKGlobalConfiguration.IAM_ENDPOINT = "https://iam.bluemix.net/oidc/token";

    String bucketName = "plasma-research";
    String api_key = "zyMKlXb7JP4GqMZzLfyukohohZsSVW-FYcznW_XNdkXy";
    String service_instance_id = "crn:v1:bluemix:public:cloud-object-storage:global:a/26e037c79ccb86eff52eb0bbcd4a8e8d:b72d161d-e0d2-491e-9832-19823b07f77c::";
    String endpoint_url = "https://s3-api.us-geo.objectstorage.softlayer.net";
    String location = "us";

    _s3Client =
      createClient(api_key, service_instance_id, endpoint_url, location);

    listBuckets(_s3Client);
    System.out.println(_s3Client.doesBucketExist("compsci220"));
    //listObjects(bucketName, _s3Client);
  }


  public static AmazonS3 createClient(
      String api_key,
      String service_instance_id,
      String endpoint_url,
      String location) {

    AWSCredentials credentials;
    if (endpoint_url.contains("objectstorage.softlayer.net")) {
      credentials = new BasicIBMOAuthCredentials(api_key, service_instance_id);
    } else {
      String access_key = api_key;
      String secret_key = service_instance_id;
      credentials = new BasicAWSCredentials(access_key, secret_key);
    }

    ClientConfiguration clientConfig =
      new ClientConfiguration()
      .withRequestTimeout(5000);

    clientConfig.setUseTcpKeepAlive(true);

    AmazonS3 s3Client = AmazonS3ClientBuilder
      .standard()
      .withCredentials(new AWSStaticCredentialsProvider(credentials))
      .withEndpointConfiguration(
          new EndpointConfiguration(endpoint_url, location))
      .withPathStyleAccessEnabled(true)
      .withClientConfiguration(clientConfig)
      .build();

    return s3Client;
  }

  /**
   * @param s3Client
   */
  public static void listBuckets(AmazonS3 s3Client) {
    System.out.println("Listing buckets");
    final List<Bucket> bucketList = s3Client.listBuckets();
    for (final Bucket bucket : bucketList) {
      System.out.println(bucket.getName());
      listObjects(bucket.getName(), _s3Client);
    }
    System.out.println();
  }

  public static void listObjects(String bucketName, AmazonS3 s3Client) {
    System.out.println("Listing objects in bucket " + bucketName);
    ObjectListing objectListing =
      s3Client.listObjects(new ListObjectsRequest().withBucketName(bucketName));

    for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
      System.out.println(" - " + objectSummary.getKey());
    }
    System.out.println();
  }

}
