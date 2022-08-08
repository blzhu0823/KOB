package com.kob.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class BackendApplicationTests {

	@Test
	void contextLoads() {
		PasswordEncoder pe = new BCryptPasswordEncoder();
		System.out.println(pe.encode("woaini123"));
		System.out.println(pe.matches("woaini123", "$2a$10$AMnfvLWsuCP5uG0h4DoTeeGtQ5L6Rm.96kuD3lLNr/pJxnSpfYXN."));
	}

}
