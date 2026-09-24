package com.nuzhd.creators;

import com.nuzhd.CustomPointcutExpression;
import com.nuzhd.domain.AdviceType;
import com.nuzhd.domain.DesignatorType;
import com.nuzhd.domain.DynamicAspectDto;
import com.nuzhd.interceptors.MethodAfterInterceptor;
import com.nuzhd.interceptors.MethodAroundInterceptor;
import com.nuzhd.interceptors.MethodBeforeInterceptor;
import com.nuzhd.validation.PointcutValidationService;
import org.aopalliance.intercept.MethodInterceptor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;

/**
 * Класс, создающий динамические аспекты на основе pointcut выражений из файла конфигурации.
 * <p>
 * На текущий момент доступно только создание аспектов типа @Around
 * <p>
 * Пример заполнения списка {@code app.dynamic-aspects.pointcut-expressions.around}
 * <p>
 * {@code execution(* ru.vtb.conp.preparer.session.adapter.in.web.v1.SessionDataController.*(..))}
 * <p>
 * {@code execution(* ru.vtb.conp.preparer.session.adapter.in.web.v2.SessionDataControllerV2.*(..))}
 */
public class DynamicAspectsCreator implements BeanFactoryPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicAspectsCreator.class);
    private final Map<AdviceType, Set<DynamicAspectDto>> adviceTypeToAspectDtos = new EnumMap<>(AdviceType.class);

    private static final String POINTCUTS_BASE_PATH = "app.dynamic-aspects.pointcuts";
    private static final String POINTCUTS_AROUND_PATH = "app.dynamic-aspects.pointcuts.around";
    private static final String POINTCUTS_BEFORE_PATH = "app.dynamic-aspects.pointcuts.before";
    private static final String POINTCUTS_AFTER_PATH = "app.dynamic-aspects.pointcuts.after";
    private static final String POINTCUTS_EMPTY_KEY = "dynamic.aspects.expressions.empty";
    private static final String CANT_CREATE_ASPECT_KEY = "dynamic.aspects.error.cant-create-aspect";
    private static final String CREATE_ASPECT_SUCCESS_KEY = "dynamic.aspects.success.created-aspect";

    private final Map<DesignatorType, PointcutValidationService> validators;
    private final MessageSource messageSource;

    public DynamicAspectsCreator(
            Environment environment,
            MessageSource messageSource,
            Map<DesignatorType, PointcutValidationService> validators
    ) {
        var binder = Binder.get(environment);

        adviceTypeToAspectDtos.putAll(
                Map.of(
                        AdviceType.AROUND,
                        binder.bind(POINTCUTS_AROUND_PATH, Bindable.setOf(DynamicAspectDto.class)).orElse(
                                Set.of()), // @Value при текущей реализации не работает
                        AdviceType.BEFORE,
                        binder.bind(POINTCUTS_BEFORE_PATH, Bindable.setOf(DynamicAspectDto.class)).orElse(Set.of()),
                        AdviceType.AFTER,
                        binder.bind(POINTCUTS_AFTER_PATH, Bindable.setOf(DynamicAspectDto.class)).orElse(Set.of())
                )
        );

        if (adviceTypeToAspectDtos.entrySet().stream().allMatch(entry -> entry.getValue().isEmpty())) {
            LOGGER.warn(messageSource.getMessage(POINTCUTS_EMPTY_KEY, new Object[] {POINTCUTS_BASE_PATH}, Locale.ROOT));
        }

        this.messageSource = messageSource;
        this.validators = validators;
    }

    @Override
    public void postProcessBeanFactory(@NotNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
        for (var entry : adviceTypeToAspectDtos.entrySet()) {
            createAdvices(beanFactory, entry.getKey(), entry.getValue());
        }
    }

    private void createAdvices(ConfigurableListableBeanFactory beanFactory,
                               AdviceType adviceType,
                               Set<DynamicAspectDto> aspects) {
        for (var aspect : aspects) {
            var pointcut = new CustomPointcutExpression(validators, messageSource);

            try {
                pointcut.setExpression(aspect.getExpression());
            } catch (IllegalArgumentException e) {
                LOGGER.error(messageSource.getMessage(CANT_CREATE_ASPECT_KEY,
                                                      new Object[] {aspect.getExpression(), e.getMessage()},
                                                      Locale.ROOT));
                continue;
            }

            var beanDefinition =
                    BeanDefinitionBuilder.genericBeanDefinition(DefaultPointcutAdvisor.class)
                                         .addPropertyValue("pointcut", pointcut)
                                         .addPropertyValue("advice", createInterceptor(adviceType, aspect))
                                         .setRole(
                                                 ROLE_INFRASTRUCTURE) // Для избежания WARN сообщений от спринга в консоли при создании аспектов
                                         .getBeanDefinition();

            ((BeanDefinitionRegistry) beanFactory).registerBeanDefinition(
                    UUID.randomUUID().toString(),
                    beanDefinition
            );

            LOGGER.info(messageSource.getMessage(CREATE_ASPECT_SUCCESS_KEY,
                                                 new Object[] {aspect.getExpression()},
                                                 Locale.ROOT)
            );
        }
    }

    private MethodInterceptor createInterceptor(AdviceType adviceType, DynamicAspectDto aspect) {
        return switch (adviceType) {
            case BEFORE -> new MethodBeforeInterceptor(messageSource,
                                                       aspect.getCustomBeforeMessage());
            case AFTER -> new MethodAfterInterceptor(messageSource,
                                                     aspect.getCustomAfterMessage());
            case AROUND -> new MethodAroundInterceptor(messageSource,
                                                       aspect.getCustomBeforeMessage(),
                                                       aspect.getCustomAfterMessage()
            );
        };
    }
}