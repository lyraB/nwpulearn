package com.nwpu.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nwpu.content.model.dto.BindTeachplanMediaDto;
import com.nwpu.content.model.dto.SaveTeachplanDto;
import com.nwpu.content.model.dto.TeachplanDto;
import com.nwpu.content.model.po.Teachplan;
import com.nwpu.content.model.po.TeachplanMedia;
import org.springframework.web.bind.annotation.PathVariable;

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
    List<TeachplanDto> findTeachplanTree(long courseId);


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

    /**
     * @description 修改课程计划顺序
     * @param id 课程计划id
     * @return void
     * @author yfh
     */
    void moveTeachplan(boolean up, Long id);

    /**
     * @description 教学计划绑定媒资
     * @param bindTeachplanMediaDto
     * @return com.nwpu.content.model.po.TeachplanMedia
     * @author yfh
     */
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);

    /**
     * @description 教学计划绑定媒资
     * @param teachPlanId
     * @author yfh
     */
    public void associationMedia(Long teachPlanId, String mediaId);
}
