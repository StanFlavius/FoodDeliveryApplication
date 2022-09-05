package com.example.fooddelivery.config;

import com.example.fooddelivery.config.jwt.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtFilter jwtFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.headers().frameOptions().disable();
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/register", "/auth").permitAll()
                .antMatchers("/getU/").permitAll()
                .antMatchers("/admin/get").hasRole("ADMIN")
                .antMatchers("/user/get").hasRole("USER")
                .antMatchers("/del/get").hasRole("DELIVERY")
                .antMatchers("/user/editPass/*").permitAll()
                .antMatchers("/user/editLoc/*").permitAll()
                .antMatchers("/menu/add").hasRole("ADMIN")
                .antMatchers("/menu/editPrice/*/*").hasRole("ADMIN")
                .antMatchers("/menu/editProductList/*").hasRole("ADMIN")
                .antMatchers("/menu/deleteMenu").hasRole("ADMIN")
                .antMatchers("/menu/getMenus/*").hasAnyRole("ADMIN", "USER")
                .antMatchers("/product").hasRole("ADMIN")
                .antMatchers("/product/*").hasRole("ADMIN")
                .antMatchers("/delivery/*").hasRole("ADMIN")
                .antMatchers("/delivery/*/*/*").hasRole("ADMIN")
                .antMatchers("/delivery/*/*/*/*").hasRole("ADMIN")
                .antMatchers("/order/edit/*").hasRole("DELIVERY")
                .antMatchers("/order/getAll").hasRole("ADMIN")
                .antMatchers("/order/getOne/*").hasRole("USER")
                .antMatchers("/order/money/*").hasRole("ADMIN")
                .antMatchers("/order/add/").hasRole("USER")
                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
