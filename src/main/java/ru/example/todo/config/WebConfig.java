package ru.example.todo.config;
/*
 * Date: 1/15/21
 * Time: 8:47 AM
 * */

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.example.todo.util.StringToDateEnumConverter;
import ru.example.todo.util.StringToSetTasksEnumConverter;
import ru.example.todo.util.StringToStatusEnumConverter;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToStatusEnumConverter());
        registry.addConverter(new StringToDateEnumConverter());
        registry.addConverter(new StringToSetTasksEnumConverter());
    }
}
