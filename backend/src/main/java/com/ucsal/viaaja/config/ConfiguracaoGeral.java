package com.ucsal.viaaja.config;

import java.time.Duration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(PropriedadesAplicacao.class)
public class ConfiguracaoGeral {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("comparacoes", "roteiros", "resumos");
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder, PropriedadesAplicacao props) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(props.getIa().getTimeoutSegundos()))
                .build();
    }
}
