package fr.openwide.alfresco.component.model.search.model.restriction;

import fr.openwide.alfresco.component.model.search.helper.CustomISO9075;


public class PathRestriction extends Restriction {

	public static final String PATH_COMPANY_HOME = "/app:company_home";
	public static final String PATH_DICTIONARY   = "/app:company_home/app:dictionary";
	public static final String PATH_GUEST_HOME   = "/app:company_home/app:guest_home";
	public static final String PATH_USER_HOMES   = "/app:company_home/app:user_homes";
	public static final String PATH_SHARED       = "/app:company_home/app:shared";
	public static final String PATH_SITES        = "/app:company_home/st:sites";

	public static final String PATH_SYSTEM       = "/sys:system";
	public static final String PATH_PEOPLE       = "/sys:system/sys:people";
	public static final String PATH_AUTHORITIES  = "/sys:system/sys:authorities";
	
	private String path;
	private String suffix = "";

	public PathRestriction(RestrictionBuilder parent, String path) {
		super(parent);
		this.path = path;
	}

	/** 
	 * Retourne tous les éléments situés sous le chemin indiqué. 
	 */
	public PathRestriction below() {
		suffix = "//*";
		return this;
	}
	/**
	 * Retourne tous les éléments situés sous le chemin indiqué, plus l'élément indiqué. 
	 */
	public PathRestriction orBelow() {
		suffix = "//.";
		return this;
	}
	
	/** 
	 * Ajoute un élement au chemin. L'élément est encodé en ISO 09075. 
	 */
	public PathRestriction append(String pathElement) {
		path += "/" + CustomISO9075.encode(pathElement);
		return this;
	}
	/** 
	 * Ajoute un élement au chemin Ajoute "/cm:". 
	 */
	public PathRestriction appendCm(String nodeName) {
		path += "/cm:" + CustomISO9075.encode(nodeName);
		return this;
	}
	
	@Override
	protected String toQueryInternal() {
		return "PATH:" + toLuceneValue(null, path + suffix);
	}

}
