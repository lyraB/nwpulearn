package com.nwpu.content.service;

import com.nwpu.content.model.dto.CoursePreviewDto;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @description 课程预览、发布接口
 * @author yfh
 */

public interface CoursePublishService {
    /**
     * @description 获取课程预览信息
     * @param courseId 课程id
     * @return com.nwpu.content.model.dto.CoursePreviewDto
     * @author yfh
     */
    public CoursePreviewDto getCoursePreviewInfo(Long courseId);

    /**
     * @description 提交审核
     * @param courseId  课程id
     * @return void
     * @author yfh
     */
    public void commitAudit(Long companyId,Long courseId);

    /**
     * @description 课程发布接口
     * @param companyId 机构id
     * @param courseId 课程id
     * @return void
     * @author yfh
     */
    public void publish(Long companyId,Long courseId);

    /**
     * @description 课程静态化
     * @param courseId  课程id
     * @return File 静态化文件
     * @author yfh
     */
    public File generateCourseHtml(Long courseId);
    /**
     * @description 上传课程静态化页面
     * @param file  静态化文件
     * @return void
     * @author yfh
     */
    public void  uploadCourseHtml(Long courseId,File file);


}
