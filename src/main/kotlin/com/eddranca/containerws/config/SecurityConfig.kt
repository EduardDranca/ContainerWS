package com.eddranca.containerws.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain? {
        http.authorizeHttpRequests { authz -> authz
                .requestMatchers("/login").permitAll()
                .anyRequest()
                .authenticated()
        }
                .oauth2Login(withDefaults())
        return http.build()
    }
}
