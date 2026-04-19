package in.scalive.votezy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final FirebaseAuthInterceptor firebaseAuthInterceptor;

    public WebConfig(FirebaseAuthInterceptor firebaseAuthInterceptor) {
        this.firebaseAuthInterceptor = firebaseAuthInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(firebaseAuthInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/voters/register",
                        "/api/elections/active",
                        "/api/elections/active/one"
                );
    }
}