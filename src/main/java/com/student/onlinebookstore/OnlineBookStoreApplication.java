package com.student.onlinebookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@ServletComponentScan
public class OnlineBookStoreApplication extends SpringBootServletInitializer {

    // Dùng khi deploy WAR lên external Tomcat
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(OnlineBookStoreApplication.class);
    }

    public static void main(String[] args) {
        // XÓA dòng set property "none" — để Spring tự khởi embedded Tomcat
        SpringApplication.run(OnlineBookStoreApplication.class, args);
    }
}