package com.example.demo.jsqlparser;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import com.example.demo.util.sql.BISqlParserUtil;
import com.example.demo.util.sql.SqlTableColumnFinder;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;

import java.util.List;

public class JsqlparserTest {
    public static void main(String[] args) throws JSQLParserException {
        JsqlparserTest jt = new JsqlparserTest();
        jt.testDruidParser();
    }

    public void testParser() throws JSQLParserException {
        String sql = "select a, if from tab";
        Statement statement = BISqlParserUtil.parse(sql);
        SqlTableColumnFinder sqlTableColumnFinder = new SqlTableColumnFinder();
        sqlTableColumnFinder.parse(statement);
        sqlTableColumnFinder.getTableColumnNames().forEach(System.out::println);
    }

    public void testDruidParser() {
        String sql = "SELECT\n" +
                "    dept.id id,\n" +
                "    dept.dept_name dept_name,\n" +
                "    dept.desc desc \n" +
                "  FROM\n" +
                "    hive264.zcy_hive.dept";
        String dbType = JdbcConstants.PRESTO;
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        SQLStatement stmt = stmtList.get(0);

        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmt.accept(statVisitor);

        System.out.println(statVisitor.getColumns()); // [t_user.name, t_user.age, t_user.id]
        System.out.println(statVisitor.getTables()); // {t_user=Select}
        System.out.println(statVisitor.getConditions()); // [t_user.id = 1]
    }
}
