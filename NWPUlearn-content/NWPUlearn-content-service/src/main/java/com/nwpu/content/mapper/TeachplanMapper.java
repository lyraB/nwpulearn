package com.nwpu.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nwpu.content.model.dto.TeachplanDto;
import com.nwpu.content.model.po.Teachplan;

import java.util.List;

/**
 * <p>
 * 课程计划 Mapper 接口
 * </p>
 *
 * @author yfh
 */
public interface TeachplanMapper extends BaseMapper<Teachplan> {
    /**
     * @description 查询某课程的课程计划，组成树型结构
     * @param courseId
     * @return com.nwpu.content.model.dto.TeachplanDto
     * @author yfh
     */
    List<TeachplanDto> selectTreeNodes(long courseId);
}
