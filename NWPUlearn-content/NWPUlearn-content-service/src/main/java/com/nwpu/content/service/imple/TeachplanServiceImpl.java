package com.nwpu.content.service.imple;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nwpu.base.exception.NwpuException;
import com.nwpu.content.mapper.TeachplanMapper;
import com.nwpu.content.mapper.TeachplanMediaMapper;
import com.nwpu.content.model.dto.BindTeachplanMediaDto;
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

import java.time.LocalDateTime;
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
    public List<TeachplanDto> findTeachplanTree(long courseId) {
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
            int count = getMaxOrder(teachplanDto.getParentid());
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
    @Transactional
    @Override
    public void deleteTeachplan(Long id) {
        Teachplan plan = teachplanMapper.selectById(id);
        if (plan==null)
            NwpuException.cast("课程计划为空，无法删除");
        doDeletePlan(plan);
    }
    @Transactional
    @Override
    public void moveTeachplan(boolean up, Long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);
        LambdaQueryWrapper<Teachplan> queryWrapper= new LambdaQueryWrapper<>();
        int order = teachplan.getOrderby();
        int maxOrder = getMaxOrder(teachplan.getParentid());
        if (order!=1 && up){ // 是上移操作，且不是最顶端
            queryWrapper.eq(Teachplan::getParentid, teachplan.getParentid());
            queryWrapper.lt(Teachplan::getOrderby, order);// 找到同一父节点且排序值小于的小节
            queryWrapper.orderByDesc(Teachplan::getOrderby).last("limit 1");// 按排序值降序排列,取第一个
        } else if (order!= maxOrder && !up) {
            queryWrapper.orderByAsc(Teachplan::getOrderby).last("limit 1");// 按排序值升序排列,取第一个
            queryWrapper.eq(Teachplan::getParentid, teachplan.getParentid());
            queryWrapper.gt(Teachplan::getOrderby,order);
        } else {
            return;
        }

        Teachplan planup = teachplanMapper.selectOne(queryWrapper);
        teachplan.setOrderby(planup.getOrderby());
        planup.setOrderby(order);
        teachplanMapper.updateById(planup);
        teachplanMapper.updateById(teachplan);
    }

    @Override
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {
        //教学计划id
        Long teachplanId = bindTeachplanMediaDto.getTeachplanId();
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if(teachplan==null){
            NwpuException.cast("教学计划不存在");
        }
        Integer grade = teachplan.getGrade();
        if(grade!=2){
            NwpuException.cast("只允许第二级教学计划绑定媒资文件");
        }
        //课程id
        Long courseId = teachplan.getCourseId();

        //先删除原来该教学计划绑定的媒资
        teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId,teachplanId));

        //再添加教学计划与媒资的绑定关系
        TeachplanMedia teachplanMedia = new TeachplanMedia();
        teachplanMedia.setCourseId(courseId);
        teachplanMedia.setTeachplanId(teachplanId);
        teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
        teachplanMedia.setMediaId(bindTeachplanMediaDto.getMediaId());
        teachplanMedia.setCreateDate(LocalDateTime.now());
        teachplanMediaMapper.insert(teachplanMedia);
        return teachplanMedia;
    }

    @Override
    public void associationMedia(Long teachPlanId, String mediaId) {
        LambdaQueryWrapper<TeachplanMedia> lq = new LambdaQueryWrapper<>();
        lq.eq(TeachplanMedia::getMediaId, mediaId);
        lq.eq(TeachplanMedia::getTeachplanId,teachPlanId);
        teachplanMediaMapper.delete(lq);
    }

    @Transactional
    void doDeletePlan(Teachplan plan){
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

    /**
     * @description 获取最大的排序号
     * @param parentId 父课程计划id
     * @return int 最大排序号
     * @author yfh
     */
    private int getMaxOrder(long parentId){
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getParentid,parentId);
        queryWrapper.orderByDesc(Teachplan::getOrderby).last("limit 1");
        Teachplan plan = teachplanMapper.selectOne(queryWrapper);
        if (plan == null)
            return 1;
        return plan.getOrderby();
    }
}
