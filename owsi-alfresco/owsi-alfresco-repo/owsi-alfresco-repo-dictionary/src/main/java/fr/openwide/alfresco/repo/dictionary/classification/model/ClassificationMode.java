package fr.openwide.alfresco.repo.dictionary.classification.model;

public enum ClassificationMode {

	/** Déplace le document reçu dans l'arborescence de classification. */
	MOVE,

	/** Laisse le document reçu où il est et crée un lien dans l'arborescence de classification. */
	LINK,

	/** Laisse le document reçu où il est et copie le dans l'arborescence de classification. */
	COPY,

	/** Déplace le document reçu dans l'arborescence de classification, puis crée un lien dans le dossier original. */
	MOVE_AND_LINK_BACK

}
