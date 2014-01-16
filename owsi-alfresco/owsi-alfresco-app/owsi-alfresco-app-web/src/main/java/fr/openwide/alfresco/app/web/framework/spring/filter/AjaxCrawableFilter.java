package fr.openwide.alfresco.app.web.framework.spring.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Quand Google rencontre un lien de type http://www.exemple.com/toto#!nom=titi, 
 * il va parser la page http://www.exemple.com/toto?_escaped_fragment_=nom%3Dtiti
 * On analyse ce _escaped_fragment_ et on fait comme s'il s'agissait de paramètres de la requête :
 * http://www.exemple.com/toto?nom=titi
 * 
 * @author asauvez
 */
public class AjaxCrawableFilter implements Filter {

	private static final String ESCAPED_FRAGMENT = "_escaped_fragment_";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (   ! (request instanceof HttpServletRequest) 
			|| ! ((HttpServletRequest) request).getMethod().equals("GET")
			|| request.getParameter(ESCAPED_FRAGMENT) == null) {
			chain.doFilter(request, response);
		} else {
			HttpServletRequestWrapper wrapper = new AjaxCrawableServletRequestWrapper((HttpServletRequest) request);
			chain.doFilter(wrapper, response);
		}
	}

	@Override
	public void destroy() {}

	private final class AjaxCrawableServletRequestWrapper extends HttpServletRequestWrapper {
		private final String fragment;
		private final MultiValueMap<String, String> params;

		private AjaxCrawableServletRequestWrapper(HttpServletRequest request) {
			super(request);
			this.fragment = request.getParameter(ESCAPED_FRAGMENT);
			
			UriComponentsBuilder uriComponentsBuilder = ServletUriComponentsBuilder.fromRequest(request);
			uriComponentsBuilder.replaceQueryParam(ESCAPED_FRAGMENT);
			uriComponentsBuilder.query(fragment);
			this.params = uriComponentsBuilder.build().getQueryParams();
		}

		@Override
		public String getParameter(String name) {
			return params.getFirst(name);
		}

		@Override
		public Enumeration<String> getParameterNames() {
			return Collections.enumeration(params.keySet());
		}

		@Override
		public String[] getParameterValues(String name) {
			List<String> list = params.get(name);
			return (list != null) ? list.toArray(new String[list.size()]) : null;
		}

		@Override
		public String getQueryString() {
			return fragment;
		}

		@Override
		public Map<String, String[]> getParameterMap() {
			Map<String, String[]> map = new HashMap<>();
			for (Entry<String, List<String>> entry : params.entrySet()) {
				map.put(entry.getKey(), entry.getValue().toArray(new String[entry.getValue().size()]));
			}
			return map;
		}
	}
}
