package com.nwpu;

import com.nwpu.base.model.PageParams;
import com.nwpu.base.model.PageResult;
import com.nwpu.content.model.dto.CourseCategoryTreeDto;
import com.nwpu.content.model.dto.QueryCourseParamsDto;
import com.nwpu.content.model.po.CourseBase;
import com.nwpu.content.service.CourseBaseInfoService;
import com.nwpu.content.service.CourseCategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author yfh
 * @version 1.0
 * @description
 * @date 2023/2/6
 */
@SpringBootTest
public class ContentServiceTests {
    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    @Autowired
    CourseCategoryService courseCategoryService;

    @Test
    void testCourseBaseGet() {
        PageParams params = new PageParams(1l,10l);
        QueryCourseParamsDto queryCourseParamsDto = new QueryCourseParamsDto();
        queryCourseParamsDto.setCourseName("spring");
        PageResult<CourseBase> result = courseBaseInfoService.queryCourseBaseList(params, queryCourseParamsDto);
        System.out.println(result);
    }

    @Test
    void testqueryTreeNodes() {
        List<CourseCategoryTreeDto> categoryTreeDtos =
                courseCategoryService.queryTreeNodes("1");
        System.out.println(categoryTreeDtos);
    }


}
