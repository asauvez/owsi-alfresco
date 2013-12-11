package fr.openwide.alfresco.repository.api.authentication.model;

import java.io.Serializable;

public class RepositoryUserRequest implements Serializable {

	private static final long serialVersionUID = 2007460653337864562L;

	private String username;
	private String password;

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

}
