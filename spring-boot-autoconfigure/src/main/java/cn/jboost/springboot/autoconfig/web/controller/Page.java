package cn.jboost.springboot.autoconfig.web.controller;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 分页查询参数
 */
@ApiModel
@Data
public class Page {
    @ApiModelProperty("页码 (1..N)")
    private Long page = 1L;

    @ApiModelProperty("每页显示的数目")
    private Integer size = 10;

    @ApiModelProperty("以下列格式定义排序：property1,asc[,property2,desc]。 升序、降序部分最多均只支持两个字段")
    private List<String> sort;
}
