package by.clevertec;

import by.clevertec.env.EnvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@Profile("dev")
public class NewsSecurityServerApplication {
	public static void main(String[] args) {
		EnvLoader.loadEnv();
		SpringApplication.run(NewsSecurityServerApplication.class, args);
	}
}
