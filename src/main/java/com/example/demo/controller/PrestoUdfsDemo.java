package com.example.demo.controller;

import com.example.demo.util.NamedParameterStatement;
import com.example.demo.util.RestfulUtils;
import com.facebook.presto.jdbc.internal.common.type.StandardTypes;
import com.facebook.presto.jdbc.internal.io.airlift.slice.Slice;
import com.facebook.presto.jdbc.internal.spi.function.SqlType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCountCallbackHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Api(value = "PrestoUdfsQuery", tags = "Presto自定义函数查询")
@RequestMapping("/prestoUdfs")
@RestController
public class PrestoUdfsDemo {
	@Autowired
	@Qualifier("prestoTemplate")
	JdbcTemplate jt ;

	/**
	 * presto创建映射表
	 * @return
	 */
	@ApiOperation(
			value = "掩码自x至y",
			notes = "掩码自x至y",
			httpMethod = "POST",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	@RequestMapping(value = "/testMaskinstr", method = RequestMethod.POST)
	public List<Map<String, Object>> testMaskinstr(){
		String sql = "select maskinstr(name, 1, 3) from mysql.test01.student";
		return jt.queryForList(sql);

	}

	public static String maskinstr(String string, long begin, long end) {
		if (begin > end) {
		}
		String input = string;
		char[] inputCharArry = input.toCharArray();
		for (int i = 0; i < input.length(); i++) {
			if (i >= (begin-1) && i <= (end-1)) {
				inputCharArry[i] = '*';
			}
		}
		return String.valueOf(inputCharArry);
	}

	@ApiOperation(
			value = "掩码前n后m",
			notes = "掩码前n后m",
			httpMethod = "POST",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	@RequestMapping(value = "/testMaskexstr", method = RequestMethod.POST)
	public List<Map<String, Object>> testMaskexstr(){
		String sql = "select maskexstr(name, 1, 3) from mysql.test01.student";
		return jt.queryForList(sql);
	}

	public static String maskexstr(String string, long begin, long end) {
		if (begin > end) {
		}
		String input = string;
		char[] inputCharArry = input.toCharArray();
		for (int i = 0; i < input.length(); i++) {
			if (i < begin || i >= (input.length()-end)) {
				inputCharArry[i] = '*';
			}
		}
		return String.valueOf(inputCharArry);
	}

	@ApiOperation(
			value = "保留自x至y",
			notes = "保留自x至y",
			httpMethod = "POST",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	@RequestMapping(value = "/insubstr", method = RequestMethod.POST)
	public List<Map<String, Object>> insubstr(){
		String sql = "select insubstr(name, 1, 3) from mysql.test01.student";
		return jt.queryForList(sql);

	}

	public static String insubstr(String string, long begin, long end) {
		if (begin > end) {
		}
		String input = string;
		char[] inputCharArry = input.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < input.length(); i++) {
			if (i >= (begin-1) && i <= (end-1)) {
				sb.append(inputCharArry[i]);
			} else {
				sb.append("*");
			}
		}
		return sb.toString();
	}

	@ApiOperation(
			value = "保留前n后m",
			notes = "保留前n后m",
			httpMethod = "POST",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	@RequestMapping(value = "/exsubstr", method = RequestMethod.POST)
	public List<Map<String, Object>> exsubstr(){
		String sql = "select exsubstr(name, 1, 3) from mysql.test01.student";
		return jt.queryForList(sql);

	}

	public static String exsubstr(String string, long begin,  long end) {
		if (begin > end) {
		}
		String input = string;
		char[] inputCharArry = input.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < input.length(); i++) {
			if (i < begin || i >= (input.length()-end)) {
				sb.append(inputCharArry[i]);
			} else {
				sb.append("*");
			}
		}
		return sb.toString();
	}

	@ApiOperation(
			value = "电话号码掩码",
			notes = "电话号码掩码",
			httpMethod = "POST",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	@RequestMapping(value = "/testMaskphone", method = RequestMethod.POST)
	public List<Map<String, Object>> testMaskphone(){
		String sql = "select maskphone(name) from mysql.test01.student";
		return jt.queryForList(sql);

	}

	@ApiOperation(
			value = "mac地址掩码",
			notes = "mac地址掩码",
			httpMethod = "POST",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	@RequestMapping(value = "/testmac", method = RequestMethod.POST)
	public List<Map<String, Object>> maskmac(){
		String sql = "select maskmac('00-2B-67-90-51-90') from mysql.test01.student";
		return jt.queryForList(sql);

	}

	@ApiOperation(
			value = "邮箱地址掩码",
			notes = "邮箱地址掩码",
			httpMethod = "POST",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	@RequestMapping(value = "/testMaskemail", method = RequestMethod.POST)
	public List<Map<String, Object>> testMaskemail(){
		String sql = "select maskemail('yangjianlei@163.com') from mysql.test01.student";
		return jt.queryForList(sql);

	}

	@ApiOperation(
			value = "ipv4掩码",
			notes = "ipv4掩码",
			httpMethod = "POST",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	@RequestMapping(value = "/testMaskipv4", method = RequestMethod.POST)
	public List<Map<String, Object>> testMaskipv4(){
		String sql = "select maskipv4('192.168.1.1') from mysql.test01.student";
		return jt.queryForList(sql);

	}

	@ApiOperation(
			value = "ipv6掩码",
			notes = "ipv6掩码",
			httpMethod = "POST",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	@RequestMapping(value = "/testMaskipv6", method = RequestMethod.POST)
	public List<Map<String, Object>> testMaskipv6(){
		String sql = "select maskipv6('fe80::45:a129:f30f:ce16%9') from mysql.test01.student";
		return jt.queryForList(sql);

	}

	@ApiOperation(
			value = "身份证号掩码",
			notes = "身份证号掩码",
			httpMethod = "POST",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	@RequestMapping(value = "/testMaskidcard", method = RequestMethod.POST)
	public List<Map<String, Object>> testMaskidcard(){
		String sql = "select maskidcard('130429199304290313') from mysql.test01.student";
		return jt.queryForList(sql);

	}

	@ApiOperation(
			value = "银行卡掩码",
			notes = "银行卡掩码",
			httpMethod = "POST",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	@RequestMapping(value = "/testMaskbankcard", method = RequestMethod.POST)
	public List<Map<String, Object>> testMaskbankcard(){
		String sql = "select maskbankcard('123456789012345678') from mysql.test01.student";
		return jt.queryForList(sql);

	}

	@ApiOperation(
			value = "md5加密",
			notes = "md5加密",
			httpMethod = "POST",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	@RequestMapping(value = "/testMd5hash", method = RequestMethod.POST)
	public List<Map<String, Object>> testMd5hash(){
		String sql = "select md5hash('20200101', '123123') from mysql.test01.student";
		return jt.queryForList(sql);

	}

	@ApiOperation(
			value = "sha1加密",
			notes = "sha1加密",
			httpMethod = "POST",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	@RequestMapping(value = "/testSha1hash", method = RequestMethod.POST)
	public List<Map<String, Object>> testSha1hash(){
		String sql = "select sha1hash('20200101', '123123') from mysql.test01.student";
		return jt.queryForList(sql);

	}

	@ApiOperation(
			value = "sha256加密",
			notes = "sha256加密",
			httpMethod = "POST",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	@RequestMapping(value = "/testSha256hash", method = RequestMethod.POST)
	public List<Map<String, Object>> testSha256hash(){
		String sql = "select sha256hash('20200101', '123123') from mysql.test01.student";
		return jt.queryForList(sql);

	}

	@ApiOperation(
			value = "hmacsha256加密",
			notes = "hmacsha256加密",
			httpMethod = "POST",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	@RequestMapping(value = "/testHmacsha256hash", method = RequestMethod.POST)
	public List<Map<String, Object>> testHmacsha256hash(){
		String sql = "select hmacsha256hash('20200101', '123123', '123') from mysql.test01.student";
		return jt.queryForList(sql);

	}

	@ApiOperation(
			value = "数值类型截断",
			notes = "数值类型截断",
			httpMethod = "POST",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	@RequestMapping(value = "/testSubnumber", method = RequestMethod.POST)
	public List<Map<String, Object>> testSubnumber(){
		String sql = "select subnumber(20200101, 3) from mysql.test01.student";
		return jt.queryForList(sql);

	}

	@ApiOperation(
			value = "获取year",
			notes = "获取year",
			httpMethod = "POST",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	@RequestMapping(value = "/testYear", method = RequestMethod.POST)
	public List<Map<String, Object>> testYear(){
		String sql = "select minute(create_time) from mysql.test01.t_file_log";
		return jt.queryForList(sql);

	}

	public static void main(String[] args) {
		System.out.println(maskinstr("zhangsan", 1,3));
		System.out.println(maskexstr("zhangsan", 1,3));
		System.out.println(insubstr("zhangsan", 1,3));
		System.out.println(exsubstr("ad12min", 1,3));

		System.out.println("abcd".indexOf("A"));
	}
}