package fr.openwide.alfresco.repo.contentstoreexport.model;

public class ContentStoreExportParams {
	
	// Liste de chemins à exporter (tel que visible dans le node browser), séparé par des virgules
	public String paths;
	
	// Liste de queries à exporter, séparé par des virgules
	public String queries;
	
	// Liste de noeuds à exporter, séparé par des virgules
	public String nodeRefs;
	
	// Export tout les noeuds 
	public String exportAll = "false";
	
	// Si false, n'export pas le contenu. Permet de savoir la taille.
	public String exportContent = "true";
	
	// Emplacement où écrire sur disque le Zip (défaut renvoie juste le Zip)
	public String writeTo;
	
	// Exporter sous forme de /contentStore/2019/12/31/....bin ou sous forme /Espace racine/...
	public String pathType = PathType.CONTENTSTORE.name();
	
	public boolean isExportAll() {
		return Boolean.parseBoolean(exportAll);
	}

	public boolean isExportContent() {
		return Boolean.parseBoolean(exportContent);
	}
	
	public enum PathType { CONTENTSTORE, ALFRESCO, BULK }
	public PathType getPathType() {
		return PathType.valueOf(pathType.toUpperCase());
	}
}