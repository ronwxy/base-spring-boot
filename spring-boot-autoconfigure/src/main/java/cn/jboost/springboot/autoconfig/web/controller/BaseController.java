package cn.jboost.springboot.autoconfig.web.controller;

import cn.jboost.springboot.autoconfig.tkmapper.domain.BaseDomain;
import cn.jboost.springboot.autoconfig.tkmapper.service.BaseService;
import cn.jboost.springboot.autoconfig.tkmapper.util.QueryResult;
import com.github.nickvl.xspring.core.log.aop.annotation.LogInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/***
* base controller for common api
* @Author ronwxy
* @Date 2019/6/20 18:11
*/
@LogInfo
public abstract class BaseController<ID extends Serializable, T extends BaseDomain<ID>> {

    protected Class<T> domainClass;

    @Autowired
    protected BaseService<ID, T> baseService;

    public BaseController() {
        Type[] types = ((ParameterizedType) (this.getClass().getGenericSuperclass())).getActualTypeArguments();
        this.domainClass = (Class<T>) types[1];
    }

    @PostMapping
    public T save(@RequestBody T t) {
        return baseService.create(t);
    }

    @PutMapping
    public T update(@RequestBody T t) {
        return baseService.updateSelective(t);
    }

    @GetMapping("{id}")
    public T findById(@PathVariable("id") ID id) {
        return baseService.selectByPk(id);
    }

    @GetMapping("batch")
    public List<T> findByIds(@RequestParam("ids") List<ID> ids) {
        return baseService.selectByPks(ids);
    }

    @GetMapping
    public List<T> listByCondition(@ModelAttribute T t,
                                   @RequestParam(value = "page", defaultValue = "1") Integer page,
                                   @RequestParam(value = "rows", defaultValue = "10") Integer rows) {
        return Optional.ofNullable(baseService.paginateList(t, page, rows)).orElse(Collections.emptyList());
    }

    @GetMapping("result")
    public QueryResult<T> paginateByCondition(@ModelAttribute T t,
                                          @RequestParam(value = "page", defaultValue = "1") Integer page,
                                          @RequestParam(value = "rows", defaultValue = "10") Integer rows) {
        return baseService.paginateQueryResult(t, page, rows);
    }


    @DeleteMapping
    public void delete(@RequestBody T t) {
            baseService.delete(t);
    }

    @DeleteMapping("{id}")
    public void deleteById(@PathVariable("id") ID id) {
            baseService.deleteByPk(id);
    }

    @DeleteMapping("batch")
    public void deleteByIds(@RequestParam("ids") Collection<ID> ids) {
            baseService.deleteByPks(ids);
    }
}
