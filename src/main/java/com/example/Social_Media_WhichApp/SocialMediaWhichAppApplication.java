package com.example.Social_Media_WhichApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling// Kích hoạt tính năng lập lịch
public class SocialMediaWhichAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialMediaWhichAppApplication.class, args);
	}

}
