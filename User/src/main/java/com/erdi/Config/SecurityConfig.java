	package com.erdi.Config;

	import org.springframework.context.annotation.Bean;
	import org.springframework.context.annotation.Configuration;
	import org.springframework.security.User.UserManager;
	import org.springframework.security.config.annotation.User.configuration.UserConfiguration;
	import org.springframework.security.config.annotation.web.builders.HttpSecurity;
	import org.springframework.security.config.http.SessionCreationPolicy;
	import org.springframework.security.web.SecurityFilterChain;

	@Configuration
	public class SecurityConfig {

		@Bean
		public UserManager UserManager(UserConfiguration UserConfiguration) throws Exception{
			return UserConfiguration.getUserManager();
		}

		@Bean
		public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
			http
					.sessionManagement(session ->
							session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
					.csrf(csrf -> csrf.disable())
					.authorizeHttpRequests(auth -> auth
							.requestMatchers("/auth/**").permitAll()
							.anyRequest().authenticated());

			return http.build();
		}
	}
