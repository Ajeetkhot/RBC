package com.example.banking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {
    "com.example.banking.entity",
    "com.example.rbc",
    "com.example.audit"
})
@EnableJpaRepositories(basePackages = {
    "com.example.banking.repository",
    "com.example.rbc",
    "com.example.audit"
})
public class BankingApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankingApplication.class, args);
    }
}
