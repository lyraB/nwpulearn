package com.nwpu.content.controller;

import com.nwpu.base.exception.ValidationGroups;
import com.nwpu.base.model.PageParams;
import com.nwpu.base.model.PageResult;
import com.nwpu.content.model.dto.AddCourseDto;
import com.nwpu.content.model.dto.CourseBaseInfoDto;
import com.nwpu.content.model.dto.EditCourseDto;
import com.nwpu.content.model.dto.QueryCourseParamsDto;
import com.nwpu.content.model.po.CourseBase;
import com.nwpu.content.service.CourseBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @description 课程信息编辑接口
 * @author yfh
 * @version 1.0
 */
@Api(value = "课程信息编辑接口",tags = "课程信息编辑接口")
@RestController
public class CourseBaseInfoController {
    @Autowired
    CourseBaseService courseBaseInfoService;


    @ApiOperation("课程查询接口")
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody
    QueryCourseParamsDto queryCourseParams){ // 前端传输过来的数据，通过json传入转成QCPD形式
        return courseBaseInfoService.
                queryCourseBaseList(pageParams, queryCourseParams);
    }

    @ApiOperation("新增课程基础信息")
    @PostMapping("/course")
    public CourseBaseInfoDto createCourseBase(@RequestBody @Validated({ValidationGroups.Inster.class}) AddCourseDto addCourseDto){
        return courseBaseInfoService.createCourseBase(1232141425L,addCourseDto);
    }

    @ApiOperation("根据课程id查询课程基础信息")
    @GetMapping("/course/{courseId}")
    public CourseBaseInfoDto getCourseBaseById(@PathVariable Long courseId){
        return courseBaseInfoService.getCourseBaseInfo(courseId);
    }

    @ApiOperation("修改课程基础信息")
    @PutMapping("/course")
    public CourseBaseInfoDto modifyCourseBase(@RequestBody @Validated({ValidationGroups.Update.class})
                                                  EditCourseDto editCourseDto){
        Long companyId = 1232141425L;
        return courseBaseInfoService.updateCourseBase(companyId,editCourseDto);
    }

    @ApiOperation("删除课程")
    @DeleteMapping("/course/{courseId}")
    public void deleteCourse(@PathVariable Long courseId) {
        courseBaseInfoService.deleteCourse(courseId);
    }

}
