package com.quicksure.insurance.utils;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
public class HttpRequestUtility {
	private static final Logger logger = Logger
			.getLogger(HttpRequestUtility.class);

	public static String getResponsebyGet(String url, List<NameValuePair> params) {
		logger.info("开始执行HttpClient get 请求，请求URL为： "+url);
		String result = "";
		HttpClient httpClient = new DefaultHttpClient();
		// 连接时间
		httpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);
		// 数据传输时间
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				2000);
		// Get请求
		HttpGet httpget = new HttpGet(url);
		try {
			// 设置参数
			String str = EntityUtils.toString(new UrlEncodedFormEntity(params,
					"UTF-8"));
			httpget.setURI(new URI(httpget.getURI().toString() + "?" + str));
			// 发送请求
			HttpResponse httpResponse = httpClient.execute(httpget);

			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				logger.error("执行HttpClient get 请求报错，statusCode为 ： "+statusCode);
			}
			// 获取返回数据
			HttpEntity entity = httpResponse.getEntity();
			String body = EntityUtils.toString(entity, "utf-8");
			result = body;
			if (entity != null) {
				EntityUtils.consume(entity);
			}
		} catch (Exception e) {
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		logger.info("结束执行HttpClient get 请求，返回结果为： "+result);
		return result;
	}
	
	public static void main(String[] args) {
		String url="https://api.weixin.qq.com/cgi-bin/token";
		HttpRequestUtility httpRequestUtility=new HttpRequestUtility();
		List<NameValuePair> params=new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("grant_type","client_credential"));
		params.add(new BasicNameValuePair("appid","wx17131d4b6c21e514"));
		params.add(new BasicNameValuePair("secret","bee8f01c6a52c77e180ab2ea63c72fff"));
		httpRequestUtility.getResponsebyGet(url, params);
		
	}
}
