package com.example.demo.controller;

import com.alibaba.druid.pool.DruidDataSource;
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
import javax.sound.midi.Soundbank;
import javax.swing.*;
import javax.ws.rs.QueryParam;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Api(value = "PrestoDataQuery", tags = "Presto数据库查询")
@RequestMapping("/presto")
@RestController
public class PrestoDemo {
	@Autowired
	@Qualifier("prestoTemplate")
	JdbcTemplate jt ;

	/**
	 * presto创建映射表
	 * @return
	 */
	@ApiOperation(
			value = "presto创建映射表",
			notes = "presto创建映射表",
			httpMethod = "POST",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public Object create(){
		jt.execute("create table mysql.test01.test(rowkey varchar ,name varchar,sex varchar) with (data_cf='info')");
		return "sucess!";

	}

	@RequestMapping(value = "/createView", method = RequestMethod.POST)
	public Object createView(){
		jt.execute("CREATE VIEW mysql.test01.testview AS " +
				"SELECT id, name " +
				"FROM mysql.test01.student");
		return "sucess!";

	}

	/**
	 * 查询hbase中的表数据
	 * @return
	 */
	@ApiOperation(
			value = "查询hbase中的表数据",
			notes = "查询hbase中的表数据",
			httpMethod = "POST",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	@RequestMapping(value = "/findHbase", method = RequestMethod.POST)
	public List<Map<String,Object>> findHbaseList(){
		return jt.queryForList("select * from hbase.default.test");
	}

	/**
	 * 查询mysql中的表数据
	 * @return
	 */
	@ApiOperation(
			value = "查询mysql中的表数据",
			notes = "查询mysql中的表数据",
			httpMethod = "POST",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	@RequestMapping(value = "/findMysql", method = RequestMethod.POST)
	public List<Map<String,Object>> findMysqlList() {
		// String sql = "select student.age, teacher.name from mysql.test01.student inner join mysql.test01.teacher on mysql.test01.student.id=mysql.test01.teacher.id";
		String sql = "SELECT sys_dict.system system, sys_dict_item.id id, sys_dict_item.dict_id dict_id, sys_dict_item.value value, sys_dict_item.label label, sys_dict_item.type type, sys_dict_item.description description, sys_dict_item.sort sort, sys_dict_item.create_time create_time, sys_dict_item.update_time update_time, sys_dict_item.remarks remarks, sys_dict_item.del_flag del_flag, sys_user.user_id user_id, sys_user.username username, sys_user.password password, sys_user.salt salt, sys_user.phone phone, sys_user.avatar avatar, sys_user.dept_id dept_id, sys_user.lock_flag lock_flag, sys_user.login_name login_name FROM mysql.zcy_test.sys_dict INNER JOIN mysql.zcy_test.sys_dict_item ON sys_dict.id=sys_dict_item.id INNER JOIN mysql.zcy_test.sys_user ON sys_dict_item.create_time=sys_user.create_time";
		// return jt.queryForList(sql);
		List<String> columns = new ArrayList<>();
		List<String> columnames = new ArrayList<>();
		List<Map<String, Object>> columnTypes = new ArrayList<>();
		List<Map<String, Object>> rows = jt.query(sql, new Object[]{}, new ResultSetExtractor<List<Map<String, Object>>>() {
			@Override
			public List<Map<String, Object>> extractData(ResultSet rs) throws SQLException, DataAccessException {
				List<Map<String, Object>> rsList = new ArrayList<>();
				int currentRow = 0;
				int columnCount = rs.getMetaData().getColumnCount();
				Map<String, Object> columnTypeMap = new LinkedHashMap<>();
				for (int i = 1; i <= columnCount; i++) {
					columnames.add(rs.getMetaData().getColumnName(i));
				}
				columnTypes.add(columnTypeMap);
				while (rs.next() && (currentRow < 1)) {
					if (currentRow >= 0) {
						Map<String, Object> tempRow = new HashMap<String, Object>();
						for (int i = 1; i <= columnCount; i++) {
							tempRow.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
						}
						rsList.add(tempRow);
					}
					currentRow = currentRow + 1;
				}
				return rsList;
			}
		});
		return rows;
	}

	@ApiOperation(
			value = "分页查询mysql中的表数据",
			notes = "分页查询mysql中的表数据",
			httpMethod = "POST",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	@RequestMapping(value = "/findMysqlByPage", method = RequestMethod.POST)
	public List<Map<String,Object>> findMysqlListByPage(){
		String sql = "select student.age, teacher.name from mysql.test01.student inner join mysql.test01.teacher" +
				" on mysql.test01.student.id=mysql.test01.teacher.id";
		return jt.query(sql, new ResultSetExtractor<List<Map<String, Object>>>() {
			@Override
			public List<Map<String, Object>> extractData(ResultSet rs) throws SQLException, DataAccessException {
				List<Map<String, Object>> rsList = new ArrayList<>();
				int currentRow = 0;
				int columnCount = rs.getMetaData().getColumnCount();
				while (rs.next() && (currentRow < 1)) {
					if (currentRow >= 0) {
						Map<String, Object> tempRow = new HashMap<String, Object>();
						for (int i = 1; i <= columnCount; i++) {
							tempRow.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
						}
						rsList.add(tempRow);
					}
					currentRow = currentRow + 1;
				}
				return rsList;
			}
		});
	}

	@ApiOperation(
			value = "数据库底层分页查询mysql中的表数据",
			notes = "数据库底层分页查询mysql中的表数据",
			httpMethod = "POST",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	@RequestMapping(value = "/findMysqlByDbPage", method = RequestMethod.POST)
	public List<Map<String,Object>> findMysqlListByDbPage(){
		// SELECT * FROM (SELECT ROW_NUMBER() over(ORDER BY T.id) as Row,T.* FROM table as T) TT
		// WHERE TT.Row BETWEEN 1 AND 10;
		// String sql = "select * from (select row_number() over(order by mysql.test01.student.id) as row, student.age, student.name from mysql.test01.student) TT" +
		// 		" where TT.row between 1 and 2";
		String sql = "select substr(name, 0, 3) from mysql.test01.student";
		return jt.queryForList(sql);
	}

	/**
	 * JOIN连接查询  mysql和hbase
	 * @return
	 */
	@ApiOperation(
			value = "JOIN连接查询",
			notes = "JOIN连接查询 mysql和hbase",
			httpMethod = "POST",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	@RequestMapping(value = "/findJoin", method = RequestMethod.POST)
	public List<Map<String,Object>> findJoin(){
		return jt.queryForList("SELECT TK_ALLSEC.SECURITY_ID SECURITY_ID, TK_ALLSEC.SYMBOL_ID SYMBOL_ID, TK_ALLSEC.SYMBOL_CODE SYMBOL_CODE, TS_MK_SEC_QUOTATION.ID ID, TS_MK_SEC_QUOTATION.BIZ_DATE BIZ_DATE, TS_MK_SEC_QUOTATION.SYMBOL_ID C6 FROM oracle.NEWRISK.TK_ALLSEC INNER JOIN oracle.NEWRISK.TS_MK_SEC_QUOTATION ON TK_ALLSEC.ID=TS_MK_SEC_QUOTATION.ID");
	}

	@ApiOperation(
			value = "查询hbase中的表数据",
			notes = "查询hbase中的表数据",
			httpMethod = "POST",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	@RequestMapping(value = "/findHbase-yield_w_tab1", method = RequestMethod.POST)
	public List<Map<String,Object>> findHbase_yield_w_tab1(){
		return jt.queryForList("select * from hbase.default.yield_w_tab1 limit 200");
	}

	@ApiOperation(
			value = "查询Oracle中的表数据",
			notes = "查询Oracle中的表数据",
			httpMethod = "POST",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	@RequestMapping(value = "/findOracleData", method = RequestMethod.POST)
	public List<Map<String,Object>> findOracleData(){
		return jt.queryForList("SELECT TK_ALLSEC.SECURITY_ID SECURITY_ID, TK_ALLSEC.SYMBOL_ID SYMBOL_ID, TK_ALLSEC.SYMBOL_CODE SYMBOL_CODE, TS_MK_SEC_QUOTATION.ID ID, TS_MK_SEC_QUOTATION.BIZ_DATE BIZ_DATE, TS_MK_SEC_QUOTATION.SYMBOL_ID C6 FROM TK_ALLSEC INNER JOIN TS_MK_SEC_QUOTATION ON TK_ALLSEC.ID=TS_MK_SEC_QUOTATION.IDSELECT TK_ALLSEC.SECURITY_ID SECURITY_ID, TK_ALLSEC.SYMBOL_ID SYMBOL_ID, TK_ALLSEC.SYMBOL_CODE SYMBOL_CODE, TS_MK_SEC_QUOTATION.ID ID, TS_MK_SEC_QUOTATION.BIZ_DATE BIZ_DATE, TS_MK_SEC_QUOTATION.SYMBOL_ID C6 FROM oracle11.orcl.TK_ALLSEC INNER JOIN oracle11.orcl.TS_MK_SEC_QUOTATION ON TK_ALLSEC.ID=TS_MK_SEC_QUOTATION.ID");
	}

	@ApiOperation(
			value = "预编译查询mysql中的表数据",
			notes = "预编译查询mysql中的表数据",
			httpMethod = "POST",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	@RequestMapping(value = "/findMysqlByPrepare", method = RequestMethod.POST)
	public List<Map<String,Object>> findMysqlByPrepare(){
		Map<String, List<Integer>> nameIndexMap = new HashMap<>();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("name", "zhangsan");
		paramMap.put("id", new Integer(1));
		String sql = "select student.age, teacher.name from mysql.test01.student inner join mysql.test01.teacher" +
				" on mysql.test01.student.id=mysql.test01.teacher.id where mysql.test01.student.id = :id and mysql.test01.student.name=:name";
		String parseSql = NamedParameterStatement.parseNamedSqlForQuote(sql, nameIndexMap, paramMap);
		System.out.println(parseSql);
		for (Map.Entry<String, List<Integer>> entry : nameIndexMap.entrySet()) {
			System.out.println("key is " + entry.getKey());
			System.out.println("value is " + entry.getValue());
		}
		int count = 0;
		for (String name : nameIndexMap.keySet()) {
			List<Integer> indexList = nameIndexMap.get(name);
			for (Integer i : indexList) {
				count++;
			}
		}
		Object[] objects = new Object[count];
		for (String name : nameIndexMap.keySet()) {
			Object objValue = paramMap.get(name);
			List<Integer> indexList = nameIndexMap.get(name);
			for (Integer i : indexList) {
				objects[i-1] = objValue;
			}
		}

		RowCountCallbackHandler rcch = new RowCountCallbackHandler();
		List<Map<String,Object>> data = jt.queryForList(parseSql, objects, rcch);
		String[] coloumnName = rcch.getColumnNames();

		return data;
	}

	@ApiOperation(
			value = "查询hive中的表数据",
			notes = "数据库底层分页查询hive中的表数据",
			httpMethod = "POST",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	@RequestMapping(value = "/findHiveData", method = RequestMethod.POST)
	public List<Map<String,Object>> findHiveData(){
		// SELECT * FROM (SELECT ROW_NUMBER() over(ORDER BY T.id) as Row,T.* FROM table as T) TT
		// WHERE TT.Row BETWEEN 1 AND 10;
		String sql = "select * from hive.test.emp";
		// String sql = "SELECT related_sequ related_sequ, tx_tp_cd tx_tp_cd, unde_sec_cd unde_sec_cd FROM hive.dwd.t_dwd_ev_it_colm_a";
		// String sql = "select * from hive.ods.ods_wind_windcustomcode_a limit 1";
		return jt.queryForList(sql);
	}

	@ApiOperation(
			value = "查询hive中的表数据",
			notes = "数据库底层分页查询hive中的表数据",
			httpMethod = "POST",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	@RequestMapping(value = "/findDynamiticHiveData", method = RequestMethod.POST)
	public List<Map<String, Object>> queryDataAndType() {
		// this.createDatasourProperties(ds);
		String sql = "SELECT sys_dict.id ID, sys_dict.type TYPE, sys_dict.description DESCRIPTION, sys_dict.create_time CREATE_TIME, sys_dict.update_time UPDATE_TIME, sys_dict.remarks REMARKS, sys_dict.del_flag DEL_FLAG, sys_dept_relation.ancestor ANCESTOR, sys_dept_relation.descendant DESCENDANT FROM mysql206.zcy_test.sys_dict INNER JOIN mysql206.zcy_test.sys_dept_relation ON sys_dict.remarks=sys_dept_relation.ancestor";
		// String sql = "select "
		List<String> list = new ArrayList<>();
		list.add("mysql206");
		list.add("jdbc:mysql://192.168.100.132:3306");
		this.createDynamicCatalog(list);
		return queryDataModel(sql);
	}


	private void createDynamicCatalog(List<String> list) {
		String prestoCatalogUrl = "http://192.168.100.154:9092/v1/catalog";
		String catalogInfo = "{ \"catalogName\": \"" + list.get(0)
				+ "\", \"connectorName\": \"mysql"
				+ "\", \"properties\": { \"connection-url\":\""
				+ ""
				+ "\", \"connection-user\":\"root"
				+ "\", \"connection-password\":\"123456\" } }";

		// String json = RestfulUtils.get("http://localhotst:8080/v1/catalog/test", String.class);
		// RestfulUtils.putForEntity("http://127.0.0.1:8080/v1/catalog/test", String.class, null, );
		RestfulUtils.put(prestoCatalogUrl, catalogInfo);
	}

	private List<Map<String, Object>> queryDataModel(String parseSql) {
		List<String> columnames = new ArrayList<>();
		List<Map<String, Object>> columnTypes = new ArrayList<>();
		List<Map<String, Object>>  rows = jt.query(parseSql, new Object[]{}, new ResultSetExtractor<List<Map<String, Object>>>() {
			@Override
			public List<Map<String, Object>> extractData(ResultSet rs) throws SQLException, DataAccessException {
				List<Map<String, Object>> rsList = new ArrayList<>();
				int currentRow = 0;
				int columnCount = rs.getMetaData().getColumnCount();
				for (int i = 1; i <= columnCount; i++) {
					columnames.add(rs.getMetaData().getColumnName(i));
				}
				while (rs.next() && (currentRow < 1)) {
					if (currentRow >= 0) {
						Map<String, Object> tempRow = new HashMap<String, Object>();
						for (int i = 1; i <= columnCount; i++) {
							tempRow.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
						}
						rsList.add(tempRow);
					}
					currentRow = currentRow + 1;
				}
				return rsList;
			}
		});
		return rows;
	}

	public static String md5hash(String string, String salt) {
		String input = string+ salt;
		if (string == null || "".equals(string)) {
		}
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		md.update(input.getBytes());
		StringBuffer buf = new StringBuffer();
		byte[] bits = md.digest();
		for(int i=0;i<bits.length;i++){
			int a = bits[i];
			if(a<0) a+=256;
			if(a<16) buf.append("0");
			buf.append(Integer.toHexString(a));
		}
		return buf.toString();
	}

	public static String sha256hash(String string, String salt) {
		String input = string+ salt;
		if (string == null || "".equals(string)) {
		}
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		md.update(input.getBytes());
		StringBuffer buf = new StringBuffer();
		byte[] bits = md.digest();
		for(int i=0;i<bits.length;i++){
			int a = bits[i];
			if(a<0) a+=256;
			if(a<16) buf.append("0");
			buf.append(Integer.toHexString(a));
		}
		return buf.toString();
	}

	public static String sha1hash(String string, String salt) {
		String input = string + salt;
		if (string == null || "".equals(string)) {
		}
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		md.update(input.getBytes());
		StringBuffer buf = new StringBuffer();
		byte[] bits = md.digest();
		for(int i=0;i<bits.length;i++){
			int a = bits[i];
			if(a<0) a+=256;
			if(a<16) buf.append("0");
			buf.append(Integer.toHexString(a));
		}
		return buf.toString();
	}

	public static String hmacsha256hash(String string, String salt,
										String key) {
		String input = string + salt;
		String keystr = key;
		if (string == null || "".equals(string)) {
		}
		StringBuilder sb = new StringBuilder();
		try {
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(keystr.getBytes("UTF-8"), "HmacSHA256");
			sha256_HMAC.init(secret_key);
			byte[] array = sha256_HMAC.doFinal(input.getBytes("UTF-8"));
			for (byte item : array) {
				sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static String getSHA256(String str){
		MessageDigest messageDigest;
		String encodestr = "";
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(str.getBytes("UTF-8"));
			encodestr = byte2Hex(messageDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encodestr;
	}
	/**
	 * 将byte转为16进制
	 * @param bytes
	 * @return
	 */
	private static String byte2Hex(byte[] bytes){
		StringBuffer stringBuffer = new StringBuffer();
		String temp = null;
		for (int i=0;i<bytes.length;i++){
			temp = Integer.toHexString(bytes[i] & 0xFF);
			if (temp.length()==1){
				//1得到一位的进行补0操作
				stringBuffer.append("0");
			}
			stringBuffer.append(temp);
		}
		return stringBuffer.toString();
	}

	public static void main(String[] args) {
		// String sql = "select * from a where to_date(a, '') in (:a,:a,:a) and b in (:b,${c},${d})";
		// Map<String, List<Integer>> map = new LinkedHashMap<>();
		// Map<String, Object> param = new LinkedHashMap<>();
		// List<String> list = new ArrayList();
		// list.add("aaa1");
		// list.add("aaa2");
		// list.add("aaa3");
		// param.put("a", list);
		// param.put("b", "bbb");
		// param.put("c", "ccc");
		// param.put("d", "ddd");
		// System.out.println(NamedParameterStatement.parseNamedSqlForQuote(sql, map, param));
		// System.out.println("111");
		// 对email@符号前的长度进行判断如果少于2个字符则保留一位明文
		String content = "yangjianlei@163.com";
		String emailPattern = "\\S+@\\S+\\.\\S+";
		String pattern = ""; // 正则表达式
		// 构造Pattern对象
		char[] chars2 = content.toCharArray();
		for (int i = 1; i < content.length(); i++) {
			if (i < content.indexOf("@")
					|| (i > content.indexOf("@") && i < content.indexOf("."))) {
				chars2[i] = '*';
			}
		}
		System.out.println(String.valueOf(chars2));

		String idcardStr = "130429199304290313";
		String idcardPattern = "^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";
		System.out.println(idcardStr.matches(idcardPattern));
		pattern = "(\\S{8})\\S+(\\S{2})";
		System.out.println(idcardStr.replaceAll(pattern, "$1****$2"));

		String input = "12345671123456";
		// 判断ip地址是否与正则表达式匹配
		char[] chars = input.toCharArray();
		for (int i = 6; i < input.length(); i++) {
			if (i < input.length() - 4) {
				chars[i] = '*';
			}
		}
		System.out.println(String.valueOf(chars));

		String mac = "BE-54-2F-D8-DF-29";
		String mac2 = "BE:54:2F:D8:DF:29";
		String regex = "^[A-F0-9]{2}([-:]?[A-F0-9]{2})([-:.]?[A-F0-9]{2})([-:]?[A-F0-9]{2})([-:.]?[A-F0-9]{2})([-:]?[A-F0-9]{2})$";
		String regex2 = "^([A-F0-9]{2})([-:]?[A-F0-9]{2})([-:.]?[A-F0-9]{2})([-:]?)([A-F0-9]{2})([-:.]?)([A-F0-9]{2})([-:]?)([A-F0-9]{2})$";
		System.out.println(mac2.matches(regex));
		System.out.println(mac.replaceAll(regex2, "$1$2$3$4$5$6**$8**"));

		PrestoDemo pd = new PrestoDemo();
		System.out.println(pd.md5hash("20200101", "123123"));
		System.out.println(pd.sha1hash("20200101", "123123"));
		System.out.println(pd.sha256hash("20200101", "123123"));
		System.out.println(pd.hmacsha256hash("20200101", "123123", "123123"));

		int j = 12345;
		int k = 2;
		String s = "1";
		for (int i = 1; i < k; i ++) {
			s += "0";
		}
		System.out.println(j-(j%Integer.valueOf(s)));
	}
}