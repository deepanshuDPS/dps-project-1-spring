package com.indower.indtest;

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

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.indower.indtest.controllerAdvice.CustomRestExceptionHandler;
import com.indower.indtest.filters.CustomFilter;
import com.indower.indtest.filters.NormalFilter;
import com.indower.indtest.utils.EnvironmentSetup;

@SpringBootApplication
@EnableMongoRepositories
@Import({ CustomRestExceptionHandler.class, CustomFilter.class, NormalFilter.class })
public class IndtestApplication {

	@Value("${spring.firebase.admin}")
	private String firebaseAdminString;

	@Autowired
	private Environment environment;

	public static void main(String[] args) {
		SpringApplication.run(IndtestApplication.class, args);
		
	}

	// this will handle etag for cache reponse handling
	@Bean
	public FirebaseApp createFireBaseApp() {
		try {
			
			InputStream stream = new ByteArrayInputStream(firebaseAdminString.getBytes(Charset.forName("UTF-8")));
 
			FirebaseOptions options = new FirebaseOptions.Builder()
					.setCredentials(GoogleCredentials.fromStream(stream))
					.build();
			FirebaseApp.initializeApp(options);
		} catch (Exception e) {
			System.out.println("firebase error "+e.getMessage());
			e.printStackTrace();
		}
		return FirebaseApp.getInstance();
	}

	@Bean
    @DependsOn(value = "createFireBaseApp")
    public FirebaseAuth createFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

	@Bean EnvironmentSetup getEnvironmentSetup(){
		if(environment!=null)
			return EnvironmentSetup.getInstance(environment.getActiveProfiles());
		else
			return EnvironmentSetup.getInstance(new String[]{});
	}

	@Bean
     public CorsFilter corsFilter() {
         final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
         final CorsConfiguration config = new CorsConfiguration();
         //config.setAllowCredentials(true);
         config.addAllowedOrigin("http://localhost:3000/");
		 config.addAllowedOrigin("https://indower.dpskreations.com/"); // this allows all origin
         config.addAllowedMethod("OPTIONS");
         config.addAllowedMethod("HEAD");
         config.addAllowedMethod("GET");
         config.addAllowedMethod("PUT");
         config.addAllowedMethod("POST");
         config.addAllowedMethod("DELETE");
         config.addAllowedMethod("PATCH");
		 config.addAllowedHeader("*");
		 //config.addAllowedHeader("content-type");
		 //System.out.println("hellow"+config.getAllowedHeaders());
         source.registerCorsConfiguration("/**", config);
         return new CorsFilter(source);
     }

}
