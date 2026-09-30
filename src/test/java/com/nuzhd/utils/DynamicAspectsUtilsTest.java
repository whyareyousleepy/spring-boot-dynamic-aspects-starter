package com.nuzhd.utils;

import com.nuzhd.domain.DesignatorType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.nuzhd.domain.DesignatorType.ARGS;
import static com.nuzhd.domain.DesignatorType.AT_ANNOTATION;
import static com.nuzhd.domain.DesignatorType.AT_ARGS;
import static com.nuzhd.domain.DesignatorType.AT_TARGET;
import static com.nuzhd.domain.DesignatorType.AT_WITHIN;
import static com.nuzhd.domain.DesignatorType.BEAN;
import static com.nuzhd.domain.DesignatorType.EXECUTION;
import static com.nuzhd.domain.DesignatorType.TARGET;
import static com.nuzhd.domain.DesignatorType.THIS;
import static com.nuzhd.domain.DesignatorType.WITHIN;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DynamicAspectsUtilsTest {

    @ParameterizedTest
    @MethodSource("provideExtractExpressions")
    void extractExpression_DifferentExpressions(DesignatorType designator,
                                                String rawExpr,
                                                String extractedExpr) {
        assertEquals(extractedExpr, DynamicAspectsUtils.extractExpression(rawExpr, designator));
    }

    private static Stream<Arguments> provideExtractExpressions() {
        return Stream.of(
                Arguments.of(EXECUTION, "execution(com.nuzhd.TestClass.method(..))",
                             "com.nuzhd.TestClass.method(..)"),
                Arguments.of(WITHIN, "within(com.nuzhd..*)", "com.nuzhd..*"),
                Arguments.of(THIS, "this(com.nuzhd.service.TestClass)", "com.nuzhd.service.TestClass"),
                Arguments.of(TARGET, "target(com.nuzhd.service.TestClass)", "com.nuzhd.service.TestClass"),
                Arguments.of(ARGS, "args(java.io.Serializable)", "java.io.Serializable"),
                Arguments.of(AT_TARGET, "@target(org.springframework.transaction.annotation.Transactional)",
                             "org.springframework.transaction.annotation.Transactional"),
                Arguments.of(AT_ARGS, "@args(com.nuzhd.SomeAnnotation)", "com.nuzhd.SomeAnnotation"),
                Arguments.of(AT_WITHIN, "@within(org.springframework.transaction.annotation.Transactional)",
                             "org.springframework.transaction.annotation.Transactional"),
                Arguments.of(AT_ANNOTATION, "@annotation(org.springframework.transaction.annotation.Transactional)",
                             "org.springframework.transaction.annotation.Transactional"),
                Arguments.of(BEAN, "bean(SomeClass)", "SomeClass")
        );
    }
}
