package kr.hjun.backend;

import io.github.cdimascio.dotenv.Dotenv;
import kr.hjun.backend.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class BackendApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
                .directory(System.getProperty("user.dir"))
                .load();

        dotenv.entries().forEach((entry) -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });

        SpringApplication.run(BackendApplication.class, args);
    }

}
