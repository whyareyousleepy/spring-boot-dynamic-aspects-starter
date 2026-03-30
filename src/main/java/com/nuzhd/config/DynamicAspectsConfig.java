package com.nuzhd.config;

import com.nuzhd.creators.DynamicAspectsCreator;
import com.nuzhd.domain.DesignatorType;
import com.nuzhd.interceptors.MethodAroundInterceptor;
import com.nuzhd.validation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Locale;
import java.util.Map;

@Configuration
@ConditionalOnProperty(name = "app.dynamic-aspects.enabled", havingValue = "true")
public class DynamicAspectsConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicAspectsConfig.class);
    private static final String CREATE_ASPECTS_START_KEY = "dynamic.aspects.info.start";


    @Bean
    public static DynamicAspectsCreator customAdvisorsConfiguration(
            ConfigurableEnvironment environment,
            Map<DesignatorType, PointcutValidationService> validators,
            MessageSource messageSource) {
        LOGGER.info(messageSource.getMessage(CREATE_ASPECTS_START_KEY, null, Locale.ROOT));
        return new DynamicAspectsCreator(environment, methodAroundInterceptor(messageSource), messageSource, validators);
    }

    @Bean
    public static MethodAroundInterceptor methodAroundInterceptor(
            MessageSource messageSource) {
        return new MethodAroundInterceptor(messageSource);
    }

    @Bean(name = "validators")
    public Map<DesignatorType, PointcutValidationService> pointcutValidators(
            MessageSource messageSource
    ) {
        return Map.of(
                DesignatorType.EXECUTION, new ExecutionPointcutValidationService(messageSource),
                DesignatorType.WITHIN, new WithinPointcutValidationService(),
                DesignatorType.THIS, new ThisPointcutValidationService(),
                DesignatorType.TARGET, new TargetPointcutValidationService(),
                DesignatorType.ARGS, new ArgsPointcutValidationService(),
                DesignatorType.AT_TARGET, new AtTargetPointcutValidationService(),
                DesignatorType.AT_ARGS, new AtArgsPointcutValidationService(),
                DesignatorType.AT_WITHIN, new AtWithinPointcutValidationService(),
                DesignatorType.AT_ANNOTATION, new AtAnnotationPointcutValidationService(),
                DesignatorType.BEAN, new BeanPointcutValidationService()
        );
    }

    @Bean
    public MessageSource dynamicAspectsMessageSource() {

        var messageSource = new ReloadableResourceBundleMessageSource();

        messageSource.setBasename("messages/dynamic_aspects_message");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);

        return messageSource;
    }
}
