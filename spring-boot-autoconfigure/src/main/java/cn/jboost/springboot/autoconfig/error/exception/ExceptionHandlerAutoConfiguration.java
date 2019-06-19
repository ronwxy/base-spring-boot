package cn.jboost.springboot.autoconfig.error.exception;

import cn.jboost.springboot.autoconfig.error.BaseErrorAttributes;
import cn.jboost.springboot.autoconfig.error.handler.DefaultWebApplicationExceptionHandler;
import cn.jboost.springboot.autoconfig.error.handler.DevWebApplicationExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
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
        return new DefaultWebApplicationExceptionHandler();
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
