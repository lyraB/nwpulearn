package com.nwpu.content.controller;

import com.nwpu.base.model.PageParams;
import com.nwpu.base.model.PageResult;
import com.nwpu.content.model.dto.QueryCourseParamsDto;
import com.nwpu.content.model.po.CourseBase;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description 课程信息编辑接口
 * @author Mr.M
 * @date 2022/9/6 11:29
 * @version 1.0
 */
@Api(value = "课程信息编辑接口",tags = "课程信息编辑接口")
@RestController
public class CourseBaseInfoController {
    @ApiOperation("课程查询接口")
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody
    QueryCourseParamsDto queryCourseParams){ // 前端传输过来的数据，通过json传入转成QCPD形式
        return null;
    }
}
