package com.example.smartReconciliationAndAudit.config;


import com.example.smartReconciliationAndAudit.security.JwtFilter;
import com.example.smartReconciliationAndAudit.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.file.ConfigurationSource;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Component
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFilter;
@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
http
        .csrf(csrf-> csrf.disable())
        .cors(cors->cors.configurationSource(config()))
        .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth->auth
        .requestMatchers("/api/auth/**","swagger-ui/**","api-docs/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/upload/**","/api/reconciliation/correct/**").hasAnyRole("ADMIN","ANALYST")
                        .anyRequest().authenticated()
        )
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
return http.build();
    }

   @Bean
    public CorsConfigurationSource config(){
       CorsConfiguration config=new CorsConfiguration();
      config.setAllowedOrigins(List.of("http://localhost:3000"));
      config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
              config.setAllowedHeaders(List.of("*"));
              config.setAllowCredentials(true);
              var src=new UrlBasedCorsConfigurationSource();
              src.registerCorsConfiguration("/**",config);
              return src;

   }
   @Bean
    public PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
   }
   @Bean
   public AuthenticationManager authManager(AuthenticationConfiguration cfg) throws Exception{
return cfg.getAuthenticationManager();
   }
}
