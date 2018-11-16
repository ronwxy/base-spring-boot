package com.springboot.boot.tkmapper.mapper;


import com.springboot.boot.tkmapper.providers.PgSqlProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import tk.mybatis.mapper.common.special.InsertListMapper;
import tk.mybatis.mapper.common.special.InsertUseGeneratedKeysMapper;

import java.util.List;

public interface PgSqlMapper<T> extends InsertListMapper<T>, InsertUseGeneratedKeysMapper<T> {

	@SelectProvider(type = PgSqlProvider.class, method = "dynamicSQL")
	List<T> selectPage(@Param("entity") T object, @Param("offset") int offset, @Param("limit") int limit);
}
