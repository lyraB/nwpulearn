package com.nwpu.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.nwpu.base.exception.NwpuException;
import com.nwpu.base.model.PageParams;
import com.nwpu.base.model.PageResult;
import com.nwpu.base.model.RestResponse;
import com.nwpu.media.mapper.MediaFilesMapper;
import com.nwpu.media.mapper.MediaProcessHistoryMapper;
import com.nwpu.media.mapper.MediaProcessMapper;
import com.nwpu.media.model.dto.QueryMediaParamsDto;
import com.nwpu.media.model.dto.UploadFileParamsDto;
import com.nwpu.media.model.dto.UploadFileResultDto;
import com.nwpu.media.model.po.MediaFiles;
import com.nwpu.media.model.po.MediaProcess;
import com.nwpu.media.service.MediaFilesService;
import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 媒资信息 服务实现类
 * </p>
 *
 * @author yfh
 */
@Slf4j
@Service
public class MediaFilesServiceImpl extends ServiceImpl<MediaFilesMapper, MediaFiles> implements MediaFilesService {

    @Autowired
    MediaFilesMapper mediaFilesMapper;
    @Autowired
    MinioClient minioClient;
    //普通文件桶
    @Value("${minio.bucket.files}")
    private String bucket_Files;
    //视频文件桶
    @Value("${minio.bucket.videofiles}")
    private String bucket_videoFiles;
    @Autowired
    MediaFilesService currentProxy; // 用于处理事务调用的代理类
    //待处理表
    @Autowired
    MediaProcessMapper mediaProcessMapper;
    //处理完成表
    @Autowired
    MediaProcessHistoryMapper mediaProcessHistoryMapper;

    @Override
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams,
                                                  QueryMediaParamsDto queryMediaParamsDto) {
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(queryMediaParamsDto.getFilename()),
                MediaFiles::getFilename, queryMediaParamsDto.getFilename());
        queryWrapper.eq(StringUtils.isNotEmpty(queryMediaParamsDto.getAuditStatus()),
                MediaFiles::getAuditStatus, queryMediaParamsDto.getAuditStatus());
        queryWrapper.eq(StringUtils.isNotEmpty(queryMediaParamsDto.getFileType()),
                MediaFiles::getFileType, queryMediaParamsDto.getFileType());
        //分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(),pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page,queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();

        return new PageResult<>(list,total,pageParams.getPageNo(),pageParams.getPageSize());
    }

    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath, String objectName) {
        File file = new File(localFilePath);
        if (!file.exists()) {
            NwpuException.cast("文件不存在");
        }
        //文件名称
        String filename = uploadFileParamsDto.getFilename();
        //文件扩展名
        String extension = filename.substring(filename.lastIndexOf("."));
        //文件mimeType
        String mimeType = getMimeType(extension);
        //文件的md5值
        String fileMd5 = getFileMd5(file);
        //文件的默认目录
        String defaultFolderPath = getDefaultFolderPath();
        //存储到minio中的对象名(带目录)
        if (StringUtils.isEmpty(objectName)){
            objectName = defaultFolderPath + fileMd5 + extension;
        }

        //将文件上传到minio
        addMediaFilesToMinIO(localFilePath, mimeType, bucket_Files, objectName);
        //文件大小
        uploadFileParamsDto.setFileSize(file.length());
        //将文件信息存储到数据库
        MediaFiles mediaFiles = addFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_Files, objectName);
        //准备返回数据
        UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
        BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);
        return uploadFileResultDto;

    }


    /**
     * @description 将文件上传minIO
     * @param bytes 文件
     * @param objectName 文件名
     * @author yfh
     */
    public void addFilesToMinIO(byte[] bytes, String bucket, String objectName) {
        //获取content type
        String extension = null;
        if(objectName.contains(".")){
            //文件扩展名
            extension = objectName.substring(objectName.lastIndexOf("."));
        }
        // 没有扩展名时会设置成字节流类型
        String  contentType = getMimeTypeByExtension(extension);

        //转为流
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        PutObjectArgs putObjectArgs = PutObjectArgs.builder().bucket(bucket).object(objectName)
                //-1表示文件分片按5M(不小于5M,不大于5T),分片数量最大10000，
                .stream(byteArrayInputStream, byteArrayInputStream.available(), -1)
                .contentType(contentType)
                .build();

        try {
            minioClient.putObject(putObjectArgs);
        } catch (Exception e) {
            e.printStackTrace();
            NwpuException.cast("上传文件到系统出错");
        }
    }

    /**
     * @description 将文件写入minIO
     * @param localFilePath  文件地址
     * @param bucket  桶
     * @param objectName 对象名称
     * @return void
     * @author yfh
     */
    public void addMediaFilesToMinIO(String localFilePath,String mimeType,String bucket, String objectName) {
        try {
            UploadObjectArgs testbucket = UploadObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .filename(localFilePath)
                    .contentType(mimeType)
                    .build();
            minioClient.uploadObject(testbucket);
            log.debug("上传文件到minio成功,bucket:{},objectName:{}",bucket,objectName);
            System.out.println("上传成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件到minio出错,bucket:{},objectName:{},错误原因:{}",bucket,objectName,e.getMessage(),e);
            NwpuException.cast("上传文件到文件系统失败");
        }
    }

    /**
     * @description 将文件信息添加到文件表
     * @param companyId  机构id
     * @param fileMd5  文件md5值
     * @param uploadFileParamsDto  上传文件的信息
     * @param bucket  桶
     * @param objectName 对象名称
     * @return com.xuecheng.media.model.po.MediaFiles
     * @author Mr.M
     * @date 2022/10/12 21:22
     */
    @Transactional
    public MediaFiles addFilesToDb(Long companyId,String fileMd5,UploadFileParamsDto uploadFileParamsDto,String bucket,String objectName){
        //从数据库查询文件
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles == null) {
            mediaFiles = new MediaFiles();
            //拷贝基本信息
            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
            mediaFiles.setId(fileMd5);
            mediaFiles.setFileId(fileMd5);
            mediaFiles.setCompanyId(companyId);
            mediaFiles.setUrl("/" + bucket + "/" + objectName);
            mediaFiles.setBucket(bucket);
            mediaFiles.setFilePath(objectName);
            mediaFiles.setCreateDate(LocalDateTime.now());
            mediaFiles.setAuditStatus("002003");
            mediaFiles.setStatus("1");
            //保存文件信息到文件表
            int insert = mediaFilesMapper.insert(mediaFiles);
            if (insert < 0) {
                log.error("保存文件信息到数据库失败,{}",mediaFiles.toString());
                NwpuException.cast("保存文件信息失败");
            }
            log.debug("保存文件信息到数据库成功,{}",mediaFiles.toString());

        }
        return mediaFiles;

    }


    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {
        // 从数据库查询
        MediaFiles mediaFile = mediaFilesMapper.selectById(fileMd5);
        if (mediaFile != null) {
            //桶
            String bucket = mediaFile.getBucket();
            //存储目录
            String filePath = mediaFile.getFilePath();
            //文件流
            InputStream stream = null;
            try {
                stream = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(bucket)
                                .object(filePath)
                                .build());

                if (stream != null) {
                    //文件已存在
                    return RestResponse.success(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //文件不存在
        return RestResponse.success(false);
    }

    @Override
    public RestResponse<Boolean> checkChunk(String fileid, int chunkIndex) {
        //得到分块文件目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileid);
        //得到分块文件的路径
        String chunkFilePath = chunkFileFolderPath + chunkIndex;

        //文件流
        InputStream fileInputStream = null;
        try {
            fileInputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket_videoFiles)
                            .object(chunkFilePath)
                            .build());

            if (fileInputStream != null) {
                //分块已存在
                return RestResponse.success(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //分块未存在
        return RestResponse.success(false);
    }

    @Override
    public RestResponse uploadChunk(String fileMd5, int chunk, byte[] bytes) {
        //得到分块文件的目录路径
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //得到分块文件的路径
        String chunkFilePath = chunkFileFolderPath + chunk;

        try {
            //将文件存储至minIO
            addFilesToMinIO(bytes, bucket_videoFiles,chunkFilePath);
            return RestResponse.success(true);
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("上传分块文件:{},失败:{}",chunkFilePath,e.getMessage());
        }
        return RestResponse.validfail(false,"上传分块失败");

    }

    @Override
    public RestResponse mergechunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto) {
        //=====获取分块文件路径=====
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //组成将分块文件路径组成 List<ComposeSource>
        List<ComposeSource> sourceObjectList = Stream.iterate(0, i -> ++i)
                .limit(chunkTotal)
                .map(i -> ComposeSource.builder()
                        .bucket(bucket_videoFiles)
                        .object(chunkFileFolderPath.concat(Integer.toString(i)))
                        .build())
                .collect(Collectors.toList());
        //=====合并=====
        //文件名称
        String fileName = uploadFileParamsDto.getFilename();
        //文件扩展名
        String extName = fileName.substring(fileName.lastIndexOf("."));
        //合并文件路径
        String mergeFilePath = getFilePathByMd5(fileMd5, extName);
        try {
            //合并文件
            ObjectWriteResponse response = minioClient.composeObject(
                    ComposeObjectArgs.builder()
                            .bucket(bucket_videoFiles)
                            .object(mergeFilePath)
                            .sources(sourceObjectList)
                            .build());
            log.debug("合并文件成功:{}",mergeFilePath);
        } catch (Exception e) {
            log.debug("合并文件失败,fileMd5:{},异常:{}",fileMd5,e.getMessage(),e);
            return RestResponse.validfail(false, "合并文件失败。");
        }

        // ====验证md5====
        //下载合并后的文件
        File minioFile = downloadFileFromMinIO(bucket_videoFiles,mergeFilePath);
        if(minioFile == null){
            log.debug("下载合并后文件失败,mergeFilePath:{}",mergeFilePath);
            return RestResponse.validfail(false, "下载合并后文件失败。");
        }

        try (InputStream newFileInputStream = new FileInputStream(minioFile)) {
            //minio上文件的md5值
            String md5Hex = DigestUtils.md5Hex(newFileInputStream);
            //比较md5值，不一致则说明文件不完整
            if(!fileMd5.equals(md5Hex)){
                return RestResponse.validfail(false, "文件合并校验失败，最终上传失败。");
            }
            //文件大小
            uploadFileParamsDto.setFileSize(minioFile.length());
        }catch (Exception e){
            log.debug("校验文件失败,fileMd5:{},异常:{}",fileMd5,e.getMessage(),e);
            return RestResponse.validfail(false, "文件合并校验失败，最终上传失败。");
        }finally {
            if(minioFile!=null){
                minioFile.delete();
            }
        }

        //文件入库
        currentProxy.addFilesToDb(companyId,fileMd5,uploadFileParamsDto,bucket_videoFiles,mergeFilePath);
        //=====清除分块文件=====
        clearChunkFiles(chunkFileFolderPath,chunkTotal);
        return RestResponse.success(true);

    }

    @Override
    public MediaFiles getFileById(String id) {
        return mediaFilesMapper.selectById(id);
    }

    //根据日期拼接目录
    private String getFileFolder(Date date, boolean year, boolean month, boolean day){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //获取当前日期字符串
        String dateString = sdf.format(date);
        //取出年、月、日
        String[] dateStringArray = dateString.split("-");
        StringBuilder folderString = new StringBuilder();
        if(year){
            folderString.append(dateStringArray[0]);
            folderString.append("/");
        }
        if(month){
            folderString.append(dateStringArray[1]);
            folderString.append("/");
        }
        if(day){
            folderString.append(dateStringArray[2]);
            folderString.append("/");
        }
        return folderString.toString();
    }

    //根据扩展名获取MimeType
    private String getMimeTypeByExtension(String extension){
        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE; //缺省值为：application/octet-stream
        if(StringUtils.isNotEmpty(extension)){
            ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
            if(extensionMatch!=null){
                contentType = extensionMatch.getMimeType();
            }
        }
        return contentType;
    }

    //得到分块文件目录
    private String getChunkFileFolderPath(String fileMd5) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + "chunk" + "/";
    }
    //下载所有分块文件
    private File[] checkChunkStatus(String fileMd5, int chunkTotal) {
        //得到分块文件的目录路径
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        File[] files = new File[chunkTotal];
        //检查分块文件是否上传完毕
        for (int i = 0; i < chunkTotal; i++) {
            String chunkFilePath = chunkFileFolderPath + i;
            //下载文件
            File chunkFile =null;
            chunkFile = downloadFileFromMinIO(bucket_videoFiles,chunkFilePath);

            files[i]=chunkFile;
        }
        return files;
    }

    //根据桶和文件路径从minio下载文件
    public File downloadFileFromMinIO(String bucket,String objectName){
        //临时文件
        File minioFile = null;
        FileOutputStream outputStream = null;
        try{
            InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());
            //创建临时文件
            minioFile=File.createTempFile("minio", ".merge");
            outputStream = new FileOutputStream(minioFile);
            IOUtils.copy(stream,outputStream);
            return minioFile;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    //得到合并文件路径
    private String getFilePathByMd5(String fileMd5,String fileExt){
        return   fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/" +fileMd5 +fileExt;
    }
    //获取文件默认存储目录路径 年/月/日
    private String getDefaultFolderPath() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String folder = sdf.format(new Date()).replace("-", "/")+"/";
        return folder;
    }

    //获取文件的md5
    private String getFileMd5(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            String fileMd5 = DigestUtils.md5Hex(fileInputStream);
            return fileMd5;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //将大文件上传到minIO，传入文件绝对路径
    public void addMediaFilesToMinIO(String filePath, String bucket, String objectName) {
        //扩展名
        String extension = null;
        if(objectName.contains(".")){
            extension = objectName.substring(objectName.lastIndexOf("."));
        }
        //获取扩展名对应的媒体类型
        String contentType = getMimeTypeByExtension(extension);
        try {
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .filename(filePath)
                            .contentType(contentType)
                            .build());
        } catch (Exception e) {
            e.printStackTrace();
            NwpuException.cast("上传文件到文件系统出错");
        }
    }

    /**
     * 清除分块文件
     * @param chunkFileFolderPath 分块文件路径
     * @param chunkTotal 分块文件总数
     */
    private void clearChunkFiles(String chunkFileFolderPath,int chunkTotal){
        try {
            List<DeleteObject> deleteObjects = Stream.iterate(0, i -> ++i)
                    .limit(chunkTotal)
                    .map(i -> new DeleteObject(chunkFileFolderPath.concat(Integer.toString(i))))
                    .collect(Collectors.toList());

            RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder().bucket("video").objects(deleteObjects).build();
            Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
            results.forEach(r->{
                DeleteError deleteError = null;
                try {
                    deleteError = r.get();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("清楚分块文件失败,objectname:{}",deleteError.objectName(),e);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            log.error("清楚分块文件失败,chunkFileFolderPath:{}",chunkFileFolderPath,e);
        }
    }

    private String getMimeType(String extension){
        if(extension==null)
            extension = "";
        //根据扩展名取出mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
        //通用mimeType，字节流
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if(extensionMatch!=null){
            mimeType = extensionMatch.getMimeType();
        }
        return mimeType;
    }

    /**
     * 添加待处理任务
     * @param mediaFiles 媒资文件信息
     */
    private void addWaitingTask(MediaFiles mediaFiles){
        //文件名称
        String filename = mediaFiles.getFilename();
        //文件扩展名
        String extension = filename.substring(filename.lastIndexOf("."));
        //文件mimeType
        String mimeType = getMimeType(extension);
        //如果是avi视频添加到视频待处理表
        if(mimeType.equals("video/x-msvideo")){
            MediaProcess mediaProcess = new MediaProcess();
            BeanUtils.copyProperties(mediaFiles,mediaProcess);
            mediaProcess.setStatus("1");//未处理
            mediaProcess.setFailCount(0);//失败次数默认为0
            mediaProcessMapper.insert(mediaProcess);
        }
    }

}
