package com.wwj.examonline;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.wwj.examonline.mapper")
public class ExamonlineApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExamonlineApplication.class, args);
    }
}
