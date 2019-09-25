package com.github.johnsonmoon.commons;

import com.mysql.jdbc.Driver;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * Create by xuyh at 2019/9/19 16:48.
 */
public class JdbcUtilsTest {
    private static final String CREATE_TABlE =
            "create table test(\n" +
                    "id int(11) not null auto_increment primary key,\n" +
                    "name varchar(255) null,\n" +
                    "description text null\n" +
                    ")";
    private static final String DROP_TABLE = "drop table test";
    private Connection connection;

    @Before
    public void before() {
        connection = JdbcUtils.connect(Driver.class.getCanonicalName(), "jdbc:mysql://192.168.100.203:3306/test_commons?useSSL=false", "dbuser", "DBUser123!");
        if (connection != null) {
            Assert.assertTrue(JdbcUtils.execute(connection, CREATE_TABlE));
        }
    }

    @Test
    public void test() {
        if (connection != null) {
            Assert.assertNotEquals(0, JdbcUtils.update(connection, "insert into test(name,description) values(?, ?)", "Johnson-1", "test-description-1"));
            Assert.assertNotEquals(0, JdbcUtils.update(connection, "insert into test(name,description) values(?, ?)", "Johnson-2", "test-description-2"));
            Assert.assertNotEquals(0, JdbcUtils.update(connection, "insert into test(name,description) values(?, ?)", "Johnson-3", "test-description-3"));
            Assert.assertNotEquals(0, JdbcUtils.update(connection, "insert into test(name,description) values(?, ?)", "Johnson-4", "test-description-4"));
            Assert.assertNotEquals(0, JdbcUtils.update(connection, "insert into test(name,description) values(?, ?)", "Johnson-5", "test-description-5"));

            List<Map<String, Object>> allResults = JdbcUtils.query(connection, "select * from test");
            Assert.assertNotNull(allResults);
            System.out.println(allResults);

            Assert.assertNotEquals(0, JdbcUtils.update(connection, JdbcUtils.buildCommand("update test set description = ? where name = ?", "abc-1", "johnson-1")));
            Assert.assertNotEquals(0, JdbcUtils.update(connection, JdbcUtils.buildCommand("update test set description = ? where name = ?", "abc-2", "johnson-2")));
            Assert.assertNotEquals(0, JdbcUtils.update(connection, JdbcUtils.buildCommand("update test set description = ? where name = ?", "abc-3", "johnson-3")));

            List<Map<String, Object>> partResults = JdbcUtils.query(connection, JdbcUtils.buildCommand("select * from test where name in (?, ?, ?)", "johnson-1", "johnson-2", "johnson-3"));
            Assert.assertNotNull(partResults);
            System.out.println(partResults);

            Assert.assertTrue(JdbcUtils.transaction(connection,
                    JdbcUtils.Command.build().sql("update test set description = ? where name = ?").params("abc-4", "johnson-4"),
                    JdbcUtils.Command.build().sql("update test set description = ? where name = ?").params("abc-5", "johnson-5")));

            Assert.assertNotEquals(0, JdbcUtils.query(connection, JdbcUtils.buildCommand("select * from test"), (row, rowNumber) -> System.out.println(row)));
        }
    }

    @After
    public void after() {
        if (connection != null) {
            JdbcUtils.execute(connection, DROP_TABLE);
            JdbcUtils.disconnect(connection);
        }
    }
}
