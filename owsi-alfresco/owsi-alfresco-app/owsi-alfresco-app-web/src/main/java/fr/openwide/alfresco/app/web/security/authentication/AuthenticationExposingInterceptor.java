package fr.openwide.alfresco.app.web.security.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import fr.openwide.alfresco.app.core.security.service.UserService;

public class AuthenticationExposingInterceptor extends HandlerInterceptorAdapter {

	private static final String AUTHENTICATION_ATTRIBUTE_NAME = "authentication";
	private static final String USER_ATTRIBUTE_NAME = "user";
	private static final String CAN_LOGOUT_ATTRIBUTE_NAME = "canLogout";

	private UserService userService;

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
		if (modelAndView != null) {
			// add authentication
			modelAndView.addObject(AUTHENTICATION_ATTRIBUTE_NAME, userService.getCurrentAuthentication().orElse(null));
			// add user
			modelAndView.addObject(USER_ATTRIBUTE_NAME, userService.getCurrentUserDetails().orElse(null));
			// add can logout
			modelAndView.addObject(CAN_LOGOUT_ATTRIBUTE_NAME, ! userService.getCurrentUserDetails().isPresent());
		}
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

}
