package com.zenith.ai;

import com.zenith.ai.model.User;
import com.zenith.ai.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ZenithAiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZenithAiApplication.class, args);
	}

	@Bean
	CommandLineRunner initSystem(UserRepository userRepository) {
		return args -> {
			// Seed Hardcoded Manager
			String managerEmail = "manager@zenith.ai";
			if (userRepository.findByEmail(managerEmail).isEmpty()) {
				User manager = new User();
				manager.setName("Master Manager");
				manager.setEmail(managerEmail);
				manager.setPassword("managerpassword123");
				manager.setRole("ROLE_MANAGER");
				manager.setRegisteredBy("System Admin");
				manager.setAccessGranted(true);
				userRepository.save(manager);
				System.out.println(">> Hardcoded Manager seeded: manager@zenith.ai / managerpassword123");
			}

			// Seed Default Support Agent
			String agentEmail = "agent@zenith.ai";
			if (userRepository.findByEmail(agentEmail).isEmpty()) {
				User agent = new User();
				agent.setName("Senior Support Agent");
				agent.setEmail(agentEmail);
				agent.setPassword("agentpassword123");
				agent.setRole("ROLE_AGENT");
				agent.setRegisteredBy("Master Manager");
				agent.setAccessGranted(true);
				userRepository.save(agent);
				System.out.println(">> Default Agent seeded: zade@zenith.ai / zademed@123");
			}
		};
	}
}