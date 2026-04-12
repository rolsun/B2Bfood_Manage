package com.group8;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class B2BFoodManageApplication {

    public static void main(String[] args) {
        SpringApplication.run(B2BFoodManageApplication.class, args);
    }

}
