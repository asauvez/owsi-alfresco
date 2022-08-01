package fr.openwide.alfresco.component.model.repository.model.cm;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.constraint.MandatoryEnforcedPropertyConstraint;
import fr.openwide.alfresco.component.model.node.model.constraint.RegexPropertyConstraint;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.repository.model.sys.SysBase;

public class CmObject extends SysBase {

	public CmObject() {
		super(CmModel.NAMESPACE.createQName("cmobject"));
	}

	protected CmObject(QName qName) {
		super(qName);
	}

	public final TextPropertyModel name = PropertyModels.newText(this, CmModel.NAMESPACE, "name",
			MandatoryEnforcedPropertyConstraint.INSTANCE,
			new RegexPropertyConstraint("(.*[\\\"\\*\\\\\\>\\<\\?\\/\\:\\|]+.*)|(.*[\\.]?.*[\\.]+$)|(.*[ ]+$)", false));

	public final CmAuditable auditable = addMandatoryAspect(CmModel.auditable);
}
