package cn.jboost.springboot.autoconfig.web.controller;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页查询结果
 */
@ApiModel
@Data
public class PageResult<T> implements Serializable {
    @ApiModelProperty("总页数")
    private Long pages;
    @ApiModelProperty("总条数")
    private Long total;
    @ApiModelProperty("当前返回的数据列表")
    private List<T> data;
}
