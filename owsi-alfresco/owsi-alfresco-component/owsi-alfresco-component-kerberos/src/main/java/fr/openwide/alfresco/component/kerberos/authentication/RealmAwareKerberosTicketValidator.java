package fr.openwide.alfresco.component.kerberos.authentication;

import org.springframework.security.kerberos.authentication.KerberosTicketValidation;
import org.springframework.security.kerberos.authentication.sun.SunJaasKerberosTicketValidator;

public class RealmAwareKerberosTicketValidator extends SunJaasKerberosTicketValidator {

	private boolean enabled;

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
		String servicePrincipal = ticket.subject().getPrincipals().iterator().next().getName();
		return new KerberosTicketValidation(username, servicePrincipal, token, ticket.getGssContext());
	}

}
