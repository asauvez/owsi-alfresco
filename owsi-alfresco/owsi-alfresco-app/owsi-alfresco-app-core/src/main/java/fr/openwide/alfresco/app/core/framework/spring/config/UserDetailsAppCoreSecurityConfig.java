package fr.openwide.alfresco.app.core.framework.spring.config;

import org.springframework.context.annotation.Configuration;

/**
 * Config for PrincipalType.USER_DETAILS.
 *  
 * The main authentication mecanism is not Alfresco. It may be users in database or any other systems.
 * 
 * To call Alfresco, you have to be in a runAs bloc. When the runAs bloc is ended, the original authentication
 * context is restored.
 */
@Configuration
public class UserDetailsAppCoreSecurityConfig extends AbstractAppCoreSecurityConfig {

}
