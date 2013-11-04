package fr.openwide.alfresco.app.web.framework.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.taglibs.standard.tag.common.core.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.util.TagUtils;

import fr.openwide.core.spring.util.SpringBeanUtils;

public class ApplicationVersionTag extends SimpleTagSupport {

	private String var;

	private String scope = TagUtils.SCOPE_PAGE;

	@Autowired
	private Environment environment;

	private PageContext initTag() {
		PageContext pageContext = (PageContext) getJspContext();
		WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(pageContext.getServletContext());
		SpringBeanUtils.autowireBean(applicationContext, this);
		return pageContext;
	}

	@Override
	public void doTag() throws JspException, IOException {
		PageContext pageContext = initTag();
		// get version
		String version = environment.getRequiredProperty("application.version");
		// escaping, just in case...
		version = Util.escapeXml(version);
		// store or print the output
		if (var != null) {
			pageContext.setAttribute(var, version, TagUtils.getScope(scope));
		} else {
			pageContext.getOut().print(version);
		}
	}

	public void setVar(String var) {
		this.var = var;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

}
