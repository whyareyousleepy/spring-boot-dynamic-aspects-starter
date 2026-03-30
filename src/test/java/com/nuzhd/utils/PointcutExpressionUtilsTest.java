package com.nuzhd.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.nuzhd.domain.DesignatorType;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

public class PointcutExpressionUtilsTest {

    private final PointcutExpressionUtils pointcutExpressionUtils = new PointcutExpressionUtils();

    @ParameterizedTest
    @MethodSource("provideExtractExpressions")
    void extractExpression_DifferentExpressions(DesignatorType designator,
                                                       String rawExpr,
                                                       String extractedExpr) {
        assertEquals(extractedExpr, PointcutExpressionUtils.extractExpression(rawExpr, designator));
    }

    private static Stream<Arguments> provideExtractExpressions() {
        return Stream.of(
                Arguments.of(EXECUTION, "execution(ru.vtb.conp.TestClass.method(..))",
                             "ru.vtb.conp.TestClass.method(..)"),
                Arguments.of(WITHIN, "within(ru.vtb.conp..*)", "ru.vtb.conp..*"),
                Arguments.of(THIS, "this(ru.vtb.conp.service.TestClass)", "ru.vtb.conp.service.TestClass"),
                Arguments.of(TARGET, "target(ru.vtb.conp.service.TestClass)", "ru.vtb.conp.service.TestClass"),
                Arguments.of(ARGS, "args(java.io.Serializable)", "java.io.Serializable"),
                Arguments.of(AT_TARGET, "@target(org.springframework.transaction.annotation.Transactional)",
                             "org.springframework.transaction.annotation.Transactional"),
                Arguments.of(AT_ARGS, "@args(ru.vtb.conp.SomeAnnotation)", "ru.vtb.conp.SomeAnnotation"),
                Arguments.of(AT_WITHIN, "@within(org.springframework.transaction.annotation.Transactional)",
                             "org.springframework.transaction.annotation.Transactional"),
                Arguments.of(AT_ANNOTATION, "@annotation(org.springframework.transaction.annotation.Transactional)",
                             "org.springframework.transaction.annotation.Transactional"),
                Arguments.of(BEAN, "bean(SomeClass)", "SomeClass")
        );
    }

}
