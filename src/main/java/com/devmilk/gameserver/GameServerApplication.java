package com.devmilk.gameserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//TODO: Chat Kısmı ve controller kısmı
@SpringBootApplication
@EnableAutoConfiguration
public class GameServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(GameServerApplication.class, args);
	}

}
