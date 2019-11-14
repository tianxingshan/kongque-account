package com.kongque;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ImportResource;

@EnableAutoConfiguration(exclude = { 
	DataSourceAutoConfiguration.class, 
	HibernateJpaAutoConfiguration.class,
	DataSourceTransactionManagerAutoConfiguration.class })
@ImportResource({ "classpath:spring/*.xml" })
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class KongqueAccountApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {

    	SpringApplication.run(KongqueAccountApplication.class, args);
    }

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(KongqueAccountApplication.class);
	}

}
