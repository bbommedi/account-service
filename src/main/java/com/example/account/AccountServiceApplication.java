package com.example.account;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {
	    "com.example.account",
	    "com.example.common"
	})
@EnableDiscoveryClient
//@Import(KafkaCommonConfig.class)
public class AccountServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }
    
    @Bean
    CommandLineRunner checkContext(ApplicationContext ctx) {
        return args -> {
            var controllers = ctx.getBeansOfType(com.example.account.controller.AccountController.class);
            System.out.println("🧩 Controller beans found: " + controllers.size());
            controllers.forEach((name, bean) -> System.out.println("→ " + name + " @ " + bean.getClass().getName()));
        };
    }
}
