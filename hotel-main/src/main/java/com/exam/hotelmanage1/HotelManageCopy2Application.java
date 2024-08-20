package com.exam.hotelmanage1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HotelManageCopy2Application {

    public static void main(String[] args) {
        SpringApplication.run(HotelManageCopy2Application.class, args);
    }

}
