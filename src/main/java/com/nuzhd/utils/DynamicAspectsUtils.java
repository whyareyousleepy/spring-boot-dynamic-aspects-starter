package com.nuzhd.utils;

import com.nuzhd.domain.DesignatorType;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class DynamicAspectsUtils {

    private DynamicAspectsUtils() {}

    public static String extractClassName(String expression) {
        String[] parts = expression.replaceAll("\\(.*\\)", "").split("\\.");
        return String.join(".", Arrays.copyOfRange(parts, 0, parts.length - 1));
    }

    public static String extractMethodName(String expression) {
        String[] parts = expression.replaceAll("\\(.*\\)", "").split("\\.");
        return parts[parts.length - 1];
    }

    public static String extractMethodName(MethodInvocation invocation) {
        return "%s()".formatted(invocation.getMethod().getName());
    }

    public static String extractFullMethodName(MethodInvocation invocation) {
        return "%s.%s()".formatted(
                invocation.getMethod().getDeclaringClass().getName(),
                invocation.getMethod().getName()
        );
    }

    public static String extractExpression(String rawExpression, DesignatorType designator) {
        if (designator == DesignatorType.INVALID || StringUtils.isBlank(rawExpression)) {
            return StringUtils.EMPTY;
        }

        var trimmed = rawExpression.trim();
        var openParentIndex = trimmed.indexOf('(');
        var closeParentIndex = trimmed.lastIndexOf(')');

        if (openParentIndex < 0 || closeParentIndex <= openParentIndex) {
            return StringUtils.EMPTY;
        }

        var prefix = trimmed.substring(0, openParentIndex).trim();
        if (!StringUtils.equalsIgnoreCase(prefix, designator.getValue())) {
            return StringUtils.EMPTY;
        }

        return trimmed.substring(openParentIndex + 1, closeParentIndex);
    }

    public static Map<String, Object> getArgsAndValues(MethodInvocation invocation) {
        var argNames = invocation.getMethod().getParameters();
        var argValues = invocation.getArguments();

        var result = new LinkedHashMap<String, Object>();

        for (int i = 0; i < argNames.length; i++) {
            result.put(argNames[i].getName(), argValues[i]);
        }

        return result;
    }

    public static boolean hasReturnValue(MethodInvocation invocation) {
        return !invocation.getMethod().getReturnType().equals(Void.TYPE);
    }

    public static boolean hasArgs(MethodInvocation invocation) {
        return invocation.getArguments().length != 0;
    }

}
