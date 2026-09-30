package com.nuzhd.validation;

import com.nuzhd.domain.DesignatorType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Locale;

import static com.nuzhd.domain.DesignatorType.EXECUTION;
import static com.nuzhd.utils.DynamicAspectsUtils.extractClassName;
import static com.nuzhd.utils.DynamicAspectsUtils.extractMethodName;
import static ru.vtb.conp.commons.dynamic.aspects.starter.messages.DynamicAspectsMessageKeys.CLASS_NOT_FOUND_KEY;
import static ru.vtb.conp.commons.dynamic.aspects.starter.messages.DynamicAspectsMessageKeys.INVALID_EXECUTION_EXPRESSION_KEY;
import static ru.vtb.conp.commons.dynamic.aspects.starter.messages.DynamicAspectsMessageKeys.METHOD_NOT_FOUND_KEY;
import static ru.vtb.conp.commons.dynamic.aspects.starter.messages.DynamicAspectsMessageKeys.WRONG_ACCESS_MODIFIER_KEY;
import static ru.vtb.conp.commons.dynamic.aspects.starter.messages.DynamicAspectsMessageKeys.WRONG_RETURN_TYPE_KEY;

public class ExecutionPointcutValidationService extends PointcutValidationService {

    private final MessageSource messageSource;

    public ExecutionPointcutValidationService(MessageSource messageSource) {
        super(messageSource, EXECUTION, INVALID_EXECUTION_EXPRESSION_KEY);
        this.messageSource = messageSource;
    }

    @Override
    public DesignatorType getDesignatorType() {
        return EXECUTION;
    }

    @Override
    public void validateExpression(String pointcutExpression) throws IllegalArgumentException {
        // Разделение на модификаторы доступа, тип возвращаемого значения, имя класса и метода
        String[] parts = pointcutExpression.trim().split("\\s+");

        if (parts.length < 2) {
            throw new IllegalArgumentException(messageSource.getMessage(INVALID_EXECUTION_EXPRESSION_KEY,
                                                                        null,
                                                                        Locale.ROOT)
            );
        }

        String rowClassName, modifiers, returnType;

        if (parts.length == 2) {
            returnType = "*";
            modifiers = "*";
            rowClassName = parts[1];
        } else if (parts.length == 3) {
            modifiers = parts[0];
            returnType = parts[1];
            rowClassName = parts[2];
        } else {
            super.validateExpression(pointcutExpression);
            return;
        }

        String className = extractClassName(rowClassName);
        String methodName = extractMethodName(rowClassName);

        // Проверка существования класса (пакета)
        if (!className.contains("*")) {
            try {
                Class<?> clazz = Class.forName(className); // Класс существует
                // Проверка методов
                if (!StringUtils.equals(methodName, "*")) {
                    // Метод существует
                    var existingMethod = Arrays.stream(clazz.getDeclaredMethods())
                                               .filter(method -> StringUtils.equalsIgnoreCase(
                                                               method.getName(),
                                                               methodName
                                                       )
                                               )
                                               .findAny()
                                               .orElseThrow(() -> new IllegalArgumentException(
                                                                    messageSource.getMessage(
                                                                            METHOD_NOT_FOUND_KEY,
                                                                            new Object[] {methodName, className},
                                                                            Locale.ROOT
                                                                    )
                                                            )
                                               );
                    if (!checkModifiers(modifiers, existingMethod)) {
                        throw new IllegalArgumentException(
                                messageSource.getMessage(WRONG_ACCESS_MODIFIER_KEY,
                                                         new Object[] {existingMethod.getName()},
                                                         Locale.ROOT
                                )
                        );
                    }
                    if (!checkReturnType(returnType, existingMethod)) {
                        throw new IllegalArgumentException(
                                messageSource.getMessage(WRONG_RETURN_TYPE_KEY,
                                                         new Object[] {existingMethod.getName(), existingMethod.getReturnType().getSimpleName()},
                                                         Locale.ROOT
                                )
                        );
                    }
                }
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(
                        messageSource.getMessage(
                                CLASS_NOT_FOUND_KEY,
                                new Object[] {className},
                                Locale.ROOT
                        )
                );
            }
        }
    }

    private boolean checkModifiers(String modifiers, Method method) {
        if (modifiers.isEmpty() || modifiers.equals("*")) {
            return true;
        }
        return switch (modifiers) {
            case "public" -> Modifier.isPublic(method.getModifiers());
            case "private" -> Modifier.isPrivate(method.getModifiers());
            case "protected" -> Modifier.isProtected(method.getModifiers());
            default -> false;
        };
    }

    private boolean checkReturnType(String returnType, Method method) {
        if (returnType.equals("*")) {
            return true;
        }
        try {
            Class<?> expectedReturnType = Class.forName(returnType);
            return expectedReturnType.isAssignableFrom(method.getReturnType());
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
