package com.github.johnsonmoon.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

/**
 * Create by xuyh at 2019/9/19 15:49.
 */
public class JdbcUtils {
    private static Logger logger = LoggerFactory.getLogger(JdbcUtils.class);

    /**
     * Connect to database.
     *
     * @param driver   {@link Driver} driver implement class name.
     * @param url      db connection url
     * @param name     db authentication name
     * @param password db authentication password
     * @return {@link Connection}
     */
    public static Connection connect(String driver, String url, String name, String password) {
        Connection connection = null;
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, name, password);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return connection;
    }

    /**
     * Disconnect from a database.
     *
     * @param connection {@link Connection}
     */
    public static void disconnect(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

    /**
     * Execute command.
     *
     * @param connection {@link Connection}
     * @param cmd        Command
     * @return true/false
     */
    public static boolean execute(Connection connection, String cmd) {
        if (connection == null || cmd == null || cmd.isEmpty()) {
            return false;
        }
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute(cmd);
            return true;
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        }
        return false;
    }

    /**
     * Execute query sql.
     *
     * @param connection {@link Connection}
     * @param sql        Query sql statement
     * @param params     Query sql parameters
     * @return result rows [{"id":1}, {"id":2}, {"id":3}, {"id":4}]
     */
    public static List<Map<String, Object>> query(Connection connection, String sql, Object... params) {
        return query(connection, Command.build().sql(sql).params(params));
    }

    /**
     * Execute query sql.
     *
     * @param connection {@link Connection}
     * @param command    {@link Command}
     * @return result rows [{"id":1}, {"id":2}, {"id":3}, {"id":4}]
     */
    public static List<Map<String, Object>> query(Connection connection, Command command) {
        if (connection == null || command == null || command.getSql() == null) {
            return null;
        }
        List<Map<String, Object>> dataList = new ArrayList<>();
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(command.getSql());
            if (command.getParams() != null) {
                for (int i = 0; i < command.getParams().size(); i++) {
                    statement.setObject(i + 1, command.getParams().get(i));
                }
            }
            resultSet = statement.executeQuery();
            List<String> columns = new ArrayList<>();
            for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                columns.add(resultSet.getMetaData().getColumnName(i + 1));
            }
            while (resultSet.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (String column : columns) {
                    row.put(column, resultSet.getObject(column));
                }
                dataList.add(row);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (Exception e) {
                    logger.warn(e.getMessage(), e);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        }
        return dataList;
    }

    /**
     * Execute update sql statement.
     *
     * @param connection {@link Connection}
     * @param sql        Update sql statement
     * @param params     Update sql parameters
     * @return affected rows
     */
    public static int update(Connection connection, String sql, Object... params) {
        return update(connection, Command.build().sql(sql).params(params));
    }

    /**
     * Execute update sql statement.
     *
     * @param connection {@link Connection}
     * @param command    {@link Command}
     * @return affected rows
     */
    public static int update(Connection connection, Command command) {
        if (connection == null || command == null || command.getSql() == null) {
            return 0;
        }
        int result = 0;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(command.getSql());
            if (command.getParams() != null) {
                for (int i = 0; i < command.getParams().size(); i++) {
                    statement.setObject(i + 1, command.getParams().get(i));
                }
            }
            result = statement.executeUpdate();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        }
        return result;
    }

    /**
     * Execute update sql transaction.
     *
     * @param connection {@link Connection}
     * @param commands   sql commands {@link Command}
     * @return true/false
     */
    public static boolean transaction(Connection connection, Command... commands) {
        if (connection == null || commands == null || commands.length == 0) {
            return false;
        }
        List<PreparedStatement> statements = new ArrayList<>();
        try {
            connection.setAutoCommit(false);
            for (Command command : commands) {
                String sql = command.getSql();
                List<Object> params = command.getParams();
                PreparedStatement statement = connection.prepareStatement(sql);
                if (params != null) {
                    for (int i = 0; i < params.size(); i++) {
                        statement.setObject(i + 1, params.get(i));
                    }
                }
                statements.add(statement);
                if (statement.executeUpdate() <= 0) {
                    throw new RuntimeException("Rollback.");
                }
            }
            connection.commit();
            return true;
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            try {
                connection.rollback();
            } catch (Exception ex) {
                logger.warn(ex.getMessage(), ex);
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
            for (PreparedStatement statement : statements) {
                try {
                    statement.close();
                } catch (Exception e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        }
        return false;
    }

    /**
     * Build an empty sql command.
     *
     * @return {@link Command}
     */
    public static Command buildCommand() {
        return Command.build();
    }

    /**
     * Build a sql command with sql statement and parameters.
     *
     * @param sql    sql statement
     * @param params sql parameters
     * @return {@link Command}
     */
    public static Command buildCommand(String sql, Object... params) {
        return Command.build().sql(sql).params(params);
    }

    /**
     * SQL command.
     * <pre>
     *     sql statement
     *     sql parameters
     * </pre>
     */
    public static class Command {
        private String sql;
        private List<Object> params;

        public static Command build() {
            return new Command();
        }

        public String getSql() {
            return sql;
        }

        public void setSql(String sql) {
            this.sql = sql;
        }

        public Command sql(String sql) {
            this.sql = sql;
            return this;
        }

        public List<Object> getParams() {
            return params;
        }

        public void setParams(List<Object> params) {
            this.params = params;
        }

        public Command params(Object... params) {
            this.params = Arrays.asList(params);
            return this;
        }

        @Override
        public String toString() {
            return "Command{" +
                    "sql='" + sql + '\'' +
                    ", params=" + params +
                    '}';
        }
    }
}
