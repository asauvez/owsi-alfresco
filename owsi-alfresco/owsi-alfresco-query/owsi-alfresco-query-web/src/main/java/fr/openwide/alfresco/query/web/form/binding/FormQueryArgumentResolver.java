package fr.openwide.alfresco.query.web.form.binding;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import fr.openwide.alfresco.query.web.form.result.DefaultResultFormatter;
import fr.openwide.alfresco.query.web.search.context.QueryContext;
import fr.openwide.alfresco.query.web.search.model.AbstractFormQuery;

public class FormQueryArgumentResolver implements HandlerMethodArgumentResolver {

	private static final String DO_REQUEST_PARAM = "doRequest";

	@Autowired private ApplicationContext applicationContext;
	@Autowired private MessageSource messageSource;
	
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> paramType = parameter.getParameterType();
		return AbstractFormQuery.class.isAssignableFrom(paramType);
	}

	@Override
	public Object resolveArgument(
			MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
			throws IOException {
		
		String formQueryBeanName = parameter.getParameterName();
		
		QueryContext queryContext = new QueryContext(webRequest.getLocale(), messageSource);
		AbstractFormQuery<?> formQuery = (AbstractFormQuery<?>) applicationContext.getAutowireCapableBeanFactory().autowire(
					parameter.getParameterType(), 
					AutowireCapableBeanFactory.AUTOWIRE_NO, 
					false);
		formQuery.setBeanName(formQueryBeanName);
		//formQuery.setQueryContext(queryContext);
		formQuery.setDefaultResultFormatter(new DefaultResultFormatter(queryContext));
		// TODO use binder ............
		formQuery.initValues(webRequest.getParameterMap());
		
		mavContainer.addAttribute(formQueryBeanName, formQuery);
		
		WebDataBinder binder;
		try {
			binder = binderFactory.createBinder(webRequest, formQuery, formQueryBeanName);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		mavContainer.addAttribute(BindingResult.MODEL_KEY_PREFIX + formQueryBeanName, binder.getBindingResult());

		if (webRequest.getParameter(DO_REQUEST_PARAM) != null || formQuery.getInputFields().isEmpty()) {
			// Ne marche pas pour l'instant. Je fais à la main.
			binder.validate();
			/*
			for (InputField<?> inputField : formQuery.getInputFields()) {
				ValidationException error = inputField.getError();
				if (error != null) {
					binder.getBindingResult().addError(new FieldError(
							formQueryBeanName,
							inputField.getPath(), 
							inputField.getValue(),
							true,
							new String[] { error.getI18nKey() }, 
							error.getArguments(),
							error.getI18nKey()));
				}
			}
			
			if (! binder.getBindingResult().hasErrors()) {
				List<ValidationException> errors = new ArrayList<ValidationException>();
				try {
					formQuery.validate(errors);
				} catch (ValidationException error) {
					errors.add(error);
				}
				for (ValidationException error : errors) {
					binder.getBindingResult().addError(new ObjectError(
							formQueryBeanName,
							new String[] { error.getI18nKey() }, 
							error.getArguments(),
							error.getI18nKey()));
				}
			}*/
			// TODO la gestion des erreurs doit se faire dans le controlleur
			// du coup l'appel à doQuery doit se faire dans le controlleur........
			// dans l'idée, les services ne devraient pas être encapsulés dans le formquery, cela simplifierai plein de choses
			/*
			if (! binder.getBindingResult().hasErrors()) {
				formQuery.doQuery();
			}*/
		}
	
		return formQuery;
	}

}
