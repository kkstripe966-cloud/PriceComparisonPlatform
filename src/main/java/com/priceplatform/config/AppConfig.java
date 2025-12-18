package com.priceplatform.config;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import com.alibaba.druid.pool.DruidDataSource;
import javax.sql.DataSource;
import java.io.IOException;

@Configuration
@EnableWebMvc
@EnableTransactionManagement
@ComponentScan(basePackages = "com.priceplatform")
@PropertySource("classpath:application.yml")
@MapperScan("com.priceplatform.dao")
public class AppConfig implements WebMvcConfigurer {

    @Autowired
    private Environment env;

    // 数据源配置
    @Bean
    public DataSource dataSource() {
        System.out.println("=== 使用硬编码数据库连接 ===");

        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/price_comparison?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false");
        dataSource.setUsername("root");
        dataSource.setPassword("wff13377072928");  // 根据你的MySQL密码修改
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        dataSource.setInitialSize(5);
        dataSource.setMinIdle(5);
        dataSource.setMaxActive(20);
        dataSource.setMaxWait(60000);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setTestWhileIdle(true);

        return dataSource;
    }

    // 辅助方法
    private int getIntProperty(String key, int defaultValue) {
        String value = env.getProperty(key);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }

    private long getLongProperty(String key, long defaultValue) {
        String value = env.getProperty(key);
        return value != null ? Long.parseLong(value) : defaultValue;
    }

    private boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = env.getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource) throws IOException {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);

        // 1. 设置Mapper XML位置
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        factoryBean.setMapperLocations(resolver.getResources("classpath:mapper/*.xml"));

        // 2. 设置实体类包路径
        factoryBean.setTypeAliasesPackage("com.priceplatform.entity");

        // 3. 配置MyBatis设置
        org.apache.ibatis.session.Configuration configuration =
                new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        factoryBean.setConfiguration(configuration);

        return factoryBean;
    }

    // 事务管理器
    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    // 密码加密器
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 文件上传解析器
    @Bean
    public StandardServletMultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    // 静态资源配置
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}