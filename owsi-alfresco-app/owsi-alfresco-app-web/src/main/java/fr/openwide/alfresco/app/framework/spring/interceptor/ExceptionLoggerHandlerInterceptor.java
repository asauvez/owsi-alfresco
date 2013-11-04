package fr.openwide.alfresco.app.framework.spring.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class ExceptionLoggerHandlerInterceptor extends HandlerInterceptorAdapter {

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		if (ex != null) {
			StringBuilder message = new StringBuilder();
			Class<?> clazz = getClass();
			if (handler != null) {
				clazz = handler.getClass();
				if (handler instanceof HandlerMethod) {
					HandlerMethod handlerMethod = (HandlerMethod) handler;
					clazz = handlerMethod.getBeanType();
					message.append(handlerMethod.toString()).append(": " );
				}
			}
			message.append(ex.getMessage());
			Logger logger = LoggerFactory.getLogger(clazz);
			// log exception as error
			logger.error(message.toString(), ex);
		}
	}

}
