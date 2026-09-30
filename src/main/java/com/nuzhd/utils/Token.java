package com.nuzhd.utils;

import org.aopalliance.intercept.MethodInvocation;

@FunctionalInterface
public interface Token {

    void append(
            StringBuilder sb,
            MethodInvocation invocation,
            Object maskedArgs,
            Object returnValue
    );
}