package com.sivalabs.todolist.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
public class AwsLocalConfig {

    @Autowired
    private AwsProperties awsProperties;

    public static final String TEST_ACCESS_KEY = "test";
    public static final String TEST_SECRET_KEY = "test";

    public static final AWSCredentials TEST_CREDENTIALS = new BasicAWSCredentials(TEST_ACCESS_KEY, TEST_SECRET_KEY);

    static {
        System.setProperty("com.amazonaws.sdk.disableCbor", "true");
    }

    @Bean
    @Primary
    public AmazonSQSAsync amazonSQSAsync() {
        AmazonSQSAsyncClientBuilder builder = AmazonSQSAsyncClientBuilder.standard();
        if(awsProperties.getEndpointURI() != null && awsProperties.getEndpointURI().trim().length() != 0){
            builder.withEndpointConfiguration(getEndpointConfiguration());
            builder.withCredentials(getCredentialsProvider());
        }
        return builder.build();
    }

    private AWSCredentialsProvider getCredentialsProvider() {
        return new AWSStaticCredentialsProvider(TEST_CREDENTIALS);
    }

    private EndpointConfiguration getEndpointConfiguration() {
        return new EndpointConfiguration(awsProperties.getEndpointURI(), awsProperties.getRegion());
    }
}
