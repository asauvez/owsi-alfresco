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

public class ConfigurationTypeTag extends SimpleTagSupport {

	private String var;

	private String scope = TagUtils.SCOPE_PAGE;

	private Boolean development;

	private Boolean deployment;

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
		// get configuration type
		String configurationType = environment.getRequiredProperty("application.configurationType");
		// escaping, just in case...
		configurationType = Util.escapeXml(configurationType);
		boolean isConfigurationTypeDevelopment = configurationType != null && configurationType.equals("development");
		// store the configuration type or evaluate body
		if (var != null) {
			Object value;
			if (development == null && deployment == null) {
				// set the configurationType
				value = configurationType;
			} else {
				value = condition(isConfigurationTypeDevelopment);
			}
			pageContext.setAttribute(var, value, TagUtils.getScope(scope));
		} else {
			if (development == null && deployment == null) {
				// display configurationType
				pageContext.getOut().print(configurationType);
			} else if (condition(isConfigurationTypeDevelopment)) {
				getJspBody().invoke(null);
			}
		}
	}

	private boolean condition(boolean isConfigurationTypeDevelopment) {
		if (isConfigurationTypeDevelopment) {
			return (development != null && development) || (development == null && deployment != null && ! deployment);
		} else { // configurationType deployment
			return (deployment != null && deployment) || (deployment == null && development != null && ! development);
		}
	}

	public void setVar(String var) {
		this.var = var;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public void setDevelopment(Boolean development) {
		this.development = development;
	}
	public void setDeployment(Boolean deployment) {
		this.deployment = deployment;
	}

}
