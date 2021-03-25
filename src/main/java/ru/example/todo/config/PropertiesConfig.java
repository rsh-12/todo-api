package ru.example.todo.config;
/*
 * Date: 3/25/21
 * Time: 2:07 PM
 * */

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import ru.example.todo.config.properties.TokenProperties;

@Configuration
@EnableConfigurationProperties({TokenProperties.class})
public class PropertiesConfig {
}
