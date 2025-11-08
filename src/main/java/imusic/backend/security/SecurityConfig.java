package imusic.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import imusic.backend.token.JwtAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, AuthenticationProvider authenticationProvider) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/uploads/**", "/api/products/paged",
                                "/api/ref/product-categories", "/api/ref/product-units","/api/products/*",
                                "/api/products/*/attributes-with-values").permitAll()

                        .requestMatchers("/api/users/*/change-login", "/api/users/*",
                                "/api/users/*/change-password", "/api/users/*/profile",
                                "/api/users/*/avatar","/api/users/by-role/*", "/api/orders/paged",
                                "/api/orders","/api/orders/*").hasAnyRole("ADMIN", "CLIENT", "MANAGER")

                        .requestMatchers("/api/ref/task-statuses", "/api/clients",
                                "/api/orders/all","/api/orders/by-client",
                                "/api/order-status-history","/api/ref/order-statuses",
                                "/api/clients/paged","/api/users/clients/available","/api/users/paged", "/api/statistics/**").hasAnyRole("ADMIN","MANAGER")

                        .requestMatchers("/api/clients/profile", "/api/cart/**").hasRole("CLIENT")

                        .requestMatchers("/api/ref/**","/api/clients/**", "/api/products/**","/api/category-attributes/**",
                                "/api/product-attributes/**", "/api/inventory-movements/**","/api/orders/**",
                                "/api/users/paged", "/api/statistics/**").hasAnyRole("ADMIN", "MANAGER")

                        .requestMatchers("/api/users/clients/*").hasRole("MANAGER")
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}



