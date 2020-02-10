package cn.jboost.springboot.autoconfig.mybatisplus.service;//package cn.jboost.springboot.parent.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.jboost.springboot.autoconfig.mybatisplus.MyBatisPlusQueryHelper;
import cn.jboost.springboot.autoconfig.mybatisplus.TimeSqlInterceptor;
import cn.jboost.springboot.common.adapter.BaseAdapter;
import cn.jboost.springboot.common.exception.ExceptionUtil;
import cn.jboost.springboot.common.web.PageResult;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * service 基类，继承该类即获得基本的curd功能
 *
 * @param <T, D> entity类型, DTO类型
 */
public abstract class BaseService<T, D extends Serializable> {

    @Autowired
    protected BaseMapper<T> mapper;

    @Autowired
    private BaseAdapter<T, D> baseAdapter;

    protected final Class<T> entityType;

    public BaseService() {
        this.entityType = (Class<T>) (((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    /**
     * 新增
     *
     * @param dto
     * @return
     */
    public D create(D dto) {
        T entity = baseAdapter.toEntity(dto);
        mapper.insert(entity);
        return baseAdapter.toDTO(entity);
    }

    /**
     * 根据ID删除
     *
     * @param id
     * @return
     */
    public int deleteById(Serializable id) {
        return mapper.deleteById(id);
    }

    /**
     * 根据ID集合删除
     *
     * @param ids
     * @return
     */
    public int deleteByIds(Collection<? extends Serializable> ids) {
        return mapper.deleteBatchIds(ids);
    }

    /**
     * 根据字段条件删除
     *
     * @param columnMap
     * @return
     */
    public int deleteByMap(Map<String, Object> columnMap) {
        return mapper.deleteByMap(columnMap);
    }

    /**
     * 根据查询条件删除
     *
     * @param wrapper
     * @return
     */
    public int delete(Wrapper<T> wrapper) {
        return mapper.delete(wrapper);
    }

    /**
     * 根据ID更新
     *
     * @param dto
     * @return
     */
    public D updateById(D dto) {
        if(Objects.isNull(ReflectUtil.getFieldValue(dto, "id"))){
            ExceptionUtil.rethrowClientSideException("id不能为空");
        }
        T entity = baseAdapter.toEntity(dto);
        mapper.updateById(entity);
        return baseAdapter.toDTO(entity);
    }

    /**
     * 根据查询条件更新
     *
     * @param entity
     * @param updateWrapper
     * @return
     */
    public T update(T entity, Wrapper<T> updateWrapper) {
        mapper.update(entity, updateWrapper);
        return entity;
    }

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    public D selectById(Serializable id) {
        return baseAdapter.toDTO(mapper.selectById(id));
    }

    /**
     * 根据ID集合查询
     *
     * @param ids
     * @return
     */
    public List<D> selectByIds(Collection<? extends Serializable> ids) {
        return (List<D>) baseAdapter.toDTO(mapper.selectBatchIds(ids));
    }

    /**
     * 根据字段条件查询
     *
     * @param columnMap
     * @return
     */
    public List<T> selectByMap(Map<String, Object> columnMap) {
        return mapper.selectByMap(columnMap);
    }

    /**
     * 根据查询条件获取单条记录
     *
     * @param query
     * @return
     */
    public T selectOneByWrapper(Wrapper<T> query) {
        return mapper.selectOne(query);
    }

    /**
     * 根据实体对象查询单条记录
     *
     * @param example
     * @return
     */
    public T selectOneByExample(T example) {
        return mapper.selectOne(MyBatisPlusQueryHelper.buildQuery(example));
    }

    public <Q> T selectOneByCriteria(Q queryCriteria) {
        return mapper.selectOne(MyBatisPlusQueryHelper.buildQuery(entityType, queryCriteria));
    }

    /**
     * 根据查询条件查询
     *
     * @param queryWrapper
     * @return
     */
    public List<T> listByWrapper(Wrapper<T> queryWrapper) {
        return mapper.selectList(queryWrapper);
    }

    /**
     * 根据实体对象查询
     *
     * @param example
     * @return
     */
    public List<T> listByExample(T example) {
        return mapper.selectList(MyBatisPlusQueryHelper.buildQuery(example));
    }

    public <Q> List<T> listByCriteria(Q queryCriteria) {
        return mapper.selectList(MyBatisPlusQueryHelper.buildQuery(entityType, queryCriteria));
    }

    /**
     * 根据查询条件查询 Map 列表
     *
     * @param queryWrapper
     * @return
     */
    public List<Map<String, Object>> listMaps(Wrapper<T> queryWrapper) {
        return mapper.selectMaps(queryWrapper);
    }

    /**
     * 根据查询条件查询对象列表
     *
     * @param queryWrapper
     * @return
     */
    public List<Object> listObjs(Wrapper<T> queryWrapper) {
        return mapper.selectObjs(queryWrapper);
    }

    /**
     * 根据查询条件获取记录条数
     *
     * @param queryWrapper
     * @return
     */
    public Integer countByWrapper(Wrapper<T> queryWrapper) {
        return mapper.selectCount(queryWrapper);
    }

    /**
     * 根据实体对象查询记录条数
     *
     * @param example
     * @return
     */
    public Integer countByExample(T example) {
        return mapper.selectCount(MyBatisPlusQueryHelper.buildQuery(example));
    }

    public <Q> Integer countByCriteria(Q queryCriteria) {
        return mapper.selectCount(MyBatisPlusQueryHelper.buildQuery(entityType, queryCriteria));
    }

    /**
     * 分页查询
     *
     * @param page
     * @param queryWrapper
     * @return
     */
    public IPage<T> pageByWrapper(IPage<T> page, Wrapper<T> queryWrapper) {
        return mapper.selectPage(page, queryWrapper);
    }

    /**
     * 根据实体对象分页查询
     *
     * @param page
     * @param example
     * @return
     */
    public IPage<T> pageByExample(IPage<T> page, T example) {
        return mapper.selectPage(page, MyBatisPlusQueryHelper.buildQuery(example));
    }

    /**
     * 根据查询类分页查询
     *
     * @param page
     * @param queryCriteria
     * @param <Q>
     * @return
     */
    public <Q> PageResult<D> pageByCriteria(Q queryCriteria, cn.jboost.springboot.common.web.Page page, boolean searchCount) {
        Page p = MyBatisPlusQueryHelper.buildPage(entityType, page, searchCount);
        return convertPage(mapper.selectPage(p, MyBatisPlusQueryHelper.buildQuery(entityType, queryCriteria)), true);
    }

    /**
     * 分页查询 Map
     *
     * @param page
     * @param queryWrapper
     * @return
     */
    public IPage<Map<String, Object>> pageMapsByWrapper(IPage<T> page, Wrapper<T> queryWrapper) {
        return mapper.selectMapsPage(page, queryWrapper);
    }

    /**
     * 批量操作 SqlSession
     */
    protected SqlSession sqlSessionBatch() {
        return SqlHelper.sqlSessionBatch(entityType);
    }

    /**
     * 获取 SqlStatement
     *
     * @param sqlMethod ignore
     * @return ignore
     */
    protected String sqlStatement(SqlMethod sqlMethod) {
        return SqlHelper.table(entityType).getSqlStatement(sqlMethod.getMethod());
    }

    /**
     * 批量插入
     * @param dtos
     * @param batchSize
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(Collection<D> dtos, int batchSize) {
        List<T> entityList = (List<T>) baseAdapter.toEntity(dtos);
        String sqlStatement = sqlStatement(SqlMethod.INSERT_ONE);
        try (SqlSession batchSqlSession = sqlSessionBatch()) {
            int i = 0;
            for (T anEntityList : entityList) {
                batchSqlSession.insert(sqlStatement, anEntityList);
                if (i >= 1 && i % batchSize == 0) {
                    batchSqlSession.flushStatements();
                }
                i++;
            }
            batchSqlSession.flushStatements();
        }
        return true;
    }

    /**
     * 批量插入或更新
     * @param dtos
     * @param batchSize
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateBatch(Collection<D> dtos, int batchSize) {
        List<T> entityList = (List<T>) baseAdapter.toEntity(dtos);
        Assert.notEmpty(entityList, "error: entityList must not be empty");
        Class<?> cls = entityType;
        TableInfo tableInfo = TableInfoHelper.getTableInfo(cls);
        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
        String keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
        try (SqlSession batchSqlSession = sqlSessionBatch()) {
            int i = 0;
            for (T entity : entityList) {
                Object idVal = ReflectionKit.getMethodValue(cls, entity, keyProperty);
                if (StringUtils.checkValNull(idVal) || Objects.isNull(selectById((Serializable) idVal))) {
                    batchSqlSession.insert(sqlStatement(SqlMethod.INSERT_ONE), entity);
                } else {
                    MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
                    param.put(TimeSqlInterceptor.PARAM_NAME, entity);
                    batchSqlSession.update(sqlStatement(SqlMethod.UPDATE_BY_ID), param);
                }
                if (i >= 1 && i % batchSize == 0) {
                    batchSqlSession.flushStatements();
                }
                i++;
            }
            batchSqlSession.flushStatements();
        }
        return true;
    }

    protected PageResult convertPage(IPage page, boolean dataConvert) {
        if (ObjectUtil.isNull(page)) {
            return null;
        }
        PageResult result = new PageResult<>();
        result.setPages(page.getPages());
        result.setTotal(page.getTotal());
        if (dataConvert) {
            result.setData((List) baseAdapter.toDTO(page.getRecords()));
        } else {
            result.setData(page.getRecords());
        }
        return result;
    }

}
