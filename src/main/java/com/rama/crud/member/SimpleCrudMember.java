package com.rama.crud.member;

import com.rama.crud.member.domain.Role;
import com.rama.crud.member.domain.User;
import com.rama.crud.member.repository.RoleRepository;
import com.rama.crud.member.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.HashSet;

@SpringBootApplication
public class SimpleCrudMember {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(SimpleCrudMember.class, args);
	}


	@Bean
	CommandLineRunner init(UserRepository userRepository, RoleRepository roleRepository) {

		return args -> {

			Role adminRole = roleRepository.findByRole("ADMIN");
			if (adminRole == null) {
				Role newAdminRole = new Role();
				newAdminRole.setRole("ADMIN");
				roleRepository.save(newAdminRole);
			}

			Role userRole = roleRepository.findByRole("USER");
			if (userRole == null) {
				Role newUserRole = new Role();
				newUserRole.setRole("USER");
				roleRepository.save(newUserRole);
			}

			User newUser = userRepository.findByEmail("admin@admin.com");
			if( newUser == null ){
				User user = new User();
				user.setFullname("admin");
				user.setEmail("admin@admin.com");
				user.setPassword(bCryptPasswordEncoder.encode("123qwe"));
				user.setEnabled(true);
				Role newUserRole = roleRepository.findByRole("ADMIN");
				user.setRoles(new HashSet<>(Arrays.asList(newUserRole)));
				userRepository.save(user);
			}
		};

	}


}
