package com.nuzhd.domain;

import java.util.Arrays;

/**
 * @see <a href="https://docs.spring.io/spring-framework/reference/core/aop/ataspectj/pointcuts.html#aop-pointcuts-designators">Supported Designators</a>
 */
public enum DesignatorType {

    EXECUTION("execution"),
    WITHIN("within"),
    THIS("this"),
    TARGET("target"),
    ARGS("args"),
    AT_TARGET("@target"),
    AT_ARGS("@args"),
    AT_WITHIN("@within"),
    AT_ANNOTATION("@annotation"),
    BEAN("bean"),
    INVALID("invalid");

    private final String value;

    public String getValue() {
        return value;
    }

    public static String[] getValues() {
        return Arrays.stream(DesignatorType.values())
                     .filter(designatorType -> !INVALID.equals(designatorType))
                     .map(DesignatorType::getValue)
                     .toArray(String[]::new);
    }

    public static DesignatorType fromValue(String designator) {
        return Arrays.stream(DesignatorType.values())
                     .filter(designatorType -> !INVALID.equals(designatorType))
                     .filter(designatorType -> designatorType.value.equalsIgnoreCase(designator))
                     .findFirst()
                     .orElse(DesignatorType.INVALID);
    }

    DesignatorType(String value) {this.value = value;}
}
