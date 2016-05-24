package fr.openwide.alfresco.demo.web.application.framework.spring.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;

import fr.openwide.alfresco.app.web.validation.model.AlertContainer;
import fr.openwide.core.jpa.exception.SecurityServiceException;

/**
 * @author Alexandre FIEVEE
 *
 */
public abstract class BusinessController extends CommonController {

	@Autowired
	protected ResourceBundleMessageSource messageSource;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * La méthode a été reprise du projet AMF COLLEGE.
	 */
//	@ExceptionHandler(Exception.class)
//	protected String handleException(Exception e, Model model, AlertContainer alertContainer, HttpServletResponse response) {
//		// log exception as error
//		logger.error(e.getMessage(), e);
//		// return user friendly output
//		model.addAttribute("title", "exception.generic.title");
//		model.addAttribute("type", e.getClass().getCanonicalName());
//		model.addAttribute("message", e.getMessage());
//		Writer stacktrace = new StringWriter();
//		e.printStackTrace(new PrintWriter(stacktrace));
//		model.addAttribute("stacktrace", stacktrace.toString());
//		alertContainer.addError("exception.generic.message");
//		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//		return null;//Views.exception;
//	}

	/**
	 * La méthode a été reprise du projet AMF COLLEGE.
	 */
	@ExceptionHandler({AccessDeniedException.class, SecurityServiceException.class})
	protected String handleSecurityException(Exception e, Model model, AlertContainer alertContainer, HttpServletResponse response) {
		// log exception as warning
		logger.warn(e.getMessage(), e);
		// return user friendly output
		model.addAttribute("title", "exception.security.title");
		alertContainer.addError("exception.security.message");
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		return null;//Views.exception;
	}
	
	/**
	 * Retourne true si le navigateur est IE9, false sinon
	 * Verification de la version du navigateur
	 * Si IE9, on set un booleen pour indiquer que l'affichage pdfviewer doit etre désactivé dans la page
	 * voir le ticket https://helios.openwide.fr/ticket/23119
	 * MSIE 7.0 pour mode de compatibilité (voir http://fr.wikipedia.org/wiki/User-Agent#Navigateurs)	 * 
	 * @param request
	 * @return true si le navigateur est IE9, false sinon
	 */
	protected boolean isMSIE9(HttpServletRequest request){
		if(request.getHeader("user-agent").contains("MSIE 9.0") || request.getHeader("user-agent").contains("MSIE 7.0")){
			return true;
		}
		return false;
	}

}
