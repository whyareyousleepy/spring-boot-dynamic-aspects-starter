package com.nuzhd.interceptors;

import com.nuzhd.utils.CompiledTemplate;
import com.nuzhd.utils.CustomMessagesProcessor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static com.nuzhd.utils.DynamicAspectsUtils.extractFullMethodName;
import static com.nuzhd.utils.DynamicAspectsUtils.getArgsAndValues;
import static com.nuzhd.utils.DynamicAspectsUtils.hasArgs;

public class MethodBeforeInterceptor implements MethodInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodBeforeInterceptor.class);

    private static final String METHOD_CALLED_KEY = "dynamic.aspects.info.method_called";

    private final String customBeforeMessage;

    private final CompiledTemplate beforeTemplate;

    private final MessageSource messageSource;

    public MethodBeforeInterceptor(
            MessageSource messageSource,
            String customBeforeMessage) {
        this.messageSource = messageSource;
        this.customBeforeMessage = customBeforeMessage;
        this.beforeTemplate = CustomMessagesProcessor.compile(customBeforeMessage);
    }

    @Nullable
    @Override
    public Object invoke(@NotNull MethodInvocation invocation) throws Throwable {
        var fullMethodName = extractFullMethodName(invocation);

        var args = hasArgs(invocation) ? getArgsAndValues(invocation) : "[NO ARGS]";

        LOGGER.info(
                StringUtils.isBlank(customBeforeMessage) ?
                        messageSource.getMessage(METHOD_CALLED_KEY, new Object[] {fullMethodName, args}, Locale.ROOT) :
                        beforeTemplate.render(invocation, args, null)
        );

        return invocation.proceed();
    }
}
