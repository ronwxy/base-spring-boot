package cn.jboost.springboot.autoconfig.web.controller;

import java.io.Serializable;
import java.util.Collection;

/**
 * bean 与 bean之间的转换
 * 子接口继承该接口，并加 @Mapper 注解，如：
 * @Mapper(componentModel = BaseAdapter.MAPSTRUCT_COMPONENT_MODEL_SPRING)
 * public interface DemoAdapter extends BaseAdapter<Demo, DemoDto>
 * @param <T> bean type  Serializable
 * @param <D> bean type Serializable
 */
public interface BaseAdapter<T extends Serializable, D extends Serializable> {
    String MAPSTRUCT_COMPONENT_MODEL_SPRING = "spring";

    D entityToDto(T entity);

    T dtoToEntity(D dto);

    Collection<D> entityToDto(Collection<T> entities);
}
