package com.nwpu.content.service.imple;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nwpu.base.exception.NwpuException;
import com.nwpu.content.mapper.TeachplanMapper;
import com.nwpu.content.mapper.TeachplanMediaMapper;
import com.nwpu.content.model.dto.SaveTeachplanDto;
import com.nwpu.content.model.dto.TeachplanDto;
import com.nwpu.content.model.po.Teachplan;
import com.nwpu.content.model.po.TeachplanMedia;
import com.nwpu.content.service.TeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 课程计划 服务实现类
 * </p>
 *
 * @author yfh
 */
@Slf4j
@Service
public class TeachplanServiceImpl extends ServiceImpl<TeachplanMapper, Teachplan> implements TeachplanService {
    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;
    /**
     * @description 查询课程计划树型结构
     * @param courseId 课程id
     * @return List<TeachplanDto>
     * @author yfh
     */
    @Override
    public List<TeachplanDto> findTeachplayTree(long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    /**
     * @description 保存课程计划（新增和修改）
     * @param teachplanDto 课程计划信息
     * @return void
     * @author yfh
     */
    @Transactional
    @Override
    public void saveTeachplan(SaveTeachplanDto teachplanDto) {
        //课程计划id
        Long id = teachplanDto.getId();
        if (id != null) {//修改课程计划
            Teachplan teachplan = teachplanMapper.selectById(id);
            BeanUtils.copyProperties(teachplanDto, teachplan);
            teachplanMapper.updateById(teachplan);
        } else {//新增计划
            //取出同父节点的小节数量
            int count = getTeachplanCount(teachplanDto.getCourseId(), teachplanDto.getParentid());
            Teachplan teachplanNew = new Teachplan();
            //设置排序号
            teachplanNew.setOrderby(count + 1);
            BeanUtils.copyProperties(teachplanDto, teachplanNew);
            teachplanMapper.insert(teachplanNew);
        }
    }

    /**
     * @description 删除课程计划
     * @param id 课程计划id
     * @return void
     * @author yfh
     */
    @Override
    public void deleteTeachplan(Long id) {
        Teachplan plan = teachplanMapper.selectById(id);
        if (plan==null)
            NwpuException.cast("课程计划为空，无法删除");
        doDeletePlan(plan);
    }
    @Transactional
    private void doDeletePlan(Teachplan plan){
        if(plan.getGrade().equals(1)) { // 如果是一级删除下方所有小节（只会有章、节两个级别）
            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Teachplan::getParentid,plan.getId());
            List<Teachplan> teachplanList = teachplanMapper.selectList(queryWrapper);
            teachplanList.forEach(this::doDeletePlan);
        } else { // 如果是二级删除管理的媒体文件（一级不会有媒体文件)
            LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TeachplanMedia::getTeachplanId, plan.getId());
            teachplanMediaMapper.delete(queryWrapper);
        }
        teachplanMapper.deleteById(plan.getId());
    }
    /**
     * @description 获取最新的排序号
     * @param courseId 课程id
     * @param parentId 父课程计划id
     * @return int 最新排序号
     * @author yfh
     */
    private int getTeachplanCount(long courseId,long parentId){
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        // 得到所有同课程同一大章下有多少小节
        queryWrapper.eq(Teachplan::getCourseId,courseId);
        queryWrapper.eq(Teachplan::getParentid,parentId);
        return teachplanMapper.selectCount(queryWrapper);
    }
}
