package cn.jboost.springboot.autoconfig.web;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.domain.Pageable;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.AlternateTypeRuleConvention;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static springfox.documentation.schema.AlternateTypeRules.newRule;

/**
 * @Author ronwxy
 * @Date 2020/6/2 9:11
 * @Version 1.0
 */
@Configuration
@ConditionalOnClass({org.springframework.web.servlet.DispatcherServlet.class, EnableSwagger2.class})
@ConditionalOnMissingBean(Docket.class)
@EnableSwagger2
public class Swagger2AutoConfiguration {

    @Value("${swagger.title:服务端接口文档}")
    private String title;

    @Value("${swagger.version:1.0}")
    private String version;

    @Value("${swagger.tokenHeader:token}")
    private String tokenHeader;

    @Value("${swagger.enabled:false}")
    private Boolean enabled;

    @Value("${swagger.basePackages}")
    private String[] basePackages;

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(title)
                .version(version)
                .build();
    }

    @Bean
    public Docket docket() {
        ParameterBuilder ticketPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        ticketPar
                .name(tokenHeader).description("token")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .defaultValue("Bearer ")
                .required(true)
                .build();
        pars.add(ticketPar.build());

        // 配置扫描
        Predicate<RequestHandler> or = null;
        if (basePackages != null && basePackages.length > 0) {
            Predicate<RequestHandler>[] predicates = new Predicate[basePackages.length];
            for (int i = 0; i < predicates.length; i++) {
                predicates[i] = RequestHandlerSelectors.basePackage(basePackages[i]);
            }
            or = Predicates.or(predicates);
        }

        ApiSelectorBuilder builder = new Docket(DocumentationType.SWAGGER_2)
                .enable(enabled)
                .apiInfo(apiInfo())
                .select()
                .paths(Predicates.not(PathSelectors.regex("/error.*")));
        if (or != null) {
            builder.apis(or);
        }
        return builder.build().globalOperationParameters(pars);
    }

    /**
     *  将Pageable转换展示在swagger中
     * @param resolver
     * @return
     */
    @Bean
    public AlternateTypeRuleConvention pageableConvention(final TypeResolver resolver) {
        return new AlternateTypeRuleConvention() {
            @Override
            public int getOrder() {
                return Ordered.HIGHEST_PRECEDENCE;
            }

            @Override
            public List<AlternateTypeRule> rules() {
                return newArrayList(newRule(resolver.resolve(Pageable.class), resolver.resolve(Page.class)));
            }
        };
    }

    @ApiModel
    @Getter
    @Setter
    static class Page {
        @ApiModelProperty("页码 (1..N)，默认1")
        private Integer current;

        @ApiModelProperty("每页显示的条数，默认10")
        private Integer size;
    }

}
