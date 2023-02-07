package com.nwpu.content.service.imple;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nwpu.content.mapper.CourseCategoryMapper;
import com.nwpu.content.model.dto.CourseCategoryTreeDto;
import com.nwpu.content.model.po.CourseCategory;
import com.nwpu.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        //查询数据库得到的课程分类
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = categoryMapper.selectTreeNodes(id);

        List<CourseCategoryTreeDto> result = new ArrayList<>();
        HashMap<String, CourseCategoryTreeDto> mapTemp = new HashMap<>();

        courseCategoryTreeDtos.stream().forEach(item->{ // stream流
            mapTemp.put(item.getId(),item);
            //只将根节点的下级节点放入list
            if(item.getParentid().equals(id)){
                result.add(item);
            }
            CourseCategoryTreeDto courseCategoryTreeDto =
                    mapTemp.get(item.getParentid());
            if(courseCategoryTreeDto!=null){
                if(courseCategoryTreeDto.getChildrenTreeNodes() ==null){
                    courseCategoryTreeDto.setChildrenTreeNodes(new
                            ArrayList<CourseCategoryTreeDto>());
                }
                //向节点的下级节点list加入节点
                courseCategoryTreeDto.getChildrenTreeNodes().add(item);
            }
        });

        return result;
    }
}
