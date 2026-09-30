package com.nuzhd.validation;

import com.nuzhd.domain.DesignatorType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.context.MessageSource;

import java.util.Locale;

public abstract class PointcutValidationService {

    private final MessageSource messageSource;
    private final DesignatorType designatorType;
    private final String invalidExpressionKey;

    protected PointcutValidationService(
            MessageSource messageSource,
            DesignatorType designatorType,
            String invalidExpressionKey
    ) {
        this.messageSource = messageSource;
        this.designatorType = designatorType;
        this.invalidExpressionKey = invalidExpressionKey;
    }

    public void validateExpression(String pointcutExpression) {
        if (StringUtils.isBlank(pointcutExpression)) {
            throw invalidExpressionException(null);
        }

        try {
            AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
            pointcut.setExpression(designatorType.getValue() + "(" + pointcutExpression + ")");
            pointcut.getPointcutExpression();
        } catch (IllegalArgumentException exception) {
            throw invalidExpressionException(exception);
        }
    }

    protected MessageSource getMessageSource() {
        return messageSource;
    }

    public DesignatorType getDesignatorType() {
        return designatorType;
    }

    private IllegalArgumentException invalidExpressionException(IllegalArgumentException cause) {
        var message = messageSource.getMessage(
                invalidExpressionKey,
                new Object[] {designatorType.getValue()},
                Locale.ROOT
        );

        if (cause == null) {
            return new IllegalArgumentException(message);
        }

        return new IllegalArgumentException(message, cause);
    }

}
