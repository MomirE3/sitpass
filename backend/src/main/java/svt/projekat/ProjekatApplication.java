package svt.projekat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class ProjekatApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProjekatApplication.class, args);
	}

}
