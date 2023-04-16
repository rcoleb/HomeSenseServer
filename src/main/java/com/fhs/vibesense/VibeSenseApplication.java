package com.fhs.vibesense;

import com.fhs.vibesense.rmq.RabbitMQConfig;
import com.fhs.vibesense.rmq.RabbitMQReceiver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class VibeSenseApplication {

    public static void main(String[] args) {
        SpringApplication.run(VibeSenseApplication.class, args);
    }

}
