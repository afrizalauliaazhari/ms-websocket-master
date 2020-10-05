package com.btpn.cakra.websocket.config;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class StackTraceMessageTest {

	@InjectMocks
	StackTraceMessage stm;

	StackTraceMessage instance;

	@Before
	public void setUp() throws Exception {
		instance = new StackTraceMessage();
	}

	@After
	public void tearDown() throws Exception {
		instance = null;
	}

	@SuppressWarnings("unused")
	@Test
	public void testStackTraceMessage() throws Exception {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw, true);
	}

}
