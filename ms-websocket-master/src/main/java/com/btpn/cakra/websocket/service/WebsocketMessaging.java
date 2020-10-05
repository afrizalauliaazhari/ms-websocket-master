package com.btpn.cakra.websocket.service;

import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.btpn.cakra.websocket.common.Http;
import com.btpn.cakra.websocket.config.EnvironmentVariable;
import com.btpn.cakra.websocket.config.StackTraceMessage;
import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

@RestController
@RequestMapping("/service")
@Service
public class WebsocketMessaging {

	private static Logger logger = Logger.getLogger("InfoLogging");
	Map<String, Date> jobList = new LinkedHashMap<>();
	MongoClient mongoClient;
	EnvironmentVariable envVar;
	MongoCollection<Document> jenkinsUrlCollection;
	MongoCollection<Document> jenkinsJobCollection;
	SimpMessagingTemplate brokerMessagingTemplate;

	private static String varGroupName = "groupName";
	private static String varJobName = "jobName";
	private static String varJobStatus = "jobStatus";
	private static String varJobHashcode = "jobHashcode";
	private static String varTimestamp = "timestamp";
	private static String varStageLabel = "stageLabel";
	private static String varGroupId = "groupId";
	private static String varJobPath = "job";
	private static String varProjectId = "projectId";
	private static String varJenkinsUrl = "jenkinsUrl";
	private static String varJenkinsUname = "jenkinsUname";
	private static String varJenkinsToken = "jenkinsToken";
	private static String varLastSuccessfulBuild = "lastSuccessfulBuild";
	private static String varDescription = "description";
	private static String varColor = "color";
	private static String varStatus = "status";

	public WebsocketMessaging(SimpMessagingTemplate brokerMessagingTemplate, MongoClient mongoClient,
			EnvironmentVariable envVar) {
		this.brokerMessagingTemplate = brokerMessagingTemplate;
		this.envVar = envVar;
		this.mongoClient = mongoClient;
	}

	@PostConstruct
	public void init() {
		this.jenkinsUrlCollection = this.mongoClient.getDatabase(envVar.getMongodbDatabase())
				.getCollection("jenkins_url");
		this.jenkinsJobCollection = this.mongoClient.getDatabase(envVar.getMongodbDatabase())
				.getCollection("jenkins_job");
	}

	@SuppressWarnings({ "unchecked" })
	public Map<String, Object>[] getBuildHistory(String url, String username, String token, Map<String, Object> env) {
		String httpUrl = url + varJobPath + "/" + env.get(varGroupName).toString() + "/" + varJobPath + "/"
				+ env.get(varJobName).toString() + "/api/json?tree=builds[number,displayName,result,timestamp]";
		String feedback = "";
		Map<String, Object>[] result = new Map[0];
		try {
			feedback = Http.scrape(httpUrl, username, token);
			if (!feedback.contains("HTTP ERROR") && !feedback.contains("503 Service Unavailable")
					&& !feedback.contains("502 Bad Gateway") && !feedback.contains("504 Gateway Timeout")
					&& !feedback.equals("error")) {
				JSONObject json = new JSONObject(feedback);
				JSONArray listJob = json.getJSONArray("builds");
				String returnStr = listJob.toString();
				result = new Gson().fromJson(returnStr, result.getClass());
				for (Map<String, Object> map : result) {
					BigDecimal d = new BigDecimal(map.get(varTimestamp).toString());
					map.put("group", env.get(varGroupName));
					map.put("name", env.get(varJobName));
					map.put(varTimestamp, d.longValue());
					map.put(varStageLabel, env.get(varStageLabel));
				}
			}
		} catch (Exception e) {
			logger.info("Error get build history cause : " + StackTraceMessage.getStackTrace(e));
		}
		return result;
	}

	@RequestMapping(value = "multiple-build-socket/{currentJob}/{totalJob}/{jobId}/{jobStatus}/{buildNumber}", method = RequestMethod.GET)
	public void multipleBuildSocket(@PathVariable Integer currentJob, @PathVariable Integer totalJob,
			@PathVariable String jobId, @PathVariable String jobStatus, @PathVariable String buildNumber) {
		try {
			FindIterable<Document> jobIterable = jenkinsJobCollection.find(new Document("_id", new ObjectId(jobId)))
					.projection(fields(include(varGroupId, varGroupName, varJobName)));
			Iterator<Document> jobIterator = jobIterable.iterator();
			if (jobIterator.hasNext()) {
				Document jobDoc = jobIterator.next();
				Map<String, Object> job = new HashMap<>(jobDoc);
				int percent = (currentJob * 100) / totalJob;
				job.put("percentage", percent);
				job.put(varJobStatus, jobStatus);
				job.put("buildNumber", buildNumber);

				Boolean inProgress = !(percent == 100 && (jobStatus.contains("SUCCESS") || jobStatus.contains("ABORTED")
						|| jobStatus.contains("FAILED")));
				job.put("inProgress", inProgress);
				brokerMessagingTemplate.convertAndSend("/topic/multiple/" + job.get(varGroupId).toString(), job);
			}
		} catch (Exception e) {
			logger.info("Error multiple build websocket cause : " + StackTraceMessage.getStackTrace(e));
		}
	}

	public void updateJobSummary(Map<String, Object> mapCheckedJob, Map<String, Object> param) {
		Map<String, Object> updated = new HashMap<>();
		updated.put(varJobStatus, mapCheckedJob);
		updated.put(varJobHashcode, mapCheckedJob.hashCode());
		try {
			jenkinsJobCollection.updateOne(new Document("_id", new ObjectId(param.get("_id").toString())),
					new Document("$set", new Document(updated)));
		} catch (Exception e) {
		}
		param.put(varJobStatus, mapCheckedJob);
		try {
			brokerMessagingTemplate.convertAndSend("/topic/job/" + param.get(varGroupId).toString(), param);
		} catch (Exception e) {
		}
	}

	@SuppressWarnings("unchecked")
	public void updateStage(Map<String, Object> jenkinsData, Map<String, Object> param,
			Map<String, Object> mapLastBuild) {
		Map<String, Object> mapCheckedDescription = new HashMap<>();
		try {
			String urlCheckDescription = jenkinsData.get(varJenkinsUrl).toString() + varJobPath + "/"
					+ param.get(varGroupName).toString() + "/" + varJobPath + "/" + param.get(varJobName).toString()
					+ "/api/json?tree=lastBuild[displayName,description,estimatedDuration]";
			String feedbackCheckDescription = Http.scrape(urlCheckDescription,
					jenkinsData.get(varJenkinsUname).toString(), jenkinsData.get(varJenkinsToken).toString());
			mapCheckedDescription = new Gson().fromJson(feedbackCheckDescription, mapCheckedDescription.getClass());
		} catch (Exception e) {
		}
		Map<String, Object> mapDescLastBuild = (Map<String, Object>) mapCheckedDescription.get("lastBuild");
		Map<String, Object> mapUpdate = new HashMap<>();
		mapUpdate.put("stageHashcode", mapLastBuild.hashCode());
		try {
			mapLastBuild.put(varDescription, mapDescLastBuild.get(varDescription));
			mapLastBuild.put("estimatedDuration", mapDescLastBuild.get("estimatedDuration"));
			mapUpdate.put("jobStage", mapLastBuild);
			jenkinsJobCollection.updateOne(new Document("_id", new ObjectId(param.get("_id").toString())),
					new Document("$set", new Document(mapUpdate)));
			Map<String, Object> mapSocket = new HashMap<>();
			mapSocket.put(varJobName, param.get(varJobName));
			mapSocket.put("jobStage", mapLastBuild);
			brokerMessagingTemplate.convertAndSend("/topic/stage/" + param.get(varProjectId).toString(), mapSocket);
		} catch (Exception e) {
		}
	}

	public void updateLog(Map<String, Object> jenkinsData, Map<String, Object> param,
			Map<String, Object> mapLastBuild) {
		String urlLog = jenkinsData.get(varJenkinsUrl).toString() + varJobPath + "/"
				+ param.get(varGroupName).toString() + "/" + varJobPath + "/" + param.get(varJobName).toString()
				+ "/lastBuild/consoleText";
		String feedbackLog = Http.scrape(urlLog, jenkinsData.get(varJenkinsUname).toString(),
				jenkinsData.get(varJenkinsToken).toString());
		Map<String, Object> mapLog = new HashMap<>();
		mapLog.put(varJobName, param.get(varJobName));
		mapLog.put(varStatus, mapLastBuild.get(varStatus));
		mapLog.put("log", feedbackLog);
		try {
			brokerMessagingTemplate.convertAndSend("/topic/log/" + param.get(varStageLabel).toString() + "/"
					+ param.get(varProjectId).toString() + "/" + mapLastBuild.get("id"), mapLog);
		} catch (Exception e) {
		}
	}

	@RequestMapping(value = "register-job/{jenkinsId}/{jobId}", method = RequestMethod.GET)
	public void regisJob(@PathVariable String jenkinsId, @PathVariable String jobId) {
		try {
			FindIterable<Document> jobIterable = jenkinsJobCollection.find(new Document("_id", new ObjectId(jobId)))
					.projection(fields(include("_id", varGroupId, varGroupName, varJobName, varStageLabel, varProjectId,
							varJobHashcode, "stageHashcode", "selectedTrigger")));
			Iterator<Document> jobIterator = jobIterable.iterator();

			FindIterable<Document> jenkinsIterable = jenkinsUrlCollection
					.find(new Document("_id", new ObjectId(jenkinsId)));
			Iterator<Document> jenkinsIterator = jenkinsIterable.iterator();
			if (jobIterator.hasNext() && jenkinsIterator.hasNext()) {
				Document jobDoc = jobIterator.next();
				Map<String, Object> job = new HashMap<>(jobDoc);

				Document jenkinsDoc = jenkinsIterator.next();
				Map<String, Object> jenkinsData = new HashMap<>(jenkinsDoc);
				if (!jobList.containsKey(job.get("_id").toString())) {
					RunningJob runningJob = new RunningJob(jenkinsData, job);
					Thread t = new Thread(runningJob);
					t.start();
					jobList.put(job.get("_id").toString(), new Date());
				}
			}
		} catch (Exception e) {
			logger.info("Error register job cause : " + StackTraceMessage.getStackTrace(e));
		}
	}

	private class RunningJob implements Runnable {
		Map<String, Object> jenkinsData;
		Map<String, Object> param;

		public RunningJob(Map<String, Object> jenkinsData, Map<String, Object> param) {
			this.jenkinsData = jenkinsData;
			this.param = param;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void run() {
			boolean isRunning = true;
			boolean isExpired = false;

			do {
				if (new Date().getTime() - jobList.get(param.get("_id").toString()).getTime() >= Integer
						.parseInt(envVar.getMinutes()) * 60 * 1000)
					isExpired = true;

				Map<String, Object> mapCheckedJob = new HashMap<>();
				Map<String, Object> mapLastBuild = new HashMap<>();

				String urlCheckJob = jenkinsData.get(varJenkinsUrl).toString() + varJobPath + "/"
						+ param.get(varGroupName).toString() + "/" + varJobPath + "/" + param.get(varJobName).toString()
						+ "/api/json?tree=name,color,lastSuccessfulBuild[displayName,timestamp]";
				String urlLastBuild = jenkinsData.get(varJenkinsUrl).toString() + varJobPath + "/"
						+ param.get(varGroupName).toString() + "/" + varJobPath + "/" + param.get(varJobName).toString()
						+ "/lastBuild/wfapi/";
				String feedbackCheckJob = Http.scrape(urlCheckJob, jenkinsData.get(varJenkinsUname).toString(),
						jenkinsData.get(varJenkinsToken).toString());
				String feedbackLastBuild = Http.scrape(urlLastBuild, jenkinsData.get(varJenkinsUname).toString(),
						jenkinsData.get(varJenkinsToken).toString());

				mapCheckedJob = new Gson().fromJson(feedbackCheckJob, mapCheckedJob.getClass());

				if (mapCheckedJob.get("name") != null) {
					try {
						mapLastBuild = new Gson().fromJson(feedbackLastBuild, mapLastBuild.getClass());

						if (mapCheckedJob.get(varLastSuccessfulBuild) != null) {
							BigDecimal d = new BigDecimal(
									((Map<String, Object>) mapCheckedJob.get(varLastSuccessfulBuild)).get(varTimestamp)
											.toString());
							((Map<String, Object>) mapCheckedJob.get(varLastSuccessfulBuild)).put(varTimestamp,
									d.longValue());
						}

						boolean isNewJob = param.get(varJobHashcode) == null;
						boolean isUpdatedJob = false;
						if (isNewJob) {
							isUpdatedJob = isNewJob;
						} else {
							isUpdatedJob = Integer.parseInt(param.get(varJobHashcode).toString()) != mapCheckedJob
									.hashCode();
						}

						if (isNewJob || isUpdatedJob) {
							updateJobSummary(mapCheckedJob, param);
						}

						updateStage(jenkinsData, param, mapLastBuild);

						updateLog(jenkinsData, param, mapLastBuild);

						isRunning = (mapLastBuild.get(varStatus).toString().contains("IN_PROGRESS")
								|| mapLastBuild.get(varStatus).toString().contains("NOT_EXECUTED")
								|| mapCheckedJob.get(varColor).toString().contains("anime"));
						jobList.put(param.get("_id").toString(), new Date());
						Thread.sleep(1000);
					} catch (Exception e) {
						logger.info("Error checking job " + param.get(varJobName).toString() + " cause : "
								+ StackTraceMessage.getStackTrace(e));
						isRunning = false;
						isExpired = true;
					}
				} else {
					isRunning = false;
				}
			} while (isRunning && !isExpired);

			jobList.remove(param.get("_id").toString());
		}
	}
}
