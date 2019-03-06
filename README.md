
### 主要版本 
- springboot 2.0.6.RELEASE 
- spring 5.0.10.RELEASE

1.在pom.xml文件中引入依赖管理 
```xml
<dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>com.springboot.base</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>1.2-SNAPSHOT</version>
                <scope>import</scope>
                <type>pom</type>
            </dependency>
        </dependencies>
</dependencyManagement>
```
2.在pom.xml文件中引入你想要使用的starters，如
```xml
<dependency>
            <groupId>com.springboot.base</groupId>
            <artifactId>swagger-spring-boot-starter</artifactId>
</dependency>
```
3.在项目的配置文件中配置starters需要的相关配置属性 

- swagger-spring-boot-starter

```yaml
    swagger:
        api-description: demo for swagger api use swagger starters #api描述 如 ${spring.application.name}
        api-title: demo #api标题 如 ${spring.application.name}
        apis-base-package: com.example.demo.api #扫描的包名  可填根包名
        group-name: demo #组名 如 ${spring.application.name}
        swagger-registry-path: http://{swaggerServerIp:port}/swagger/register  #swaggerapi 注册接口地址
```

- tkmapper-spring-boot-starter   druid数据源配置

```yaml
    spring:
        datasource:
            druid:
                driver-class-name: org.postgresql.Driver
                url: jdbc:postgresql://{dbServerIp:port}/db_name?charSet=UTF-8
                username: dbUsername
                password: dbPassword
                # 自定义配置
                initialSize: 2  # 初始化大小
                minIdle: 1   # 最小连接
                maxActive: 5 # 最大连接
                druidServletSettings:
                    allow: 127.0.0.1
                    deny:
                    loginUsername: admin
                    loginPassword: Passw0rd
                    resetEnable: true
                druidFilterSettings:
                    exclusions: '*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*'
                maxWait: 60000   # 配置获取连接等待超时的时间
                timeBetweenEvictionRunsMillis: 60000 # 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
                minEvictableIdleTimeMillis: 300000 # 配置一个连接在池中最小生存的时间，单位是毫秒
                validationQuery: SELECT 'x'
                testWhileIdle: true
                testOnBorrow: false
                testOnReturn: false
                poolPreparedStatements: true # 打开PSCache，并且指定每个连接上PSCache的大小
                maxPoolPreparedStatementPerConnectionSize: 20
                filters: stat #,wall（添加wall代码里不能直接拼接sql，druid有sql注入校验） # 配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙
                connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000 # 通过connectProperties属性来打开mergeSql功能；慢SQL记录
                useGlobalDataSourceStat: true # 合并多个DruidDataSource的监控数据
```
tkmapper配置， 无默认配置
- 自动扫描entity及mapper包，扫描路径与springboot默认路径一致
- 扫描`@Mapper`
- 自动注册`com.springboot.autoconfig.tkmapper.mapper.BaseMapper` 基础接口

- qiniu-spring-boot-starter 

```yaml
    qiniu:
        access-key: xxx
        secret-key: xxx #密钥
        buckets:
         - bucket-name: xxx #桶名
            bucket-host: xxx #桶域名
            scheme: http #协议
            bucket-private: true #是否是私有桶
            pipeline: imageThumbQueue #管道名
        token-expired-time: 3600 #token过期时间秒
```
- alimq-spring-boot-starter

```yaml
    aliyun:
      mq:
        onsAddr: http://onsaddr-internet.aliyun.com/rocketmq/nsaddr4client-internet #mq地址
        topic: xxx #主题名称
        accessKey: xxx #key
        secretKey: xxx #密钥
        producer:  #生产者配置
            enabled: true #是否开启生产者
            producerId: xxx #生产者ID
        consumer:   #消费者配置
            enabled: true #是否开启消费者
            consumerId: xxx #消费者ID
        tag-suffix: xxx #主题标签后缀，用来区分同一主题下不同tag
```
- dubbo-spring-boot-starter

```yaml
    dubbo:#dubbo zookeeper注册中心配置
        zk:
          registry: ip:port #注册中心zookeeper地址
```

用法 代码

```java
@Configuration
@EnableDubboProvider
@EnableDubboConsumer
public class DubboConfig{

}
```

- aoplog-spring-boot-starter
没有配置

-  error-spring-boot-starter
    - 没有配置
    - 提供默认的全局异常处理器`com.springboot.autoconfig.error.exception.ExceptionHandlerAutoConfiguration$DefaultGlobalExceptionHandler`
    
- alimns-spring-boot-starter
```yaml
   mns:
     access-id: xxx
     access-key: xxx
     account-endpoint: xxx
``` 
- redisclient-spring-boot-starter
```yaml
    spring:
      redis:
        host: ip
        database: 1
    redis:
      clients:
        db1:
          host: ip
          database: 2
      clients-enabled: true #是否开启多个客户端
```

```java
    @Configuration
    @RedisClient("db1")//除了默认的连接池会实例化另一个连接池
    public class ConfigClass{
        @Bean
        public RedisTokenStore getRedisTokenStore(RedisClientFactory factory){
            //获取db1的连接池
            RedisConnectionFactory connectionFactory = factory.getInstance("db1",RedisConnectionFactory.class);
            return new RedisTokenStore(connectionFactory);
        }
    }
```

### 排除自动配置示例  

```java
    @SpringBootApplication(exclude = Swagger2AutoConfiguration.class)
    public class TestApplication {
        public static void main(String[] args) {
            new SpringApplicationBuilder(TestApplication.class).run(args);
    	}
    }
```    
    
