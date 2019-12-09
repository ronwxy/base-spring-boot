package cn.jboost.springboot.autoconfig.mybatisplus;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.jboost.springboot.common.annotation.Query;
import cn.jboost.springboot.common.exception.CommonErrorCodeEnum;
import cn.jboost.springboot.common.exception.ExceptionUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.MyBatisSystemException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Slf4j
public class MyBatisPlusQueryHelper {

    /**
     * 根据查询标准类构建查询对象
     *
     * @param target        要查询的实体类
     * @param queryCriteria 查询标准类
     * @param <T>
     * @param <Q>
     * @return
     */
    public static <T, Q> Wrapper<T> buildQuery(Class<T> target, Q queryCriteria) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        if (queryCriteria == null) {
            return wrapper;
        }
        Field[] fields = ReflectUtil.getFields(queryCriteria.getClass());
        Arrays.stream(fields).forEach(field -> {
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            Query query = field.getAnnotation(Query.class);
            if (query != null) {
                String propName = query.propName();
                //如果未指定propName，则默认为当前属性名
                String attributeName = StrUtil.isEmpty(propName) ? field.getName() : propName;
                //根据属性名找到数据库字段名
                String columnName = getColumnName(target, attributeName);
                if (StrUtil.isEmpty(columnName)) {
                    log.warn("property {} is not mapped to db column", attributeName);
                    return;
                }
                Object val;
                try {
                    val = field.get(queryCriteria);
                } catch (IllegalAccessException ex) {
                    throw new MyBatisSystemException(ex);
                }
                if (ObjectUtil.isNotNull(val)) {
                    switch (query.type()) {
                        case EQUAL:
                            wrapper.eq(columnName, val);
                            break;
                        case NOT_EQUAL:
                            wrapper.ne(columnName, val);
                            break;
                        case GREATER_THAN:
                            wrapper.gt(columnName, val);
                            break;
                        case GREATER_EQUAL:
                            wrapper.ge(columnName, val);
                            break;
                        case LESS_THAN:
                            wrapper.lt(columnName, val);
                            break;
                        case LESS_EQUAL:
                            wrapper.le(columnName, val);
                            break;
                        case BETWEEN:
                            Object[] objs = (Object[]) val;
                            if (objs.length == 2) {
                                wrapper.between(columnName, objs[0], objs[1]);
                            } else {
                                log.warn("property [{}] does not have 2 elements.", field.getName());
                            }
                            break;
                        case NOT_BETWEEN:
                            Object[] values = (Object[]) val;
                            if (values.length == 2) {
                                wrapper.notBetween(columnName, values[0], values[1]);
                            } else {
                                log.warn("property [{}] does not have 2 elements.", field.getName());
                            }
                            break;
                        case LIKE:
                            wrapper.like(columnName, val.toString());
                            break;
                        case NOT_LIKE:
                            wrapper.notLike(columnName, val.toString());
                            break;
                        case LEFT_LIKE:
                            wrapper.likeLeft(columnName, val.toString());
                            break;
                        case RIGHT_LIKE:
                            wrapper.likeRight(columnName, val.toString());
                            break;
                        case IN:
                            if (CollUtil.isNotEmpty((Collection) val)) {
                                wrapper.in(columnName, (Collection) val);
                            }
                            break;
                        case NOT_IN:
                            if (CollUtil.isNotEmpty((Collection) val)) {
                                wrapper.notIn(columnName, (Collection) val);
                            }
                            break;
                        case IS_NULL:
                            wrapper.isNull(columnName);
                            break;
                        case IS_NOT_NULL:
                            wrapper.isNotNull(columnName);
                            break;
                        default:
                            log.warn("unsupported query type [{}]", query.type());
                            break;
                    }
                } else {
                    switch (query.type()) {
                        case IS_NULL:
                            wrapper.isNull(columnName);
                            break;
                        case IS_NOT_NULL:
                            wrapper.isNotNull(columnName);
                            break;
                        default:
                            log.debug("query type [{}] has no value", query.type());
                            break;
                    }
                }
            }
            field.setAccessible(accessible);
        });
        return wrapper;
    }

    /**
     * 将实体对象构造为查询对象
     *
     * @param entity 实体对象
     * @return
     */
    public static <T> Wrapper<T> buildQuery(T entity) {
        QueryWrapper<T> wrapper = new QueryWrapper<T>();
        if (entity == null) {
            return wrapper;
        }
        Field[] fields = ReflectUtil.getFields(entity.getClass());
        Arrays.stream(fields).forEach(f -> {
            try {
                String columnName = getColumnName(f);
                if (StrUtil.isNotEmpty(columnName)) {
                    Object value = f.get(entity);
                    if (value != null) {
                        wrapper.eq(columnName, value);
                    }
                }
            } catch (IllegalAccessException ex) {
                ExceptionUtil.rethrowServerSideException(ex.getMessage(), ex);
            }
        });
        return wrapper;
    }

    /**
     * 获取属性对应的数据库列名
     *
     * @param field
     * @return
     */
    public static String getColumnName(Field field) {
        TableField fieldAnnotation = field.getAnnotation(TableField.class);
        if (fieldAnnotation == null || fieldAnnotation.exist()) {
            String columnName;
            if (fieldAnnotation != null && StrUtil.isNotBlank(fieldAnnotation.value())) {
                columnName = fieldAnnotation.value();
            } else {
                columnName = StrUtil.toUnderlineCase(field.getName());
            }
            return columnName;
        }
        return null;
    }

    /**
     * 根据对象属性名称获取数据库列名
     *
     * @param propName
     * @return
     */
    public static <T> String getColumnName(Class<T> target, String propName) {
        try {
            Field field = target.getDeclaredField(propName);
            return getColumnName(field);
        } catch (NoSuchFieldException ex) {
            ExceptionUtil.rethrowClientSideException(CommonErrorCodeEnum.COLUMN_ABSENT.getMessage(), ex);
            return null;
        }
    }

    private static final String ASC = "asc";
    private static final String DESC = "desc";

    /**
     * 将自定义分页对象转换为mybatis-plus的分页对象
     *
     * @param target 目标实体类
     * @param page   自定义分页对象
     * @param <T>
     * @return
     */
    public static <T> Page buildPage(Class<T> target, cn.jboost.springboot.autoconfig.web.controller.Page page) {
        Page p = new Page();
        p.setCurrent(page.getPage());
        p.setSize(page.getSize());
        p.setOrders(buildOrderItems(target, page.getSort()));
        return p;
    }

    /**
     * 根据排序字符串构建OrderItem列表，升序、降序部分最多均只支持两个字段
     *
     * @param target 目标实体类
     * @param sort   排序字符串列表，形如 {"createTime", "desc", "age", "asc"} 根据创建降序，年龄升序
     *               前端传参形如： api_url?sort=createTime,desc,age,asc
     * @param <T>
     * @return
     */
    public static <T> List<OrderItem> buildOrderItems(Class<T> target, List<String> sort) {
        if (CollUtil.isEmpty(sort)) {
            return null;
        }
        List<OrderItem> orderItems = Lists.newArrayList();
        if (StrUtil.equals(ASC, sort.get(sort.size() - 1))) {
            addOrderItems(target, sort, orderItems, ASC);
        } else if (StrUtil.equalsIgnoreCase(DESC, sort.get(sort.size() - 1))) {
            addOrderItems(target, sort, orderItems, DESC);
        } else {
            ExceptionUtil.rethrowClientSideException(CommonErrorCodeEnum.SORT_ERROR.getMessage());
        }
        return orderItems;
    }

    private static <T> void addOrderItems(Class<T> target, List<String> sort, List<OrderItem> orderItems, String lastOrder) {
        int anotherOrderIndex = -1;
        if (StrUtil.equals(ASC, lastOrder)) {
            anotherOrderIndex = sort.indexOf(DESC);
        } else {
            anotherOrderIndex = sort.indexOf(ASC);
        }

        if (anotherOrderIndex == 0 || anotherOrderIndex == sort.size() - 2) {
            ExceptionUtil.rethrowClientSideException(CommonErrorCodeEnum.SORT_ERROR.getMessage());
        } else if (anotherOrderIndex > 2 || sort.size() - anotherOrderIndex > 4) {
            ExceptionUtil.rethrowClientSideException(CommonErrorCodeEnum.SORT_LIMIT.getMessage());
        }
        if (anotherOrderIndex > 0) {
            for (int i = 0; i < anotherOrderIndex; i++) {
                if (StrUtil.equals(ASC, lastOrder)) {
                    orderItems.add(OrderItem.desc(getColumnName(target, sort.get(i))));
                } else {
                    orderItems.add(OrderItem.asc(getColumnName(target, sort.get(i))));
                }
            }
        }

        for (int j = anotherOrderIndex + 1; j < sort.size() - 1; j++) {
            if (StrUtil.equals(ASC, lastOrder)) {
                orderItems.add(OrderItem.asc(getColumnName(target, sort.get(j))));
            } else {
                orderItems.add(OrderItem.desc(getColumnName(target, sort.get(j))));
            }
        }
    }

}
