package com.nwpu.media.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nwpu.base.model.PageParams;
import com.nwpu.base.model.PageResult;
import com.nwpu.base.model.RestResponse;
import com.nwpu.media.model.dto.QueryMediaParamsDto;
import com.nwpu.media.model.dto.UploadFileParamsDto;
import com.nwpu.media.model.dto.UploadFileResultDto;
import com.nwpu.media.model.po.MediaFiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 媒资信息 服务类
 * </p>
 *
 * @author yfh
 * @since 2023-02-21
 */
public interface MediaFilesService extends IService<MediaFiles> {
    /**
     * @description 媒资文件查询方法
     * @param pageParams 分页参数
     * @param queryMediaParamsDto 查询条件
     * @return com.nwpu.base.model.PageResult<com.xuecheng.media.model.po.MediaFiles>
     * @author yfh
     */
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

    /**
     * 上传文件
     * @param companyId 机构id
     * @param uploadFileParamsDto 上传文件信息
     * @param localFilePath 文件磁盘路径
     * @return 文件信息
     */
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath,String objectName);


    /**
     * @description 将文件信息添加到文件表
     * @param companyId  机构id
     * @param fileMd5  文件md5值
     * @param uploadFileParamsDto  上传文件的信息
     * @param bucket  桶
     * @param objectName 对象名称
     * @return com.nwpu.media.model.po.MediaFiles
     * @author yfh
     */
    public MediaFiles addFilesToDb(Long companyId, String fileMd5,
                                   UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName);


    /**
     * @description 检查文件是否存在
     * @param fileMd5 文件的md5
     * @return com.nwpu.base.model.RestResponse<java.lang.Boolean> false不存在，true存在
     * @author yfh
     */
    public RestResponse<Boolean> checkFile(String fileMd5);

    /**
     * @description 检查分块是否存在
     * @param fileid  文件id
     * @param chunkIndex  分块序号
     * @return com.nwpu.base.model.RestResponse<java.lang.Boolean> false不存在，true存在
     * @author yfh
     */
    public RestResponse<Boolean> checkChunk(String fileid, int chunkIndex);

    /**
     * @description 上传分块
     * @param fileMd5  文件md5
     * @param chunk  分块序号
     * @param bytes  文件字节
     * @return com.nwpu.base.model.RestResponse
     * @author yfh
     */
    public RestResponse uploadChunk(String fileMd5,int chunk,byte[] bytes);

    /**
     * @description 合并分块
     * @param companyId  机构id
     * @param fileMd5  文件md5
     * @param chunkTotal 分块总和
     * @param uploadFileParamsDto 文件信息
     * @return com.nwpu.base.model.RestResponse
     * @author yfh
     */
    public RestResponse mergechunks(Long companyId,String fileMd5,int chunkTotal,UploadFileParamsDto uploadFileParamsDto);


    /**
     * @description 根据id查询文件信息
     * @param id  文件id
     * @return com.xuecheng.media.model.po.MediaFiles 文件信息
     * @author yfh
     */
    public MediaFiles getFileById(String id);

}
