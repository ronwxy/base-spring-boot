package cn.jboost.springboot.autoconfig.tkmapper.typehandlers;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.util.List;
import java.util.Map;

/**
 * default customizer implements to register customized typehandler;
 */
public class MySqlConfigurationCustomizer implements ConfigurationCustomizer {
	@Override
	public void customize(Configuration configuration) {
		TypeHandlerRegistry registry = configuration.getTypeHandlerRegistry();
		registry.register(List.class, JdbcType.OTHER, JsonTypeHandler.class);
		registry.register(Map.class, JdbcType.OTHER, JsonTypeHandler.class);
		registry.register(Object[].class, JdbcType.ARRAY, ArrayTypeHandler.class);
		registry.register(String[].class, JdbcType.ARRAY, ArrayTypeHandler.class);
		registry.register(Integer[].class, JdbcType.ARRAY, ArrayTypeHandler.class);
		registry.register(Long[].class, JdbcType.ARRAY, ArrayTypeHandler.class);
		registry.register(Double[].class, JdbcType.ARRAY, ArrayTypeHandler.class);
		registry.register(Boolean[].class, JdbcType.ARRAY, ArrayTypeHandler.class);
		TypeAliasRegistry typeAliasRegistry = configuration.getTypeAliasRegistry();
		typeAliasRegistry.registerAlias("json", JsonTypeHandler.class);
	}
}
