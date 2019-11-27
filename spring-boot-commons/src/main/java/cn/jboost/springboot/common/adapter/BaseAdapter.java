package cn.jboost.springboot.common.adapter;

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

    /**
     * entity 转 DTO
     * @param entity
     * @return
     */
    D toDTO(T entity);

    /**
     * DTO 转 entity
     * @param dto
     * @return
     */
    T toEntity(D dto);

    /**
     * entity集合转DTO集合
     * @param entities
     * @return
     */
    Collection<D> toDTO(Collection<T> entities);

    /**
     * DTO集合转entity集合
     * @param dtos
     * @return
     */
    Collection<T> toEntity(Collection<D> dtos);
}
