package ru.example.todo.config;
/*
 * Date: 1/15/21
 * Time: 8:47 AM
 * */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import ru.example.todo.security.JwtTokenFilterConfigurer;
import ru.example.todo.service.JwtTokenService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenService jwtTokenService;

    public WebSecurityConfig(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests()
                .mvcMatchers("/").permitAll()
                .mvcMatchers("/actuator/**").permitAll()
                .mvcMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated();

        http.apply(new JwtTokenFilterConfigurer(jwtTokenService));
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .mvcMatchers("/v2/api-docs")
                .mvcMatchers("/swagger-resources/**")//
                .mvcMatchers("/swagger-ui/**")//
                .mvcMatchers("/configuration/**")//
                .mvcMatchers("/webjars/**")//
                .mvcMatchers("/css/**")//
                .mvcMatchers("/public/**");
    }

    @Bean
    GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults(""); // Remove the ROLE_ prefix
    }

}
