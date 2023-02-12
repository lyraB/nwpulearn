package com.nwpu.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nwpu.content.model.dto.SaveTeachplanDto;
import com.nwpu.content.model.dto.TeachplanDto;
import com.nwpu.content.model.po.Teachplan;

import java.util.List;

/**
 * <p>
 * 课程计划 服务类
 * </p>
 *
 * @author yfh
 * @since 2023-01-31
 */
public interface TeachplanService extends IService<Teachplan> {
    /**
     * @description 查询课程计划树型结构
     * @param courseId 课程id
     * @return List<TeachplanDto>
     * @author yfh
     */
    List<TeachplanDto> findTeachplayTree(long courseId);


    /**
     * @description 保存课程计划（新增和修改）
     * @param teachplanDto 课程计划信息
     * @return void
     * @author yfh
     */
    void saveTeachplan(SaveTeachplanDto teachplanDto);

    /**
     * @description 删除课程计划
     * @param id 课程计划id
     * @return void
     * @author yfh
     */
    void deleteTeachplan(Long id);
}
