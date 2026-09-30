package com.nuzhd.validation;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import static com.nuzhd.domain.DesignatorType.TARGET;
import static ru.vtb.conp.commons.dynamic.aspects.starter.messages.DynamicAspectsMessageKeys.INVALID_POINTCUT_EXPRESSION_KEY;

@Component
public class TargetPointcutValidationService extends PointcutValidationService {

    public TargetPointcutValidationService(MessageSource messageSource) {
        super(messageSource, TARGET, INVALID_POINTCUT_EXPRESSION_KEY);
    }
}

