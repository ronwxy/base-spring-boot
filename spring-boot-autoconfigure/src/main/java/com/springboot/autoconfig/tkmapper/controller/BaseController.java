package com.springboot.autoconfig.tkmapper.controller;

import com.springboot.autoconfig.tkmapper.domain.Auditable;
import com.springboot.autoconfig.tkmapper.domain.BaseDomain;
import com.springboot.autoconfig.tkmapper.domain.LogicalDeletable;
import com.springboot.autoconfig.tkmapper.service.BaseService;
import com.springboot.autoconfig.web.UserParamUtil;
import com.springboot.common.web.QueryResultDto;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;


public abstract class BaseController<ID extends Serializable, T extends BaseDomain<ID>, R extends Serializable> {

    protected Class<T> domainClass;
    protected Class<R> dtoClass;
//    @Autowired
//    protected DtoConverter<T, R> converter;
    @Autowired
    protected BaseAdapter<T, R> beanAdapter;
    @Autowired
    protected BaseService<ID, T> baseService;

    @SuppressWarnings("unchecked")
    public BaseController() {
        Type[] types = ((ParameterizedType) (this.getClass().getGenericSuperclass())).getActualTypeArguments();
        this.domainClass = (Class<T>) types[1];
        this.dtoClass = (Class<R>) types[2];
    }

    @PostMapping
    public R save(@RequestBody T t) {
        if (Auditable.class.isAssignableFrom(domainClass)) {
            Auditable au = (Auditable) t;
            au.setCreateTime(new Date());
            au.setOperatorId(UserParamUtil.currentUserId());
        }
        return beanAdapter.entityToDto(baseService.create(t));
    }

    @PutMapping
    public R update(@RequestBody T t) {
        if (Auditable.class.isAssignableFrom(domainClass)) {
            Auditable au = (Auditable) t;
            au.setUpdateTime(new Date());
            au.setOperatorId(UserParamUtil.currentUserId());
        }
        return beanAdapter.entityToDto(baseService.updateSelective(t));
    }

    @GetMapping("{id}")
    public R findById(@PathVariable("id") ID id) {
        return beanAdapter.entityToDto(baseService.selectByPk(id));
    }

    @GetMapping("batch")
    public List<R> findByIds(@RequestParam("ids") List<ID> ids) {
        return baseService.selectByPks(ids).stream().map(x -> beanAdapter.entityToDto(x)).collect(Collectors.toList());
    }

    @DeleteMapping
    public void delete(@RequestBody T t) {
        if (Auditable.class.isAssignableFrom(domainClass)) {
            Auditable au = (Auditable) t;
            au.setUpdateTime(new Date());
            au.setOperatorId(UserParamUtil.currentUserId());
        }
        if (LogicalDeletable.class.isAssignableFrom(domainClass)) {
            ((LogicalDeletable) t).setDeleted(true);
            baseService.updateSelective(t);
        } else {
            baseService.delete(t);
        }
    }

    @DeleteMapping("{id}")
    public void deleteById(@PathVariable("id") ID id) {
        if (LogicalDeletable.class.isAssignableFrom(domainClass)) {
            T t = baseService.selectByPk(id);
            ((LogicalDeletable) t).setDeleted(true);
            if (Auditable.class.isAssignableFrom(domainClass)) {
                Auditable au = (Auditable) t;
                au.setUpdateTime(new Date());
                au.setOperatorId(UserParamUtil.currentUserId());
            }
            baseService.updateSelective(t);
        } else {
            baseService.deleteByPk(id);
        }
    }

    @DeleteMapping("batch")
    public void deleteByIds(@RequestParam("ids") Collection<ID> ids) {
        if (LogicalDeletable.class.isAssignableFrom(domainClass)) {
            List<T> list = baseService.selectByPks(ids);
            list.stream().map(x -> (LogicalDeletable) x).forEach(x -> x.setDeleted(true));
            if (Auditable.class.isAssignableFrom(domainClass)) {
                list.stream().map(x -> (Auditable) x).forEach(x -> {
                    x.setUpdateTime(new Date());
                    x.setOperatorId(UserParamUtil.currentUserId());
                });
            }
            list.forEach(x -> baseService.updateSelective(x));
        } else {
            baseService.deleteByPks(ids);
        }
    }

    @GetMapping("paged")
    public QueryResultDto<R> pagedList(@ModelAttribute T t,
                                       @RequestParam(value = "page", defaultValue = "1") Integer page,
                                       @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        List<T> tmp = Optional.ofNullable(baseService.getMapper().selectByRowBounds(t, new RowBounds(pageSize * (page - 1), pageSize)))
                .orElse(Collections.emptyList());
        QueryResultDto<R> result = new QueryResultDto<>(tmp.size(), tmp.stream().map(x -> beanAdapter.entityToDto(x)).collect(Collectors.toList()));
        return result;
    }

    @GetMapping("list")
    public List<R> list(@ModelAttribute T t) {
        List<T> tmp = Optional.ofNullable(baseService.getMapper().select(t)).orElse(Collections.emptyList());
        List<R> result = tmp.stream().map(x -> beanAdapter.entityToDto(x)).collect(Collectors.toList());
        return result;
    }

    @GetMapping("one")
    public R selectOne(@ModelAttribute T t) {
        return beanAdapter.entityToDto(baseService.selectOne(t));
    }
}
