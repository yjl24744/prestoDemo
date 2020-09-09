package com.example.demo.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;


@Configuration
public class GlobalDataSourceConfiguration {
	@Bean(name = "prestoDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.presto")
	public DataSource prestoDataSource() {
		return new DruidDataSource();
	}
	
	@Autowired
	@Qualifier("prestoDataSource")
	DataSource dataSource ;

	@Bean(name = "prestoTemplate")
	public JdbcTemplate prestoJdbcTemplate() {
		return new JdbcTemplate(dataSource);
	}
 
}