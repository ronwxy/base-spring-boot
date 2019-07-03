package cn.jboost.springboot.autoconfig.error;

import cn.jboost.springboot.autoconfig.error.handler.BaseWebApplicationExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.Servlet;

@Configuration
@ConditionalOnClass({Servlet.class, DispatcherServlet.class, WebMvcConfigurer.class})
@ConditionalOnMissingBean(ResponseEntityExceptionHandler.class)
@AutoConfigureBefore(ErrorMvcAutoConfiguration.class)
public class ExceptionHandlerAutoConfiguration {
    @Profile({"test", "formal", "prod"})
    @Bean
    public ResponseEntityExceptionHandler defaultGlobalExceptionHandler() {
        //测试、正式环境，不输出异常的stack trace
        return new BaseWebApplicationExceptionHandler(false);
    }

    @Profile({"default","local","dev"})
    @Bean
    public ResponseEntityExceptionHandler devGlobalExceptionHandler() {
        //本地、开发环境，输出异常的stack trace
        return new BaseWebApplicationExceptionHandler(true);
    }

    @Profile({"test", "formal", "prod"})
    @Bean
    public ErrorAttributes basicErrorAttributes() {
        //测试、正式环境，不输出异常的stack trace
        return new BaseErrorAttributes(false);
    }

    @Profile({"default","local","dev"})
    @Bean
    public ErrorAttributes devBasicErrorAttributes() {
        //本地、开发环境，输出异常的stack trace
        return new BaseErrorAttributes(true);
    }
}
