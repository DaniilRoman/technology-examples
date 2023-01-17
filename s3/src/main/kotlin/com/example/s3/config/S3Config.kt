package com.example.s3.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import java.io.File


@Service
class TestService(var s3: AmazonS3) {


    @PostConstruct
    fun setup() {
        if (!s3.doesBucketExistV2("test-bucket")) {
            s3.createBucket("test-bucket")
        }

        val testFile = File(TestService::class.java.getResource("/testPng.png").toURI()) // works only locally
        s3.putObject("test-bucket", "testKey", testFile)

        val testPng = s3.getObject("test-bucket", "testKey")
        testPng.objectContent.delegateStream.transferTo(File("./output.png").outputStream())

        s3.deleteObject("test-bucket", "testKey")
        s3.deleteBucket("test-bucket") // cannot delete if there's aby object inside
    }
}

@Configuration
class S3Config(@Value("\${cloud.aws.credentials.access-key}")
               private val awsAccessKey: String,
               @Value("\${cloud.aws.credentials.secret-key}")
               private val awsSecretKey: String,
               @Value("\${cloud.aws.region.static}")
               private val region: String,
               @Value("\${cloud.aws.endpoint}")
               private val endpoint: String?
    ) {

    @Primary
    @Bean
    fun amazonS3Client(): AmazonS3 {
        val builder = AmazonS3ClientBuilder
            .standard()
            .withCredentials(
                AWSStaticCredentialsProvider(
                    BasicAWSCredentials(awsAccessKey, awsSecretKey)
                )
            )
            .withPathStyleAccessEnabled(true)

        if (endpoint != null) {
            builder.withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(endpoint, region))
        } else {
            builder.withRegion(region)
        }

        return builder.build()
    }
}
