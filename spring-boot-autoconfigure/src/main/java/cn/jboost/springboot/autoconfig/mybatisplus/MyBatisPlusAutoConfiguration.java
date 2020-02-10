package cn.jboost.springboot.autoconfig.mybatisplus;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnClass({PaginationInterceptor.class})
@Import({com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration.class})
@AutoConfigureAfter(com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration.class)
public class MyBatisPlusAutoConfiguration {

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor interceptor = new PaginationInterceptor();
        interceptor.setLimit(10000); //最大单页限制数，-1 无限制
        return interceptor;
    }

    @Bean
    public TimeSqlInterceptor sqlInterceptor() {
        return new TimeSqlInterceptor();
    }

    @Bean
    public JboostSqlInjector jboostSqlInjector() {
        return new JboostSqlInjector();
    }
}
