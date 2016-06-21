package fr.openwide.alfresco.app.core.site.model;

public class RepositorySite {

	private String shortName;
	private String sitePreset = "site-dashboard";
	private String title;
	private String description = "";
	private SiteVisibility visibility = SiteVisibility.PRIVATE;
	
	public RepositorySite() {}
	
	public RepositorySite(String title) {
		this(title, title.trim().toLowerCase().replaceAll("[\\ ]+", "-").replaceAll("[^0-9a-z\\-]", ""));
	}
	
	public RepositorySite(String title, String shortName) {
		this.title = title;
		this.shortName = shortName;
	}
	
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public String getSitePreset() {
		return sitePreset;
	}
	public void setSitePreset(String sitePreset) {
		this.sitePreset = sitePreset;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public SiteVisibility getVisibility() {
		return visibility;
	}
	public void setVisibility(SiteVisibility visibility) {
		this.visibility = visibility;
	}
	
}
