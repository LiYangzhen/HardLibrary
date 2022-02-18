package fudan.se.hardlibrary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

/**
 * Welcome to 2021 Software Engineering Lab2.
 * This is your first lab to write your own code and build a spring boot application.
 * Enjoy it :)
 *
 * @author LBW
 */
@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class HardLibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(HardLibraryApplication.class, args);
    }

}

