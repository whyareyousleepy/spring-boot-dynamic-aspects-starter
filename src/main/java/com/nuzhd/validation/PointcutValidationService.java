package com.nuzhd.validation;

public interface PointcutValidationService {

    String CLASS_NOT_FOUND_KEY = "dynamic.aspects.error.class_not_found";
    String METHOD_NOT_FOUND_KEY = "dynamic.aspects.error.method_not_found";
    String INVALID_EXECUTION_EXPRESSION_KEY = "dynamic.aspects.error.execution.invalid_expression";
    String WRONG_ACCESS_MODIFIER_KEY = "dynamic.aspects.error.execution.wrong_access_modifier";
    String WRONG_RETURN_TYPE_KEY = "dynamic.aspects.error.execution.wrong_return_type";

    void validateExpression(String pointcutExpression);

}
