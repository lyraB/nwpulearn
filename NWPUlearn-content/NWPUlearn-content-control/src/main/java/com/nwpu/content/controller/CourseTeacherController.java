package com.nwpu.content.controller;

import com.nwpu.base.exception.ValidationGroups;
import com.nwpu.content.model.dto.CourseTeacherDto;
import com.nwpu.content.model.po.CourseTeacher;
import com.nwpu.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author yfh
 * @version 1.0
 * @description
 * @date 2023/2/14
 */
@Api(value = "课程教师接口",tags = "课程教师接口")
@RestController
public class CourseTeacherController {
    @Autowired
    CourseTeacherService courseTeacherService;

    @ApiOperation("查询课程教师")
    @ApiImplicitParam(value = "courseId",name = "课程Id",required = true,dataType
            = "Long",paramType = "path")
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> getCourseTeachers(@PathVariable Long courseId) {
        return courseTeacherService.getCourseTeachers(courseId);
    }

    @ApiOperation("添加和修改课程教师")
    @PostMapping("/courseTeacher")
    public CourseTeacher saveCourseTeachers(@RequestBody @Validated CourseTeacherDto dto) {
        return courseTeacherService.saveCourseTeachers(dto);
    }

    @ApiOperation("删除课程教师")
    @DeleteMapping("/courseTeacher/{courseId}/{id}")
    public void deleteCourseTeacher(@PathVariable Long courseId, @PathVariable Long id) {
        courseTeacherService.deleteCourseTeacher(courseId,id);
    }

}
