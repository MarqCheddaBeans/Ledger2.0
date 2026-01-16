package com.pluralsight.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

public class DatabaseConfig {

    // We will build the BasicDataSource and store it here.
    private BasicDataSource bds;

    // This method defines the DataSource bean.
    // Spring will call this and register the DataSource in the ApplicationContext.
    @Bean
    public DataSource dataSource() {
        return bds;
    }

    // Constructor — Spring will call this and inject the datasource.url property here.
    // We will also manually read username/password from System properties — as you wanted — just like Workbook 8 style.
    public DatabaseConfig(@Value("${datasource.url}") String url) {

        // Read username and password from system properties — these were passed as command-line args.
        String username = System.getProperty("dbUsername");
        String password = System.getProperty("dbPassword");

        // Build the BasicDataSource.
        bds = new BasicDataSource();
        bds.setUrl(url);
        bds.setUsername(username);
        bds.setPassword(password);

    }
}
