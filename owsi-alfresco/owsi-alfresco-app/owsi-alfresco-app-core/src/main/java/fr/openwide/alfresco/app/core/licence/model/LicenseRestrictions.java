package fr.openwide.alfresco.app.core.licence.model;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class LicenseRestrictions {

	private Date lastUpdate;
	private Integer users;
	// private documents;
	private String licenseMode;
	private boolean readOnly;
	private boolean updated;
	private Date licenseValidUntil;
	private String licenseHolder;
	private int level;
	// private warnings;
	// private errors;
	
	public Date getLastUpdate() {
		return lastUpdate;
	}
	public Integer getUsers() {
		return users;
	}
	public String getLicenseMode() {
		return licenseMode;
	}
	public boolean isReadOnly() {
		return readOnly;
	}
	public boolean isUpdated() {
		return updated;
	}
	public Date getLicenseValidUntil() {
		return licenseValidUntil;
	}
	public Long getLicenseValidForDays() {
		return (licenseValidUntil != null) 
				? TimeUnit.DAYS.convert(licenseValidUntil.getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS) + 1
				: null;
	}
	public String getLicenseHolder() {
		return licenseHolder;
	}
	public int getLevel() {
		return level;
	}
}
