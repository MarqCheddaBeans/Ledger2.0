package com.pluralsight.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {

        if(args.length != 2){
            System.out.println("Enter User and Pass");
            System.exit(1);
        } else{
            System.setProperty("dbUsername", args[0]);
            System.setProperty("dbPassword", args[1]);
            SpringApplication.run(Application.class, args);
        }



	}

}
