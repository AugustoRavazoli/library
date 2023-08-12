package com.example.library;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.example.library.user.UserRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
	
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
      .authorizeHttpRequests(authorize -> authorize
        .requestMatchers(
          new AntPathRequestMatcher("/error"),
          new AntPathRequestMatcher("/webjars/**")
        )
        .permitAll()
        .requestMatchers(
          new AntPathRequestMatcher("/"),
          new AntPathRequestMatcher("/register"),
          new AntPathRequestMatcher("/login")
        )
        .anonymous()
        .anyRequest().authenticated()
      )
      .formLogin(form -> form
        .loginPage("/login")
        .loginProcessingUrl("/login")
        .defaultSuccessUrl("/list-books", true)
        .failureUrl("/login?error")
      )
      .logout(logout -> logout
        .logoutUrl("/logout")
        .logoutSuccessUrl("/login?logout")
        .deleteCookies("JSESSIONID")
        .invalidateHttpSession(true)
      )
      .exceptionHandling(exceptions -> exceptions
        .authenticationEntryPoint(http401UnauthorizedEntryPoint())
      )
      .build();
  }
	
  @Bean
  public AuthenticationEntryPoint http401UnauthorizedEntryPoint() {
    return (request, response, authenticationException) -> response.sendError(401);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
	
  @Bean
  public UserDetailsService userDetailsService(UserRepository userRepository) {
    return username -> userRepository.findByUsername(username)
      .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }

}
