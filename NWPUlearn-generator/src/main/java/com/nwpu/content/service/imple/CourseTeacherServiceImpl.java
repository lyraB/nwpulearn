package com.nwpu.content.service.imple;

import com.nwpu.content.model.po.CourseTeacher;
import com.nwpu.content.mapper.CourseTeacherMapper;
import com.nwpu.content.service.CourseTeacherService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

}
