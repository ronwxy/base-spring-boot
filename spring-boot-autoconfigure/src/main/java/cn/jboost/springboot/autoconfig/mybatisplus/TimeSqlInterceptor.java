package cn.jboost.springboot.autoconfig.mybatisplus;

import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.handlers.AbstractSqlParserHandler;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * @author yuxk
 * @version V1.0
 * @Title: sql拦截器
 * @Description: 自动更新创建时间和更新时间
 * @date 2019/11/23 9:18
 */
@Intercepts(value = {@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class TimeSqlInterceptor extends AbstractSqlParserHandler implements Interceptor {

    /**
     * 创建时间
     */
    private static final String CREATE_TIME = "createTime";
    /**
     * 更新时间
     */
    private static final String UPDATE_TIME = "updateTime";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        // SQL操作命令
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        // 获取新增或修改的对象参数
        Object parameter = invocation.getArgs()[1];
        //参数直接为Entity对象或存在命名为et的参数才处理；
        //mybatis对未命名的Entity对象参数，parameter直接是对象本身，对命名的或参数列表，会将其封装到ParamMap中
        if (parameter.getClass().equals(MapperMethod.ParamMap.class)) {
            if (!((Map<String, Object>) parameter).containsKey(Constants.ENTITY)) {
                return invocation.proceed();
            } else {
                parameter = ((Map<String, Object>) parameter).get(Constants.ENTITY);
            }
        }

        // 获取对象中所有的私有成员变量（对应表字段）
        Field[] declaredFields = parameter.getClass().getDeclaredFields();
        if (parameter.getClass().getSuperclass() != null) {
            Field[] superField = parameter.getClass().getSuperclass().getDeclaredFields();
            declaredFields = ArrayUtil.addAll(declaredFields, superField);
        }

        String fieldName;
        for (Field field : declaredFields) {
            fieldName = field.getName();
            if (Objects.equals(CREATE_TIME, fieldName)) {
                if (SqlCommandType.INSERT.equals(sqlCommandType)) {
                    field.setAccessible(true);
                    field.set(parameter, LocalDateTime.now());
                }
            }
            if (Objects.equals(UPDATE_TIME, fieldName)) {
                if (SqlCommandType.UPDATE.equals(sqlCommandType)) {
                    field.setAccessible(true);
                    field.set(parameter, LocalDateTime.now());
                }
            }
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {

    }

}
