package com.nuzhd.domain;

public class DynamicAspectDto {

    private String expression;
    private String customBeforeMessage;
    private String customAfterMessage;

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getCustomBeforeMessage() {
        return customBeforeMessage;
    }

    public void setCustomBeforeMessage(String customBeforeMessage) {
        this.customBeforeMessage = customBeforeMessage;
    }

    public String getCustomAfterMessage() {
        return customAfterMessage;
    }

    public void setCustomAfterMessage(String customAfterMessage) {
        this.customAfterMessage = customAfterMessage;
    }
}
