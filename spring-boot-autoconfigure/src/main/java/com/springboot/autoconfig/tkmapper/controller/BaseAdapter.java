package com.springboot.autoconfig.tkmapper.controller;

import com.springboot.autoconfig.tkmapper.domain.BaseDomain;

import java.io.Serializable;
import java.util.List;

/**
 * domain bean 与 dto bean之间的转换
 * 子接口继承该接口，并加 @Mapper 注解，如：
 * @Mapper(componentModel = BaseAdapter.MAPSTRUCT_COMPONENT_MODEL_SPRING)
 * public interface DemoAdapter extends BaseAdapter<Demo, DemoDto>
 * @param <T> domain bean type  必须继承BaseDomain
 * @param <D> dto bean type Serializable
 */
public interface BaseAdapter<T extends BaseDomain, D extends Serializable> {
    String MAPSTRUCT_COMPONENT_MODEL_SPRING = "spring";

    D entityToDto(T entity);

    T dtoToEntity(D dto);

    List<D> entityToDtoList(List<T> entities);
}
