package com.sivalabs.awsdemo.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsAsyncClientBuilder;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {

    public static final String TEST_ACCESS_KEY = "test";
    public static final String TEST_SECRET_KEY = "test";
    public static final AWSCredentials TEST_CREDENTIALS = new BasicAWSCredentials(TEST_ACCESS_KEY, TEST_SECRET_KEY);

    private final ApplicationProperties properties;

    static {
        System.setProperty("com.amazonaws.sdk.disableCbor", "true");
    }

    public AwsConfig(ApplicationProperties properties) {
        this.properties = properties;
    }

    @Bean
    public AmazonS3 amazonS3() {
        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard().enablePathStyleAccess();
        if (properties.getEndpointUri() != null && properties.getEndpointUri().trim().length() != 0) {
            builder.withEndpointConfiguration(getEndpointConfiguration());
            builder.withCredentials(getCredentialsProvider());
        }
        return builder.build();
    }

    @Bean
    public AmazonSQSAsync amazonSQS() {
        AmazonSQSAsyncClientBuilder builder = AmazonSQSAsyncClientBuilder.standard();
        this.applyEndpointOverride(builder);
        AmazonSQSAsync amazonSQSAsync = builder.build();
        amazonSQSAsync.createQueue(properties.getQueueName());
        return amazonSQSAsync;
    }

    @Bean
    public AmazonSNSAsync amazonSNS() {
        AmazonSNSAsyncClientBuilder builder = AmazonSNSAsyncClientBuilder.standard();
        this.applyEndpointOverride(builder);
        AmazonSNSAsync amazonSNSAsync = builder.build();
        amazonSNSAsync.createTopic(properties.getTopicName());
        return amazonSNSAsync;
    }

    private void applyEndpointOverride(AwsAsyncClientBuilder<?,?> builder) {
        if (properties.getEndpointUri() != null && properties.getEndpointUri().trim().length() != 0) {
            builder.withEndpointConfiguration(getEndpointConfiguration());
            builder.withCredentials(getCredentialsProvider());
        }
    }

    private AWSCredentialsProvider getCredentialsProvider() {
        return new AWSStaticCredentialsProvider(TEST_CREDENTIALS);
    }

    private AwsClientBuilder.EndpointConfiguration getEndpointConfiguration() {
        return new AwsClientBuilder.EndpointConfiguration(properties.getEndpointUri(), properties.getRegion());
    }
}
