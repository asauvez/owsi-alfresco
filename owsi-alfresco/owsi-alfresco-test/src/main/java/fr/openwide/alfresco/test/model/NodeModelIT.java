package fr.openwide.alfresco.test.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_EMPTY)
public class NodeModelIT<P extends PropertiesModelIT> {
	
	private List<String> aspectNames = new ArrayList<String>();
	private Date createdAt;
	private boolean isFolder;
	private boolean isFile;
	private UserInfoModelIT createdByUser;
    private Date modifiedAt;
    private UserInfoModelIT modifiedByUser;
    private String name;
    private String id;
    private String nodeType;
    private P properties;
    private String parentId;
    private PathInfoModelIT path;
    private PermissionsInfoModelIT permissions;
    private ContentInfoModelIT content;
    
    public NodeModelIT() {}
    
	public NodeModelIT(String name, String nodeType, P properties) {
		this.name = name;
		this.nodeType = nodeType;
		this.properties = properties;
	}
	
	public List<String> getAspectNames() {
		return aspectNames;
	}
	public void setAspectNames(List<String> aspectNames) {
		this.aspectNames = aspectNames;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public boolean getIsFolder() {
		return isFolder;
	}
	public void setIsFolder(boolean isFolder) {
		this.isFolder = isFolder;
	}
	public boolean getIsFile() {
		return isFile;
	}
	public void setIsFile(boolean isFile) {
		this.isFile = isFile;
	}
	public UserInfoModelIT getCreatedByUser() {
		return createdByUser;
	}
	public void setCreatedByUser(UserInfoModelIT createdByUser) {
		this.createdByUser = createdByUser;
	}
	public Date getModifiedAt() {
		return modifiedAt;
	}
	public void setModifiedAt(Date modifiedAt) {
		this.modifiedAt = modifiedAt;
	}
	public UserInfoModelIT getModifiedByUser() {
		return modifiedByUser;
	}
	public void setModifiedByUser(UserInfoModelIT modifiedByUser) {
		this.modifiedByUser = modifiedByUser;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNodeType() {
		return nodeType;
	}
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	public P getProperties() {
		return properties;
	}
	public void setProperties(P properties) {
		this.properties = properties;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public PathInfoModelIT getPath() {
		return path;
	}
	public void setPath(PathInfoModelIT path) {
		this.path = path;
	}
	public PermissionsInfoModelIT getPermissions() {
		return permissions;
	}
	public void setPermissions(PermissionsInfoModelIT permissions) {
		this.permissions = permissions;
	}

	public ContentInfoModelIT getContent() {
		return content;
	}
	public void setContent(ContentInfoModelIT content) {
		this.content = content;
	}
}
