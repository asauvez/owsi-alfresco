package fr.openwide.alfresco.app.web.validation.binding;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import fr.openwide.alfresco.app.web.validation.model.ValidationResponse;

/**
 * http://jquery.malsup.com/form/#file-upload
 */
public class MappingIframeJsonHttpMessageConverter extends MappingJackson2HttpMessageConverter {

	@Override
	protected void writeInternal(Object object, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		StringBuilder textarea = new StringBuilder("<textarea");
		if (object instanceof ValidationResponse && ((ValidationResponse) object).hasErrors()) {
			textarea.append(" status=\"").append(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).append("\"");
		}
		textarea.append(">");
		IOUtils.write(textarea, outputMessage.getBody());
		super.writeInternal(object, outputMessage);
		IOUtils.write("</textarea>", outputMessage.getBody());
	}

}
