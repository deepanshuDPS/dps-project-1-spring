package com.indower.indtest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.indower.indtest.controllerAdvice.CustomRestExceptionHandler;

@SpringBootApplication
@EnableMongoRepositories
@Import(CustomRestExceptionHandler.class)
public class IndtestApplication {

	public static void main(String[] args) {
		SpringApplication.run(IndtestApplication.class, args);
		try {
			FirebaseOptions options = new FirebaseOptions.Builder()
					.setCredentials(GoogleCredentials.fromStream(
							new ClassPathResource("/static/indower-testing-firebase-adminsdk-i1scx-f7b4c9f72c.json")
									.getInputStream()))
					.build();
			FirebaseApp.initializeApp(options);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// // this will handle etag for cache reponse handling
	// @Bean
	// public Filter filter(){
	// ShallowEtagHeaderFilter filter = new ShallowEtagHeaderFilter();
	// return filter;
	// }

	
}
