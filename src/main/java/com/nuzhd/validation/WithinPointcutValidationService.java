package com.nuzhd.validation;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import static com.nuzhd.domain.DesignatorType.WITHIN;
import static ru.vtb.conp.commons.dynamic.aspects.starter.messages.DynamicAspectsMessageKeys.INVALID_POINTCUT_EXPRESSION_KEY;

@Component
public class WithinPointcutValidationService extends PointcutValidationService {

    public WithinPointcutValidationService(MessageSource messageSource) {
        super(messageSource, WITHIN, INVALID_POINTCUT_EXPRESSION_KEY);
    }
}
