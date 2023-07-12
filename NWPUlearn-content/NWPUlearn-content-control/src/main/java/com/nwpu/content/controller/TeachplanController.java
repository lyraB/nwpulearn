package com.nwpu.content.controller;

import com.nwpu.base.exception.NwpuException;
import com.nwpu.content.model.dto.BindTeachplanMediaDto;
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

        return teachplanService.findTeachplanTree(courseId);
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

    @ApiOperation("课程计划顺序移动")
    @PostMapping("/teachplan/{upordown}/{id}")
    public void moveTeachplan(@PathVariable String upordown, @PathVariable Long id) {
        boolean up = false;
        if (upordown.equals("moveup")){
            up = true;
        } else if (upordown.equals("movedown")) {
            up = false;
        } else {
            NwpuException.cast("排序要求错误");
        }
        teachplanService.moveTeachplan(up, id);
    }
    @ApiOperation(value = "课程计划和媒资信息绑定")
    @PostMapping("/teachplan/association/media")
    public void associationMedia(@RequestBody BindTeachplanMediaDto bindTeachplanMediaDto){
        teachplanService.associationMedia(bindTeachplanMediaDto);
    }

    @ApiOperation(value = "删除课程计划中的媒资信息")
    @DeleteMapping("/teachplan/association/media/{teachPlanId}/{mediaId}")
    public void deleteAssociation(@PathVariable  Long teachPlanId, @PathVariable  String mediaId){

    }

}
