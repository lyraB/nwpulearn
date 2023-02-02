package com.nwpu.content.model.dto;
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
public class QueryCourseParamsDto { // 与前端通信使用的数据格式
    //审核状态
    private String auditStatus;
    //课程名称
    private String courseName;
    //发布状态
    private String publishStatus;

}
