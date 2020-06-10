package lol.maki.lab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class NullSyslogServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(NullSyslogServerApplication.class, args);
	}

}
