package com.nwpu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages={"com.nwpu.content.feign"})
public class ContentApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentApiApplication.class, args);
    }
}
