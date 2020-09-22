package com.example.demo.util.sql;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;

/**
 * SQL解析处理
 * 
 * @author LT
 * @date 2019年12月30日
 */
public class BISqlParserUtil {
	/**
	 * SQL解析
	 * 
	 * @param sqlSQL
	 * @return SQL表达式
	 * @throws JSQLParserException 异常信息
	 */
	public static Statement parse(String sql) throws JSQLParserException {
		return parse(sql, true);
	}

	/**
	 * SQL解析
	 * 
	 * @param sql SQL
	 * @param match 参数解析
	 * @return SQL表达式
	 * @throws JSQLParserException
	 */
	public static Statement parse(String sql, boolean match) throws JSQLParserException {
		if (match) {
			sql = parseParam(sql);
		}
		return CCJSqlParserUtil.parse(sql);
	}

	/**
	 * 解析参数
	 * 
	 * @param sql SQL
	 * @return SQL
	 */
	private static String parseParam(String sql) {
		StringBuilder parsedSql = new StringBuilder();
		int length = sql.length();
		boolean inSingleQuote = false;
		boolean inDoubleQuote = false;
		for (int i = 0; i < length; i++) {
			char c = sql.charAt(i);
			if (inSingleQuote) {
				if (c == '\'') {
					inSingleQuote = false;
				}
			} else if (inDoubleQuote) {
				if (c == '"') {
					inDoubleQuote = false;
				}
			} else {
				if (c == '\'') {
					inSingleQuote = true;
				} else if (c == '"') {
					inDoubleQuote = true;
				} else if (c == '$' && sql.charAt(i + 1) == '{' && i + 2 < length) {
					parsedSql.append(":");
					int j = i + 2;
					char pc = 0;
					while (j < length && (pc = sql.charAt(j)) != '}') {// Character.isJavaIdentifierPart(sql.charAt(j))
						parsedSql.append(pc);
						j++;
					}
					i = j;
					continue;
				}
			}
			parsedSql.append(c);
		}
		return parsedSql.toString();
	}
}
