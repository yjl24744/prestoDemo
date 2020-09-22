package com.example.demo.util.sql;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.util.List;

public class SqlReplaceAllColumnUtil extends TablesNamesFinder {

    public List<String> columns;

    public SqlReplaceAllColumnUtil(List<String> columns) {
        this.columns = columns;
    }

    public void parse(Statement statement) {
        statement.accept(this);
    }

    @Override
    public void visit(AllTableColumns allTableColumns) {
        // buffer.append(allTableColumns.getTable().getFullyQualifiedName()).append(".*");
    }
}
