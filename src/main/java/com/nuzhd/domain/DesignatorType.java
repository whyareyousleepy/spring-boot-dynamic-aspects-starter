package com.nuzhd.domain;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/// [Supported Designators](https://docs.spring.io/spring-framework/reference/core/aop/ataspectj/pointcuts.html#aop-pointcuts-designators)
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
        return Arrays.stream(DesignatorType.getValues())
                     .map(String::toUpperCase)
                     .filter(value -> StringUtils.equalsIgnoreCase(designator, value))
                     .map(DesignatorType::valueOf)
                     .findFirst()
                     .orElse(DesignatorType.INVALID);
    }

    DesignatorType(String value) {this.value = value;}
}
