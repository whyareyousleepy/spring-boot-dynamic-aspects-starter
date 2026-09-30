package com.nuzhd.validation;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import static com.nuzhd.domain.DesignatorType.AT_TARGET;
import static ru.vtb.conp.commons.dynamic.aspects.starter.messages.DynamicAspectsMessageKeys.INVALID_POINTCUT_EXPRESSION_KEY;

@Component
public class AtTargetPointcutValidationService extends PointcutValidationService {

    public AtTargetPointcutValidationService(MessageSource messageSource) {
        super(messageSource, AT_TARGET, INVALID_POINTCUT_EXPRESSION_KEY);
    }
}

