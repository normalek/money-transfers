package com.revolut.transfers.config;

import com.revolut.transfers.exception.BaseException;
import org.h2.jdbcx.JdbcConnectionPool;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.locator.ClasspathSqlLocator;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import java.util.Properties;

public class JDBIConfig {
    private static Jdbi jdbi;

    static {
        try {
            Properties properties = new Properties();
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("db/db.properties"));
            JdbcConnectionPool connectionPool =
                    JdbcConnectionPool.create(properties.getProperty("h2.url"),
                            properties.getProperty("h2.username"), properties.getProperty("h2.password"));
            jdbi = Jdbi.create(connectionPool);
            jdbi.installPlugin(new SqlObjectPlugin());
            jdbi.open().createScript(ClasspathSqlLocator.create().locate("db/transfers-scheme")).execute();
        } catch (Exception e) {
            throw new BaseException("Cannot initialize Database. Check the configuration files.");
        }
    }

    public static Jdbi getJdbi() {
        return jdbi;
    }
}
