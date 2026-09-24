package com.nuzhd.validation;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import static com.nuzhd.domain.DesignatorType.THIS;
import static ru.vtb.conp.commons.dynamic.aspects.starter.messages.DynamicAspectsMessageKeys.INVALID_POINTCUT_EXPRESSION_KEY;

@Component
public class ThisPointcutValidationService extends PointcutValidationService {

    public ThisPointcutValidationService(MessageSource messageSource) {
        super(messageSource, THIS, INVALID_POINTCUT_EXPRESSION_KEY);
    }
}

