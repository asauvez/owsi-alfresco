package fr.openwide.alfresco.component.kerberos.authentication;

import org.springframework.security.kerberos.authentication.KerberosTicketValidation;
import org.springframework.security.kerberos.authentication.sun.SunJaasKerberosTicketValidator;

public class RealmAwareKerberosTicketValidator extends SunJaasKerberosTicketValidator {

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
		String username = ticket.username().split("@")[0]; // throw away the realm
		return new KerberosTicketValidation(username, servicePrincipal, token, ticket.getGssContext());
	}

	@Override
	public void setServicePrincipal(String servicePrincipal) {
		super.setServicePrincipal(servicePrincipal);
		this.servicePrincipal = servicePrincipal;
	}

}
