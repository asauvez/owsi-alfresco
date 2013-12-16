package fr.openwide.alfresco.query.core.node.model.property;

import java.io.Serializable;

import fr.openwide.alfresco.query.api.node.model.NameReference;
import fr.openwide.alfresco.query.core.node.model.Model;
import fr.openwide.alfresco.query.core.node.model.TypeModel;

public abstract class PropertyModel<C extends Serializable> extends Model {

	private final TypeModel type;

	public PropertyModel(TypeModel type, NameReference nameReference) {
		super(nameReference);
		this.type = type;
	}

	@Override
	public String toLucene() {
		return "@" + super.toLucene();
	}

	public abstract Class<C> getValueClass();

	public TypeModel getType() {
		return type;
	}

}
