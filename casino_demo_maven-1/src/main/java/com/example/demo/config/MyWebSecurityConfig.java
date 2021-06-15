package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;



@EnableWebSecurity
@Configuration
public class MyWebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())
        .withUser("admin").password(new BCryptPasswordEncoder().encode("123")).roles("ADMIN", "USER").and()
        .withUser("test1").password(new BCryptPasswordEncoder().encode("123")).roles("USER").and()
        .withUser("test2").password(new BCryptPasswordEncoder().encode("123")).roles("USER").and()
        .withUser("test3").password(new BCryptPasswordEncoder().encode("123")).roles("USER").and()
        .withUser("test4").password(new BCryptPasswordEncoder().encode("123")).roles("USER").and()
        .withUser("test5").password(new BCryptPasswordEncoder().encode("123")).roles("USER").and()
        .withUser("test6").password(new BCryptPasswordEncoder().encode("123")).roles("USER");
	}
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable();
		http.formLogin() // 定義當需要使用者登入時候，轉到的登入頁面。
				.and().authorizeRequests() // 定義哪些URL需要被保護、哪些不需要被保護
				.antMatchers("/favicon.ico","/assets/**","/images/**").permitAll()
				.antMatchers("/**").authenticated()
				.anyRequest() // 任何請求,登入後可以訪問
				.authenticated();
	}
	
}
