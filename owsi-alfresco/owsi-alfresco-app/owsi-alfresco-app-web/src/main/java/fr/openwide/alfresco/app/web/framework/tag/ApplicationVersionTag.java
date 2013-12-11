package fr.openwide.alfresco.app.web.framework.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.taglibs.standard.tag.common.core.Util;
import org.springframework.web.util.TagUtils;

public class ApplicationVersionTag extends ApplicationContextAwareTag {

	private String var;

	private String scope = TagUtils.SCOPE_PAGE;

	@Override
	protected final void doTag(PageContext pageContext) throws JspException, IOException {
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
