package ru.vtb.conp.commons.dynamic.aspects.starter.validators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nuzhd.CustomPointcutExpression;
import com.nuzhd.config.DynamicAspectsConfig;
import com.nuzhd.domain.DesignatorType;
import com.nuzhd.validation.ExecutionPointcutValidationService;
import com.nuzhd.validation.PointcutValidationService;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static com.nuzhd.domain.DesignatorType.EXECUTION;

class ExecutionPointcutValidationServiceTest {

    private CustomPointcutExpression pointcut;

    private void voidMethod() {}

    public String stringMethod() {
        return "String";
    }

    @BeforeEach
    void setUp() {
        DynamicAspectsConfig config = new DynamicAspectsConfig();
        var messageSource = config.dynamicAspectsMessageSource();
        Map<DesignatorType, PointcutValidationService> validators = Map.of(
                EXECUTION, new ExecutionPointcutValidationService(messageSource)
        );
        pointcut = new CustomPointcutExpression(validators, messageSource);
    }

    @Test
    void setExpression_InvalidDesignator_ThrowsExceptionWithCorrectMessage() {
        var exception = assertThrows(IllegalArgumentException.class,
                                     () -> pointcut.setExpression("exec(* *())")
        );

        assertThat(exception.getMessage())
                .contains(
                        "Pointcut выражение должно начинаться с одного из ключевых слов: [execution, within, this, target, args, @target, @args, @within, @annotation, bean]"
                );
    }

    @Test
    void setExpression_AbsentDesignator_ThrowsException() {
        var exception = assertThrows(IllegalArgumentException.class,
                                     () -> pointcut.setExpression("(public * *(..))")
        );

        assertThat(exception.getMessage())
                .contains(
                        "Pointcut выражение должно начинаться с одного из ключевых слов: [execution, within, this, target, args, @target, @args, @within, @annotation, bean]"
                );
    }

    @Test
    void setExpression_ExecutionDesignator_CertainMethod_CorrectExpression() {
        assertDoesNotThrow(
                () -> pointcut.setExpression(
                        "execution(* ru.vtb.conp.commons.dynamic.aspects.starter.validators.ExecutionPointcutValidationServiceTest.voidMethod(..))")
        );
    }

    @Test
    void setExpression_ExecutionDesignator_CertainMethod_MethodDoesntExist() {
        var exception = assertThrows(IllegalArgumentException.class,
                                     () -> pointcut.setExpression("execution(* ru.vtb.conp.commons.dynamic.aspects.starter.validators.ExecutionPointcutValidationServiceTest.strangeMethod(..))")
        );

        assertThat(exception.getMessage())
                .contains(
                        "Метода strangeMethod() не существует в классе ru.vtb.conp.commons.dynamic.aspects.starter.validators.ExecutionPointcutValidationServiceTest. Проверьте корректность имени пакета, класса и метода"
                );
    }

    @Test
    void setExpression_ExecutionDesignator_UnexistingClass_ThrowsError() {
        var exception = assertThrows(IllegalArgumentException.class,
                                     () -> pointcut.setExpression("execution(* ru.vtb.conp.commons.dynamic.aspects.starter.validators.SomeClass.*(..))")
        );

        assertThat(exception.getMessage())
                .contains(
                        "Класса ru.vtb.conp.commons.dynamic.aspects.starter.validators.SomeClass не существует. Проверьте корректность имени пакета и класса"
                );
    }

    @Test
    void setExpression_ExecutionDesignator_WrongAccessModifier_ThrowsError() {
        var exception = assertThrows(IllegalArgumentException.class,
                                     () -> pointcut.setExpression("execution(private * ru.vtb.conp.commons.dynamic.aspects.starter.validators.ExecutionPointcutValidationServiceTest.stringMethod(..))")
        );

        assertThat(exception.getMessage())
                .contains(
                        "Некорректный модификатор доступа у метода stringMethod()."
                );
    }

    @Test
    void setExpression_ExecutionDesignator_WrongReturnType_ThrowsError() {
        var exception = assertThrows(IllegalArgumentException.class,
                                     () -> pointcut.setExpression("execution(public void ru.vtb.conp.commons.dynamic.aspects.starter.validators.ExecutionPointcutValidationServiceTest.stringMethod(..))")
        );

        assertThat(exception.getMessage())
                .contains(
                        "Некорректный возвращаемый тип у метода stringMethod(). Метод имеет возвращаемый тип String"
                );
    }

    @Test
    void setExpression_ExecutionDesignator_AllClassesInPackage_CorrectExpression() {
        assertDoesNotThrow(
                () -> pointcut.setExpression(
                        "execution(* ru.vtb.conp.commons.dynamic.aspects.starter.*.*(..))")
        );
    }

    @Test
    void setExpression_ExecutionDesignator_ComplexExpression_CorrectExpression() {
        assertDoesNotThrow(
                () -> pointcut.setExpression(
                        "execution(* ru.vtb.conp.commons.dynamic.aspects.starter.SomeClass.*(..)) && args(accountHolderNamePattern)")
        );
    }
}