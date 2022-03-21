package fr.openwide.alfresco.app.web.framework.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.RequestContextUtils;

import fr.openwide.core.spring.util.SpringBeanUtils;

public abstract class ApplicationContextAwareTag extends SimpleTagSupport {

	@Autowired
	protected Environment environment;

	protected PageContext initTag() {
		PageContext pageContext = (PageContext) getJspContext();
		WebApplicationContext applicationContext = RequestContextUtils.getWebApplicationContext(pageContext.getRequest());
		SpringBeanUtils.autowireBean(applicationContext, this);
		return pageContext;
	}

	@Override
	public final void doTag() throws JspException, IOException {
		PageContext pageContext = initTag();
		doTag(pageContext);
	}

	protected abstract void doTag(PageContext pageContext) throws JspException, IOException;

}
