package com.nwpu;

import com.nwpu.content.config.MultipartSupportConfig;
import com.nwpu.content.feign.MediaServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author yfh
 * @version 1.0
 * @description
 * @date 2023/6/12
 */
@SpringBootTest
public class FeignUploadTest {
    @Autowired
    MediaServiceClient mediaServiceClient;

    //远程调用，上传文件
    @Test
    public void test() {

        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(new File("D:\\learn\\学成\\db\\test.html"));
        mediaServiceClient.uploadFile(multipartFile,"course");
    }

}
