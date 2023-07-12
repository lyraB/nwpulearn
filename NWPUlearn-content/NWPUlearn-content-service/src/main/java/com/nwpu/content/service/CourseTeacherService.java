package com.nwpu.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nwpu.content.model.dto.CourseTeacherDto;
import com.nwpu.content.model.po.CourseTeacher;

import java.util.List;

/**
 * <p>
 * 课程-教师关系表 服务类
 * </p>
 *
 * @author yfh
 * @since 2023-01-31
 */
public interface CourseTeacherService extends IService<CourseTeacher> {

    /**
     * @description 查询课程的教师
     * @param courseId 课程id
     * @return List<CourseTeacher>
     * @author yfh
     */
    List<CourseTeacher> getCourseTeachers(Long courseId);

    /**
     * @description 保存课程的教师
     * @param dto 课程教师保存dto
     * @return List<CourseTeacher>
     * @author yfh
     */
    CourseTeacher saveCourseTeachers(CourseTeacherDto dto);

    /**
     * @description 保存课程的教师
     * @param courseId 课程id
     * @param id 课程教师表主键id
     * @author yfh
     */
    void deleteCourseTeacher(Long courseId, Long id);
}
