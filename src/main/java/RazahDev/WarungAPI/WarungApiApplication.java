package RazahDev.WarungAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableJpaRepositories
@EnableJpaAuditing
public class WarungApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(WarungApiApplication.class, args);
	}

}
