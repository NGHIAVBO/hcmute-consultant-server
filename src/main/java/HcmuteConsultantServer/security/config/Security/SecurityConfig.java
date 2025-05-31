package HcmuteConsultantServer.security.config.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import HcmuteConsultantServer.constant.SecurityConstants;
import HcmuteConsultantServer.model.exception.CustomAccessDeniedHandler;
import HcmuteConsultantServer.model.exception.CustomJWTHandler;
import HcmuteConsultantServer.security.authentication.UserDetailService;
import HcmuteConsultantServer.security.jwt.JwtEntryPoint;
import HcmuteConsultantServer.security.jwt.JwtTokenFilter;
import HcmuteConsultantServer.security.oauth2.CustomOAuth2UserService;
import HcmuteConsultantServer.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import HcmuteConsultantServer.security.oauth2.OAuth2AuthenticationFailureHandler;
import HcmuteConsultantServer.security.oauth2.OAuth2AuthenticationSuccessHandler;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailService userDetailService;

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Autowired
    private CustomJWTHandler customJWTHandler;

    @Autowired
    private JwtEntryPoint jwtEntryPoint;

    @Autowired
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "/public/**").permitAll()  // Cho phép truy cập không cần xác thực vào /
                .antMatchers("/ws/**").permitAll()
                .antMatchers("/oauth2/authorize/google", "/oauth2/callback/google").permitAll()  // Các endpoint OAuth2 công khai
                .antMatchers("https://hcmute-consultant-server-production.up.railway.app/oauth2/authorize/google",
                        "https://hcmute-consultant-server-production.up.railway.app/oauth2/callback/google").permitAll()  // Các endpoint OAuth2 công khai
                .antMatchers(SecurityConstants.NOT_JWT).permitAll()
                .antMatchers(SecurityConstants.JWT).authenticated()
                .antMatchers("/api/v1/upload").permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .accessDeniedHandler(customAccessDeniedHandler)  // Đảm bảo chỉ định 1 handler
                .authenticationEntryPoint(jwtEntryPoint) // Thêm EntryPoint cho JWT
                .and()
                .oauth2Login()  // Đảm bảo chỉ gọi 1 lần
                .loginPage("/login")
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler)
                .authorizationEndpoint()
                .baseUri("/oauth2/authorize")
                .authorizationRequestRepository(cookieAuthorizationRequestRepository())
                .and()
                .redirectionEndpoint()
                .baseUri("/oauth2/callback/*")
                .and()
                .userInfoEndpoint()
                .userService(customOAuth2UserService)
                .and()
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Quản lý session stateless
                .and()
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setMaxAge(3600L);
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "https://hcmute-consultant.vercel.app",
                "https://hcmute-consultant-server-production.up.railway.app"
        ));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList(
                "Access-Control-Allow-Headers",
                "Access-Control-Allow-Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers",
                "Origin",
                "Cache-Control",
                "Content-Type",
                "Authorization"
        ));
        configuration.setAllowedMethods(Arrays.asList("DELETE", "GET", "POST", "PATCH", "PUT", "OPTIONS"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

