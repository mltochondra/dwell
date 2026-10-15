package skim.dwell;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DwellApplication {

	public static void main(String[] args) {
		SpringApplication.run(DwellApplication.class, args);
	}

}
