package com.nuzhd.validation;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import static com.nuzhd.domain.DesignatorType.BEAN;
import static ru.vtb.conp.commons.dynamic.aspects.starter.messages.DynamicAspectsMessageKeys.INVALID_POINTCUT_EXPRESSION_KEY;

@Component
public class BeanPointcutValidationService extends PointcutValidationService {

    public BeanPointcutValidationService(MessageSource messageSource) {
        super(messageSource, BEAN, INVALID_POINTCUT_EXPRESSION_KEY);
    }
}

