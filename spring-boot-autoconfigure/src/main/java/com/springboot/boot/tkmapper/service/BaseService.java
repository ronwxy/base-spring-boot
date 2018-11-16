package com.springboot.boot.tkmapper.service;

import com.springboot.boot.tkmapper.QueryResult;
import com.springboot.boot.tkmapper.mapper.BaseMapper;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.MyBatisSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * only used for the annotated entity like this:
 * <pre>
 *     {@code
 *     {@literal @}Table(name="t_user")
 *     	public class User extends BaseDomain{
 *
 *     	}
 *     }
 *
 * </pre>
 *
 * @param <PK>
 * @param <T>
 */
public abstract class BaseService<PK, T> implements IService<PK, T> {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	protected final Class<T> domainType;
	protected final Field pkField;

	@Autowired
	protected BaseMapper<T> mapper;

	@SuppressWarnings("unchecked")
	public BaseService() {
		this.domainType = (Class<T>) (((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[1]);
		List<Field> fields = FieldUtils.getFieldsListWithAnnotation(domainType,
				Id.class);
		if (fields.size() != 1) {
			throw new IllegalStateException("Can't inhert BaseService ["
					+ domainType.getTypeName() + "]");
		} else {
			pkField = fields.get(0);
		}

	}

	public Mapper<T> getMapper() {
		return mapper;
	}

	public Class<T> getDomainType() {
		return this.domainType;
	}

	public T selectByPk(PK pk) {
		return mapper.selectByPrimaryKey(pk);
	}

	public List<T> selectByPks(Collection<PK> pks) {
		Example example = new Example(domainType);
		example.createCriteria().andIn(pkField.getName(), pks);
		return mapper.selectByExample(example);
	}

	public int countByExample(Example example) {
		return mapper.selectCountByExample(example);
	}

	public T selectOneByExample(Example example) {
		List<T> data = mapper.selectByExample(example);
		if (data.isEmpty()) {
			return null;
		} else {
			return data.get(0);
		}
	}

	@Override
	public T selectUniqueByExample(Example example) {
		List<T> data = mapper.selectByExample(example);
		if (data.size() != 1)
			throw new IncorrectResultSizeDataAccessException(1, data.size());
		return data.get(0);
	}

	public List<T> selectByExample(Example example) {
		return mapper.selectByExample(example);
	}

	public QueryResult<T> paginateByExample(Example example, int page, int rows) {
		RowBounds rowBounds = new RowBounds((page - 1) * rows, rows);
		return paginateByExampleAndRowBounds(example, rowBounds);
	}

	public List<T> selectByExampleAndRowBounds(Example example,
											   RowBounds rowBounds) {
		return mapper.selectByExampleAndRowBounds(example, rowBounds);
	}

	public QueryResult<T> paginateByExampleAndRowBounds(Example example,
														RowBounds rowBounds) {
		int cnt = mapper.selectCountByExample(example);
		List<T> data;
		if (cnt != 0) {
			data = mapper.selectByExampleAndRowBounds(example, rowBounds);
		} else {
			data = Collections.emptyList();
		}
		return new QueryResult<>(cnt, data);
	}

	public T create(T entity) {
		Object pk = _getPkValue(entity);
		if (pk == null) {
			mapper.insertUseGeneratedKeys(entity);
		} else {
			mapper.insert(entity);
		}
		return entity;
	}

	public T update(T entity) {
		int rows = mapper.updateByPrimaryKey(entity);
		if (rows == 0) {
			throw new DataRetrievalFailureException("No update for ["
					+ domainType.getTypeName() + "], pk:" + _getPkValue(entity));
		}
		return entity;
	}

	public T updateSelective(T entity) {
		int rows = mapper.updateByPrimaryKeySelective(entity);
		if (rows == 0) {
			throw new DataRetrievalFailureException("No update for ["
					+ domainType.getTypeName() + "], pk:" + _getPkValue(entity));
		}
		Object pk = _getPkValue(entity);
		return mapper.selectByPrimaryKey(pk);
	}

	public int updateByExample(T entity, Example example) {
		return mapper.updateByExample(entity, example);
	}

	public int updateByExampleSelective(T entity, Example example) {
		return mapper.updateByExampleSelective(entity, example);
	}

	public void deleteByPk(PK pk) {
		int rows = mapper.deleteByPrimaryKey(pk);
		if (rows == 0) {
			throw new DataRetrievalFailureException("No delete for ["
					+ domainType.getTypeName() + "], pk:" + pk);
		}
	}

	public int deleteByPks(Collection<PK> pks) {
		Example example = new Example(domainType);
		example.createCriteria().andIn(pkField.getName(), pks);
		return mapper.deleteByExample(example);
	}

	public int deleteByExample(Example example) {
		return mapper.deleteByExample(example);
	}

	private Object _getPkValue(T entity) {
		try {
			pkField.setAccessible(true);
			return pkField.get(entity);
		} catch (IllegalAccessException ex) {
			throw new MyBatisSystemException(ex);
		}
	}

}
