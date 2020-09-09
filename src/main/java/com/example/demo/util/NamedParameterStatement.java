package com.example.demo.util;

import com.example.demo.config.IaeCacheManager;
import org.springframework.cache.Cache;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Date;
import java.sql.*;
import java.util.*;

/**
 * SQL业务参数处理
 * 
 * @author LT 2019年11月19日
 */
public class NamedParameterStatement extends PreparedStatementWrapper {
	private final String parsedSql;
	private final Map<String, List<Integer>> nameIndexMap;

	private Object getCache(String key, Class<?> cls) {
		Cache cache = IaeCacheManager.getDefaultCache();
		Object cacheObject = null;
		if (cache != null) {
			cacheObject = cache.get(key, cls);
		}
		return cacheObject;
	}

	private void setCache(String key, Object obj) {
		Cache cache = IaeCacheManager.getDefaultCache();
		if (cache == null) {
			return;
		}
		cache.putIfAbsent(key, obj);
	}

	/**
	 * Creates a NamedParameterStatement. Wraps a call to
	 * c.{@link Connection#prepareStatement(String) prepareStatement}.
	 *
	 * @param conn the database connection
	 * @param sql the parameterized sql
	 * @throws SQLException if the statement could not be created
	 */
	@SuppressWarnings("unchecked")
	public NamedParameterStatement(Connection conn, String sql) throws SQLException {
		Map<String, Object> cacheObject = (Map<String, Object>) getCache(sql, Map.class);
		if (cacheObject != null) {
			nameIndexMap = (Map<String, List<Integer>>) cacheObject.get("nameIndexMap");
			parsedSql = String.valueOf(cacheObject.get("parsedSql"));
		} else {
			nameIndexMap = new HashMap<String, List<Integer>>();
			parsedSql = parseNamedSql(sql, nameIndexMap);
			cacheObject = new HashMap<String, Object>();
			cacheObject.put("nameIndexMap", nameIndexMap);
			cacheObject.put("parsedSql", parsedSql);
			setCache(sql, cacheObject);
		}
		ps = conn.prepareStatement(parsedSql);
	}

	/**
	 * Creates a NamedParameterStatement. Wraps a call to
	 * c.{@link Connection#prepareStatement(String) prepareStatement}.
	 * 
	 * @param conn the database connection
	 * @param sql the parameterized sql
	 * @throws SQLException if the statement could not be created
	 */
	public NamedParameterStatement(Connection conn, String sql, Map<String, Object> paramMap)
			throws SQLException {
		// Map<String, Object> cacheObject = (Map<String, Object>) getCache(sql,
		// Map.class);
		// if (cacheObject != null) {
		// nameIndexMap = (Map<String, List<Integer>>)
		// cacheObject.get("nameIndexMap");
		// parsedSql = String.valueOf(cacheObject.get("parsedSql"));
		// } else {
		nameIndexMap = new HashMap<String, List<Integer>>();
		parsedSql = parseNamedSqlForQuote(sql, nameIndexMap, paramMap);
		// cacheObject = new HashMap<String, Object>();
		// cacheObject.put("nameIndexMap", nameIndexMap);
		// cacheObject.put("parsedSql", parsedSql);
		// setCache(sql, cacheObject);
		// }
		ps = conn.prepareStatement(parsedSql);
	}

	/**
	 * 获取参数名称集合
	 * 
	 * @return 参数名称集合
	 */
	public Set<String> getParameterNames() {
		if (nameIndexMap == null) {
			return null;
		}
		return nameIndexMap.keySet();
	}

	/**
	 * Returns the indexes for a parameter.
	 * 
	 * @param name parameter name
	 * @return parameter indexes
	 * @throws IllegalArgumentException if the parameter does not exist
	 */
	private List<Integer> getIndexes(String name) {
		List<Integer> indexes = nameIndexMap.get(name);
		if (indexes == null) {
			throw new IllegalArgumentException("Parameter not found: " + name);
		}
		return indexes;
	}

	/**
	 * 解析SQL参数，引号中的参数赋值
	 * 
	 * @param sql sql with named parameters
	 * @return the parsed sql
	 */
	public static String parseNamedSqlForQuote(String sql, Map<String, List<Integer>> nameIndexMap,
                                               Map<String, Object> param) {
		if (param == null) {
			param = new HashMap<String, Object>();
		}
		int length = sql.length();
		StringBuffer parsedSql = new StringBuffer(length);
		StringBuffer quoteValue = new StringBuffer(length);
		int index = 1;
		for (int i = 0; i < length; i++) {
			char c = sql.charAt(i);
			if (c == '\'') {
				parsedSql.append(c);
				while (i++ < length && (c = sql.charAt(i)) != '\'') {
					// if (c == ':') {
					// int j = i;
					// char qc = 0;
					// while (j + 1 < length && (qc = sql.charAt(j + 1)) != '\''
					// && qc != '%'
					// && qc != ':' && qc != '$') {
					// quoteValue.append(qc);
					// j++;
					// }
					// if (quoteValue.length() > 0) {
					// String name = quoteValue.toString();
					// Object objValue = param.get(name);
					// String value = objValue == null ? "" :
					// String.valueOf(objValue);
					// parsedSql.append(value);
					// List<Integer> indexList = nameIndexMap.get(name);
					// if (indexList == null) {
					// indexList = new LinkedList<Integer>();
					// nameIndexMap.put(name, indexList);
					// }
					// }
					// quoteValue.delete(0, quoteValue.length());
					// i = j;
					// } else
					if ((c == '$' && sql.charAt(i + 1) == '{')) {
						int j = i + 1;
						char qc;
						while (j++ < length && (qc = sql.charAt(j)) != '\'' && qc != '}'
								&& qc != '%' && qc != ':' && qc != '$') {
							quoteValue.append(qc);
						}
						if (quoteValue.length() > 0) {
							String name = quoteValue.toString();
							Object objValue = param.get(name);
							String value = objValue == null ? "" : String.valueOf(objValue);
							parsedSql.append(value);
							List<Integer> indexList = nameIndexMap.get(name);
							if (indexList == null) {
								indexList = new LinkedList<Integer>();
								nameIndexMap.put(name, indexList);
							}
						}
						quoteValue.delete(0, quoteValue.length());
						i = j;
					} else {
						parsedSql.append(c);
					}
				}
			} else if (c == '"') {
				parsedSql.append(c);
				while (i++ < length && (c = sql.charAt(i)) != '\"') {
					// if (c == ':') {
					// int j = i;
					// char qc = 0;
					// while (j + 1 < length && (qc = sql.charAt(j + 1)) != '\"'
					// && qc != '%'
					// && qc != ':' && qc != '$') {
					// quoteValue.append(qc);
					// j++;
					// }
					// if (quoteValue.length() > 0) {
					// String name = quoteValue.toString();
					// Object objValue = param.get(name);
					// String value = objValue == null ? "" :
					// String.valueOf(objValue);
					// parsedSql.append(value);
					// List<Integer> indexList = nameIndexMap.get(name);
					// if (indexList == null) {
					// indexList = new LinkedList<Integer>();
					// nameIndexMap.put(name, indexList);
					// }
					// }
					// quoteValue.delete(0, quoteValue.length());
					// i = j;
					// } else
					if ((c == '$' && sql.charAt(i + 1) == '{')) {
						int j = i + 1;
						char qc;
						while (j++ < length && (qc = sql.charAt(j)) != '\"' && qc != '}'
								&& qc != '%' && qc != ':' && qc != '$') {
							quoteValue.append(qc);
						}
						if (quoteValue.length() > 0) {
							String name = quoteValue.toString();
							Object objValue = param.get(name);
							String value = objValue == null ? "" : String.valueOf(objValue);
							parsedSql.append(value);
							List<Integer> indexList = nameIndexMap.get(name);
							if (indexList == null) {
								indexList = new LinkedList<Integer>();
								nameIndexMap.put(name, indexList);
							}
						}
						quoteValue.delete(0, quoteValue.length());
						i = j;
					} else {
						parsedSql.append(c);
					}
				}
			} else if (c == '$' && sql.charAt(i + 1) == '{' && i + 2 < length) {
				int j = i + 3;
				while (j < length && sql.charAt(j) != '}') {// Character.isJavaIdentifierPart(sql.charAt(j))
					j++;
				}
				String name = sql.substring(i + 2, j);
				c = '?'; // replace the parameter with a question mark
				i += name.length() + 2; // skip past the end if the
										// parameter
				name = name.trim();
				List<Integer> indexList = nameIndexMap.get(name);
				if (indexList == null) {
					indexList = new LinkedList<Integer>();
					nameIndexMap.put(name, indexList);
				}
				indexList.add(index);
				index++;
			} else if (c == ':' && i + 1 < length
					&& Character.isJavaIdentifierStart(sql.charAt(i + 1))) {
				int j = i + 2;
				while (j < length && Character.isJavaIdentifierPart(sql.charAt(j))) {
					j++;
				}
				String name = sql.substring(i + 1, j);
				c = '?'; // replace the parameter with a question mark
				i += name.length(); // skip past the end if the parameter
				List<Integer> indexList = nameIndexMap.get(name);
				if (indexList == null) {
					indexList = new LinkedList<Integer>();
					nameIndexMap.put(name, indexList);
				}
				indexList.add(index);
				index++;
			}
			parsedSql.append(c);
		}
		return parsedSql.toString();

	}

	/**
	 * Parses a sql with named parameters. The parameter-index mappings are put
	 * into the map, and the parsed sql is returned.
	 * 
	 * @param sql sql with named parameters
	 * @return the parsed sql
	 */
	public static String parseNamedSql(String sql, Map<String, List<Integer>> nameIndexMap) {
		// I was originally using regular expressions, but they didn't work well
		// for ignoring
		// parameter-like strings inside quotes.
		int length = sql.length();
		StringBuffer parsedSql = new StringBuffer(length);
		boolean inSingleQuote = false;
		boolean inDoubleQuote = false;
		int index = 1;
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
					int j = i + 3;
					while (j < length && sql.charAt(j) != '}') {// Character.isJavaIdentifierPart(sql.charAt(j))
						j++;
					}
					String name = sql.substring(i + 2, j);
					c = '?'; // replace the parameter with a question mark
					i += name.length() + 2; // skip past the end if the
											// parameter
					name = name.trim();
					List<Integer> indexList = nameIndexMap.get(name);
					if (indexList == null) {
						indexList = new LinkedList<Integer>();
						nameIndexMap.put(name, indexList);
					}
					indexList.add(index);
					index++;
				} else if (c == ':' && i + 1 < length
						&& Character.isJavaIdentifierStart(sql.charAt(i + 1))) {
					int j = i + 2;
					while (j < length && Character.isJavaIdentifierPart(sql.charAt(j))) {
						j++;
					}
					String name = sql.substring(i + 1, j);
					c = '?'; // replace the parameter with a question mark
					i += name.length(); // skip past the end if the parameter
					List<Integer> indexList = nameIndexMap.get(name);
					if (indexList == null) {
						indexList = new LinkedList<Integer>();
						nameIndexMap.put(name, indexList);
					}
					indexList.add(index);
					index++;
				}
			}
			parsedSql.append(c);
		}
		return parsedSql.toString();
	}

	public void setArray(String name, Array value) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setArray(index, value);
		}
	}

	public void setAsciiStream(String name, InputStream value) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setAsciiStream(index, value);
		}
	}

	public void setAsciiStream(String name, InputStream value, int length) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setAsciiStream(index, value, length);
		}
	}

	public void setBigDecimal(String name, BigDecimal value) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setBigDecimal(index, value);
		}
	}

	public void setBinaryStream(String name, InputStream value) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setBinaryStream(index, value);
		}
	}

	public void setBinaryStream(String name, InputStream value, int length) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setBinaryStream(index, value, length);
		}
	}

	public void setBinaryStream(String name, InputStream value, long length) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setBinaryStream(index, value, length);
		}
	}

	public void setBlob(String name, Blob value) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setBlob(index, value);
		}
	}

	public void setBlob(String name, InputStream value) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setBlob(index, value);
		}
	}

	public void setBlob(String name, InputStream value, long length) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setBlob(index, value, length);
		}
	}

	public void setBoolean(String name, boolean value) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setBoolean(index, value);
		}
	}

	public void setByte(String name, byte value) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setByte(index, value);
		}
	}

	public void setBytes(String name, byte[] value) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setBytes(index, value);
		}
	}

	public void setCharacterStream(String name, Reader value) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setCharacterStream(index, value);
		}
	}

	public void setCharacterStream(String name, Reader value, int length) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setCharacterStream(index, value, length);
		}
	}

	public void setCharacterStream(String name, Reader value, long length) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setCharacterStream(index, value, length);
		}
	}

	public void setClob(String name, Clob value) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setClob(index, value);
		}
	}

	public void setClob(String name, Reader value) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setClob(index, value);
		}
	}

	public void setClob(String name, Reader value, long length) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setClob(index, value, length);
		}
	}

	public void setDate(String name, Date value) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setDate(index, value);
		}
	}

	public void setDate(String name, Date value, Calendar cal) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setDate(index, value, cal);
		}
	}

	public void setDouble(String name, double value) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setDouble(index, value);
		}
	}

	public void setFloat(String name, float value) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setFloat(index, value);
		}
	}

	public void setInt(String name, int value) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setInt(index, value);
		}
	}

	public void setLong(String name, long value) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setLong(index, value);
		}
	}

	public void setNCharacterStream(String name, Reader value) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setNCharacterStream(index, value);
		}
	}

	public void setNCharacterStream(String name, Reader value, long length) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setNCharacterStream(index, value, length);
		}
	}

	public void setNClob(String name, NClob value) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setNClob(index, value);
		}
	}

	public void setNClob(String name, Reader value) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setNClob(index, value);
		}
	}

	public void setNClob(String name, Reader value, long length) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setNClob(index, value, length);
		}
	}

	public void setNString(String name, String value) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setNString(index, value);
		}
	}

	public void setNull(String name, int sqlType) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setNull(index, sqlType);
		}
	}

	public void setObject(String name, Object value) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setObject(index, value);
		}
	}

	public void setObject(String name, Object value, int targetSqlType) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setObject(index, value, targetSqlType);
		}
	}

	public void setObject(String name, Object value, int targetSqlType, int scaleOrLength)
			throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setObject(index, value, targetSqlType, scaleOrLength);
		}
	}

	public void setRef(String name, Ref value) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setRef(index, value);
		}
	}

	public void setRowId(String name, RowId value) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setRowId(index, value);
		}
	}

	public void setShort(String name, short value) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setShort(index, value);
		}
	}

	public void setSQLXML(String name, SQLXML value) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setSQLXML(index, value);
		}
	}

	public void setString(String name, String value) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setString(index, value);
		}
	}

	public void setTime(String name, Time value) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setTime(index, value);
		}
	}

	public void setTime(String name, Time value, Calendar cal) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setTime(index, value, cal);
		}
	}

	public void setTimestamp(String name, Timestamp value) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setTimestamp(index, value);
		}
	}

	public void setTimestamp(String name, Timestamp value, Calendar cal) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setTimestamp(index, value, cal);
		}
	}

	// @SuppressWarnings("deprecation")
	// public void setUnicodeStream(String name, InputStream value, int length)
	// throws SQLException {
	// for (Integer index : getIndexes(name)) {
	// ps.setUnicodeStream(index, value, length);
	// }
	// }

	public void setURL(String name, URL value) throws SQLException {
		for (Integer index : getIndexes(name)) {
			ps.setURL(index, value);
		}
	}

}