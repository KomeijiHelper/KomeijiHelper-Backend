package komeiji.back.config;

import komeiji.back.interceptor.LoginHandlerInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> excludeUrls = new ArrayList<>();
        excludeUrls.add("/**/login");
        excludeUrls.add("/**/logout");
        excludeUrls.add("/**/test");
        excludeUrls.add("/**/register");
        excludeUrls.add("/swagger-ui/**");
        excludeUrls.add("/swagger-ui.html/**");
        excludeUrls.add("/v3/api-docs/**");
        excludeUrls.add("/mail/sendCaptcha/register");
        excludeUrls.add("/mail/checkCaptcha/register");
        registry.addInterceptor(new LoginHandlerInterceptor()).addPathPatterns("/**").excludePathPatterns(excludeUrls);
    }
}
