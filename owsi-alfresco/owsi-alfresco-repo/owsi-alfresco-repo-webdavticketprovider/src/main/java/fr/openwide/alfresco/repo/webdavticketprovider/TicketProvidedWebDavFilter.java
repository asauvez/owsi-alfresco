package fr.openwide.alfresco.repo.webdavticketprovider;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class TicketProvidedWebDavFilter implements Filter {
	
	private String fakeUser = "admin";
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		String requestURI = req.getRequestURI();
		
		
		System.err.println(" ================ curl -v -X " + req.getMethod() + " http://owsi65:8080" + req.getRequestURI());
//		System.err.println(new TreeMap(req.getParameterMap()));
//		Enumeration<String> names = req.getHeaderNames();
//		while (names.hasMoreElements()) {
//			String name = names.nextElement();
//			System.err.println("header " + name + ": " +req.getHeader(name));
//		}
		
		String[] split = requestURI.split("/");
		if ("owsi-aos".equals(split[3])) {
			chain.doFilter(new HttpServletRequestWrapper(req) {
				@Override
				public String getRequestURI() {
					return requestURI.substring(requestURI.indexOf("/webdav/"));
				}
				@Override
				public String getParameter(String name) {
					if ("ticket".equals(name)) {
						return split[4];
					}
					return super.getParameter(name);
				}
			}, response);
		} else {
			chain.doFilter(new HttpServletRequestWrapper(req) {
				@Override
				public String getRemoteUser() {
					return (requestURI.contains("/_vti_bin/")) ? fakeUser : null;
				}
			}, response);
		}
	}

	@Override public void init(FilterConfig filterConfig) throws ServletException {}
	@Override public void destroy() { }
}
