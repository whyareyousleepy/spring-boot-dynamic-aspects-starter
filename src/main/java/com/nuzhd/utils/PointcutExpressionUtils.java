package com.nuzhd.utils;

import org.apache.commons.lang3.StringUtils;

import com.nuzhd.domain.DesignatorType;

import java.util.Arrays;

public class PointcutExpressionUtils {

    public static String extractClassName(String expression) {
        String[] parts = expression.replaceAll("\\(.*\\)", "").split("\\.");
        return String.join(".", Arrays.copyOfRange(parts, 0, parts.length - 1));
    }

    public static String extractMethodName(String expression) {
        String[] parts = expression.replaceAll("\\(.*\\)", "").split("\\.");
        return parts[parts.length - 1];
    }

    public static String extractExpression(String rawExpression, DesignatorType designator) {
        return switch (designator) {
            case EXECUTION -> StringUtils.substring(rawExpression, 10, rawExpression.length() - 1);
            case WITHIN, TARGET -> StringUtils.substring(rawExpression, 7, rawExpression.length() - 1);
            case THIS, ARGS, BEAN -> StringUtils.substring(rawExpression, 5, rawExpression.length() - 1);
            case AT_ARGS -> StringUtils.substring(rawExpression, 6, rawExpression.length() - 1);
            case AT_TARGET, AT_WITHIN -> StringUtils.substring(rawExpression, 8, rawExpression.length() - 1);
            case AT_ANNOTATION -> StringUtils.substring(rawExpression, 12, rawExpression.length() - 1);
            case INVALID -> StringUtils.EMPTY;
        };
    }

}
