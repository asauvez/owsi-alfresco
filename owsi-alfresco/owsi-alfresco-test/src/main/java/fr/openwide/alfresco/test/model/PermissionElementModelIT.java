package fr.openwide.alfresco.test.model;

public class PermissionElementModelIT implements Comparable<PermissionElementModelIT> {

	private String authorityId;
	private String name;
	private String accessStatus;

	public String getAuthorityId() {
		return authorityId;
	}
	public void setAuthorityId(String authorityId) {
		this.authorityId = authorityId;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getAccessStatus() {
		return accessStatus;
	}
	public void setAccessStatus(String accessStatus) {
		this.accessStatus = accessStatus;
	}

	@Override
	public String toString() {
		return authorityId + "/" + name + "/" + accessStatus;
	}
	@Override
	public int compareTo(PermissionElementModelIT o) {
		return this.toString().compareTo(o.toString());
	}
}