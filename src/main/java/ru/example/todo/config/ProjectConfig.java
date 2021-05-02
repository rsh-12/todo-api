package ru.example.todo.config;
/*
 * Date: 3/25/21
 * Time: 10:29 AM
 * */

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.example.todo.service.TokenStore;
import ru.example.todo.service.impl.InMemoryTokenStore;
import ru.example.todo.util.StringToDateEnumConverter;
import ru.example.todo.util.StringToSetTasksEnumConverter;
import ru.example.todo.util.StringToStatusEnumConverter;

@Configuration
public class ProjectConfig implements WebMvcConfigurer {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }


    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToStatusEnumConverter());
        registry.addConverter(new StringToDateEnumConverter());
        registry.addConverter(new StringToSetTasksEnumConverter());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*");
    }
}
