package cn.jboost.springboot.autoconfig.swagger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.swagger2.web.Swagger2Controller;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Map;

@ConditionalOnWebApplication
@ConditionalOnClass({org.springframework.web.servlet.DispatcherServlet.class, EnableSwagger2.class})
@ConditionalOnMissingBean(Docket.class)
@Configuration
@Profile({"dev"})
@EnableSwagger2
@ConfigurationProperties(prefix = "swagger")
public class Swagger2AutoConfiguration {
    @Value("${swagger.group-name:${spring.application.name}}")
    private String groupName;
    @Value("${swagger.apis-base-package:com.xxx}")
    private String apisBasePackage;
    @Value("${swagger.api-title:${spring.application.name}}")
    private String apiTitle;
    @Value("${swagger.api-description:${spring.application.name}}")
    private String apiDescription;
    @Value("${swagger.swagger-register-url:http://localhost:11090/swagger/register}")
    private String swaggerRegisterUrl;

    public String getSwaggerRegisterUrl() {
        return swaggerRegisterUrl;
    }

    public void setSwaggerRegisterUrl(String swaggerRegisterUrl) {
        this.swaggerRegisterUrl = swaggerRegisterUrl;
    }

    @Bean
    public Docket restApi() {
        ParameterBuilder builder = new ParameterBuilder();
        builder.name("x-auth-token").description("授权token")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(false);
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName(groupName)
                .select()
                .apis(RequestHandlerSelectors.basePackage(apisBasePackage))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(Collections.singletonList(builder.build()))
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(apiTitle)
                .description(apiDescription)
                .build();
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getApisBasePackage() {
        return apisBasePackage;
    }

    public void setApisBasePackage(String apisBasePackage) {
        this.apisBasePackage = apisBasePackage;
    }

    public String getApiTitle() {
        return apiTitle;
    }

    public void setApiTitle(String apiTitle) {
        this.apiTitle = apiTitle;
    }

    public String getApiDescription() {
        return apiDescription;
    }

    public void setApiDescription(String apiDescription) {
        this.apiDescription = apiDescription;
    }

    @Profile({"dev"})
    @Bean
    public CommandLineRunner swaggerRegistar(ConfigurableApplicationContext context) {
        return new SwaggerInfoRegistar(context);
    }

    /**
     * use to register swagger api info url to swagger api registry;
     *
     * @author liubo
     */
    public class SwaggerInfoRegistar implements CommandLineRunner {
        private final Logger logger = LoggerFactory.getLogger(SwaggerInfoRegistar.class);
        private static final String swaggerV2DocsPath = Swagger2Controller.DEFAULT_URL;

        private ConfigurableApplicationContext context;

        SwaggerInfoRegistar(ConfigurableApplicationContext context) {
            this.context = context;
        }

        @Override
        public void run(String... args) throws Exception {
            String url = buildLocalSwaggerDocsUrl();
            registerLocalSwaggerUrl(url);
        }

        /**
         * register the v2/api-docs url
         *
         * @param url
         */
        private void registerLocalSwaggerUrl(String url) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("project", getApiTitle());
            body.add("url", url);
            ResponseEntity<Map> re = restTemplate.postForEntity(getSwaggerRegisterUrl(), body, Map.class);
            if (HttpStatus.OK.equals(re.getStatusCode())) {
                logger.info("swagger api registered success to {}", getSwaggerRegisterUrl());
            } else {
                logger.warn("swagger api registered failed [{}]", re.getBody().get("msg"));
            }
        }

        /**
         * obtain current application's swagger docs url base on v2/api-docs
         *
         * @return an url for api-docs to read json;
         * @throws UnknownHostException
         */
        private String buildLocalSwaggerDocsUrl() throws UnknownHostException, MalformedURLException {
            ServerProperties sp = context.getBean(ServerProperties.class);
            String root = (sp.getServlet().getContextPath() == null) ? "" : sp.getServlet().getContextPath();
            Integer port = (sp.getPort() == null) ? 8080 : sp.getPort();
            String host = InetAddress.getLocalHost().getHostAddress();

            URL tmpUrl = new URL("http", host, port, root + swaggerV2DocsPath);
            String groupName = getGroupName();
            String url = (StringUtils.isEmpty(groupName)) ? tmpUrl.toString()
                    : tmpUrl.toString() + "?group=" + groupName;
            logger.info("local swagger docs url address is [{}]", url);
            return url;
        }
    }

}
