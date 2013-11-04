package fr.openwide.alfresco.query.core.node.model.property;

import java.io.InputStream;

import fr.openwide.alfresco.query.core.node.model.TypeModel;
import fr.openwide.alfresco.query.core.node.model.value.NameReference;

public class ContentPropertyModel extends PropertyModel<InputStream> {

	public ContentPropertyModel(TypeModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<InputStream> getValueClass() {
		return InputStream.class;
	}

}
