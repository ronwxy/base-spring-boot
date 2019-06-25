package cn.jboost.springboot.autoconfig.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

/**
 * @author ronwxy
 * @date 2017/10/23 18:22
 */
@Configuration
@ConditionalOnClass(DruidDataSource.class)
@ConditionalOnMissingBean(DruidDataSource.class)
@ConfigurationProperties(prefix = "spring.datasource.druid")
public class DruidAutoConfiguration {
    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private int initialSize = 1; // 初始化大小
    private int minIdle = 1; // 最小连接
    private int maxActive = 5; // 最大连接
    private int maxWait = 30 * 1000; // 配置获取连接等待超时的时间
    private int timeBetweenEvictionRunsMillis = 60 * 1000; // 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
    private int minEvictableIdleTimeMillis = 10 * 60 * 1000; // 配置一个连接在池中最小生存的时间，单位是毫秒
    private String validationQuery; // SELECT 'x'
    private boolean testWhileIdle;
    private boolean testOnBorrow;
    private boolean testOnReturn;
    private boolean poolPreparedStatements; // 打开PSCache，并且指定每个连接上PSCache的大小
    private int maxPoolPreparedStatementPerConnectionSize;
    private String filters; //stat,wall # 配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙
    private Properties connectionProperties; // 通过connectProperties属性来打开mergeSql功能；慢SQL记录
    private boolean useGlobalDataSourceStat; // 合并多个DruidDataSource的监控数据

    private Map<String, String> druidServletSettings;
    private Map<String, String> druidFilterSettings;

    @Bean(name = "dataSource", initMethod = "init", destroyMethod = "close")
    @ConditionalOnMissingBean(DataSource.class)
    @Primary
    public DruidDataSource dataSource() {
        DruidDataSource datasource = new DruidDataSource();
        datasource.setDriverClassName(driverClassName);
        datasource.setUrl(url);
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setInitialSize(initialSize);
        datasource.setMinIdle(minIdle);
        datasource.setMaxActive(maxActive);
        datasource.setMaxWait(maxWait);
        datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        datasource.setValidationQuery(validationQuery);
        datasource.setTestWhileIdle(testWhileIdle);
        datasource.setTestOnBorrow(testOnBorrow);
        datasource.setTestOnReturn(testOnReturn);
        datasource.setPoolPreparedStatements(poolPreparedStatements);
        datasource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
        try {
            datasource.setFilters(filters);
        } catch (SQLException e) {
            //TODO 异常处理
        }
        datasource.setConnectProperties(connectionProperties);
        datasource.setUseGlobalDataSourceStat(useGlobalDataSourceStat);
        return datasource;
    }

    /**
     * 注册一个StatViewServlet
     */
    @Bean
    public ServletRegistrationBean druidStatViewServlet() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        //添加初始化参数：initParams
        //白名单：
        servletRegistrationBean.addInitParameter("allow", druidServletSettings.getOrDefault("allow", "127.0.0.1"));
        //IP黑名单 (存在共同时，deny优先于allow) : 如果满足deny的话提示:Sorry, you are not permitted to view this page.
        if(druidFilterSettings.get("deny") != null) {
            servletRegistrationBean.addInitParameter("deny", druidServletSettings.get("deny"));
        }
        //登录查看信息的账号密码.
        servletRegistrationBean.addInitParameter("loginUsername", druidServletSettings.getOrDefault("loginUsername", "admin"));
        servletRegistrationBean.addInitParameter("loginPassword", druidServletSettings.getOrDefault("loginPassword", "Passw0rd"));
        //是否能够重置数据.
        servletRegistrationBean.addInitParameter("resetEnable", druidServletSettings.getOrDefault("resetEnable", "false"));// 禁用HTML页面上的“Reset All”功能
        return servletRegistrationBean;
    }

    /**
     * 注册一个：filterRegistrationBean
     */
    @Bean
    public FilterRegistrationBean druidStatFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
        filterRegistrationBean.setName("druidWebStatFilter");
        //添加过滤规则.
        filterRegistrationBean.addUrlPatterns("/*");
        //添加忽略的格式信息.
        filterRegistrationBean.addInitParameter("exclusions", druidFilterSettings.getOrDefault("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*"));
        return filterRegistrationBean;
    }


    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public int getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(int maxWait) {
        this.maxWait = maxWait;
    }

    public int getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(int timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public int getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(int minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public boolean isPoolPreparedStatements() {
        return poolPreparedStatements;
    }

    public void setPoolPreparedStatements(boolean poolPreparedStatements) {
        this.poolPreparedStatements = poolPreparedStatements;
    }

    public int getMaxPoolPreparedStatementPerConnectionSize() {
        return maxPoolPreparedStatementPerConnectionSize;
    }

    public void setMaxPoolPreparedStatementPerConnectionSize(int maxPoolPreparedStatementPerConnectionSize) {
        this.maxPoolPreparedStatementPerConnectionSize = maxPoolPreparedStatementPerConnectionSize;
    }

    public String getFilters() {
        return filters;
    }

    public void setFilters(String filters) {
        this.filters = filters;
    }

    public Properties getConnectionProperties() {
        return connectionProperties;
    }

    public void setConnectionProperties(Properties connectionProperties) {
        this.connectionProperties = connectionProperties;
    }

    public boolean isUseGlobalDataSourceStat() {
        return useGlobalDataSourceStat;
    }

    public void setUseGlobalDataSourceStat(boolean useGlobalDataSourceStat) {
        this.useGlobalDataSourceStat = useGlobalDataSourceStat;
    }

    public Map<String, String> getDruidServletSettings() {
        return druidServletSettings;
    }

    public void setDruidServletSettings(Map<String, String> druidServletSettings) {
        this.druidServletSettings = druidServletSettings;
    }

    public Map<String, String> getDruidFilterSettings() {
        return druidFilterSettings;
    }

    public void setDruidFilterSettings(Map<String, String> druidFilterSettings) {
        this.druidFilterSettings = druidFilterSettings;
    }
}
