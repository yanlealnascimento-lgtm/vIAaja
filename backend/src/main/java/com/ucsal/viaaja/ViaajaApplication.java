package com.ucsal.viaaja;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ViaajaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ViaajaApplication.class, args);
    }
}
