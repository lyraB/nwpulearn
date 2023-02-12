package com.nwpu.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yfh
 * @version 1.0
 * @description 修改课程dto
 * @date 2023/2/9
 */
@Data
@ApiModel(value="EditCourseDto", description="修改课程基本信息")
public class EditCourseDto extends AddCourseDto{
    @ApiModelProperty(value = "课程名称", required = true)
    private Long id;

}
