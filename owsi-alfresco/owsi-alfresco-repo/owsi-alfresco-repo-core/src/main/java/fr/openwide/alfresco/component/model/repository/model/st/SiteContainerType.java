package fr.openwide.alfresco.component.model.repository.model.st;

public enum SiteContainerType {
		
		DOCUMENT_LIBRARY("documentLibrary"),
		WIKI("wiki"),
		LINKS("links"),
		DISCUSSIONS("discussions"),
		DATALISTS("dataLists"),
		BLOG("blog"),
		CALENDAR("calendar");
		
		private final String code;
		private SiteContainerType(String code) {
			this.code = code;
		}
		public String getCode() {
			return code;
		}
	}
