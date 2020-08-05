package org.example.conf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.parameter.FilterArgumentResolver;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class BaseConfiguration implements WebMvcConfigurer, ApplicationContextAware {

    private static Gson generalGson;

    private static ApplicationContext context;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new FilterArgumentResolver());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static Object getBean(String beanName) {
        if (context != null) {
            return context.getBean(beanName);
        }
        return null;
    }

    public static Gson generalGson() {
        if (generalGson == null) {
            generalGson = ((GsonBuilder) BaseConfiguration.getBean("gsonBuilder")).create();
        }
        return generalGson;
    }

    @Bean
    public GsonBuilder gsonBuilder() {
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .serializeSpecialFloatingPointValues()
                ;
    }
}
