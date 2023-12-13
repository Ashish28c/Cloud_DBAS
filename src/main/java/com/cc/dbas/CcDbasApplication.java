package com.cc.dbas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class CcDbasApplication {

	public static void main(String[] args) {
		SpringApplication.run(CcDbasApplication.class, args);
		log.info("hello started");
		
	}

}
