package fr.openwide.alfresco.component.model.authority.model;


import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

import javax.mail.internet.InternetAddress;

import fr.openwide.alfresco.api.core.authentication.model.UserReference;
import fr.openwide.alfresco.api.core.authority.model.AuthorityReference;

public class CachedUser implements Serializable {

	private UserReference userReference;
	private String firstName;
	private String lastName;
	private String email;

	public CachedUser(UserReference userReference, String firstName, String lastName, String email) {
		this.userReference = userReference;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}
	
	public UserReference getUserReference() {
		return userReference;
	}
	public AuthorityReference getAuthorityReference() {
		return userReference.toAuthority();
	}
	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public String getFullName() {
		String firstName = getFirstName();
		String lastName = getLastName();
		return ((firstName != null) ? firstName : "") + " " + ((lastName != null) ? lastName : "").trim();
	}
	public String getEmail() {
		return email;
	}
	public InternetAddress getInternetAddress() {
		try {
			return new InternetAddress(getEmail(), getFullName());
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}
	
	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (object == this) {
			return true;
		}
		if (object instanceof CachedUser) {
			CachedUser other = (CachedUser) object;
			return Objects.equals(userReference, other.getUserReference());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(userReference);
	}

	@Override
	public String toString() {
		return userReference.toString();
	}

}
