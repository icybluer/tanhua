package com.tanhua.server.config;

import com.github.dozermapper.core.DozerBeanMapper;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DozerConfig {

    @Bean
    public Mapper mapper() {
        DozerBeanMapper mapper = (DozerBeanMapper) DozerBeanMapperBuilder.buildDefault();
        return mapper;
    }
}