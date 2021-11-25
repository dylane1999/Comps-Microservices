package com.intellivest.userservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
class OktaOAuth2WebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    /**
     * overrides configure and adds okta oath resource on all routes besides those
     * listed, also disables csrf protection for now
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests().antMatchers(HttpMethod.PUT, "/user").permitAll().and()
                .authorizeRequests().antMatchers(HttpMethod.GET, "/actuator/**").permitAll().and().authorizeRequests()
                .anyRequest().authenticated().and().oauth2ResourceServer().jwt();

        http.headers().addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Origin", "*"));

        http.csrf().disable().cors();
    }
}