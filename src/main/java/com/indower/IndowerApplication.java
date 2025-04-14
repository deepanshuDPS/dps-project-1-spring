package com.indower;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.indower.controllerAdvice.CustomRestExceptionHandler;
import com.indower.filters.AuthFilter;
import com.indower.filters.NormalFilter;
import com.indower.filters.ThrottleFilter;
import com.indower.utils.EnvironmentSetup;

@SpringBootApplication
@EnableMongoRepositories
@Import({ CustomRestExceptionHandler.class, ThrottleFilter.class, AuthFilter.class, NormalFilter.class })
public class IndowerApplication {

	@Value("${spring.firebase.admin}")
	private String firebaseAdminString;

	@Value("${spring.aws.accesskey}")
	private String accessKey;

	@Value("${spring.aws.secretkey}")
	private String accessSecret;

	@Value("${spring.aws.region}")
	private String region;

	@Value("${spring.encryption.key}")
	private String encryptionKey;

	@Value("${spring.encryption.country}")
	private String encryptionCountry;

	@Autowired
	private Environment environment;

	public static void main(String[] args) {
		SpringApplication.run(IndowerApplication.class, args);

	}

	// this will handle etag for cache reponse handling
	@Bean
	public FirebaseApp createFireBaseApp() {
		try {

			InputStream stream = new ByteArrayInputStream(firebaseAdminString.getBytes(Charset.forName("UTF-8")));

			FirebaseOptions options = FirebaseOptions.builder()
					.setCredentials(GoogleCredentials.fromStream(stream))
					.build();
			FirebaseApp.initializeApp(options);
		} catch (Exception e) {
			// System.out.println("firebase error " + e.getMessage());
			e.printStackTrace();
		}
		return FirebaseApp.getInstance();
	}

	@Bean
	@DependsOn(value = "createFireBaseApp")
	public FirebaseAuth createFirebaseAuth() {
		return FirebaseAuth.getInstance();
	}

	@Bean
	@DependsOn(value = "createFireBaseApp")
	public FirebaseRemoteConfig createFirebaseRemoteConfig() {
		return FirebaseRemoteConfig.getInstance();
	}

	@Bean
	EnvironmentSetup getEnvironmentSetup() {
		if (environment != null)
			return EnvironmentSetup.getInstance(environment.getActiveProfiles(), encryptionKey, encryptionCountry);
		else
			return EnvironmentSetup.getInstance(new String[] {}, encryptionKey, encryptionCountry);
	}

	@Bean
	public CorsFilter corsFilter() {
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		final CorsConfiguration config = new CorsConfiguration();
		if (getEnvironmentSetup().isProd()) // only for prod
			config.addAllowedOrigin("https://indower.dpskreations.com/"); // this allows all origin
		else {
			config.addAllowedOrigin("http://localhost:3000/");
			config.addAllowedOrigin("https://ind-dev-app.dpskreations.com/");
		}
		config.addAllowedMethod("OPTIONS");
		config.addAllowedMethod("HEAD");
		config.addAllowedMethod("GET");
		config.addAllowedMethod("PUT");
		config.addAllowedMethod("POST");
		config.addAllowedMethod("DELETE");
		config.addAllowedMethod("PATCH");
		config.setMaxAge(43200L); // 12 hours caching allowed for preflight
		config.addAllowedHeader("*");
		config.addExposedHeader("*");
		config.setAllowCredentials(true);
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}

	@Bean
	public AmazonS3 s3Client() {
		AWSCredentials credentials = new BasicAWSCredentials(accessKey, accessSecret);
		return AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(region).build();
	}

}
