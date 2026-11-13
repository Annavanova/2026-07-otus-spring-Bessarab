package ru.otus.hw.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringWebFluxTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.reactive.ThymeleafReactiveViewResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Configuration
public class ThymeleafConfig {

    @Bean
    public ITemplateResolver templateResolver(ApplicationContext applicationContext) {
        var resolver = new SpringResourceTemplateResolver();
        resolver.setApplicationContext(applicationContext);
        resolver.setPrefix("classpath:/templates/"); // ваши файлы остаются здесь
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCacheable(false);
        return resolver;
    }

    @Bean
    public SpringWebFluxTemplateEngine templateEngine(ITemplateResolver templateResolver) {
        var engine = new SpringWebFluxTemplateEngine();
        engine.setTemplateResolver(templateResolver);
        return engine;
    }

    @Bean
    public ThymeleafReactiveViewResolver viewResolver(SpringWebFluxTemplateEngine templateEngine) {
        var resolver = new ThymeleafReactiveViewResolver();
        resolver.setTemplateEngine(templateEngine);
        return resolver;
    }
}
