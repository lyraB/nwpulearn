package com.nwpu.content.service.imple;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nwpu.base.exception.NwpuException;
import com.nwpu.content.mapper.CourseTeacherMapper;
import com.nwpu.content.model.dto.CourseTeacherDto;
import com.nwpu.content.model.po.CourseTeacher;
import com.nwpu.content.service.CourseTeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程-教师关系表 服务实现类
 * </p>
 *
 * @author yfh
 */
@Slf4j
@Service
public class CourseTeacherServiceImpl extends ServiceImpl<CourseTeacherMapper, CourseTeacher> implements CourseTeacherService {
    @Autowired
    CourseTeacherMapper courseTeacherMapper;
    @Override
    public List<CourseTeacher> getCourseTeachers(Long courseId) {
        Map<String, Object> map = new HashMap<>();
        map.put("course_id", courseId);
        return courseTeacherMapper.selectByMap(map);
    }

    @Transactional
    @Override
    public CourseTeacher saveCourseTeachers(CourseTeacherDto dto) {
        CourseTeacher newTeacher;
        if (dto.getId()==null) { // 为新增
            newTeacher = new CourseTeacher();
            BeanUtils.copyProperties(dto, newTeacher);
            courseTeacherMapper.insert(newTeacher);
        } else {
            newTeacher = courseTeacherMapper.selectById(dto.getId());
            BeanUtils.copyProperties(dto, newTeacher);
            courseTeacherMapper.updateById(newTeacher);
        }
        return newTeacher;
    }
    @Transactional
    @Override
    public void deleteCourseTeacher(Long courseId, Long id) {
        if (courseId==null||id==null)
            NwpuException.cast("课程id和id不能为空");
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId,courseId);
        queryWrapper.eq(CourseTeacher::getId,id);
        CourseTeacher teacher = courseTeacherMapper.selectOne(queryWrapper);
        if (teacher == null)
            NwpuException.cast("该教师不存在");
        courseTeacherMapper.deleteById(teacher.getId());
    }
}
