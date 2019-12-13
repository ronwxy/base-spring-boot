package cn.jboost.springboot.autoconfig.web.controller;


import cn.jboost.springboot.autoconfig.mybatisplus.service.BaseService;
import cn.jboost.springboot.common.adapter.BaseAdapter;
import cn.jboost.springboot.common.web.Page;
import cn.jboost.springboot.common.web.PageResult;
import cn.jboost.springboot.logging.annotation.LogInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

/***
 * 需要转换成DTO类型bean的base controller
 * @Author ronwxy
 * @Date 2019/6/20 18:11\
 *
 * T: 实体类型
 * D: DTO类型
 * Q：criteria查询类型
 */
@LogInfo
public abstract class BaseController<T, D extends Serializable, Q> {

    @Autowired
    protected BaseAdapter<T, D> baseAdapter;
    @Autowired
    protected BaseService<T, D> baseService;

    protected final Class<T> entityType;

    public BaseController() {
        this.entityType = (Class<T>) (((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    @PostMapping
    public D create(@Validated @RequestBody D d) {
        return baseService.create(d);
    }

    @PutMapping
    public D update(@Validated @RequestBody D d) {
        return baseService.updateById(d);
    }

    @GetMapping("{id}")
    public D findById(@PathVariable("id") Serializable id) {
        return baseService.selectById(id);
    }

    @GetMapping("batch")
    public List<D> findByIds(@RequestParam("ids") List<? extends Serializable> ids) {
        return baseService.selectByIds(ids);
    }

    @GetMapping()
    public List<D> listByCondition(@ModelAttribute Q q, Page page) {
        PageResult<D> result = baseService.pageByCriteria(q, page, false);
        return result.getData();
    }

    @GetMapping("page")
    public PageResult<D> paginateByCondition(@ModelAttribute Q q, Page page) {
        return baseService.pageByCriteria(q, page, true);
    }

    @DeleteMapping("{id}")
    public void deleteById(@PathVariable("id") Serializable id) {
        baseService.deleteById(id);
    }

    @DeleteMapping("batch")
    public void deleteByIds(@RequestParam("ids") Collection<? extends Serializable> ids) {
        baseService.deleteByIds(ids);
    }
}
