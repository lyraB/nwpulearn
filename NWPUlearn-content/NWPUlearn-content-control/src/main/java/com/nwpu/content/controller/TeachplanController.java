package com.nwpu.content.controller;

import com.nwpu.content.model.dto.SaveTeachplanDto;
import com.nwpu.content.model.dto.TeachplanDto;
import com.nwpu.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author yfh
 * @version 1.0
 * @description 课程计划接口
 * @date 2023/2/10
 */
@Api(value = "课程计划接口",tags = "课程计划接口")
@RestController
public class TeachplanController {
    @Autowired
    TeachplanService teachplanService;


    @ApiOperation("查询课程计划树形结构")
    @ApiImplicitParam(value = "courseId",name = "课程Id",required = true,dataType
            = "Long",paramType = "path")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> getTreeNodes(@PathVariable Long courseId){

        return teachplanService.findTeachplayTree(courseId);
    }

    @ApiOperation("课程计划创建或修改")
    @PostMapping("/teachplan")
    public void saveTeachplan( @RequestBody @Validated SaveTeachplanDto teachplandto){
        teachplanService.saveTeachplan(teachplandto);
    }

    @ApiOperation("课程计划创建或修改")
    @DeleteMapping("/teachplan/{id}")
    public void deleteTeachplan(@PathVariable  Long id){
        teachplanService.deleteTeachplan(id);
    }

}
