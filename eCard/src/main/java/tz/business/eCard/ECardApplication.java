package tz.business.eCard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
/**
 * @author JAMES EDWARD
 *
 * @January 19 , 2025
 */

@SpringBootApplication(scanBasePackages = "tz.business.eCard")
public class ECardApplication {

	public static void main(String[] args) {
		SpringApplication.run(ECardApplication.class, args);
	}
}
