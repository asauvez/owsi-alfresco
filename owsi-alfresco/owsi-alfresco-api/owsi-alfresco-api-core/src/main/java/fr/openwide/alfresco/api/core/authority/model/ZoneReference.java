package fr.openwide.alfresco.api.core.authority.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonValue;

/** http://docs.alfresco.com/6.0/concepts/secur-zones.html */
public class ZoneReference implements Serializable {
	
	/** is for authorities defined within Alfresco Content Services and not synchronized from an external source. 
	 * This is the default zone for authentication.*/
	public static final ZoneReference AUTH_ALF = ZoneReference.zone("AUTH.ALF");
	
	/** is for person and group nodes to be found by a normal search. 
	 * If no zone is specified for a person or group node, they will be a member of this default zone. */
	public static final ZoneReference APP_DEFAULT = ZoneReference.zone("APP.DEFAULT");
	/**  is for hidden authorities related to Alfresco Share. */
	public static final ZoneReference APP_SHARE = ZoneReference.zone("APP.SHARE");
	/** will be added for authorities related to RM. */
	public static final ZoneReference APP_RM = ZoneReference.zone("APP.RM");
	
	private String name;

	private ZoneReference(String name) {
		this.name = name;
	}
	
	public static ZoneReference zone(String name) {
		return new ZoneReference(name);
	}
	public static ZoneReference authExt(String subsystem) {
		return zone("AUTH.EXT." + subsystem);
	}

	@JsonValue
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (object == this) {
			return true;
		}
		if (object instanceof ZoneReference) {
			ZoneReference other = (ZoneReference) object;
			return Objects.equals(name, other.getName());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

}
