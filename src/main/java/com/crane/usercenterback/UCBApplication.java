package com.crane.usercenterback;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@MapperScan("com.crane.usercenterback.mapper")
@ServletComponentScan
public class UCBApplication {

    public static void main(String[] args) {
        SpringApplication.run(UCBApplication.class, args);
    }

}
