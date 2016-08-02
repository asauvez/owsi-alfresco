package fr.openwide.alfresco.app.core.site.model;

public enum SiteVisibility {
	PUBLIC("PUBLIC"),
	PRIVATE("PRIVATE"),
	MODERATED("MODERATED");
	
	private final String visibility;
	
	private SiteVisibility(String visibility){
		this.visibility = visibility;
	}
	
	@Override
	public String toString(){
		return visibility;
	}
}
