package com.example.factory.app;

import com.example.factory.annotion.EnableLoggerTrace;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author cx521
 */
@SpringBootApplication
@EnableLoggerTrace
public class FactoryDemoApplication {

    public static void main(String[] args) {
       SpringApplication.run(FactoryDemoApplication.class, args);
    }

}