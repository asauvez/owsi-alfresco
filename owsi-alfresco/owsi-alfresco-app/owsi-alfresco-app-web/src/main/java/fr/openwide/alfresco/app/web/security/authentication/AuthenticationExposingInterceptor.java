package fr.openwide.alfresco.app.web.security.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import fr.openwide.alfresco.app.core.security.model.BusinessUser;
import fr.openwide.alfresco.app.core.security.service.UserService;

public class AuthenticationExposingInterceptor extends HandlerInterceptorAdapter {

	private static final String AUTHENTICATION_ATTRIBUTE_NAME = "authentication";
	private static final String USER_ATTRIBUTE_NAME = "user";
	private static final String CAN_LOGOUT_ATTRIBUTE_NAME = "canLogout";

	private UserService userService;

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
		// add authentication
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		modelAndView.addObject(AUTHENTICATION_ATTRIBUTE_NAME, authentication);
		// add user
		BusinessUser user = (authentication != null) ? userService.getCurrentUser() : null;
		modelAndView.addObject(USER_ATTRIBUTE_NAME, user);
		// add can logout
		boolean canLogout = (authentication != null) ? (authentication instanceof UsernamePasswordAuthenticationToken) : false;
		modelAndView.addObject(CAN_LOGOUT_ATTRIBUTE_NAME, canLogout);
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

}
