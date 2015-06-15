package fr.openwide.alfresco.app.core.framework.spring.config;

public class SecurityHierarchyBuilder {

	private StringBuilder sb = new StringBuilder();
	
	public SecurityHierarchyBuilder add(String ifRole, String ... thenRoles) {
		for (String thenRole : thenRoles) {
			sb.append(ifRole).append(" > ").append(thenRole).append("\n");
		}
		return this;
	}
	
	public String build() {
		return sb.toString();
	}
}
