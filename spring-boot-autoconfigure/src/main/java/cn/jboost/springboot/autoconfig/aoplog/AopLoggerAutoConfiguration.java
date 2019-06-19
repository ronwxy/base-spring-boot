package cn.jboost.springboot.autoconfig.aoplog;

import com.github.nickvl.xspring.core.log.aop.AOPLogger;
import com.github.nickvl.xspring.core.log.aop.ReqIdFilter;
import com.github.nickvl.xspring.core.log.aop.UniversalLogAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Configuration
@ConditionalOnClass(AOPLogger.class)
@ConditionalOnMissingBean(AOPLogger.class)
public class AopLoggerAutoConfiguration {

	private static final boolean SKIP_NULL_FIELDS = true;
	private static final Set<String> EXCLUDE_SECURE_FIELD_NAMES = Collections.emptySet();

	@Bean
	public AOPLogger aopLogger() {
		AOPLogger aopLogger = new AOPLogger();
		aopLogger.setLogAdapter(new UniversalLogAdapter(SKIP_NULL_FIELDS, EXCLUDE_SECURE_FIELD_NAMES));
		return aopLogger;
	}

	@Bean
	public FilterRegistrationBean reqIdFilter() {
		ReqIdFilter reqIdFilter = new ReqIdFilter();
		FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		registrationBean.setFilter(reqIdFilter);
		List<String> urlPatterns = Collections.singletonList("/*");
		registrationBean.setUrlPatterns(urlPatterns);
		registrationBean.setOrder(100);
		return registrationBean;
	}
}
