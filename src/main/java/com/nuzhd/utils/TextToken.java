package com.nuzhd.utils;

import org.aopalliance.intercept.MethodInvocation;

public record TextToken(String value) implements Token {

    @Override
    public void append(
            StringBuilder sb,
            MethodInvocation invocation,
            Object maskedArgs,
            Object returnValue
    ) {
        sb.append(value);
    }
}