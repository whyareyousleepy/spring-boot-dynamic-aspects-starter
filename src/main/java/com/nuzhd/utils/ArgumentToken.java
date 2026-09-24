package com.nuzhd.utils;

import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.nuzhd.consts.TemplateConstants.UNKNOWN_ARG_TEMPLATE;
import static com.nuzhd.utils.DynamicAspectsUtils.extractMethodName;

public record ArgumentToken(String argumentName) implements Token {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArgumentToken.class);

    @Override
    public void append(StringBuilder sb, MethodInvocation invocation, Object maskedArgs, Object returnValue) {
        var argValue = DynamicAspectsUtils.getArgsAndValues(invocation).get(argumentName);
        if (argValue == null) {
            LOGGER.warn(
                    "В методе {} нет параметра {}. Проверьте правильность имени параметра в кастомном сообщении",
                    extractMethodName(invocation),
                    argumentName
            );
            sb.append(UNKNOWN_ARG_TEMPLATE.formatted(argumentName));
        } else {
            sb.append(argValue);
        }
    }
}