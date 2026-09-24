package com.nuzhd.config;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.vtb.conp.commons.dynamic.aspects.starter.messages.DynamicAspectsMessageKeys.CREATE_ASPECTS_START_KEY;

class DynamicAspectsMessageSourceTest {

    private final DynamicAspectsConfig config = new DynamicAspectsConfig();

    @Test
    void dynamicAspectsMessageSource_ReturnsMessageFromRussianBundle() {
        var messageSource = config.dynamicAspectsMessageSource();

        var message = messageSource.getMessage(
                CREATE_ASPECTS_START_KEY,
                null,
                Locale.forLanguageTag("ru-RU")
        );

        assertThat(message).isEqualTo("Создание динамических аспектов...");
    }

    @Test
    void dynamicAspectsMessageSource_ReturnsMessageFromBaseBundle() {
        var messageSource = config.dynamicAspectsMessageSource();

        var message = messageSource.getMessage(
                CREATE_ASPECTS_START_KEY,
                null,
                Locale.ROOT
        );

        assertThat(message).isEqualTo("Creating dynamic aspects...");
    }
}
