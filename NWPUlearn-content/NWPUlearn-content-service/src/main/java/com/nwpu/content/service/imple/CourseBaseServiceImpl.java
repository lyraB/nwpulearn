package com.nwpu.content.service.imple;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nwpu.base.exception.NwpuException;
import com.nwpu.base.model.PageParams;
import com.nwpu.base.model.PageResult;
import com.nwpu.content.mapper.*;
import com.nwpu.content.model.Code;
import com.nwpu.content.model.dto.AddCourseDto;
import com.nwpu.content.model.dto.CourseBaseInfoDto;
import com.nwpu.content.model.dto.EditCourseDto;
import com.nwpu.content.model.dto.QueryCourseParamsDto;
import com.nwpu.content.model.po.*;
import com.nwpu.content.service.CourseBaseService;
import com.nwpu.content.service.CourseMarketService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 课程基本信息 服务实现类
 * </p>
 *
 * @author yfh
 */
@Slf4j
@Service
public class CourseBaseServiceImpl extends ServiceImpl<CourseBaseMapper, CourseBase> implements CourseBaseService {
    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Autowired
    CourseMarketMapper courseMarketMapper;

    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    @Autowired
    CourseMarketService courseMarketService;

    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    CourseTeacherMapper courseTeacherMapper;

    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {
        //构建查询条件对象
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        //构建查询条件，根据课程名称模糊匹配
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()),
                CourseBase::getName,queryCourseParamsDto.getCourseName());
        //根据课程审核状态查询
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()),
                CourseBase::getAuditStatus,queryCourseParamsDto.getAuditStatus());
        //根据课程发布状态查询
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()),
                CourseBase::getStatus,queryCourseParamsDto.getPublishStatus());
        //分页对象
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(),pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<CourseBase> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 返回结果集
        return new PageResult<>(list, total,
                pageParams.getPageNo(), pageParams.getPageSize());
    }

    @Transactional
    @Override
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto) {
        //新增对象
        CourseBase courseBaseNew = new CourseBase();
        //将填写的课程信息赋值给新增对象
        BeanUtils.copyProperties(dto,courseBaseNew);
        //设置审核状态
        courseBaseNew.setAuditStatus(Code.Submit_Audit_NO);
        //设置发布状态
        courseBaseNew.setStatus(Code.Publish_NO);
        //机构id
        courseBaseNew.setCompanyId(companyId);
        //添加时间
        courseBaseNew.setCreateDate(LocalDateTime.now());
        //插入课程基本信息表
        int insert = courseBaseMapper.insert(courseBaseNew);
        Long courseId = courseBaseNew.getId();
        //课程营销信息
        CourseMarket courseMarketNew = new CourseMarket();
        BeanUtils.copyProperties(dto,courseMarketNew);
        courseMarketNew.setId(courseId);
        //收费规则
        String charge = dto.getCharge();
        //收费课程必须写价格且价格大于0
        if(charge.equals(Code.Charge_OK)){
            Float price = dto.getPrice();
            if(price == null || price<=0){
                throw new NwpuException("课程设置了收费价格不能为空且必须大于0");
            }
        }
        //插入课程营销信息
        int flag = saveCourseMarket(courseMarketNew);
        if(flag==-1)
            NwpuException.cast("新增课程营销信息失败");
        //添加成功
        //返回添加的课程信息
        return getCourseBaseInfo(courseId);
    }

    @Transactional
    @Override
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto dto) {
        //本学院只能修改本学院的课程
        Long courseId = dto.getId();
        CourseBase courseBaseUpdate = courseBaseMapper.selectById(courseId);
        if(!companyId.equals(courseBaseUpdate.getCompanyId())){
            NwpuException.cast("只允许修改本学院的课程");
        }
        BeanUtils.copyProperties(dto,courseBaseUpdate);
        //更新到数据库
        courseBaseUpdate.setChangeDate(LocalDateTime.now());
        courseBaseMapper.updateById(courseBaseUpdate);
        //查询营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        if(courseMarket==null){
            courseMarket = new CourseMarket();
        }
        int flag = saveCourseMarket(courseMarket);
        if(flag==-1)
            NwpuException.cast("课程营销信息修改失败");

        return getCourseBaseInfo(courseId);
    }
    @Transactional
    @Override
    public void deleteCourse(Long courseId) {
        CourseBase course = courseBaseMapper.selectById(courseId);
        if (course == null)
            NwpuException.cast("课程不存在");
        courseMarketMapper.deleteById(courseId);
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, courseId);
        teachplanMapper.delete(queryWrapper);
        //TODO: teachplan_media 以及 teachplan_work 表中相关数据未删除
        LambdaQueryWrapper<CourseTeacher> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(CourseTeacher::getCourseId, courseId);
        courseTeacherMapper.delete(queryWrapper1);

        courseBaseMapper.deleteById(courseId);

    }

    /**
     * @description 根据课程id查询基本信息
     * @param courseId
     * @return CourseBaseInfoDto
     * @author yfh
     */
    @Override
    public CourseBaseInfoDto getCourseBaseInfo(long courseId){
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        if(courseBase == null){
            return null;
        }

        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase,courseBaseInfoDto);
        if(courseMarket != null){
            BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);
        }
        //查询分类名称
        CourseCategory courseCategoryBySt =
                courseCategoryMapper.selectById(courseBase.getSt());
        courseBaseInfoDto.setStName(courseCategoryBySt.getName());
        CourseCategory courseCategoryByMt =
                courseCategoryMapper.selectById(courseBase.getMt());
        courseBaseInfoDto.setMtName(courseCategoryByMt.getName());
        return courseBaseInfoDto;
    }

    /**
     * @description 课程营销校验及保存功能
     * @param courseMarket
     * @return int
     * @author yfh
     */
    private int saveCourseMarket(CourseMarket courseMarket){
        String charge = courseMarket.getCharge();
        if(StringUtils.isBlank(charge)){
            NwpuException.cast("请设置收费规则");
        }
        //收费课程必须写价格
        if(charge.equals(Code.Charge_OK)){
            Float price = courseMarket.getPrice();
            if(price == null || price<=0){
                NwpuException.cast("课程设置收费价格不能为空且必须大于0");
            }
        }
        boolean b = courseMarketService.saveOrUpdate(courseMarket);
        return b?1:-1;
    }

}
