package com.nuzhd.creators;

import com.nuzhd.CustomPointcutExpression;
import com.nuzhd.domain.DesignatorType;
import com.nuzhd.interceptors.MethodAroundInterceptor;
import com.nuzhd.validation.PointcutValidationService;
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

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/// Класс, создающий динамические аспекты на основе pointcut выражений из файла конфигурации.
///
/// На текущий момент доступно только создание аспектов типа @Around
///
/// Пример заполнения списка `app.dynamic-aspects.pointcut-expressions.around`
///
/// \- execution(* ru.vtb.conp.preparer.session.adapter.in.web.v1.SessionDataController.*(..))
///
/// \- execution(* ru.vtb.conp.preparer.session.adapter.in.web.v2.SessionDataControllerV2.*(..))
public class DynamicAspectsCreator implements BeanFactoryPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicAspectsCreator.class);
    private final Set<String> around;
    private final MethodAroundInterceptor methodAroundInterceptor;
    private static final String POINTCUTS_PATH = "app.dynamic-aspects.pointcut-expressions.around";

    private final Map<DesignatorType, PointcutValidationService> validators;
    private final MessageSource messageSource;
    private static final String LIST_EMPTY_KEY = "dynamic.aspects.expressions.empty";
    private static final String CANT_CREATE_ASPECT_KEY = "dynamic.aspects.error.cant-create-aspect";
    private static final String CREATE_ASPECT_SUCCESS_KEY = "dynamic.aspects.success.created-aspect";

    public DynamicAspectsCreator(
            Environment environment,
            MethodAroundInterceptor methodAroundInterceptor,
            MessageSource messageSource,
            Map<DesignatorType, PointcutValidationService> validators
    ) {
        var binder = Binder.get(environment);
        around = binder.bind(POINTCUTS_PATH,
                             Bindable.setOf(String.class)).get(); // @Value при текущей реализации не работает
        if (this.around.isEmpty()) {
            LOGGER.warn(messageSource.getMessage(LIST_EMPTY_KEY, new Object[] {POINTCUTS_PATH}, Locale.ROOT));
        }
        this.methodAroundInterceptor = methodAroundInterceptor;
        this.messageSource = messageSource;
        this.validators = validators;
    }

    @Override
    public void postProcessBeanFactory(@NotNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
        for (var expr : around) {
            var pointcut = new CustomPointcutExpression(validators, messageSource);

            try {
                pointcut.setExpression(expr);
            } catch (IllegalArgumentException e) {
                LOGGER.error(messageSource.getMessage(CANT_CREATE_ASPECT_KEY, new Object[] {expr}, Locale.ROOT));
                continue;
            }

            var beanDefinition =
                    BeanDefinitionBuilder.genericBeanDefinition(DefaultPointcutAdvisor.class)
                                         .addPropertyValue("pointcut", pointcut)
                                         .addPropertyValue("advice", methodAroundInterceptor)
                                         .getBeanDefinition();

            ((BeanDefinitionRegistry) beanFactory).registerBeanDefinition(
                    UUID.randomUUID().toString(),
                    beanDefinition
            );

            LOGGER.info(messageSource.getMessage(CREATE_ASPECT_SUCCESS_KEY, new Object[] {expr}, Locale.ROOT));
        }
    }
}