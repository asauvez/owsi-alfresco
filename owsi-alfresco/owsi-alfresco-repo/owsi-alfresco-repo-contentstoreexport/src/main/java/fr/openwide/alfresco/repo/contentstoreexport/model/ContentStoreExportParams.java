package fr.openwide.alfresco.repo.contentstoreexport.model;

public class ContentStoreExportParams {
	
	// Liste de chemins à exporter (tel que visible dans le node browser)
	public String[] paths;
	
	// Liste de queries à exporter
	public String[] queries;
	
	// Liste de noeuds à exporter
	public String[] nodeRefs;

	// Liste de sites à exporter
	public String[] sites;

	// Export tout les noeuds minimums nécessaires à lancer Alfresco
	public boolean exportBase = true;

	// Export tout les noeuds 
	public boolean exportAll = false;
	
	// Si false, n'export pas le contenu. Permet de savoir la taille.
	public boolean exportContent = true;

	// Si true, exporte également les anciennes versions.
	public boolean exportVersions = true;

	// Emplacement où écrire sur disque le Zip (défaut renvoie juste le Zip)
	public String writeTo;
	
	// Lance une nouvelle transaction au bout de n profondeur
	public int newTransactionEveryDepth = Integer.MAX_VALUE;
	
	// Exporter sous forme de /contentStore/2019/12/31/....bin ou sous forme /Espace racine/...
	public String pathType = PathType.CONTENTSTORE.name();
	public enum PathType { CONTENTSTORE, ALFRESCO, BULK }
	public PathType getPathType() {
		return PathType.valueOf(pathType.toUpperCase());
	}
	
	// N'exporte que les données modifiées depuis cette période (ex: P1D)
	public String since;
	
	public boolean acp = false;
	public boolean acpPermissions = true;
}