package com.nuzhd;

import com.nuzhd.domain.DesignatorType;
import com.nuzhd.utils.PointcutExpressionUtils;
import com.nuzhd.validation.PointcutValidationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.context.MessageSource;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

import static com.nuzhd.domain.DesignatorType.INVALID;

public class CustomPointcutExpression extends AspectJExpressionPointcut {

    private static final String INVALID_DESIGNATOR_KEY = "dynamic.aspects.error.invalid-designator";
    private final Map<DesignatorType, PointcutValidationService> validators;
    private final MessageSource messageSource;

    public CustomPointcutExpression(Map<DesignatorType, PointcutValidationService> validators,
                                    MessageSource messageSource) {
        this.validators = validators;
        this.messageSource = messageSource;
    }

    @Override
    protected void onSetExpression(String expression) throws IllegalArgumentException {
        DesignatorType designatorType = DesignatorType.fromValue(StringUtils.substringBefore(expression, "("));
        if (INVALID.equals(designatorType)) {
            throw new IllegalArgumentException(
                    messageSource.getMessage(INVALID_DESIGNATOR_KEY,
                                             new Object[] {Arrays.toString(DesignatorType.getValues())},
                                             Locale.ROOT));
        }
        String expressionBody = PointcutExpressionUtils.extractExpression(expression, designatorType);
        validators.get(designatorType).validateExpression(expressionBody);
    }

}
