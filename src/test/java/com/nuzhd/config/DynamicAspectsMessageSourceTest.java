package com.nuzhd.config;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class DynamicAspectsMessageSourceTest {

    private final DynamicAspectsConfig config = new DynamicAspectsConfig();

    @Test
    void dynamicAspectsMessageSource_ReturnsMessageFromRussianBundle() {
        var messageSource = config.dynamicAspectsMessageSource();

        var message = messageSource.getMessage(
                "dynamic.aspects.info.start",
                null,
                Locale.forLanguageTag("ru-RU")
        );

        assertThat(message).isEqualTo("Создание динамических аспектов...");
    }

    @Test
    void dynamicAspectsMessageSource_ReturnsEmptyMessageFromBaseBundle() {
        var messageSource = config.dynamicAspectsMessageSource();

        var message = messageSource.getMessage(
                "dynamic.aspects.info.start",
                null,
                Locale.ROOT
        );

        assertThat(message).isEmpty();
    }
}
