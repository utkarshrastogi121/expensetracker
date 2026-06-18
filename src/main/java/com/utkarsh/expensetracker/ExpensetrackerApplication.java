package com.utkarsh.expensetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.TimeZone;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class ExpensetrackerApplication {

	public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        System.out.println("ENV DATABASE_URL = " + System.getenv("DATABASE_URL"));
        SpringApplication.run(ExpensetrackerApplication.class, args);
	}

}
