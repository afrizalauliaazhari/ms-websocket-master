package com.btpn.cakra.websocket.common;

import java.io.IOException;
import java.net.URI;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

public class Http {

	public static String scrape(String urlString, String username, String password) {
		String result = "error";
		CloseableHttpClient httpClient = null;
		try {
			URI uri = URI.create(urlString);
			HttpHost host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(new AuthScope(uri.getHost(), uri.getPort()),
					new UsernamePasswordCredentials(username, password));

			// Create AuthCache instance
			AuthCache authCache = new BasicAuthCache();

			SSLContext sslContext = SSLContextBuilder.create().loadTrustMaterial(new TrustSelfSignedStrategy()).build();

			HostnameVerifier allowAllHosts = new NoopHostnameVerifier();
			SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(sslContext, allowAllHosts);

			// Generate BASIC scheme object and add it to the local auth cache
			BasicScheme basicAuth = new BasicScheme();
			authCache.put(host, basicAuth);
			httpClient = HttpClients.custom().setSSLSocketFactory(connectionFactory)
					.setDefaultCredentialsProvider(credsProvider).build();
			HttpGet httpGet = new HttpGet(uri);

			// Add AuthCache to the execution context
			HttpClientContext localContext = HttpClientContext.create();
			localContext.setAuthCache(authCache);

			HttpResponse response = httpClient.execute(host, httpGet, localContext);

			HttpEntity entity = response.getEntity();

			result = EntityUtils.toString(entity);
			EntityUtils.consume(entity);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result;

	}
}
