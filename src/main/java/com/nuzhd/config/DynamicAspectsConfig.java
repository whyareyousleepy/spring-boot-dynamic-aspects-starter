package com.nuzhd.config;

import com.nuzhd.creators.DynamicAspectsCreator;
import com.nuzhd.domain.DesignatorType;
import com.nuzhd.validation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Locale;
import java.util.Map;

import static ru.vtb.conp.commons.dynamic.aspects.starter.messages.DynamicAspectsMessageKeys.CREATE_ASPECTS_START_KEY;

@Configuration
@ConditionalOnProperty(name = "app.dynamic-aspects.enabled", havingValue = "true")
public class DynamicAspectsConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicAspectsConfig.class);

    @Bean
    public static DynamicAspectsCreator customAdvisorsConfiguration(
            ConfigurableEnvironment environment,
            @Qualifier("dynamicAspectsMessageSource")
            MessageSource messageSource,
            Map<DesignatorType, PointcutValidationService> validators) {
        LOGGER.info(messageSource.getMessage(CREATE_ASPECTS_START_KEY, null, Locale.ROOT));
        return new DynamicAspectsCreator(environment, messageSource, validators);
    }

    @Bean
    public MessageSource dynamicAspectsMessageSource() {

        var messageSource = new ReloadableResourceBundleMessageSource();

        messageSource.setBasename("messages/dynamic_aspects_message");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);

        return messageSource;
    }

    @Bean(name = "validators")
    public Map<DesignatorType, PointcutValidationService> pointcutValidators(
            @Qualifier("dynamicAspectsMessageSource")
            MessageSource messageSource
    ) {
        return Map.of(
                DesignatorType.EXECUTION, new ExecutionPointcutValidationService(messageSource),
                DesignatorType.WITHIN, new WithinPointcutValidationService(messageSource),
                DesignatorType.THIS, new ThisPointcutValidationService(messageSource),
                DesignatorType.TARGET, new TargetPointcutValidationService(messageSource),
                DesignatorType.ARGS, new ArgsPointcutValidationService(messageSource),
                DesignatorType.AT_TARGET, new AtTargetPointcutValidationService(messageSource),
                DesignatorType.AT_ARGS, new AtArgsPointcutValidationService(messageSource),
                DesignatorType.AT_WITHIN, new AtWithinPointcutValidationService(messageSource),
                DesignatorType.AT_ANNOTATION, new AtAnnotationPointcutValidationService(messageSource),
                DesignatorType.BEAN, new BeanPointcutValidationService(messageSource)
        );
    }
}
