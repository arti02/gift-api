package com.giftapi;

import org.springframework.boot.SpringApplication;

public class TestGiftApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(GiftApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
