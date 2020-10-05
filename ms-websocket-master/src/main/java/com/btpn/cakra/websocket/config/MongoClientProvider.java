package com.btpn.cakra.websocket.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

@Configuration
public class MongoClientProvider {

	@Bean
	public MongoClient getMongoClient(EnvironmentVariable env) {

		// prepare integration test
		onSetIntegrationTest(env);

		MongoCredential mongoCredential = MongoCredential.createCredential(env.getMongodbUsername(),
				env.getMongodbDatabase(), env.getMongodbPassword().toCharArray());
		List<ServerAddress> seeds = new ArrayList<>();
		List<String> serverList = Arrays.asList(env.getMongodbHost().split(",[ ]*"));
		for (String server : serverList) {
			seeds.add(new ServerAddress(server, Integer.parseInt(env.getMongodbPort())));
		}
		MongoClientOptions options = new MongoClientOptions.Builder()
				.serverSelectionTimeout(Integer.parseInt(env.getMongodbTimeout())).build();

		return new MongoClient(seeds, mongoCredential, options);
	}

	private void onSetIntegrationTest(EnvironmentVariable env) {
		if (env.getMongodbPassword() == null) {
			env.setMongodbPassword("pass");
			env.setMongodbUsername("user");
			env.setMongodbDatabase("db");
			env.setMongodbHost("host");
			env.setMongodbPort("1234");
			env.setMongodbTimeout("60000");
		}

	}

}
