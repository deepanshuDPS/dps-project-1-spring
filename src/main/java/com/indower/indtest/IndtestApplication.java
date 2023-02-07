package com.indower.indtest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.ErrorPageFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.indower.indtest.controllerAdvice.CustomRestExceptionHandler;
import com.indower.indtest.filters.CustomFilter;

@SpringBootApplication
@EnableMongoRepositories
@Import({ CustomRestExceptionHandler.class, CustomFilter.class })
public class IndtestApplication {

	private static char quotes ='"';
	private static String firebaseAdminString = "{"+quotes+"type"+quotes+": "+quotes+"service_account"+quotes+",		"+quotes+"project_id"+quotes+": "+quotes+"indower-testing"+quotes+",		"+quotes+"private_key_id"+quotes+": "+quotes+"f7b4c9f72c6192261d7022dee12050537b11c135"+quotes+",		"+quotes+"private_key"+quotes+": "+quotes+"-----BEGIN PRIVATE KEY-----\\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCmN7ebEkLIso5I\\n7prn2MaeWyMgAGHseh7AmRXdOIDGGD4D7sx2/rPw4Y+0n/wTxLQSh9axCbXFkGMS\\nXRkTREWZx9AHhJ/S/Ruw3gJ3qpSWBbzLCKOAZiF5qXS1/6PxDqgjRILvdXnromS9\\nVKsRcfavyTaI4EQvuJm/YpgBpY7v2hw9G4ieqnQUcv99cSARtJIfeQ4per0OaQkF\\npXIgHpyFCD5uuoGE/c09cZVUg9WQaiK4DN8Yz4dUIiJQEtsXNsCq6Ou3Sd1X1mtP\\nzVHFFUwC6ZillP640kvAYv4QH4SBKTOsCWCdh/B0r+RfqymiKpTvKHME27ggOAP+\\n5l3bwWHjAgMBAAECggEANT7wACsPXb9lORPGZ7LXnW7Qh7XXqKtOIqBnkqBBEDQ/\\n2X6r3QqmlemL4W7Gj4aZsL33Fmtn4UvpkL5mA/gA28xYZbAkirAuKLBV1gO86XoZ\\nCmlQBxewbYrNKmgkGlp5niKaVx8o2UUCRGxVCJebUKCCacJ91VOrXNW2oABrE4Yk\\nzniO4SWpsP/hN88c/iAmsC6t1+xwUj5PJSLWk9h8D1oH2c8j+Opf8AHIDD43kaoV\\n/4GDknDQqbHKq6oYCmVJ5hYZu5lweVIFy0u6E/dVCC52/jnHAAlSVdtIJpaF6HYU\\ngrRnvlTCvRsMgrS5W7N/kGLJtEZ4wRUXsJjkYAHHkQKBgQDbkPiwvvRlxdA1I+gh\\nHTZDWVFOpunv7409iHNhb32snZbvTHK4d6AgjdZtrxzp7fXVAz9Ty8pZjzL6sbRh\\nbslaDJR+ZYYziyUcLLJQQVPiMQ0AX6ugr8ZkfWzzLv5MDzh3i1Eyk6gREQ9CGr1G\\nAAeVxNRD3Y2v+4qnwDjUmJx6HQKBgQDBzIfwaWYT4kkdB1HFmpwmCspkDEfg3c7s\\n7oCGTfdxNfh6CMIw5THdCuS7iL41BO/bWzUnv/rsIppHxL8OE6tRdRb+/fo+F9/g\\nYCFBwZYKGmXDNjiD/DbrS6wZJd8AUQROeFvEvzeIvZKdkLx6fekC36xSL55zW6ZB\\nCB1wxA6L/wKBgQCW1sZyrgy6aiY6i5tqPGZN6Gt6LCMkqZ6PelFBy9U7o9vQM+XQ\\nIqaIFskL+zRS5R5wo1V7HNK7qivsm+5+zRxY17GgD+EdQedRrsclaWvWRbpX2mHO\\n91LFMIGLo2oiGIbDYZt7soLQjra7xCDpmRWc52an5On0z0sXKXAhZWKCZQKBgQCm\\n4Kk58nhMrPEKRxA52N2WwHnw1sZaMoWJbgJMI/zrhm7VHo+hLOrYDRZTCffHjd3A\\nhHRsKxs1lMJHBHsD8xn1hZ7sqeR+7W4BNqXMipbZUkvIYfseY+Mij8G80eiJzDmq\\nHGfhLkryu1LxzP0PeTZZHCO1jX2ilgu+Oe+n95sTHwKBgBY/TGrLh7ca6OpPvcE8\\nz4/HQa/v3aHSrjoptv99HrUS9ckZ/RwHDDg/ltWE4EbpoVuDy1CTV1xKNuOQ36ZZ\\nFIiiknWVYMRoaDvLig2QVONv+yF6DSdJGr2E6Nlub4v8TBI7Baz4N2X8B68oPkt/\\npqsOAwS3Bjz/JdxLXxR+c1PS\\n-----END PRIVATE KEY-----\\n"+quotes+",		"+quotes+"client_email"+quotes+": "+quotes+"firebase-adminsdk-i1scx@indower-testing.iam.gserviceaccount.com"+quotes+",		"+quotes+"client_id"+quotes+": "+quotes+"105746185804683539615"+quotes+",		"+quotes+"auth_uri"+quotes+": "+quotes+"https://accounts.google.com/o/oauth2/auth"+quotes+",		"+quotes+"token_uri"+quotes+": "+quotes+"https://oauth2.googleapis.com/token"+quotes+",		"+quotes+"auth_provider_x509_cert_url"+quotes+": "+quotes+"https://www.googleapis.com/oauth2/v1/certs"+quotes+",		"+quotes+"client_x509_cert_url"+quotes+": "+quotes+"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-i1scx%40indower-testing.iam.gserviceaccount.com"+quotes+"}";
	
	public static void main(String[] args) {
		SpringApplication.run(IndtestApplication.class, args);
		
	}

	// this will handle etag for cache reponse handling
	@Bean
	public FirebaseApp firebaseApp() {
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
	public ErrorPageFilter filter() {
		return new ErrorPageFilter();
	}
	

	@Bean
	public FilterRegistrationBean disableSpringBootErrorFilter(ErrorPageFilter filter) {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		filterRegistrationBean.setFilter(filter);
		filterRegistrationBean.setEnabled(false);
		return filterRegistrationBean;
	}

}
