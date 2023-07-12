package com.nwpu.media;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"com.nwpu.media"})
public class MediaControlApplication {

    public static void main(String[] args) {
        SpringApplication.run(MediaControlApplication.class, args);
    }

}
