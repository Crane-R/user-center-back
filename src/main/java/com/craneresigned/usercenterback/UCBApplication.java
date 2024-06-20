package com.craneresigned.usercenterback;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.craneresigned.usercenterback.mapper")
public class UCBApplication {

    public static void main(String[] args) {
        SpringApplication.run(UCBApplication.class, args);
    }

}
