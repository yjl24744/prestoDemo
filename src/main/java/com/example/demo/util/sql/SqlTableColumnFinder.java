package com.example.demo.util.sql;

import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 表字段解析
 * 
 * @author LT
 * @date 2020年4月30日
 */
public class SqlTableColumnFinder extends TablesNamesFinder {
	// private CowLogger log =
	// CowLoggerFactory.getLogger(TablesCipherEncoder.class);
	private List<String> tableColumnNames = new ArrayList<String>();
	private Map<String, String> tableNameAliasMapping = new LinkedHashMap<String, String>();
	private Map<String, String> columnAliasMapping = new LinkedHashMap<String, String>();
	// private String currentTableName;
	private boolean isContainsAllColumns = false;

	public SqlTableColumnFinder() {

	}

	public void parse(Statement statement) {
		init(false);
		statement.accept(this);
	}

	public List<String> getTableColumnNames() {
		return tableColumnNames;
	}

	public Set<String> getTableNames() {
		return tableNameAliasMapping.keySet();
	}

	public String getTableAlias(String tableName) {
		return tableNameAliasMapping.get(tableName);
	}

	public String getColumnAlias(String columnName) {
		return columnAliasMapping.get(columnName.toUpperCase());
	}

	public boolean isContainsAllColumns() { return isContainsAllColumns; }

	public Map<String, String> getColumnAliasMapping() { return this.columnAliasMapping; }

	@Override
	public void visit(ExpressionList expressionList) {
		super.visit(expressionList);
	}

	@Override
	public void visit(Table tableName) {
		String name = tableName.getName();
		String alias = tableName.getAlias() == null ? null : tableName.getAlias().getName();
		tableNameAliasMapping.put(name, alias);
		// currentTableName = name;
		super.visit(tableName);
	}

	@Override
	public void visit(SelectExpressionItem item) {
		Expression expression = item.getExpression();
		if (item instanceof SelectExpressionItem && expression instanceof Column) {
			Alias alias = item.getAlias();
			String aliasName = alias == null ? null : alias.getName();
			Column column = (Column) expression;
			tableColumnNames.add(column.getFullyQualifiedName());
			if (StringUtils.isNotBlank(aliasName)) {
				columnAliasMapping.put(column.getFullyQualifiedName().toUpperCase(), aliasName);
			}
		}
		item.getExpression().accept(this);
	}

	@Override
	public void visit(PlainSelect plainSelect) {
		if (plainSelect.getFromItem() != null) {
			plainSelect.getFromItem().accept(this);
		}
		if (plainSelect.getJoins() != null) {
			for (Join join : plainSelect.getJoins()) {
				join.getRightItem().accept(this);
			}
		}
		if (plainSelect.getSelectItems() != null) {
			for (SelectItem item : plainSelect.getSelectItems()) {
				item.accept(this);
			}
		}
	}

	@Override
	public void visit(Column tableColumn) {
		super.visit(tableColumn);
	}

	@Override
	public void visit(EqualsTo equalsTo) {
		visitBinaryExpression(equalsTo);
	}

	@Override
	public void visit(Select select) {
		if (select.getWithItemsList() != null) {
			for (WithItem withItem : select.getWithItemsList()) {
				withItem.accept(this);
			}
		}
		select.getSelectBody().accept(this);
	}

	@Override
	public void visit(AllColumns allColumns) {
		if (allColumns != null) {
			isContainsAllColumns = true;
		} else {
			isContainsAllColumns = false;
		}
	}
}