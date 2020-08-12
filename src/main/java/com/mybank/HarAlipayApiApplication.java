package com.mybank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class HarAlipayApiApplication {
//	javax.servlet.http.HttpServlet
	public static void main(String[] args) {
        System.out.println("start");
	    SpringApplication.run(HarAlipayApiApplication.class, args);
	}

}
