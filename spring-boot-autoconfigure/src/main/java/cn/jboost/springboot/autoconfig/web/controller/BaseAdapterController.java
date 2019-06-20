package cn.jboost.springboot.autoconfig.web.controller;

import cn.jboost.springboot.autoconfig.tkmapper.domain.BaseDomain;
import cn.jboost.springboot.autoconfig.tkmapper.service.BaseService;
import cn.jboost.springboot.autoconfig.tkmapper.util.QueryResult;
import cn.jboost.springboot.common.web.QueryResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/***
* 需要转换成DTO类型bean的base controller
* @Author ronwxy
* @Date 2019/6/20 18:11
*/
public abstract class BaseAdapterController<ID extends Serializable, T extends BaseDomain<ID>, R extends Serializable> {

    protected Class<T> domainClass;
    protected Class<R> dtoClass;

    @Autowired
    protected BaseAdapter<T, R> beanAdapter;
    @Autowired
    protected BaseService<ID, T> baseService;

    public BaseAdapterController() {
        Type[] types = ((ParameterizedType) (this.getClass().getGenericSuperclass())).getActualTypeArguments();
        this.domainClass = (Class<T>) types[1];
        this.dtoClass = (Class<R>) types[2];
    }

    @PostMapping
    public R save(@RequestBody T t) {
        return beanAdapter.entityToDto(baseService.create(t));
    }

    @PutMapping
    public R update(@RequestBody T t) {
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

    @GetMapping()
    public List<R> listByCondition(@ModelAttribute T t,
                                   @RequestParam(value = "page", defaultValue = "1") Integer page,
                                   @RequestParam(value = "rows", defaultValue = "10") Integer rows) {
        List<T> tmp = Optional.ofNullable(baseService.paginateList(t, page, rows)).orElse(Collections.emptyList());
        List<R> result = tmp.stream().map(x -> beanAdapter.entityToDto(x)).collect(Collectors.toList());
        return result;
    }

    @GetMapping("paginate")
    public QueryResultDto<R> paginateByCondition(@ModelAttribute T t,
                                          @RequestParam(value = "page", defaultValue = "1") Integer page,
                                          @RequestParam(value = "rows", defaultValue = "10") Integer rows) {
        QueryResult<T> tmp = baseService.paginateQueryResult(t, page, rows);
        QueryResultDto<R> result = new QueryResultDto<>(tmp.totalRecords, tmp.data.stream().map(x -> beanAdapter.entityToDto(x)).collect(Collectors.toList()));
        return result;
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

    @GetMapping("one")
    public R selectOne(@ModelAttribute T t) {
        return beanAdapter.entityToDto(baseService.selectOne(t));
    }
}
