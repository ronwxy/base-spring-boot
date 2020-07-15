package cn.jboost.springboot.autoconfig.mybatisplus;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

/***
 * 需要转换成DTO类型bean的base controller
 *
 * T: 实体类型
 * D: DTO类型
 * Q：criteria查询类型
 *
 * @Author ronwxy
 * @Date 2020/5/19 14:28
 * @Version 1.0
 */
public abstract class BaseController<T, D extends Serializable, Q> {

    @Autowired
    protected BaseService<T, D, Q> baseService;

    protected final Class<T> entityType;

    public BaseController() {
        this.entityType = (Class<T>) (((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    @ApiOperation(value = "新增")
    @PostMapping
    public D create(@Validated @RequestBody D d) {
        return baseService.create(d);
    }

    @ApiOperation(value = "修改")
    @PutMapping
    public void update(@Validated @RequestBody D d) {
        baseService.update(d);
    }

    @ApiOperation(value = "根据ID查询")
    @GetMapping("{id}")
    public D findById(@PathVariable("id") Serializable id) {
        return baseService.findById(id);
    }


    @ApiOperation(value = "根据条件分页查询")
    @GetMapping
    public IPage<D> findByCriteria(@ModelAttribute Q query, Page page) {
        return baseService.findByCriteria(query, page);
    }

    @ApiOperation(value = "根据ID删除")
    @DeleteMapping("{id}")
    public void deleteById(@PathVariable("id") Serializable id) {
        baseService.deleteById(id);
    }

    @ApiOperation(value = "根据ID列表批量删除")
    @DeleteMapping("batch")
    public void deleteByIds(@RequestParam("ids") Collection<? extends Serializable> ids) {
        baseService.deleteByIds(ids);
    }
}
