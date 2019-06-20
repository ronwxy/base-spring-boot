package cn.jboost.springboot.autoconfig.tkmapper.service;//package cn.jboost.springboot.parent.service;

import cn.jboost.springboot.autoconfig.tkmapper.mapper.BaseMapper;
import cn.jboost.springboot.autoconfig.tkmapper.util.QueryResult;
import org.apache.ibatis.session.RowBounds;
import tk.mybatis.mapper.entity.Example;

import java.util.Collection;
import java.util.List;

public interface IBaseService<PK, T> {

    /**
    * 获取满足条件的第一条记录
    * @param t
    * @return 
    */
    T selectOne(T t);

    /**
    * 根据主键获取
    * @param
    * @return
    */
    T selectByPk(PK pk);

    /**
    * 根据主键集合获取
    * @param
    * @return
    */
    List<T> selectByPks(Collection<PK> pks);

    /**
    * 获取满足条件的记录条数
    * @param
    * @return
    */
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

    /**
    * 根据Example条件查询
    * @param
    * @return
    */
    List<T> selectListByExample(Example example);

    /**
    * 分页查询
    * @param
    * @return
    */
    List<T> paginateListByExample(Example example, int page, int rows);

    /**
    * 查询列表
    * @param
    * @return
    */
    List<T> selectList(T t);

    /**
    * 分页查询
    * @param
    * @return
    */
    List<T> paginateList(T t, int page, int rows);

    /**
    * 根据Example条件分页查询，返回包含总记录条数
    * @param
    * @return
    */
    QueryResult<T> paginateQueryResultByExample(Example example, int page, int rows);

    /**
    * 分页查询，返回包含总记录条数
    * @param
    * @return
    */
    QueryResult<T> paginateQueryResult(T t, int page, int rows);

    /**
    * 分页查询
    * @param
    * @return
    */
    List<T> selectByExampleAndRowBounds(Example example, RowBounds rowBounds);

    QueryResult<T> paginateByExampleAndRowBounds(Example example,
                                                 RowBounds rowBounds);

    /**
    * 创建
    * @param
    * @return
    */
    T create(T entity);

    /**
    * 批量创建
    * @param
    * @return
    */
    int insertList(List<T> list);

    T update(T entity);

    /**
     * 根据主键更新属性不为null的值
     * @param entity
     * @return
     */
    T updateSelective(T entity);

    /**
     * 更新属性不为null的值
     * @param entity
     * @return
     */
    int updateByExample(T entity, Example example);

    /**
     * 更新属性不为null的值
     * @param entity
     * @return
     */
    int updateByExampleSelective(T entity, Example example);

    void delete(T t);

    void deleteByPk(PK pk);

    int deleteByPks(Collection<PK> pks);

    int deleteByExample(Example example);


}
