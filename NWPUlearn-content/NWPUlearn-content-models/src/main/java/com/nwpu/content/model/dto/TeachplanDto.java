package com.nwpu.content.model.dto;

import com.nwpu.content.model.po.Teachplan;
import com.nwpu.content.model.po.TeachplanMedia;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author yfh
 * @version 1.0
 * @description 课程计划树形结构dto
 * @date 2023/2/10
 */
@Data
@ToString
public class TeachplanDto extends Teachplan {
    //课程计划关联的媒资信息
    TeachplanMedia teachplanMedia;
    //子Teachplan结点
    List<TeachplanDto> teachPlanTreeNodes;

}
