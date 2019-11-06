package fr.openwide.alfresco.test.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PermissionsInfoModelIT {

	private List<PermissionElementModelIT> inherited;
	private List<PermissionElementModelIT> locallySet;
	private List<String> settable;
	private Boolean isInheritanceEnabled;

	public List<PermissionElementModelIT> getInherited() {
		return inherited;
	}
	public void setInherited(List<PermissionElementModelIT> inherited) {
		this.inherited = inherited;
	}
	
	public List<PermissionElementModelIT> getLocallySet() {
		return locallySet;
	}
	public void setLocallySet(List<PermissionElementModelIT> locallySet) {
		this.locallySet = locallySet;
	}

	public List<String> getSettable() {
		return settable;
	}
	public void setSettable(List<String> settable) {
		this.settable = settable;
	}

	public Boolean getIsInheritanceEnabled() {
		return isInheritanceEnabled;
	}
	public void setIsInheritanceEnabled(Boolean isInheritanceEnabled) {
		this.isInheritanceEnabled = isInheritanceEnabled;
	}
}