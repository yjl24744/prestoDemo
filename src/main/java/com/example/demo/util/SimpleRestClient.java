package com.example.demo.util;

import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class SimpleRestClient {

	private static class SingletonHolder {
		public final static SimpleRestClient instance = new SimpleRestClient();
	}

	public static SimpleRestClient getInstance() {
		return SingletonHolder.instance;
	}

	/**
	 * 设置restTemplate对象
	 */
	public RestTemplate initRestTemplate() {
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setReadTimeout(30 * 1000);
		requestFactory.setConnectTimeout(30 * 1000);

		// 添加转换器
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
		messageConverters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
		messageConverters.add(new FormHttpMessageConverter());
		messageConverters.add(new MappingJackson2HttpMessageConverter());

		RestTemplate restTemplate = new RestTemplate(messageConverters);
		restTemplate.setRequestFactory(requestFactory);
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler());

		return restTemplate;
	}

	/**
	 * 设置restTemplate对象
	 */
	public org.springframework.web.client.AsyncRestTemplate initAsyncRestTemplate() {
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setReadTimeout(30 * 1000);
		requestFactory.setConnectTimeout(30 * 1000);
		requestFactory.setTaskExecutor(new SimpleAsyncTaskExecutor());
		// 添加转换器
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
		messageConverters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
		messageConverters.add(new FormHttpMessageConverter());
		messageConverters.add(new MappingJackson2HttpMessageConverter());

		org.springframework.web.client.AsyncRestTemplate restTemplate = new org.springframework.web.client.AsyncRestTemplate();
		restTemplate.setAsyncRequestFactory(requestFactory);
		restTemplate.setMessageConverters(messageConverters);
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
		return restTemplate;
	}

}