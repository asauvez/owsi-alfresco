package fr.openwide.alfresco.demo.web.application.framework.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.alfresco.demo.core.application.business.user.service.IMdphUserService;


/**
 *
 * Cette classe ne doit pas être directement héritée des controller.
 * Il faut utiliser les autres controller de ce package car chacun intègre une gestion des exceptions adpatée.
 *
 * @author Alexandre FIEVEE
 *
 */
public abstract class CommonController {
/*
	@Autowired
	protected IMdphUserService mdphUserService;
	*/
	/**
	 * Retourne la redirection vers le mapping souhaité.
	 * @param mapping
	 * @return la redirection vers le mapping souhaité
	 */
	protected String getRedirect(String mapping) {
		boolean appendSlash = (mapping != null && !mapping.startsWith("/"));

		StringBuilder redirect = new StringBuilder();
		redirect.append("redirect:");
		if (appendSlash) {
			redirect.append("/");
		}
		redirect.append(mapping);

		return redirect.toString();
	}

}
