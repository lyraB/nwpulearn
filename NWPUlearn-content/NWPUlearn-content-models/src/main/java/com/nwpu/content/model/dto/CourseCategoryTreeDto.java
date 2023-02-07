package com.nwpu.content.model.dto;

import com.nwpu.content.model.po.CourseCategory;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author yfh
 * @version 1.0
 * @description 课程类别树节点
 * @date 2023/2/5
 */
@Data
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {
    List childrenTreeNodes; // 子节点列表
}
