package com.nwpu.content.model.dto;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author yfh
 * @version 1.0
 * @description 课程教师修改相关dto
 * @date 2023/2/14
 */
@Data
@ToString
public class CourseTeacherDto{
    private Long id;

    /**
     * 课程标识
     */
    @NotNull
    private Long courseId;

    /**
     * 教师姓名
     */
    @NotNull
    private String teacherName;

    /**
     * 教师职位
     */
    private String position;

    /**
     * 教师简介
     */
    private String introduction;

    /**
     * 照片
     */
    private String photograph;
}
