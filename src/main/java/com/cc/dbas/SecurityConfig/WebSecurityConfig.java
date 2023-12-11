package com.cc.dbas.SecurityConfig;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig  {
	
	 @Autowired
	    private jwtAuthenticationEntryPoint point;
	 
	 @Autowired
	    private JwtRequestFilter filter;
	 
	 @Lazy
	 @Autowired
	  private UserDetailsService userDetailService;
	 
	 
	 @Bean
		public PasswordEncoder passwordEncoder() {
			return new BCryptPasswordEncoder();
		}


	    @Bean
	    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

	        http.csrf(csrf -> csrf.disable())
	                .authorizeRequests().
	                requestMatchers("/users/authenticate", "/users/createuser").permitAll()
	                .anyRequest()
	                .authenticated()
	                .and().exceptionHandling(ex -> ex.authenticationEntryPoint(point))
	                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
	        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
	        return http.build();
	    }
	    
	   

	    @Bean
	    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) {
	        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
	        provider.setPasswordEncoder(passwordEncoder);
	        provider.setUserDetailsService(userDetailsService);
	        return new ProviderManager(provider);
	    }

}
