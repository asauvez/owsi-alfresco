package fr.openwide.alfresco.demo.web.application.business;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import fr.openwide.alfresco.app.web.validation.model.AlertContainer;
import fr.openwide.alfresco.demo.web.application.framework.spring.controller.BusinessController;

@Controller
public class AccueilController extends BusinessController {

	public static final String LOGIN_URL = "/security/login";
	public static final String REFRESH_URL = "/security/refresh";

	private static final String HEADER_REFRESH_NAME = "Refresh";
	private static final String HEADER_REFRESH_VALUE_PATTERN = "{0,number,#};url={1}{2}";

//	@Autowired
	//private ITacheService tacheService;

//	@Autowired
//	private IBannetteService bannetteService;

//	@PreAuthorize(BusinessPermissionConstants.ROLE_UTILISATEUR)
	@RequestMapping(value = "/accueil", method = RequestMethod.GET)
	public String accueil(Model model){// throws MdphException {
		model.addAttribute("bannette", null);//bannetteService.getUserBannette(new EnveloppeComparator()));

//		List<Tache> taches = tacheService.getTaches();
//		Collections.sort(taches, new TacheComparator(true));
//		model.addAttribute("taches", taches);

//		model.addAttribute("rechercheForm", new RechercheForm());
		return "accueil";//Views.accueil;
	}

//	@PreAuthorize(BusinessPermissionConstants.ROLE_UTILISATEUR)
	@RequestMapping(value = { "", "/" }, method = RequestMethod.GET)
	public String root() {
		return getRedirect("accueil");
	}

	@RequestMapping(method=RequestMethod.GET, value=LOGIN_URL)
	public String getLoginPage(HttpSession session, AlertContainer alertContainer) {
		AuthenticationException authenticationException = (AuthenticationException) session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		if (authenticationException != null) {
			if (authenticationException instanceof AuthenticationServiceException) {
				alertContainer.addError("login.repositoryNotFound");
			} else if (authenticationException instanceof BadCredentialsException) {
				alertContainer.addError("login.invalidPassword");
			} else {
				throw authenticationException;
			}
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
		return "login";//Views.Security.login;
	}

	@RequestMapping(method=RequestMethod.GET, value=REFRESH_URL)
	public void refreshPage(HttpServletRequest request, HttpServletResponse response) {
		response.setHeader(HEADER_REFRESH_NAME, MessageFormat.format(HEADER_REFRESH_VALUE_PATTERN, 
				TimeUnit.SECONDS.toSeconds(1), request.getContextPath(), LOGIN_URL));
	}

}
