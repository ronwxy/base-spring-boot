package com.springboot.boot.tkmapper.service;

import com.springboot.boot.tkmapper.QueryResult;
import com.springboot.boot.tkmapper.mapper.BaseMapper;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Collection;
import java.util.List;

@Service
public interface IService<PK, T> {

	T selectByPk(PK pk);

	List<T> selectByPks(Collection<PK> pks);

	int countByExample(Example example);

	/**
	 * find one from the result,if more than one,obtain the first row;
	 *
	 * @param example
	 * @return the first one from the result
	 */
	T selectOneByExample(Example example);

	/**
	 * find the unique one from the result,if more than one rows,throw {@link org.springframework.dao.IncorrectResultSizeDataAccessException},
	 * this method is used for unique condition from the dataset;
	 *
	 * @param example
	 * @return the unique result
	 * @throws org.springframework.dao.IncorrectResultSizeDataAccessException
	 * @see BaseMapper#selectOneByExample(Object)
	 */
	T selectUniqueByExample(Example example);

	List<T> selectByExample(Example example);

	QueryResult<T> paginateByExample(Example example, int page, int row);

	List<T> selectByExampleAndRowBounds(Example example, RowBounds rowBounds);

	QueryResult<T> paginateByExampleAndRowBounds(Example example,
												 RowBounds rowBounds);

	T create(T entity);

	T update(T entity);

	/**
	 * 根据主键更新属性不为null的值
	 *
	 * @param entity
	 * @return
	 */
	T updateSelective(T entity);

	int updateByExample(T entity, Example example);

	int updateByExampleSelective(T entity, Example example);

	void deleteByPk(PK pk);

	int deleteByPks(Collection<PK> pks);

	int deleteByExample(Example example);

}
