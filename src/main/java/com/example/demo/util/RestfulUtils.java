package com.example.demo.util;

import com.alibaba.druid.support.json.JSONUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Restful工具类
 * 
 * @author LT
 * @date 2019年12月24日
 */
public class RestfulUtils extends RestfulTools {

	/**
	 * post请求,包含了路径,返回类型,Header,Parameter
	 * 
	 * @param url:地址
	 * @param returnClassName:返回对象类型,如:String.class
	 * @param inputHeader
	 * @param inputParameter
	 * @param jsonBody
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T post(String url, Class<T> returnClassName, Map<String, String> inputHeader,
                             Map<String, Object> uriParameter, Map<String, Object> paramMap) {
		// 请求Header
		HttpHeaders httpHeaders = new HttpHeaders();
		// 设置请求的类型及编码
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		httpHeaders.setContentType(type);
		httpHeaders.add("Accept", "application/json");
		List<MediaType> acceptableMediaTypes = new ArrayList<>();
		acceptableMediaTypes.add(MediaType.ALL);
		httpHeaders.setAccept(acceptableMediaTypes);
		// 拼接Header
		if (inputHeader != null && inputHeader.size() > 0) {
			for (Map.Entry<String, String> entry : inputHeader.entrySet()) {
				httpHeaders.set(entry.getKey(), inputHeader.get(entry.getKey()));
			}
		}
		List<String> contentTypes = httpHeaders.get(HttpHeaders.CONTENT_TYPE);
		HttpEntity<?> formEntity = null;
		Object jsonBody = null;
		if (contentTypes != null && contentTypes.size() > 0) {
			String contentType = contentTypes.get(0);
			if (StringUtils.isNotBlank(contentType)
					&& "application/x-www-form-urlencoded".equalsIgnoreCase(contentType.trim())) {
				MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<String, Object>();
				postParameters.setAll(paramMap);
				formEntity = new HttpEntity<>(postParameters, httpHeaders);
			} else {
				jsonBody = JSONUtils.toJSONString(paramMap);
			}
		}
		if (formEntity == null) {
			formEntity = new HttpEntity(jsonBody, httpHeaders);
		}
		if (uriParameter == null) {
			return restTemplate.postForObject(url, formEntity, returnClassName);
		}
		restTemplate.postForEntity(url, formEntity, returnClassName, uriParameter);
		return restTemplate.postForObject(url, formEntity, returnClassName, uriParameter);
	}

	/**
	 * post请求,包含了路径,返回类型,Header,Parameter
	 * 
	 * @param url:地址
	 * @param returnClassName:返回对象类型,如:String.class
	 * @param inputHeader
	 * @param inputParameter
	 * @param jsonBody
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> ResponseEntity postForEntity(String url, Class<T> returnClassName,
                                                   Map<String, String> inputHeader, Map<String, Object> uriParameter,
                                                   Map<String, Object> paramMap) {
		// 请求Header
		HttpHeaders httpHeaders = new HttpHeaders();
		// 设置请求的类型及编码
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		httpHeaders.setContentType(type);
		httpHeaders.add("Accept", "application/json");
		List<MediaType> acceptableMediaTypes = new ArrayList<>();
		acceptableMediaTypes.add(MediaType.ALL);
		httpHeaders.setAccept(acceptableMediaTypes);
		// 拼接Header
		if (inputHeader != null && inputHeader.size() > 0) {
			for (Map.Entry<String, String> entry : inputHeader.entrySet()) {
				httpHeaders.set(entry.getKey(), inputHeader.get(entry.getKey()));
			}
		}
		List<String> contentTypes = httpHeaders.get(HttpHeaders.CONTENT_TYPE);
		HttpEntity<?> formEntity = null;
		Object jsonBody = null;
		if (contentTypes != null && contentTypes.size() > 0) {
			String contentType = contentTypes.get(0);
			if (StringUtils.isNotBlank(contentType)
					&& "application/x-www-form-urlencoded".equalsIgnoreCase(contentType.trim())) {
				MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<String, Object>();
				postParameters.setAll(paramMap);
				formEntity = new HttpEntity<>(postParameters, httpHeaders);
			} else {
				jsonBody = JSONUtils.toJSONString(paramMap);
			}
		}
		if (formEntity == null) {
			formEntity = new HttpEntity(jsonBody, httpHeaders);
		}
		if (uriParameter == null) {
			return restTemplate.postForEntity(url, formEntity, returnClassName);
		}
		return restTemplate.postForEntity(url, formEntity, returnClassName, uriParameter);
	}

	/**
	 * Get方法
	 * 
	 * @param url:地址
	 * @param returnClassName:返回对象类型,如:String.class
	 * @param parameters:parameter参数
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T get(String url, Class<T> returnClassName, Map<String, String> inputHeader,
                            Map<String, Object> paramMap) {
		// if (parameters == null) {
		// return restTemplate.getForObject(url, returnClassName);
		// }
		// return restTemplate.getForObject(url, returnClassName, parameters);
		// 请求Header
		HttpHeaders httpHeaders = new HttpHeaders();
		// 设置请求的类型及编码
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		httpHeaders.setContentType(type);
		httpHeaders.add("Accept", "application/json");
		List<MediaType> acceptableMediaTypes = new ArrayList<>();
		acceptableMediaTypes.add(MediaType.ALL);
		httpHeaders.setAccept(acceptableMediaTypes);
		// 拼接Header
		if (inputHeader != null && inputHeader.size() > 0) {
			for (Map.Entry<String, String> entry : inputHeader.entrySet()) {
				httpHeaders.set(entry.getKey(), inputHeader.get(entry.getKey()));
			}
		}
		List<String> contentTypes = httpHeaders.get(HttpHeaders.CONTENT_TYPE);
		HttpEntity<?> formEntity = null;
		Object jsonBody = null;
		if (contentTypes != null && contentTypes.size() > 0) {
			String contentType = contentTypes.get(0);
			if (StringUtils.isNotBlank(contentType)
					&& "application/x-www-form-urlencoded".equalsIgnoreCase(contentType.trim())) {
				MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<String, Object>();
				postParameters.setAll(paramMap);
				formEntity = new HttpEntity<>(postParameters, httpHeaders);
			} else {
				jsonBody = JSONUtils.toJSONString(paramMap);
			}
		}
		formEntity = new HttpEntity(jsonBody, httpHeaders);
		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET,
				formEntity, String.class);
		return (T) responseEntity.getBody();
	}

	/**
	 * Get方法
	 * 
	 * @param url:地址
	 * @param returnClassName:返回对象类型,如:String.class
	 * @param parameters:parameter参数
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> ResponseEntity gettForEntity(String url, Class<T> returnClassName,
                                                   Map<String, String> inputHeader, Map<String, Object> paramMap) {
		// if (parameters == null) {
		// return restTemplate.getForObject(url, returnClassName);
		// }
		// return restTemplate.getForObject(url, returnClassName, parameters);
		// 请求Header
		HttpHeaders httpHeaders = new HttpHeaders();
		// 设置请求的类型及编码
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		httpHeaders.setContentType(type);
		httpHeaders.add("Accept", "application/json");
		List<MediaType> acceptableMediaTypes = new ArrayList<>();
		acceptableMediaTypes.add(MediaType.ALL);
		httpHeaders.setAccept(acceptableMediaTypes);
		// 拼接Header
		if (inputHeader != null && inputHeader.size() > 0) {
			for (Map.Entry<String, String> entry : inputHeader.entrySet()) {
				httpHeaders.set(entry.getKey(), inputHeader.get(entry.getKey()));
			}
		}
		List<String> contentTypes = httpHeaders.get(HttpHeaders.CONTENT_TYPE);
		HttpEntity<?> formEntity = null;
		Object jsonBody = null;
		if (contentTypes != null && contentTypes.size() > 0) {
			String contentType = contentTypes.get(0);
			if (StringUtils.isNotBlank(contentType)
					&& "application/x-www-form-urlencoded".equalsIgnoreCase(contentType.trim())) {
				MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<String, Object>();
				postParameters.setAll(paramMap);
				formEntity = new HttpEntity<>(postParameters, httpHeaders);
			} else {
				jsonBody = JSONUtils.toJSONString(paramMap);
			}
		}
		formEntity = new HttpEntity(jsonBody, httpHeaders);
		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET,
				formEntity, String.class);
		return responseEntity;
	}

	/**
	 * Get方法
	 *
	 * @param url:地址
	 * @param returnClassName:返回对象类型,如:String.class
	 * @param parameters:parameter参数
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> ResponseEntity putForEntity(String url, Class<T> returnClassName,
                                                  Map<String, String> inputHeader, Map<String, Object> paramMap) {
		// if (parameters == null) {
		// return restTemplate.getForObject(url, returnClassName);
		// }
		// return restTemplate.getForObject(url, returnClassName, parameters);
		// 请求Header
		HttpHeaders httpHeaders = new HttpHeaders();
		// 设置请求的类型及编码
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		httpHeaders.setContentType(type);
		httpHeaders.add("Accept", "application/json");
		List<MediaType> acceptableMediaTypes = new ArrayList<>();
		acceptableMediaTypes.add(MediaType.ALL);
		httpHeaders.setAccept(acceptableMediaTypes);
		// 拼接Header
		if (inputHeader != null && inputHeader.size() > 0) {
			for (Map.Entry<String, String> entry : inputHeader.entrySet()) {
				httpHeaders.set(entry.getKey(), inputHeader.get(entry.getKey()));
			}
		}
		List<String> contentTypes = httpHeaders.get(HttpHeaders.CONTENT_TYPE);
		HttpEntity<?> formEntity = null;
		Object jsonBody = null;
		if (contentTypes != null && contentTypes.size() > 0) {
			String contentType = contentTypes.get(0);
			if (StringUtils.isNotBlank(contentType)
					&& "application/x-www-form-urlencoded".equalsIgnoreCase(contentType.trim())) {
				MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<String, Object>();
				postParameters.setAll(paramMap);
				formEntity = new HttpEntity<>(postParameters, httpHeaders);
			} else {
				jsonBody = JSONUtils.toJSONString(paramMap);
			}
		}
		formEntity = new HttpEntity(jsonBody, httpHeaders);
		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT,
				formEntity, String.class);
		return responseEntity;
	}
}
