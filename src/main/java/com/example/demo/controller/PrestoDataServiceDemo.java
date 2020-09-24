package com.example.demo.controller;

import com.example.demo.util.NamedParameterStatement;
import com.example.demo.util.RestfulUtils;
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

@Api(value = "DataServiceQuery", tags = "查询服务Presto测试")
@RequestMapping("/prestoDataService")
@RestController
public class PrestoDataServiceDemo {
	@Autowired
	@Qualifier("prestoTemplate")
	JdbcTemplate jt ;

	@ApiOperation(
			value = "查询服务sql测试",
			notes = "查询服务sql测试",
			httpMethod = "POST",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	@RequestMapping(value = "/findData", method = RequestMethod.POST)
	public List<String> findData(){
		// SELECT * FROM (SELECT ROW_NUMBER() over(ORDER BY T.id) as Row,T.* FROM table as T) TT
		// WHERE TT.Row BETWEEN 1 AND 10;
		// String sql = "select * from (select row_number() over(order by mysql.test01.student.id) as row, student.age, student.name from mysql.test01.student) TT" +
		// 		" where TT.row between 1 and 2";
		// String sql = "select substr(name, 0, 3) from mysql.test01.student";
		String sql = "SELECT WR_TEST1.C138 C138, WR_TEST1.LASTNAME LASTNAME, WR_TEST1.FIRSTNAME FIRSTNAME, WR_TEST1.ADDRESS ADDRESS, WR_TEST1.C142 C142, WR_TEST1.C143 C143, WR_TEST1.C144 C144, WR_TEST1.MAC MAC, WR_TEST1.IP4 IP4, WR_TEST1.IP6 IP6, WR_TEST1.ID_CARD ID_CARD, WR_TEST1.BANK_CARD BANK_CARD, WR_TEST1.MD_5 MD_5, WR_TEST1.MD5 MD5, WR_TEST1.SHA1 SHA1, WR_TEST1.SHA256 SHA256, WR_TEST1.HMACSHA256 HMACSHA256, WR_TEST1.NUMBER NUMBER, YJL_VIEWTEST_1.dept_id DEPT_ID, YJL_VIEWTEST_1.name NAME, YJL_VIEWTEST_1.sort SORT, YJL_VIEWTEST_1.create_time CREATE_TIME, YJL_VIEWTEST_1.update_time UPDATE_TIME, YJL_VIEWTEST_1.del_flag DEL_FLAG, YJL_VIEWTEST_1.parent_id PARENT_ID, YJL_VIEWTEST_1.user_id USER_ID, YJL_VIEWTEST_1.username USERNAME, YJL_VIEWTEST_1.password PASSWORD, YJL_VIEWTEST_1.salt SALT, YJL_VIEWTEST_1.phone PHONE, YJL_VIEWTEST_1.avatar AVATAR, YJL_VIEWTEST_1.lock_flag LOCK_FLAG, YJL_VIEWTEST_1.login_name LOGIN_NAME FROM (SELECT test_security.Id C138, test_security.LastName LASTNAME, test_security.FirstName FIRSTNAME, test_security.Address ADDRESS, test_security.City C142, test_security.Phone C143, test_security.Email C144, test_security.Mac MAC, test_security.Ip4 IP4, test_security.Ip6 IP6, test_security.id_card ID_CARD, test_security.bank_card BANK_CARD, test_security.MD_5 MD_5, test_security.MD5 MD5, test_security.SHA1 SHA1, test_security.SHA256 SHA256, test_security.HMACSHA256 HMACSHA256, test_security.number NUMBER FROM mysql206.zcy_test.test_security) WR_TEST1 INNER JOIN ( SELECT sys_user.user_id user_id, sys_user.username username, sys_user.password password, sys_user.salt salt, sys_user.phone phone, sys_user.avatar avatar, sys_user.lock_flag lock_flag, sys_user.login_name login_name, sys_dept.dept_id dept_id, sys_dept.name name, sys_dept.sort sort, sys_dept.create_time create_time, sys_dept.update_time update_time, sys_dept.del_flag del_flag, sys_dept.parent_id parent_id FROM mysql206.zcy_test.sys_dept INNER JOIN mysql206.zcy_test.sys_user ON sys_dept.dept_id=sys_user.dept_id ) YJL_VIEWTEST_1 ON WR_TEST1.C138=YJL_VIEWTEST_1.dept_id WHERE 1=1 AND WR_TEST1.C138 = 1 AND WR_TEST1.LASTNAME = '123'";
		// String sql = "select * from mysql206.zcy_test.sys_user where user_id = 111111";
		List<String> columnames = new LinkedList<>();
		Map<String, String> columnAliasMap = new LinkedHashMap<>();
		List<Map<String, Object>>  rows = jt.query(sql, new Object[]{}, new ResultSetExtractor<List<Map<String, Object>>>() {
			@Override
			public List<Map<String, Object>> extractData(ResultSet rs) throws SQLException, DataAccessException {
				List<Map<String, Object>> rsList = new ArrayList<>();
				int currentRow = 0;
				int columnCount = rs.getMetaData().getColumnCount();
				for (int i = 1; i <= columnCount; i++) {
					columnAliasMap.put(rs.getMetaData().getColumnName(i), rs.getMetaData().getColumnLabel(i));
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
		return columnames;
	}

	@ApiOperation(
			value = "查询sql字段列名和别名对应关系",
			notes = "查询sql字段列名和别名对应关系",
			httpMethod = "POST",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	@RequestMapping(value = "/findColumnAliasMap", method = RequestMethod.POST)
	public Map<String, String> findColumnAliasMap(){
		// SELECT * FROM (SELECT ROW_NUMBER() over(ORDER BY T.id) as Row,T.* FROM table as T) TT
		// WHERE TT.Row BETWEEN 1 AND 10;
		// String sql = "select * from (select row_number() over(order by mysql.test01.student.id) as row, student.age, student.name from mysql.test01.student) TT" +
		// 		" where TT.row between 1 and 2";
		// String sql = "select substr(name, 0, 3) from mysql.test01.student";
		// String sql = "select * from mysql206.zcy_test.sys_user where user_id = 111111";
		String sql = "select id a, dept_name b, dept_desc as c from mysql206.zcy_test.dept";
		List<String> columnames = new LinkedList<>();
		Map<String, String> columnAliasMap = new LinkedHashMap<>();
		List<Map<String, Object>>  rows = jt.query(sql, new Object[]{}, new ResultSetExtractor<List<Map<String, Object>>>() {
			@Override
			public List<Map<String, Object>> extractData(ResultSet rs) throws SQLException, DataAccessException {
				List<Map<String, Object>> rsList = new ArrayList<>();
				int currentRow = 0;
				int columnCount = rs.getMetaData().getColumnCount();
				for (int i = 1; i <= columnCount; i++) {
					columnAliasMap.put(rs.getMetaData().getColumnName(i), rs.getMetaData().getColumnLabel(i));
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
		return columnAliasMap;
	}
}