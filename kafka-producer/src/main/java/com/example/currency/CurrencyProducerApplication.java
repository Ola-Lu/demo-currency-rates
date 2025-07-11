package com.example.currency;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CurrencyProducerApplication {
  public static void main(String[] args) {
    SpringApplication.run(CurrencyProducerApplication.class, args);
  }
}

