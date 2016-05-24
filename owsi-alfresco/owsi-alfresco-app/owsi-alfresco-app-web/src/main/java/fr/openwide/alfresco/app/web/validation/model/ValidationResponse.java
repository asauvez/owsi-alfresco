package fr.openwide.alfresco.app.web.validation.model;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.context.request.NativeWebRequest;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.openwide.core.spring.util.StringUtils;

public class ValidationResponse {

	private static final String FIELD_ERRORS_MESSAGE_KEY = "alert.fieldErrors.warning";

	private List<Alert> globalAlerts = new ArrayList<>();
	private List<FieldMessage> fieldErrors = new ArrayList<>();

	private AlertContainer alertContainer = new AlertContainer();
	private MessageSource messageSource;
	private NativeWebRequest webRequest;

	private String viewName;	// Page à renvoyer comme si on renvoyé un ModelAndView
	private String redirection;	// URL où $.formBindAjaxPost() va rediriger le navigateur. 

	public void addGlobalAlerts(BindingResult bindingResult) {
		addErrors(bindingResult, false);
	}

	public void addFieldErrors(BindingResult bindingResult) {
		addErrors(bindingResult, true);
	}

	private void addErrors(BindingResult bindingResult, boolean addFieldErrors) {
		if (bindingResult.hasFieldErrors()) {
			for (FieldError objectError : bindingResult.getFieldErrors()) {
				String field = objectError.getField();
				
				String message = getMessage(objectError, field);
				
				if (addFieldErrors) {
					fieldErrors.add(new FieldMessage(field, message));
				} else {
					globalAlerts.add(Alert.newWarning(message, null));
				}
			}
		}
		
		if (bindingResult.hasGlobalErrors()) {
			for (ObjectError objectError : bindingResult.getGlobalErrors()) {
				String msg = getMessage(objectError);
				if (StringUtils.hasText(msg)) {
					globalAlerts.add(Alert.newWarning(getMessage(objectError), null));
				}
			}
		}
		
		if (addFieldErrors && bindingResult.hasFieldErrors()) {
			globalAlerts.add(Alert.newWarning(messageSource.getMessage(FIELD_ERRORS_MESSAGE_KEY, null, null), null));
		}
	}

	/**
	 * Utilisé pour les erreurs globales (il se peut qu'un formulaire ne soit pas valide du fait qu'une condition sur un ensemble
	 * de champs ne soit pas respectée).
	 * @see ValidationResponse#getMessage(ObjectError, Object[])
	 */
	private String getMessage(ObjectError objectError) {
		Object[] args = { objectError.getObjectName() };
		return getMessage(objectError, args);
	}

	/**
	 * Utilisé pour les erreurs sur les champs.
	 * @see ValidationResponse#getMessage(ObjectError, Object[])
	 */
	private String getMessage(FieldError objectError, String field) {
		/*
		 * Recherche du premier message valide ; exemples de codes à surcharger :
		 * 	typeMismatch.sujetForm.horaireDebut		NotEmpty.sujetForm.libelle
		 * 	typeMismatch.horaireDebut				NotEmpty.libelle
		 * 	typeMismatch.java.util.Date				NotEmpty.java.lang.String
		 * 	typeMismatch							NotEmpty
		 */
		Object[] args = { objectError.getRejectedValue(), field, objectError.getObjectName() };
		return getMessage(objectError, args);
	}

	/**
	 * Retourne le premier message d'erreur valide.
	 * @param objectError
	 * @param args
	 * @return le message d'erreur
	 */
	private String getMessage(ObjectError objectError, Object[] args) {
		String message = null;
		for (String code : objectError.getCodes()) {
			try {
				message = messageSource.getMessage(code, args, null);
				break;
			} catch (NoSuchMessageException e) {
				// try next message
			}
		}
		if (! StringUtils.hasText(message)) {
			// message par défaut
			message = objectError.getDefaultMessage();
		}
		return message;
	}

	public void addFieldError(String field, String messageCode, String ... args) {
		String message = messageSource.getMessage(messageCode, args, null);
		fieldErrors.add(new FieldMessage(field, message));
	}

	public void setGlobalError(String details, String messageCode, String ... args) {
		globalAlerts.clear();
		addGlobalError(details, messageCode, args);
	}

	public void addGlobalError(String details, String messageCode, String ... args) {
		String message = messageSource.getMessage(messageCode, args, null);
		globalAlerts.add(Alert.newError(message, details));
	}

	public boolean hasErrors() {
		if (! fieldErrors.isEmpty()) {
			return true;
		}
		for (Alert alert : globalAlerts) {
			if (AlertType.error.equals(alert.getType()) || AlertType.warning.equals(alert.getType())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("globalAlerts", globalAlerts)
				.append("fieldErrors", fieldErrors)
				.append("viewName", viewName)
				.toString();
	}

	public List<FieldMessage> getFieldErrors() {
		return fieldErrors;
	}

	public List<Alert> getGlobalAlerts() {
		return globalAlerts;
	}
	public void setGlobalAlerts(List<Alert> globalAlerts) {
		this.globalAlerts = globalAlerts;
	}

	@JsonIgnore
	public AlertContainer getAlertContainer() {
		return alertContainer;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	public void setWebRequest(NativeWebRequest webRequest) {
		this.webRequest = webRequest;
	}

	public String getViewName() {
		return viewName;
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public String getRedirection() {
		return redirection;
	}
	
	/**
	 * @param redirection Si on ne passe pas une URL absolu, prefixe l'URL fournie par le context path.
	 */
	public void setRedirection(String redirection) {
		if (! redirection.startsWith("http")) {
			HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
			String contextPath = request.getContextPath();
			redirection = contextPath + redirection;
		}
		setRedirection(redirection);
	}
}

