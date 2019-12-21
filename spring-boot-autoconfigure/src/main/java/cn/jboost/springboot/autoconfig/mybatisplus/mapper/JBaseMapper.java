package cn.jboost.springboot.autoconfig.mybatisplus.mapper;

import cn.jboost.springboot.autoconfig.tkmapper.mapper.BaseMapper;

public interface JBaseMapper<T> extends BaseMapper<T> {
    T updateSelective(T entity);
}
