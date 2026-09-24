package com.nuzhd.validation;

import com.nuzhd.CustomPointcutExpression;
import com.nuzhd.config.DynamicAspectsConfig;
import com.nuzhd.domain.DesignatorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

import static com.nuzhd.domain.DesignatorType.EXECUTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.vtb.conp.commons.dynamic.aspects.starter.messages.DynamicAspectsMessageKeys.CLASS_NOT_FOUND_KEY;
import static ru.vtb.conp.commons.dynamic.aspects.starter.messages.DynamicAspectsMessageKeys.INVALID_DESIGNATOR_KEY;
import static ru.vtb.conp.commons.dynamic.aspects.starter.messages.DynamicAspectsMessageKeys.METHOD_NOT_FOUND_KEY;
import static ru.vtb.conp.commons.dynamic.aspects.starter.messages.DynamicAspectsMessageKeys.WRONG_ACCESS_MODIFIER_KEY;
import static ru.vtb.conp.commons.dynamic.aspects.starter.messages.DynamicAspectsMessageKeys.WRONG_RETURN_TYPE_KEY;

class ExecutionPointcutValidationServiceTest {

    private CustomPointcutExpression pointcut;
    private DynamicAspectsConfig config;

    private void voidMethod() {
    }

    public String stringMethod() {
        return "String";
    }

    @BeforeEach
    void setUp() {
        config = new DynamicAspectsConfig();
        var messageSource = config.dynamicAspectsMessageSource();
        Map<DesignatorType, PointcutValidationService> validation = Map.of(
                EXECUTION, new ExecutionPointcutValidationService(messageSource)
        );
        pointcut = new CustomPointcutExpression(validation, messageSource);
    }

    @Test
    void setExpression_InvalidDesignator_ThrowsExceptionWithCorrectMessage() {
        var exception = assertThrows(IllegalArgumentException.class,
                                     () -> pointcut.setExpression("exec(* *())")
        );

        var expectedMessage = config.dynamicAspectsMessageSource().getMessage(
                INVALID_DESIGNATOR_KEY,
                new Object[] {Arrays.toString(DesignatorType.getValues())},
                Locale.ROOT
        );

        assertThat(exception.getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    void setExpression_AbsentDesignator_ThrowsException() {
        var exception = assertThrows(IllegalArgumentException.class,
                                     () -> pointcut.setExpression("(public * *(..))")
        );

        var expectedMessage = config.dynamicAspectsMessageSource().getMessage(
                INVALID_DESIGNATOR_KEY,
                new Object[] {Arrays.toString(DesignatorType.getValues())},
                Locale.ROOT
        );

        assertThat(exception.getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    void setExpression_ExecutionDesignator_CertainMethod_CorrectExpression() {
        assertDoesNotThrow(
                () -> pointcut.setExpression(
                        "execution(* com.nuzhd.validation.ExecutionPointcutValidationServiceTest.voidMethod(..))")
        );
    }

    @Test
    void setExpression_ExecutionDesignator_CertainMethod_MethodDoesntExist() {
        var exception = assertThrows(IllegalArgumentException.class,
                                     () -> pointcut.setExpression(
                                             "execution(* com.nuzhd.validation.ExecutionPointcutValidationServiceTest.strangeMethod(..))")
        );

        var expectedMessage = config.dynamicAspectsMessageSource().getMessage(
                METHOD_NOT_FOUND_KEY,
                new Object[] {"strangeMethod", "com.nuzhd.validation.ExecutionPointcutValidationServiceTest"},
                Locale.ROOT
        );

        assertThat(exception.getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    void setExpression_ExecutionDesignator_UnexistingClass_ThrowsError() {
        var exception = assertThrows(IllegalArgumentException.class,
                                     () -> pointcut.setExpression(
                                             "execution(* com.nuzhd.validation.SomeClass.*(..))")
        );

        var expectedMessage = config.dynamicAspectsMessageSource().getMessage(
                CLASS_NOT_FOUND_KEY,
                new Object[] {"com.nuzhd.validation.SomeClass"},
                Locale.ROOT
        );

        assertThat(exception.getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    void setExpression_ExecutionDesignator_WrongAccessModifier_ThrowsError() {
        var exception = assertThrows(IllegalArgumentException.class,
                                     () -> pointcut.setExpression(
                                             "execution(private * com.nuzhd.validation.ExecutionPointcutValidationServiceTest.stringMethod(..))")
        );

        var expectedMessage = config.dynamicAspectsMessageSource().getMessage(
                WRONG_ACCESS_MODIFIER_KEY,
                new Object[] {"stringMethod"},
                Locale.ROOT
        );

        assertThat(exception.getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    void setExpression_ExecutionDesignator_WrongReturnType_ThrowsError() {
        var exception = assertThrows(IllegalArgumentException.class,
                                     () -> pointcut.setExpression(
                                             "execution(public void com.nuzhd.validation.ExecutionPointcutValidationServiceTest.stringMethod(..))")
        );

        var expectedMessage = config.dynamicAspectsMessageSource().getMessage(
                WRONG_RETURN_TYPE_KEY,
                new Object[] {"stringMethod", "String"},
                Locale.ROOT);

        assertThat(exception.getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    void setExpression_ExecutionDesignator_AllClassesInPackage_CorrectExpression() {
        assertDoesNotThrow(
                () -> pointcut.setExpression("execution(* com.nuzhd.*.*(..))")
        );
    }

    @Test
    void setExpression_ExecutionDesignator_ComplexExpression_CorrectExpression() {
        assertDoesNotThrow(
                () -> pointcut.setExpression(
                        "execution(* com.nuzhd.validation.ExecutionPointcutValidationServiceTest.*(..)) && args(java.lang.String)")
        );
    }
}