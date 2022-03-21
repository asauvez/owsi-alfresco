package fr.openwide.alfresco.app.web.validation.binding; 

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import fr.openwide.alfresco.app.web.validation.model.AlertContainer;

public class AlertContainerMethodArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> paramType = parameter.getParameterType();
		return AlertContainer.class.isAssignableFrom(paramType);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, 
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		AlertContainer alertContainer = new AlertContainer();
		mavContainer.addAttribute(AlertContainer.ALERTS_FIELD_NAME, alertContainer);
		return alertContainer;
	}

}
