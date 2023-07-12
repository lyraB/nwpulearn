package com.nwpu.search.controller;

import com.nwpu.base.model.PageParams;
import com.nwpu.search.dto.SearchCourseParamDto;
import com.nwpu.search.dto.SearchPageResultDto;
import com.nwpu.search.po.CourseIndex;
import com.nwpu.search.service.CourseSearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @description 课程搜索接口
 * @author yfh
 * @version 1.0
 */
@Api(value = "课程搜索接口",tags = "课程搜索接口")
@RestController
@RequestMapping("/course")
public class CourseSearchController {

 @Autowired
 CourseSearchService courseSearchService;


@ApiOperation("课程搜索列表")
@GetMapping("/list")
 public SearchPageResultDto<CourseIndex> list(PageParams pageParams, SearchCourseParamDto searchCourseParamDto){

    return courseSearchService.queryCoursePubIndex(pageParams,searchCourseParamDto);
   
  }
}
