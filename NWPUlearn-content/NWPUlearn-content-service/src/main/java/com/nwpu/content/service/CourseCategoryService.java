package com.nwpu.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nwpu.content.model.dto.CourseCategoryTreeDto;
import com.nwpu.content.model.po.CourseCategory;

import java.util.List;

/**
 * <p>
 * 课程分类 服务类
 * </p>
 *
 * @author yfh
 * @since 2023-02-05
 */
public interface CourseCategoryService {
    public List<CourseCategoryTreeDto> queryTreeNodes(String id);

}
