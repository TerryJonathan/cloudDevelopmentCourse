package ax.ha.clouddevelopment;

import software.amazon.awscdk.*;
import software.amazon.awscdk.services.iam.AnyPrincipal;
import software.amazon.awscdk.services.iam.Effect;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.route53.targets.BucketWebsiteTarget;
import software.amazon.awscdk.services.s3.BlockPublicAccess;
import software.amazon.awscdk.services.s3.Bucket;
import software.constructs.Construct;
import software.amazon.awscdk.services.route53.*;
import java.util.List;
import java.util.Map;

public class WebsiteBucketStack extends Stack {

    /**
     * Creates a CloudFormation stack for a simple S3 bucket used as a website
     */
    public WebsiteBucketStack(final Construct scope,
                              final String id,
                              final StackProps props,
                              final String groupName) {
        super(scope, id, props);
        // Copied and pasted from previous exersice
        final Bucket siteBucket =
                Bucket.Builder.create(this, "websiteBucket")
                        .bucketName(groupName + ".cloud-ha.com") // Changed this to match criteria
                        .publicReadAccess(true)
                        .blockPublicAccess(BlockPublicAccess.BLOCK_ACLS)
                        .removalPolicy(RemovalPolicy.DESTROY)
                        .websiteIndexDocument("index.html")
                        .build();

                CfnOutput.Builder.create(this, "websiteBucketOutput")
                        .description(String.format("URL of your bucket.", groupName))
                        .value(siteBucket.getBucketWebsiteUrl())
                        .exportName(groupName + "-s3-test")
                        .build();
        // Copied and pasted from previous exersice

                PolicyStatement.Builder.create()
                        .sid("IPAllow")
                        .effect(Effect.ALLOW)
                        .principals(List.of(new AnyPrincipal()))
                        .actions(List.of("s3:GetObject"))// perhaps should be *  //s3:GetObject
                        .resources(List.of(siteBucket.getBucketArn()+"/*"))
                        .conditions(Map.of("IpAddress", Map.of("aws:SourceIp", List.of("0.0.0.0/0"))))
                        .build();



                HostedZoneAttributes hostedZoneAttributes = HostedZoneAttributes.builder()
                        .hostedZoneId("Z0413857YT73A0A8FRFF")
                        .zoneName("cloud-ha.com")
                        .build();



                RecordSet recordSet = RecordSet.Builder.create(this, "MyRecordSet")
                        .recordType(RecordType.A)
                        .target(RecordTarget.fromAlias(new BucketWebsiteTarget(siteBucket)))
                        .zone(HostedZone.fromHostedZoneAttributes(this,"MyHostedZone",hostedZoneAttributes))
                        // the properties below are optional
                        .comment("comment")
                        .deleteExisting(false)
                        .recordName("jonathan-terry.cloud-ha.com")
                        .ttl(Duration.minutes(30))
                        .build();



        // Define your resources here. You can look in the code from the previous laboration for inspiration to a start,
        // but otherwise you should be able finnish this by reading the AWS CDK API documentation the assignment text refers to.

        // Also, you have the file cloudformation-template.yaml which contains a solution in CloudFormation format.
        // This file can not be converted into Java code, but it should give you a good overview of what the expected result of a 'cdk synth' should be
    }
}