package com.nwpu.content.service.imple;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nwpu.content.mapper.CourseCategoryMapper;
import com.nwpu.content.model.dto.CourseCategoryTreeDto;
import com.nwpu.content.model.po.CourseCategory;
import com.nwpu.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 课程分类 服务实现类
 * </p>
 *
 * @author yfh
 */
@Slf4j
@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {
    @Autowired
    CourseCategoryMapper categoryMapper;

    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
//        //查询数据库得到的课程分类
//        List<CourseCategoryTreeDto> courseCategoryTreeDtos = categoryMapper.selectTreeNodes(id);
//        return getChildren(courseCategoryTreeDtos, id);
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = categoryMapper.selectTreeNodes(id);
        //将list转map,以备使用,排除根节点
        Map<String, CourseCategoryTreeDto> mapTemp = courseCategoryTreeDtos.stream().filter(
                item->!id.equals(item.getId())).collect(Collectors.toMap(key -> key.getId(), value -> value, (key1, key2) -> key2));
        //最终返回的list
        List<CourseCategoryTreeDto> categoryTreeDtos = new ArrayList<>();
        //依次遍历每个元素,排除根节点
        courseCategoryTreeDtos.stream().filter(item->!id.equals(item.getId())).forEach(item->{
            if(item.getParentid().equals(id)){
                categoryTreeDtos.add(item);
            }
            //找到当前节点的父节点
            CourseCategoryTreeDto courseCategoryTreeDto = mapTemp.get(item.getParentid());
            if(courseCategoryTreeDto!=null){
                if(courseCategoryTreeDto.getChildrenTreeNodes() ==null){
                    courseCategoryTreeDto.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
                }
                //下边开始往ChildrenTreeNodes属性中放子节点
                courseCategoryTreeDto.getChildrenTreeNodes().add(item);
            }
        });
        return categoryTreeDtos;
    }

    public List<CourseCategoryTreeDto> getChildren(List<CourseCategoryTreeDto> list, String id) {
        return list.stream()
                .filter(p -> Objects.equals(p.getParentid(), id)) // 找到id相同的
                .peek(f -> f.setChildrenTreeNodes(getChildren(list, f.getId())))
                .collect(Collectors.toList());
    }
}
