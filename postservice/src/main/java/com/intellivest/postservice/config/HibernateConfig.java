package com.intellivest.postservice.config;

import java.util.Properties;

import javax.sql.DataSource;

import com.intellivest.postservice.posts.Post;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class HibernateConfig {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(HibernateConfig.class);

  @Value("${database.jdbcUrl}")
  private String jdbcUrl;

  @Value("${database.username}")
  private String username;
  
  @Value("${database.password}")
  private String password;


  @Bean  /** init the session factory with assets and props */
  public LocalSessionFactoryBean sessionFactory() {
    LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
    sessionFactory.setDataSource(dataSource());
    sessionFactory.setPackagesToScan("com.baeldung.hibernate.bootstrap.model");
    sessionFactory.setHibernateProperties(hibernateProperties());
    sessionFactory.setAnnotatedClasses(Post.class);
    log.info("--------- SQL Session Initialized ---------");
    return sessionFactory;
  }

  @Bean  /** init the data source with credentials */
  public DataSource dataSource() {
    BasicDataSource dataSource = new BasicDataSource();
    dataSource.setDriverClassName("org.h2.Driver");
    dataSource.setUrl(this.jdbcUrl);
    dataSource.setUsername(this.username);
    dataSource.setPassword(this.password);
    return dataSource;
  }

  @Bean   /** init transaction manager to process operations to the db  */
  public PlatformTransactionManager hibernateTransactionManager() {
    HibernateTransactionManager transactionManager = new HibernateTransactionManager();
    transactionManager.setSessionFactory(sessionFactory().getObject());
    return transactionManager;
  }

  /** all other added hibernate configs and propeties */
  private final Properties hibernateProperties() {
    Properties hibernateProperties = new Properties();
    hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "none");
    hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");

    return hibernateProperties;
  }
}