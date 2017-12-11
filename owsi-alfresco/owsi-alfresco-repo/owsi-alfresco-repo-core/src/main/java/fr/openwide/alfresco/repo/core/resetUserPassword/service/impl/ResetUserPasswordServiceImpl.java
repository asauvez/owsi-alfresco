package fr.openwide.alfresco.repo.core.resetUserPassword.service.impl;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * owsi.reset-user-password.users=admin:admin123,toto
 * 
 * @author asauvez
 */
public class ResetUserPasswordServiceImpl implements InitializingBean {

	private String users = "";

	private MutableAuthenticationService authenticationService;

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Override
	public void afterPropertiesSet() throws Exception {
		if (! users.isEmpty()) {
			for (String userNameAndPassword : users.split(",")) {
				int pos = userNameAndPassword.indexOf(":");
				String userName = (pos != -1) ? userNameAndPassword.substring(0, pos) : userNameAndPassword.trim();
				String password = (pos != -1) ? userNameAndPassword.substring(pos+1) : userName.trim();
				
				LOGGER.warn("Reset " + userName + " password.");
				
				AuthenticationUtil.runAs(
					new AuthenticationUtil.RunAsWork<Void>() {
						@Override
						public Void doWork() throws Exception {
							authenticationService.setAuthentication(userName, password.toCharArray());
							return null;
						}
					}, AuthenticationUtil.getSystemUserName());
			}
		}
	}
	
	public void setUsers(String users) {
		this.users = users;
	}
	public void setAuthenticationService(MutableAuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}
}
