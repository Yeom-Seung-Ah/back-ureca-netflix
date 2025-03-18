package com.netflix.ureca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:config/secu.properties")
public class NetflixUrecaBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(NetflixUrecaBackApplication.class, args);
	}

}
