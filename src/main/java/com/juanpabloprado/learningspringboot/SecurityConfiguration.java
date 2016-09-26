package com.juanpabloprado.learningspringboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
  @Override protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
        .anyRequest().authenticated()
        .and()
        .formLogin()
        .permitAll();
  }

  @Autowired
  public void configureJpaBasedUsers(AuthenticationManagerBuilder auth, SpringDataUserDetailsService userDetailsService) throws Exception {
    auth.userDetailsService(userDetailsService);
  }

  //@Autowired
  //public void configureInMemoryUsers(AuthenticationManagerBuilder auth) throws Exception {
  //  auth.inMemoryAuthentication()
  //      .withUser("juan").password("prado").roles("ADMIN", "USER")
  //      .and()
  //      .withUser("rob").password("winch").roles("USER")
  //      .and()
  //      .withUser("user1").password("password").roles("USER").disabled(true)
  //      .and()
  //      .withUser("user2").password("password").roles("USER").accountLocked(true);
  //}
}
