package com.nuzhd.validation;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import static com.nuzhd.domain.DesignatorType.ARGS;
import static ru.vtb.conp.commons.dynamic.aspects.starter.messages.DynamicAspectsMessageKeys.INVALID_ARGS_EXPRESSION_KEY;

@Component
public class ArgsPointcutValidationService extends PointcutValidationService {

    public ArgsPointcutValidationService(MessageSource messageSource) {
        super(messageSource, ARGS, INVALID_ARGS_EXPRESSION_KEY);
    }
}
