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
import static com.nuzhd.utils.DynamicAspectsUtils.hasReturnValue;

public class MethodAfterInterceptor implements MethodInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodAfterInterceptor.class);

    private static final String METHOD_EXECUTED_KEY = "dynamic.aspects.info.method_executed";

    private final String customAfterMessage;

    private final CompiledTemplate afterTemplate;

    private final MessageSource messageSource;

    public MethodAfterInterceptor(
            MessageSource messageSource,
            String customAfterMessage) {
        this.messageSource = messageSource;
        this.customAfterMessage = customAfterMessage;
        this.afterTemplate = CustomMessagesProcessor.compile(customAfterMessage);
    }

    @Nullable
    @Override
    public Object invoke(@NotNull MethodInvocation invocation) throws Throwable {
        var fullMethodName = extractFullMethodName(invocation);

        var result = invocation.proceed();

        var returnValue = hasReturnValue(invocation) ? result : "[NO RETURN VALUE]";
        var args = hasArgs(invocation) ? getArgsAndValues(invocation) : "[NO ARGS]";

        LOGGER.info(
                StringUtils.isBlank(customAfterMessage) ?
                        messageSource.getMessage(METHOD_EXECUTED_KEY, new Object[] {fullMethodName, returnValue}, Locale.ROOT) :
                        afterTemplate.render(invocation, args, returnValue)
        );

        return result;
    }
}
