package fr.openwide.alfresco.app.web.framework.tag;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class WebjarVersionTag extends SimpleTagSupport {

	private String webjar;

	private String var;

	private String scope = TagUtils.SCOPE_PAGE;

	private Pattern versionPattern = Pattern.compile("(-[0-9]+)$");

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
		String version = environment.getRequiredProperty("application.webjars." + webjar + ".version");
		// escaping, just in case...
		version = Util.escapeXml(version);
		// get the upstream version (for instance 1.10.2-1 -> 1.10.2)
		Matcher matcher = versionPattern.matcher(version);
		if (matcher.find()) {
			version = version.substring(0, matcher.start());
		}
		// store or print the output
		if (var != null) {
			pageContext.setAttribute(var, version, TagUtils.getScope(scope));
		} else {
			pageContext.getOut().print(version);
		}
	}

	public void setWebjar(String webjar) {
		this.webjar = webjar;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

}
