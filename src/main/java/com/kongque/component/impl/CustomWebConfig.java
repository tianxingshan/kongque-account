package com.kongque.component.impl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * @Auther: yuehui
 * @Date: 6/26 2019 15:08
 * @Description:
 */
@Configuration
public class CustomWebConfig {

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {

        return new MappingJackson2HttpMessageConverter(JsonMapper.mapper);
    }
}
