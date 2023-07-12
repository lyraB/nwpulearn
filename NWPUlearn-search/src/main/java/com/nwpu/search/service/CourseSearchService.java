package com.nwpu.search.service;

import com.nwpu.base.model.PageParams;
import com.nwpu.search.dto.SearchCourseParamDto;
import com.nwpu.search.dto.SearchPageResultDto;
import com.nwpu.search.po.CourseIndex;

/**
 * @description 课程搜索service
 * @author yfh
 * @version 1.0
 */
public interface CourseSearchService {


    /**
     * @description 搜索课程列表
     * @param pageParams 分页参数
     * @param searchCourseParamDto 搜索条件
     * @return com.nwpu.base.model.PageResult<com.nwpu.search.po.CourseIndex> 课程列表
     * @author yfh
    */
    SearchPageResultDto<CourseIndex> queryCoursePubIndex(PageParams pageParams, SearchCourseParamDto searchCourseParamDto);

 }
