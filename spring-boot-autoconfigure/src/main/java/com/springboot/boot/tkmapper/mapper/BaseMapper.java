package com.springboot.boot.tkmapper.mapper;


import tk.mybatis.mapper.common.Mapper;

public interface BaseMapper<T> extends Mapper<T>, PgSqlMapper<T> {
}
