package com.nwpu.content.controller;

import com.nwpu.content.model.dto.CoursePreviewDto;
import com.nwpu.content.service.CourseBaseService;
import com.nwpu.content.service.CoursePublishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author yfh
 * @version 1.0
 * @description 课程预览
 * @date 2023/5/30
 */
@Api(value = "课程预览发布接口",tags = "课程预览发布接口")
@RestController
public class CoursePublishController {
    @Autowired
    CoursePublishService coursePublishService;


    @GetMapping("/coursepreview/{courseId}")
    public ModelAndView preview(@PathVariable("courseId") Long courseId){

        //获取课程预览信息
        CoursePreviewDto coursePreviewInfo = coursePublishService.getCoursePreviewInfo(courseId);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("model",coursePreviewInfo);
        modelAndView.setViewName("course_template");
        return modelAndView;

    }
    @ApiOperation("视频播放界面课程信息预览接口")
    @GetMapping("/open/course/whole/{courseId}")
    public CoursePreviewDto getPreviewInfo(@PathVariable("courseId") Long courseId) {
        //获取课程预览信息
        CoursePreviewDto coursePreviewInfo = coursePublishService.getCoursePreviewInfo(courseId);
        return coursePreviewInfo;
    }
    @ApiOperation("课程审核")
    @PostMapping("/courseaudit/commit/{courseId}")
    public void commitAudit(@PathVariable("courseId") Long courseId){
        Long companyId = 1232141425L;
        coursePublishService.commitAudit(companyId,courseId);

    }

    @ApiOperation("课程发布")
    @PostMapping ("/coursepublish/{courseId}")
    public void coursepublish(@PathVariable("courseId") Long courseId){
        Long companyId = 1232141425L;
        coursePublishService.publish(companyId,courseId);
    }



}
