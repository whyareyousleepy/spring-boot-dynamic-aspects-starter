package com.nuzhd.validation;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import static com.nuzhd.domain.DesignatorType.AT_WITHIN;
import static ru.vtb.conp.commons.dynamic.aspects.starter.messages.DynamicAspectsMessageKeys.INVALID_POINTCUT_EXPRESSION_KEY;

@Component
public class AtWithinPointcutValidationService extends PointcutValidationService {

    public AtWithinPointcutValidationService(MessageSource messageSource) {
        super(messageSource, AT_WITHIN, INVALID_POINTCUT_EXPRESSION_KEY);
    }
}

