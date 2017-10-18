package fr.openwide.alfresco.component.kerberos.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.kerberos.authentication.KerberosTicketValidation;
import org.springframework.security.kerberos.authentication.sun.SunJaasKerberosTicketValidator;

import fr.openwide.alfresco.component.kerberos.framework.spring.config.ComponentKerberosSecurityConfig;

public class RealmAwareKerberosTicketValidator extends SunJaasKerberosTicketValidator {

	private static final Logger LOGGER = LoggerFactory.getLogger(ComponentKerberosSecurityConfig.class);
	
	private boolean enabled;
	private String servicePrincipal;

	public RealmAwareKerberosTicketValidator(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (enabled) {
			super.afterPropertiesSet();
		}
	}

	@Override
	public KerberosTicketValidation validateTicket(byte[] token) {
		KerberosTicketValidation ticket = super.validateTicket(token);
		
		LOGGER.debug(ticket.username());
		
		String username = ticket.username().split("@")[0]; // throw away the realm
		return new KerberosTicketValidation(username, servicePrincipal, ticket.responseToken(), ticket.getGssContext());
	}

	@Override
	public void setServicePrincipal(String servicePrincipal) {
		super.setServicePrincipal(servicePrincipal);
		this.servicePrincipal = servicePrincipal;
	}

}
