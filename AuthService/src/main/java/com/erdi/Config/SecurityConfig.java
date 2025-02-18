	package com.erdi.Config;

	import org.springframework.context.annotation.Bean;
	import org.springframework.context.annotation.Configuration;
	import org.springframework.security.authentication.AuthenticationManager;
	import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
	import org.springframework.security.config.annotation.web.builders.HttpSecurity;
	import org.springframework.security.config.http.SessionCreationPolicy;
	import org.springframework.security.web.SecurityFilterChain;
	import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

	@Configuration
	public class SecurityConfig {

		@Bean
		public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
			return authenticationConfiguration.getAuthenticationManager();
		}

		@Bean
		public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
			CookieCsrfTokenRepository csrfTokenRepository = customCookieCsrfTokenRepository();
			http
					.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
					.csrf(csrf -> csrf
							.csrfTokenRepository(csrfTokenRepository)
							.ignoringRequestMatchers("/**"))
					.authorizeHttpRequests(auth -> auth
							.requestMatchers("/auth/**").permitAll()
							.anyRequest().authenticated());

			return http.build();
		}

		private CookieCsrfTokenRepository customCookieCsrfTokenRepository() {
			CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
			repository.setCookieCustomizer(cookieBuilder -> cookieBuilder.secure(false));
			return repository;
		}
	}
