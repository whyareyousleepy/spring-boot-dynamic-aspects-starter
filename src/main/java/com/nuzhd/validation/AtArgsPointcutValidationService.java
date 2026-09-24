package com.nuzhd.validation;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import static com.nuzhd.domain.DesignatorType.AT_ARGS;
import static ru.vtb.conp.commons.dynamic.aspects.starter.messages.DynamicAspectsMessageKeys.INVALID_POINTCUT_EXPRESSION_KEY;

@Component
public class AtArgsPointcutValidationService extends PointcutValidationService {

    public AtArgsPointcutValidationService(MessageSource messageSource) {
        super(messageSource, AT_ARGS, INVALID_POINTCUT_EXPRESSION_KEY);
    }
}

