package com.springboot.boot.error.exception;

import com.springboot.boot.error.BaseErrorAttributes;
import com.springboot.boot.error.handler.DefaultGlobalExceptionHandler;
import com.springboot.boot.error.handler.DevWebApplicationExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.Servlet;

@Configuration
@ConditionalOnClass({Servlet.class, DispatcherServlet.class, WebMvcConfigurerAdapter.class})
@ConditionalOnMissingBean(ResponseEntityExceptionHandler.class)
@AutoConfigureBefore(ErrorMvcAutoConfiguration.class)
public class ExceptionHandlerAutoConfiguration {
    @Profile({"test", "formal", "prod"})
    @Bean
    public ResponseEntityExceptionHandler defaultGlobalExceptionHandler() {
        return new DefaultGlobalExceptionHandler();
    }

    @Profile({"local","dev"})
    @Bean
    public ResponseEntityExceptionHandler devGlobalExceptionHandler() {
        return new DevWebApplicationExceptionHandler();
    }

    @Bean
    public ErrorAttributes basicErrorAttributes() {
        return new BaseErrorAttributes();
    }
}
