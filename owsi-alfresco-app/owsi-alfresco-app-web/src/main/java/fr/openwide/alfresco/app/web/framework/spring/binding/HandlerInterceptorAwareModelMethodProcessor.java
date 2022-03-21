package fr.openwide.alfresco.app.web.framework.spring.binding;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.MethodParameter;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.ModelMethodProcessor;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Iterables;

public class HandlerInterceptorAwareModelMethodProcessor extends ModelMethodProcessor {

	private HandlerInterceptor[] interceptors;

	public HandlerInterceptorAwareModelMethodProcessor(Object[] interceptors) {
		if (interceptors != null) {
			Iterable<HandlerInterceptor> iterables = Iterables.filter(Arrays.asList(interceptors), HandlerInterceptor.class);
			this.interceptors = Iterables.toArray(iterables, HandlerInterceptor.class);
		}
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
		HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
		ModelAndView mv = new ModelAndView();
		applyPostHandle(request, response, mv);
		ModelMap model = mavContainer.getModel();
		model.addAllAttributes(mv.getModel());
		return super.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
	}

	/**
	 * {@see org.springframework.web.servlet.HandlerExecutionChain#applyPostHandle(request, response, mv)}
	 */
	private void applyPostHandle(HttpServletRequest request, HttpServletResponse response, ModelAndView mv) throws Exception {
		if (interceptors == null) {
			return;
		}
		for (int i = interceptors.length - 1; i >= 0; i--) {
			HandlerInterceptor interceptor = interceptors[i];
			interceptor.postHandle(request, response, null, mv);
		}
	}

}
