package com.nuzhd.utils;

import org.aopalliance.intercept.MethodInvocation;

public final class CompiledTemplate {

    public static final CompiledTemplate EMPTY = new CompiledTemplate(new Token[0]);

    private final Token[] tokens;

    public CompiledTemplate(Token[] tokens) {
        this.tokens = tokens;
    }

    public String render(
            MethodInvocation invocation,
            Object maskedArgs,
            Object returnValue
    ) {

        StringBuilder sb = new StringBuilder();

        for (Token token : tokens) {
            token.append(sb, invocation, maskedArgs, returnValue);
        }

        return sb.toString();
    }
}