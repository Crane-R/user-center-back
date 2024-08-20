package com.crane.usercenterback;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@MapperScan("com.crane.usercenterback.mapper")
@ServletComponentScan
@Slf4j
public class UCBApplication {

    public static void main(String[] args) {
        SpringApplication.run(UCBApplication.class, args);
        log.info("Knife4j接口文档地址：http://localhost:8080/api/doc.html");
    }

}
