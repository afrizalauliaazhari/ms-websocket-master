package com.btpn.cakra.websocket.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(SpringJUnit4ClassRunner.class)
public class HealthCheckTest {

	@InjectMocks
	HealthCheck hc;

	private MockMvc mockMvc;

	@Before
	public void initBefore() {
		mockMvc = MockMvcBuilders.standaloneSetup(hc).build();
	}

	@Test
	public void healthCheckTest() throws Exception {
		mockMvc.perform(get("/healthcheck"));
	}

}
