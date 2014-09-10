package fr.openwide.alfresco.app.web.validation.binding;

import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.MessageSource;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodProcessor;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import fr.openwide.alfresco.app.web.validation.model.AlertContainer;
import fr.openwide.alfresco.app.web.validation.model.ValidationResponse;

public class ValidationResponseMethodProcessor extends AbstractMessageConverterMethodProcessor {

	public static final String TARGET_REQUEST_PATH_HEADER_NAME = "targetRequestPath";

	private static final String X_REQUESTED_WITH_HEADER_NAME = "x-requested-with";
	private static final String XML_HTTP_REQUEST = "XMLHttpRequest";

	private MessageSource messageSource;

	public ValidationResponseMethodProcessor(List<HttpMessageConverter<?>> messageConverters, MessageSource messageSource) {
		super(messageConverters);
		this.messageSource = messageSource;
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> paramType = parameter.getParameterType();
		return ValidationResponse.class.isAssignableFrom(paramType);
	}

	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		Class<?> paramType = returnType.getParameterType();
		return ValidationResponse.class.isAssignableFrom(paramType);
	}

	/**
	 * La méthode est appelée dès lors qu'une méthode d'un controller contient en paramètre un objet de type ValidationResponse.
	 */
	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		// build validationResponse
		ValidationResponse validationResponse = new ValidationResponse();
		validationResponse.setMessageSource(messageSource);
		return validationResponse;
	}

	/**
	 * La méthode est appelée dès lors qu'une méthode d'un controller renvoie un objet de type ValidationResponse.
	 * La méthode est complexe et gère notamment le cas des upload de fichiers réalisés en Ajax (jQuery passe par une Iframe).
	 * Le format renvoyé est du JSon, interprété dans la JSP par un Javascript pour afficher les erreurs.
	 */
	@Override
	public void handleReturnValue(Object returnValue, MethodParameter returnType,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
		if (returnValue == null) {
			return;
		} else if (returnValue instanceof ValidationResponse) {
			ValidationResponse validation = (ValidationResponse) returnValue;
			if (validation.getViewName() != null) {
				mavContainer.setViewName(validation.getViewName());
				return;
			}
			
			mavContainer.setRequestHandled(true);
			HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
			// get xRequestedWith : vérifie si le contenu provient d'une requete ajax ou d'une iframe
			String xRequestedWith = webRequest.getHeader(X_REQUESTED_WITH_HEADER_NAME);
			if (validation.hasErrors()) {
				// avec IE8 en iframe le contenu de l'iframe n'est pas mis à jour si le statut != 200
				if (XML_HTTP_REQUEST.equals(xRequestedWith)) {
					// standard ajax : mark response as error
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
			} else {
				// deal with AlertContainer for the subsequent request
				AlertContainer alertContainer = validation.getAlertContainer();
				if (! alertContainer.isEmpty()) {
					HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
					// build flashmap for the specific targetRequestPath or for the next request if no information
					FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
					flashMap.put(AlertContainer.ALERTS_FIELD_NAME, alertContainer);
					// get targetRequestPath: may be null if request is not standard ajax
					String targetRequestPath = webRequest.getHeader(TARGET_REQUEST_PATH_HEADER_NAME);
					if (targetRequestPath != null) {
						UriComponents uriComponents = UriComponentsBuilder.fromUriString(targetRequestPath).build();
						flashMap.setTargetRequestPath(uriComponents.getPath());
						flashMap.addTargetRequestParams(uriComponents.getQueryParams());
					}
					// save flashmap
					FlashMapManager flashMapManager = RequestContextUtils.getFlashMapManager(request);
					flashMapManager.saveOutputFlashMap(flashMap, request, response);
				}
			}
			if (XML_HTTP_REQUEST.equals(xRequestedWith)) {
				// standard ajax : output return value
				writeWithMessageConverters(returnValue, returnType, webRequest);
			} else {
				// iframe : renvoie le json avec un mimetype text/html;charset=utf-8
				ServletServerHttpResponse outputMessage = createOutputMessage(webRequest);
				MediaType html = new MediaType(MediaType.TEXT_HTML.getType(), MediaType.TEXT_HTML.getSubtype(), StandardCharsets.UTF_8);
				new MappingIframeJsonHttpMessageConverter().write(returnValue, html, outputMessage);
			}
		} else {
			// should not happen
			throw new UnsupportedOperationException("Unexpected return type: " +
					returnType.getParameterType().getName() + " in method: " + returnType.getMethod());
		}
	}

}
