package com.atguigu.gmall.sms.config;

import com.zaxxer.hikari.HikariDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
//    @ConfigurationProperties("spring.datasource")
    public DataSource hikariDataSource(@Value("${spring.datasource.url}")String url,
                                       @Value("${spring.datasource.driver-class-name}")String driverClassName,
                                       @Value("${spring.datasource.username}")String username,
                                       @Value("${spring.datasource.password}")String password){

        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl(url);
        hikariDataSource.setDriverClassName(driverClassName);
        hikariDataSource.setUsername(username);
        hikariDataSource.setPassword(password);
        return new DataSourceProxy(hikariDataSource);
    }

    /**
     * 需要将 DataSourceProxy 设置为主数据源，否则事务无法回滚
     *
     * @param hikariDataSource The DruidDataSource
     * @return The default datasource
     */
//    @Primary
//    @Bean("dataSource")
//    public DataSource dataSource(HikariDataSource hikariDataSource) {
//        return new DataSourceProxy(hikariDataSource);
//    }
}
