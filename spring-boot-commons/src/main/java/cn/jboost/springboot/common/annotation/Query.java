package cn.jboost.springboot.common.annotation;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 属性查询注解，用于更丰富的查询语法
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Query {
    /**
     *  实体属性名称，默认为当前属性名
     */
    String propName() default "";
    /**
     * 查询条件
     */
    Type type() default Type.EQUAL;

    enum Type {
        EQUAL, //等于
        NOT_EQUAL, //不等于
        GREATER_THAN, //大于
        GREATER_EQUAL, //大有等于
        LESS_THAN, //小于
        LESS_EQUAL, //小于等于
        BETWEEN, //居于两者之间
        NOT_BETWEEN, //不居于两者之间
        LIKE, //模糊匹配 %query%
        NOT_LIKE,//不模糊匹配
        LEFT_LIKE, //左模糊匹配
        RIGHT_LIKE, //右模糊匹配
        IN, //包含查询
        NOT_IN, //不包含
        IS_NULL, // is null
        IS_NOT_NULL //is not null
    }
}
