/**
 * 
 */
package com.kongque.component;

import javax.servlet.Filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.support.OpenSessionInViewFilter;  
/**
 * @author yuehui
 *
 * @2018年1月30日
 */
@Configuration
@ComponentScan
public class CommonInitializer {

	 @Bean
	    public FilterRegistrationBean filterRegistration() {
	        FilterRegistrationBean registration = new FilterRegistrationBean();
	        registration.setFilter(openSessionInView());
	        registration.addUrlPatterns("/*");

	        return registration;
	    }

	    @Bean
	    public Filter openSessionInView() {
	        return new OpenSessionInViewFilter();
	    }
}
