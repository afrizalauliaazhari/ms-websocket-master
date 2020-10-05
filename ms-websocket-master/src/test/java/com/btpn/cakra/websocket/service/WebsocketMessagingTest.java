package com.btpn.cakra.websocket.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

@RunWith(SpringJUnit4ClassRunner.class)
public class WebsocketMessagingTest {

	@InjectMocks
	WebsocketMessaging wbm;

	@Mock
	MongoCollection<Document> jenkinsJobCollection;

	@Mock
	MongoClient mongoClient;

	@SuppressWarnings("unused")
	private MockMvc mockMvc;

	@Before
	public void initBefore() {
		mockMvc = MockMvcBuilders.standaloneSetup(jenkinsJobCollection).build();
	}

	@Test
	public void updateJobSummaryTest() {
		Map<String, Object> mapCheckedJob = new HashMap<String, Object>(), param = new HashMap<String, Object>();
		param.put("_id", "5e59b04d1be43658d3a44876");
		param.put("groupId", "123");
		wbm.updateJobSummary(mapCheckedJob, param);
	}

	@Test
	public void updateStageTest() {
		Map<String, Object> jenkinsData = new HashMap<String, Object>(), param = new HashMap<String, Object>(),
				mapLastBuild = new HashMap<String, Object>();
		param.put("_id", "5e59b04d1be43658d3a44876");
		param.put("groupId", "123");
		wbm.updateStage(jenkinsData, param, mapLastBuild);
	}

	@Test
	public void allGet() throws Exception {
		mockMvc.perform(get("/services/multiple-build-socket/111/123/456/abc/qwert"));
		mockMvc.perform(get("/services/register-job/abc/qwert"));
	}

}
