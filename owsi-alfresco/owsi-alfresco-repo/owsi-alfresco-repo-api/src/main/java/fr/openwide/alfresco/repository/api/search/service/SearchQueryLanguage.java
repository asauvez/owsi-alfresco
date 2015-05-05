package fr.openwide.alfresco.repository.api.search.service;

public enum SearchQueryLanguage {

	FTS_ALFRESCO("fts-alfresco"),
	CMIS_ALFRESCO("cmis-alfresco"),
	CMIS_STRICT("cmis-strict"),
	SOLR_FTS_ALFRESCO("solr-fts-alfresco"),
	SOLR_ALFRESCO("solr-alfresco"),
	SOLR_CMIS("solr-cmis"),
	XPATH("xpath"),
	JCR_XPATH("jcr-xpath"),
	LUCENE("lucene");

	private String alfrescoName;

	private SearchQueryLanguage(String alfrescoName) {
		this.alfrescoName = alfrescoName;
	}
	
	public String getAlfrescoName() {
		return alfrescoName;
	}
	
}
