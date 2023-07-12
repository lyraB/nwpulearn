package com.nwpu.content.feign;

import com.nwpu.content.config.MultipartSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @description 媒资管理服务远程接口
 * @author yfh
 * @version 1.0
 */
//@FeignClient(value = "media-control",configuration = MultipartSupportConfig.class)
@FeignClient(value = "media-control", fallbackFactory = MediaServiceClientFallbackFactory.class)
@RequestMapping("/media")
public interface MediaServiceClient {
    @PostMapping(value = "/upload/coursefile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    String uploadFile(@RequestPart("filedata") MultipartFile upload,
                      @RequestParam(value = "objectName",required=false) String objectName);

}
