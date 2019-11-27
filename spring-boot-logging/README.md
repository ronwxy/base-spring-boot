
- 该项目基于https://github.com/nickvl/aop-logging.git （添加了ReqId标记某次客户端请求：com.github.nickvl.xspring.core.log.aop.ReqIdFilter, 添加了方法执行时长：com.github.nickvl.xspring.core.log.aop.AOPLogger.logTheMethod方法中elapsedTime）
- 如果需要跟踪基于dubbo的远程方法调用，需将com.github.nickvl.xspring.core.log.aop.AOPLogger.logTheMethod方法内的注释打开

### Add the dependency to your maven pom.xml
    <dependencies>
    ...
      <dependency>
            <groupId>com.springboot.base</groupId>
             <artifactId>xspring-aop-logging</artifactId>
            <version>1.0.0</version>
       </dependency>
    ...
    </dependencies>

### Apply the logging utility in your project

1.Activates the logger in spring's context

1.1.Xml based configuration style

    <?xml version="1.0" encoding="UTF-8"?>
    <beans
            xmlns="http://www.springframework.org/schema/beans"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xmlns:aop-logger="urn:nickvl/xspring/aop-logger"
            xsi:schemaLocation="
            http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
            urn:nickvl/xspring/aop-logger urn:nickvl/xspring/aop-logger/aop-logger.xsd">

        <!-- Activates the logger and @AspectJ style of Spring AOP. There are additional configuration options. -->
        <aop-logger:annotation-logger>
            <aop-logger:config>
                <aop-logger:reflection-to-string skip-null-fields="true" multi-element-structure-crop-threshold="20" />
            </aop-logger:config>
        </aop-logger:annotation-logger>
        ...
    </beans>

1.2.Java-based configuration style


    package com.me.shop.config;
    import com.github.nickvl.xspring.core.log.aop.AOPLogger;
    import com.github.nickvl.xspring.core.log.aop.UniversalLogAdapter;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.context.annotation.EnableAspectJAutoProxy;

    import java.util.Collections;
    import java.util.Set;

    @Configuration
    @EnableAspectJAutoProxy
    public class LoggerConfig {

        private static final boolean SKIP_NULL_FIELDS = true;
        private static final int CROP_THRESHOLD = 7;
        private static final Set<String> EXCLUDE_SECURE_FIELD_NAMES = Collections.<String>emptySet();

        @Bean
        public AOPLogger getLoggerBean() {
            AOPLogger aopLogger = new AOPLogger();
            aopLogger.setLogAdapter(new UniversalLogAdapter(SKIP_NULL_FIELDS, CROP_THRESHOLD, EXCLUDE_SECURE_FIELD_NAMES));
            return aopLogger;
        }
    }

1.3. add ReqIdFilter to your web.xml


    <filter>
        <filter-name>aopLogReqIdFilter</filter-name>
        <filter-class>com.github.nickvl.xspring.core.log.aop.ReqIdFilter</filter-class>
    </filter>

    <filter-mapping>
        <filter-name>aopLogReqIdFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>


    or register in springboot like this:


    @Bean
    public FilterRegistrationBean getDemoFilter(){
        ReqIdFilter reqIdFilter=new ReqIdFilter();
        FilterRegistrationBean registrationBean=new FilterRegistrationBean();
        registrationBean.setFilter(reqIdFilter);
        List<String> urlPatterns=new ArrayList<String>();
        urlPatterns.add("/*");
        registrationBean.setUrlPatterns(urlPatterns);
        registrationBean.setOrder(100);
        return registrationBean;
    }



2.Add log annotation on required methods


    package com.me.shop;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.ws.server.endpoint.annotation.Endpoint;
    import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
    import org.springframework.ws.server.endpoint.annotation.RequestPayload;
    import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

    import com.me.shop.oxm.PaymentContract;
    import com.me.shop.shop.oxm.PaymentContractResponse;
    import com.me.shop.shop.NotEnoughMoneyException;

    import com.github.nickvl.xspring.core.log.aop.annotation.LogDebug;
    import com.github.nickvl.xspring.core.log.aop.annotation.LogInfo;

    /**
     * Billing shop endpoint.
     */
    @LogInfo
    @LogException(value = {@Exc(value = Exception.class, stacktrace = false)}, warn = {@Exc({IllegalArgumentException.class})})
    public class BillingShopEndpoint {

        private static final String NS = "urn:PaycashShopService";

        @Autowired
        private ShopService shop;


        @ResponsePayload
        @PayloadRoot(localPart = "PaymentContract", namespace = NS)
        public PaymentContractResponse processPaymentContract(@RequestPayload PaymentContract request) {
            return shop.checkPayment(request);
        }

        // other methods
    }

3.Configure logging in your application

### Example

Commons logging configured to log using log4j framework:

    2014-05-21 23:22:31,073 TRACE [benchmark.LoggableServiceImpl] (main) - calling: aopLogMethod(2 arguments: b=33)
    2014-05-21 23:22:31,074 TRACE [benchmark.LoggableServiceImpl] (main) - returning: aopLogMethod(2 arguments):34


4.MDC variables

  reqId: %X{reqId}, marked as unique request

  elapsedTime: %X{elapsedTime}, the method invoked consume times in mills.

  *the two below should uncomment code in com.github.nickvl.xspring.core.log.aop.AOPLogger.logTheMethod*
  
  callingClass: %X{callingClass}, the method invoked on class.

  callingMethod: %X{callingMethod}, the method invoked on class method.
  

5.注意，打包时出现javadoc报错，则需跳过javadoc，如：  mvn clean install -Dmaven.javadoc.skip=true


