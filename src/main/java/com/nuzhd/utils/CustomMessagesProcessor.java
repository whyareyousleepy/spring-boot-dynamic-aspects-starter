package com.nuzhd.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.nuzhd.consts.TemplateConstants.METHOD_ARGS_TEMPLATE;
import static com.nuzhd.consts.TemplateConstants.METHOD_FULL_NAME_TEMPLATE;
import static com.nuzhd.consts.TemplateConstants.METHOD_NAME_TEMPLATE;
import static com.nuzhd.consts.TemplateConstants.METHOD_RETURN_VALUE_TEMPLATE;
import static com.nuzhd.consts.TemplateConstants.UNKNOWN_PLACEHOLDER_TEMPLATE;
import static com.nuzhd.utils.DynamicAspectsUtils.extractMethodName;

public final class CustomMessagesProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomMessagesProcessor.class);

    private static final Pattern PLACEHOLDER = Pattern.compile("\\{([^{}]+)}");

    private CustomMessagesProcessor() {}

    public static CompiledTemplate compile(String template) {

        if (template == null || template.isBlank()) {
            return CompiledTemplate.EMPTY;
        }

        List<Token> tokens = new ArrayList<>();

        Matcher matcher = PLACEHOLDER.matcher(template);

        int last = 0;

        while (matcher.find()) {

            if (matcher.start() > last) {
                tokens.add(new TextToken(template.substring(last, matcher.start())));
            }

            String placeholder = matcher.group(1);

            switch (placeholder) {

                case METHOD_NAME_TEMPLATE -> tokens.add((sb, invocation, maskedArgs, returnValue) ->
                                                                sb.append(extractMethodName(invocation)));

                case METHOD_FULL_NAME_TEMPLATE -> tokens.add((sb, invocation, maskedArgs, returnValue) ->
                                                                     sb.append(
                                                                             DynamicAspectsUtils.extractFullMethodName(
                                                                                     invocation)));

                case METHOD_ARGS_TEMPLATE -> tokens.add((sb, invocation, maskedArgs, returnValue) ->
                                                                sb.append(maskedArgs));

                case METHOD_RETURN_VALUE_TEMPLATE -> tokens.add((sb, invocation, maskedArgs, returnValue) ->
                                                                        sb.append(returnValue));

                default -> {

                    if (!placeholder.startsWith("arg:")) {
                        LOGGER.warn("Плейсхолдер {{}} не поддерживается. Проверьте правильность имени плейсхолдера",
                                    placeholder);
                        tokens.add(new TextToken(UNKNOWN_PLACEHOLDER_TEMPLATE.formatted(placeholder)));
                    } else {
                        tokens.add(new ArgumentToken(placeholder.substring(4)));
                    }
                }
            }

            last = matcher.end();
        }

        if (last < template.length()) {
            tokens.add(new TextToken(template.substring(last)));
        }

        return new CompiledTemplate(tokens.toArray(Token[]::new));
    }

}
