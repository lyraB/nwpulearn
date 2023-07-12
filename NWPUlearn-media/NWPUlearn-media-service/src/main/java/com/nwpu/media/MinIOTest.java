package com.nwpu.media;

import io.minio.*;
import io.minio.errors.MinioException;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author yfh
 * @version 1.0
 * @description
 * @date 2023/2/21
 */

public class MinIOTest {
    static MinioClient minioClient = MinioClient.builder().endpoint("http://192.168.101.65:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();
    //上传文件
    public static void upload(String file)throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket("testbucket").build());
            //检查testbucket桶是否创建，没有创建自动创建
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket("testbucket").build());
            } else {
                System.out.println("Bucket 'testbucket' already exists.");
            }
            //上传
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket("testbucket") // 指定bucket
                            .object("img/wizarding-world-portrait.png") // 指定上传的路径
                            .filename(file) // 本地路径
                            .build());
            System.out.println("上传成功");
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
            System.out.println("HTTP trace: " + e.httpTrace());
        }
    }
    //删除文件
    public static void delete(String bucket,String filepath)throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucket).object(filepath).build());
            System.out.println("删除成功");
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
            System.out.println("HTTP trace: " + e.httpTrace());
        }
    }
    //下载文件
    public static void getFile(String bucket,String filepath,String outFile)throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            try (InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(filepath)
                            .build());
                 FileOutputStream fileOutputStream = new FileOutputStream(new File(outFile));
            ) {
                // Read data from stream
                IOUtils.copy(stream,fileOutputStream);
                System.out.println("下载成功");
            }

        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
            System.out.println("HTTP trace: " + e.httpTrace());
        }

    }

    public static void main(String[] args)throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        upload("D:\\learn\\wizarding-world-portrait.png");
//        delete("testbucket","1.mp4");
//        delete("testbucket","img/wizarding-world-portrait.png");
        getFile("testbucket","img/wizarding-world-portrait.png",
                "D:\\learn\\wizarding-world-portrait.png");
    }


}
