package svt.projekat.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import svt.projekat.service.implementation.UserDetailsServiceImpl;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig {

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(this.userDetailsService());
        authProvider.setPasswordEncoder(this.passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(restAuthenticationEntryPoint))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/users/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/requests/createRequest").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/facilities").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/analytics/facility/{facilityId}/weekly").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/analytics/facility/{facilityId}/monthly").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/analytics/facility/{facilityId}/yearly").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/analytics/facility/{facilityId}/custom").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/facilities/{userId}/isManagerOrAdmin").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/requests").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/requests/rejectRequest/{id}").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/requests/acceptRequest/{id}").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/facilities/eligibleForReview").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/facilities/visits/{facilityId}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/disciplines/names").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/profile/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/profile/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/images/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/facilities/filter").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/facilities/cities").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/facilities/{id}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/facilities/homepage").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/facilities/newFacilities").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/facilities/createWithImages").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/facilities/{id}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/facilities/{id}/updateFacilityDetails").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/facilities/{id}/updateFacilityImages").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/exercises/create").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/reviews/create").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/reviews/hide/{id}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/reviews/show/{id}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/reviews/delete/{id}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/reviews/facility/{facilityId}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/reviews/all").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/reviews/user").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/reviews/reply").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/manager/assign").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/manager/remove").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users/all-users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/manager/manager").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/facilities/inactive").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/facilities/active").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/facilities/{facilityId}/isManager").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/facilities/{id}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/reviews/facility/{facilityId}/sortByRating").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/reviews/facility/{facilityId}/sortByDate").hasAnyRole("USER", "ADMIN")
                )
                .addFilterBefore(new AuthenticationTokenFilter(userDetailsService(), tokenUtils), UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(authenticationProvider())
                .build();
    }
}
