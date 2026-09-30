package com.nuzhd.validation;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import static com.nuzhd.domain.DesignatorType.AT_ANNOTATION;
import static ru.vtb.conp.commons.dynamic.aspects.starter.messages.DynamicAspectsMessageKeys.INVALID_POINTCUT_EXPRESSION_KEY;

@Component
public class AtAnnotationPointcutValidationService extends PointcutValidationService {

    public AtAnnotationPointcutValidationService(MessageSource messageSource) {
        super(messageSource, AT_ANNOTATION, INVALID_POINTCUT_EXPRESSION_KEY);
    }
}

