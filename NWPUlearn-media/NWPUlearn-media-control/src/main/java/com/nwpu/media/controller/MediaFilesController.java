package com.nwpu.media.controller;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.nwpu.base.exception.NwpuException;
import com.nwpu.base.model.PageParams;
import com.nwpu.base.model.PageResult;
import com.nwpu.base.model.RestResponse;
import com.nwpu.media.model.dto.QueryMediaParamsDto;
import com.nwpu.media.model.dto.UploadFileParamsDto;
import com.nwpu.media.model.dto.UploadFileResultDto;
import com.nwpu.media.model.po.MediaFiles;
import com.nwpu.media.service.MediaFilesService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * <p>
 * 媒资信息 前端控制器
 * </p>
 *
 * @author yfh
 */
@Slf4j
@RestController
public class MediaFilesController {

    @Autowired
    private MediaFilesService  mediaFilesService;

    @ApiOperation("媒资列表查询接口")
    @PostMapping("/files")
    public PageResult<MediaFiles> list(PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto){
        Long companyId = 1232141425L;
        return mediaFilesService.queryMediaFiels(companyId,pageParams,queryMediaParamsDto);
    }


    @ApiOperation("上传文件")
    @PostMapping(value = "/upload/coursefile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public UploadFileResultDto upload(@RequestPart("filedata") MultipartFile filedata,
                                      @RequestParam(value = "objectName",required=false) String objectName) throws IOException {

        Long companyId = 1232141425L;
        //准备上传的文件信息
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        //文件大小
        uploadFileParamsDto.setFileSize(filedata.getSize());
        //文件类型
        uploadFileParamsDto.setFileType("001001");
        //原始文件名称
        uploadFileParamsDto.setFilename(filedata.getOriginalFilename());
        //创建临时文件
        File tempFile = File.createTempFile("minio", "temp");
        //上传的文件拷贝到临时文件
        filedata.transferTo(tempFile);
        //文件路径
        String absolutePath = tempFile.getAbsolutePath();
        //上传文件
        return mediaFilesService.uploadFile(companyId, uploadFileParamsDto, absolutePath,objectName);

    }



    // 大文件分块上传相关api
    @ApiOperation(value = "大文件上传前检查文件")
    @PostMapping("/upload/checkfile")
    public RestResponse<Boolean> checkfile( @RequestParam("fileMd5") String fileMd5) throws Exception {

        return mediaFilesService.checkFile(fileMd5);
    }

    @ApiOperation(value = "分块文件上传前的检测")
    @PostMapping("/upload/checkchunk")
    public RestResponse<Boolean> checkchunk(@RequestParam("fileMd5") String fileMd5,
                                            @RequestParam("chunk") int chunk) throws Exception {

        return mediaFilesService.checkChunk(fileMd5,chunk);
    }

    @ApiOperation(value = "上传分块文件")
    @PostMapping("/upload/uploadchunk")
    public RestResponse uploadchunk(@RequestParam("file") MultipartFile file,
                                    @RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("chunk") int chunk) throws Exception {


        return mediaFilesService.uploadChunk(fileMd5, chunk, file.getBytes());
    }

    @ApiOperation(value = "合并文件")
    @PostMapping("/upload/mergechunks")
    public RestResponse mergechunks(@RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("fileName") String fileName,
                                    @RequestParam("chunkTotal") int chunkTotal) throws Exception {
        Long companyId = 1232141425L;

        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        uploadFileParamsDto.setFileType("001002");
        uploadFileParamsDto.setTags("课程视频");
        uploadFileParamsDto.setRemark("");
        uploadFileParamsDto.setFilename(fileName);


        return mediaFilesService.mergechunks(companyId, fileMd5, chunkTotal, uploadFileParamsDto);
    }

    @ApiOperation("视频播放界面视频地址接口")
    @GetMapping("/open/preview/{mediaId}")
    public RestResponse<String> getPlayUrlByMediaId(@PathVariable String mediaId) {
        MediaFiles mediaFiles = mediaFilesService.getFileById(mediaId);
        if(mediaFiles == null || StringUtils.isEmpty(mediaFiles.getUrl())){
            NwpuException.cast("视频没有转码处理");
        }
        return RestResponse.success(mediaFiles.getUrl());
    }



}
