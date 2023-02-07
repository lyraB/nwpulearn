package com.nwpu.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nwpu.content.model.dto.CourseCategoryTreeDto;
import com.nwpu.content.model.po.CourseCategory;

import java.util.List;

/**
 * <p>
 * 课程分类 Mapper 接口
 * </p>
 *
 * @author yfh
 */
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {
    public List<CourseCategoryTreeDto> selectTreeNodes(String id);
}
