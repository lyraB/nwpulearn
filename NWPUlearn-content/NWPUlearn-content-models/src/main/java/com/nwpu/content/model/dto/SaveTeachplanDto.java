package com.nwpu.content.model.dto;

import com.nwpu.base.exception.ValidationGroups;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.*;

/**
 * @author yfh
 * @version 1.0
 * @description 保存课程计划dto
 */
@Data
@ToString
public class SaveTeachplanDto {
    /**
     * 课程计划id
     */
    private Long id;
    /**
     * 课程计划名称
     */
    @NotBlank
    private String pname;
    /**
     * 课程计划父级Id
     */
    @NotNull
    private Long parentid;
    /**
     * 层级，分为1、2级
     */
    @NotNull
    @Min(1)
    @Max(2)
    private Integer grade;
    /**
     * 课程类型:1视频、2文档
     */
    private String mediaType;
    /**
     * 课程标识
     */
    @NotNull
    private Long courseId;
    /**
     * 课程发布标识
     */
    private Long coursePubId;
    /**
     * 是否支持试学或预览（试看）
     */
    private String isPreview;
}
