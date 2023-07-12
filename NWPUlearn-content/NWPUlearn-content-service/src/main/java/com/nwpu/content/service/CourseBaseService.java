package com.nwpu.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nwpu.base.model.PageParams;
import com.nwpu.base.model.PageResult;
import com.nwpu.content.model.dto.AddCourseDto;
import com.nwpu.content.model.dto.CourseBaseInfoDto;
import com.nwpu.content.model.dto.EditCourseDto;
import com.nwpu.content.model.dto.QueryCourseParamsDto;
import com.nwpu.content.model.po.CourseBase;

/**
 * <p>
 * 课程基本信息 服务类
 * </p>
 *
 * @author yfh
 * @since 2023-01-31
 */
public interface CourseBaseService extends IService<CourseBase> {
    /**
     * @description 课程查询接口
     * @param pageParams 分页参数
     * @param queryCourseParamsDto 课程查找条件
     * @return com.xuecheng.base.model.PageResult<com.xuecheng.content.model.po.CourseBase> 分页后的查找结果
     * @author yfh
     */
    PageResult<CourseBase> queryCourseBaseList(PageParams pageParams,
                                               QueryCourseParamsDto queryCourseParamsDto);

    /**
     * @description 根据id查询课程基本信息
     * @param courseId 课程id
     * @return com.xuecheng.content.model.dto.CourseBaseInfoDto
     * @author yfh
     */
    CourseBaseInfoDto getCourseBaseInfo(long courseId);

    /**
     * @description 课程添加接口
     * @param companyId 机构id
     * @param addCourseDto 添加的课程基本信息
     * @return CourseBaseInfoDto 前端需要响应信息
     * @author yfh
     */
    CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);


    /**
     * @description 修改课程信息
     * @param companyId 机构id
     * @param dto 课程信息
     * @return com.xuecheng.content.model.dto.CourseBaseInfoDto
     * @author yfh
     */
    CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto dto);


    /**
     * @description 删除课程
     * @param courseId 课程id
     * @author yfh
     */
    void deleteCourse(Long courseId);
}
