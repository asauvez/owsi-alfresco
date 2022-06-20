package fr.openwide.alfresco.test.client;

import org.apache.http.client.methods.HttpPost;

import fr.openwide.alfresco.test.model.UserInfoModelIT;

public class UserRestClient {

	private final AlfrescoRestClient client;
	
	public UserRestClient(AlfrescoRestClient client) {
		this.client = client;
	}
	
	public UserInfoModelIT createTestUser(String username) {
		UserInfoModelIT user = new UserInfoModelIT();
		user.setId(username);
		user.setFirstName(username);
		user.setLastName(username);
		user.setEmail(username);
		user.setPassword(username);
		return user;
	}
	
	public void createUser(UserInfoModelIT user) {
		String url = "/api/-default-/public/alfresco/versions/1/people";
		HttpPost request = client.postRequest(url, user);
		client.request(request, String.class);
	}

	public void deleteUser(String username) {
		String url = "/s/api/people/" + username;
		client.delete(url);
	}

	public void createUserThenDelete(UserInfoModelIT user, Runnable runnable) {
		createUser(user);
		try {
			runnable.run();
		} finally {
			deleteUser(user.getId());
		}
	}
}
