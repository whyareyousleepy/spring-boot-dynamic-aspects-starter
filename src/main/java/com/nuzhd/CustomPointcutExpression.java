package com.nuzhd;

import com.nuzhd.domain.DesignatorType;
import com.nuzhd.validation.PointcutValidationService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.context.MessageSource;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

import static com.nuzhd.domain.DesignatorType.INVALID;
import static com.nuzhd.utils.DynamicAspectsUtils.extractExpression;
import static ru.vtb.conp.commons.dynamic.aspects.starter.messages.DynamicAspectsMessageKeys.INVALID_DESIGNATOR_KEY;
import static ru.vtb.conp.commons.dynamic.aspects.starter.messages.DynamicAspectsMessageKeys.VALIDATOR_NOT_FOUND;

public class CustomPointcutExpression extends AspectJExpressionPointcut {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomPointcutExpression.class);

    private final Map<DesignatorType, PointcutValidationService> validators;
    private final MessageSource messageSource;

    public CustomPointcutExpression(Map<DesignatorType, PointcutValidationService> validators,
                                    MessageSource messageSource) {
        this.validators = validators;
        this.messageSource = messageSource;
    }

    @Override
    protected void onSetExpression(String expression) throws IllegalArgumentException {
        DesignatorType designatorType = DesignatorType.fromValue(
                StringUtils.trim(
                        StringUtils.substringBefore(expression, "(")
                )
        );

        if (INVALID.equals(designatorType)) {
            throw new IllegalArgumentException(messageSource.getMessage(
                    INVALID_DESIGNATOR_KEY,
                    new Object[] {Arrays.toString(DesignatorType.getValues())},
                    Locale.ROOT)
            );
        }

        String expressionBody = extractExpression(expression, designatorType);
        var validator = validators.get(designatorType);
        if (validator == null) {
            LOGGER.warn(messageSource.getMessage(VALIDATOR_NOT_FOUND, new Object[] {designatorType}, Locale.ROOT));
            return;
        }
        validator.validateExpression(expressionBody);
    }
}