package fr.openwide.alfresco.component.model.repository.model;

import fr.openwide.alfresco.api.core.remote.model.NamespaceReference;
import fr.openwide.alfresco.component.model.repository.model.exif.ExifExif;

public interface ExifModel {

	// https://github.com/Alfresco/alfresco-repository/blob/master/src/main/resources/alfresco/model/contentModel.xml
	NamespaceReference NAMESPACE = NamespaceReference.create("exif", "http://www.alfresco.org/model/exif/1.0");

	// ---- Aspects

	ExifExif exif = new ExifExif();
}
