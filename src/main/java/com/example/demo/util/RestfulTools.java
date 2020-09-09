package com.example.demo.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * RestTemplate同步请求工具类
 * 
 * @author lzc
 * @version 1.0, 2017-9-20
 * @since 1.0, 2017-9-20
 */
public class RestfulTools {

	protected static RestTemplate restTemplate;

	static {
		restTemplate = SimpleRestClient.getInstance().initRestTemplate();
	}

	/**
	 * Get方法
	 * 
	 * @param url:地址
	 * @param returnClassName:返回对象类型,如:String.class
	 * @param parameters:parameter参数
	 * @return
	 */
	public static <T> T get(String url, Class<T> returnClassName, Map<String, Object> parameters) {

		if (parameters == null) {
			return restTemplate.getForObject(url, returnClassName);
		}
		return restTemplate.getForObject(url, returnClassName, parameters);
	}

	/**
	 * Get方法
	 * 
	 * @param url:地址
	 * @param returnClassName:返回对象类型,如:String.class
	 * @param parameters:parameter参数
	 * @return
	 */
	public static <T> T get(String url, Class<T> returnClassName) {
		return restTemplate.getForObject(url, returnClassName);
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
	public static <T> T post(String url, Class<T> returnClassName, Map<String, String> inputHeader,
                             Map<String, Object> uriParameter, Object jsonBody) {
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
				httpHeaders.add(entry.getKey(), inputHeader.get(entry.getKey()));
			}
		}
		@SuppressWarnings({ "unchecked", "rawtypes" })
        HttpEntity<?> formEntity = new HttpEntity(jsonBody, httpHeaders);
		if (uriParameter == null) {
			return restTemplate.postForObject(url, formEntity, returnClassName);
		}
		return restTemplate.postForObject(url, formEntity, returnClassName, uriParameter);
	}

	/**
	 * post请求,包含了路径,返回类型,Header,Parameter
	 * 
	 * @param url:地址
	 * @param returnClassName:返回对象类型,如:String.class
	 * @param
	 * @param
	 * @param
	 * @return
	 */
	public static <T> T post(String url, Class<T> returnClassName, Map<String, Object> uriParameter,
                             Map<String, Object> requestParameter) {
		// 请求Header
		HttpHeaders httpHeaders = new HttpHeaders();
		// 设置请求的类型及编码
		MediaType type = MediaType
				.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8");
		httpHeaders.setContentType(type);
		httpHeaders.add("Accept", "application/x-www-form-urlencoded");
		List<MediaType> acceptableMediaTypes = new ArrayList<>();
		acceptableMediaTypes.add(MediaType.ALL);
		httpHeaders.setAccept(acceptableMediaTypes);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();

		if (requestParameter != null) {
			for (Map.Entry<String, Object> entry : requestParameter.entrySet()) {
				params.add(entry.getKey(),
						StringUtils.defaultBlankIfNull(requestParameter.get(entry.getKey())));
			}
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
        HttpEntity<?> formEntity = new HttpEntity(params, httpHeaders);
		if (uriParameter == null) {
			return restTemplate.postForObject(url, formEntity, returnClassName);
		}
		return restTemplate.postForObject(url, formEntity, returnClassName, uriParameter);
	}

	public static <T> T post(String url, Class<T> returnClassName, String jsonData) {
		return restTemplate.postForObject(url, null, returnClassName, jsonData);
	}

	public static String post(String url, Object jsonBody) {
		// 请求Header
		HttpHeaders httpHeaders = new HttpHeaders();
		// 设置请求的类型及编码
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		httpHeaders.setContentType(type);
		httpHeaders.add("Accept", "application/json");
		List<MediaType> acceptableMediaTypes = new ArrayList<>();
		acceptableMediaTypes.add(MediaType.ALL);
		httpHeaders.setAccept(acceptableMediaTypes);

		@SuppressWarnings({ "unchecked", "rawtypes" })
        HttpEntity<?> httpEntity = new HttpEntity(jsonBody, httpHeaders);
		return restTemplate.postForObject(url, httpEntity, String.class);
	}

	public static void put(String url, Object jsonBody) {
		// 请求Header
		HttpHeaders httpHeaders = new HttpHeaders();
		// 设置请求的类型及编码
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		httpHeaders.setContentType(type);
		httpHeaders.add("Accept", "application/json");
		List<MediaType> acceptableMediaTypes = new ArrayList<>();
		acceptableMediaTypes.add(MediaType.ALL);
		httpHeaders.setAccept(acceptableMediaTypes);

		@SuppressWarnings({ "unchecked", "rawtypes" })
        HttpEntity<?> httpEntity = new HttpEntity(jsonBody, httpHeaders);
		restTemplate.put(url, httpEntity);
		// return restTemplate.postForObject(url, httpEntity, String.class);
	}

	// /**
	// * 发送/获取 服务端数据(主要用于解决发送put,delete方法无返回值问题).
	// *
	// * @param url 绝对地址
	// * @param method 请求方式
	// * @param bodyType 返回类型
	// * @param <T> 返回类型
	// * @return 返回结果(响应体)
	// */
	// public <T> T exchange(String url, HttpMethod method, Class<T> bodyType) {
	// // 请求头
	// HttpHeaders headers = new HttpHeaders();
	// MimeType mimeType = MimeTypeUtils.parseMimeType("application/json");
	// MediaType mediaType = new MediaType(mimeType.getType(),
	// mimeType.getSubtype(), Charset.forName("UTF-8"));
	// // 请求体
	// headers.setContentType(mediaType);
	// //提供json转化功能
	// ObjectMapper mapper = new ObjectMapper();
	// String str = null;
	// try {
	// if (!params.isEmpty()) {
	// str = mapper.writeValueAsString(params);
	// }
	// } catch (JsonProcessingException e) {
	// e.printStackTrace();
	// }
	// // 发送请求
	// HttpEntity<String> entity = new HttpEntity<>(str, headers);
	// RestTemplate restTemplate = new RestTemplate();
	// ResponseEntity<T> resultEntity = restTemplate.exchange(url, method,
	// entity, bodyType);
	// return resultEntity.getBody();
	// }

	public static void delete(String url, Object... urlVariables) {
		restTemplate.delete(url, urlVariables);
	}

	public static void delete(String url, Map<String, ?> urlVariables) {
		restTemplate.delete(url, urlVariables);
	}

}
