package com.nwpu.content.model.dto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
/**
/**
 * @description 课程查询参数Dto
 * @author yfh
 * @version 1.0
 */
@Data
@ToString
@ApiModel("课程查询类")
public class QueryCourseParamsDto { // 与前端通信使用的数据格式
    @ApiModelProperty("审核状态") //用于生成api文档，描述对象字段
    private String auditStatus;
    @ApiModelProperty("课程名称")
    private String courseName;
    @ApiModelProperty("发布状态")
    private String publishStatus;

}
