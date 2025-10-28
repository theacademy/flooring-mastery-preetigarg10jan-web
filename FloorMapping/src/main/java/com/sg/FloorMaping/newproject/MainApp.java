package com.sg.FloorMaping.newproject;

import com.sg.FloorMaping.newproject.Controller.Controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class MainApp {
	public static void main(String[] args) {
	 try {
		 ApplicationContext ctx = SpringApplication.run(MainApp.class, args);
		 Controller controller = ctx.getBean(Controller.class);
		 controller.run();

	} catch (Exception e) {
		System.out.println("Fatal error starting application: " + e.getMessage());
		e.printStackTrace();
	}
}

}
