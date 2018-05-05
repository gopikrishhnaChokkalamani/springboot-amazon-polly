package com.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.polly.AmazonPolly;
import com.amazonaws.services.polly.AmazonPollyClientBuilder;
import com.amazonaws.services.polly.model.SynthesizeSpeechRequest;

@Configuration
public class AmazonPollyConfig {

	@Bean
	public AmazonPolly amazonPolly() {
		return AmazonPollyClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(
						new BasicAWSCredentials("yourAccessKey", "yourSecretAccessKey")))
				.withRegion("US_EAST_1").build();
	}

	
	 
	/*@Bean
	public Voice getVoice() {
		// Create describe voices request.
		DescribeVoicesRequest describeVoicesRequest = new DescribeVoicesRequest().withLanguageCode("en-US");
		// Synchronously ask Amazon Polly to describe available TTS voices.
		DescribeVoicesResult describeVoicesResult = amazonPolly().describeVoices(describeVoicesRequest);
		return describeVoicesResult.getVoices().get(3);
	}*/

	@Bean
	public SynthesizeSpeechRequest synthesizeSpeechRequest() {
		return new SynthesizeSpeechRequest();
	}
}
