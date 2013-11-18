package fr.openwide.alfresco.query.web.form.util;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

public class MessageUtils {

	private static final String UNKNOWN_CODE_PREFIX = "??? ";

	public static MessageSourceResolvable direct(String text) {
		return new DefaultMessageSourceResolvable(null, text);
	}

	public static MessageSourceResolvable code(String code, Object ... args) {
		return new DefaultMessageSourceResolvable(new String[] { code }, args, UNKNOWN_CODE_PREFIX + code);
	}

	public static MessageSourceResolvable codes(String ... codes) {
		return new DefaultMessageSourceResolvable(codes, null, UNKNOWN_CODE_PREFIX + codes[0]);
	}

	private MessageUtils() {}

}
