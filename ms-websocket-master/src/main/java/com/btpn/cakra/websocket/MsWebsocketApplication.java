package com.btpn.cakra.websocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication(scanBasePackages = { "com.btpn.cakra.websocket" })
@EnableAutoConfiguration(exclude = { MongoAutoConfiguration.class })
public class MsWebsocketApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsWebsocketApplication.class, args);
	}
}
