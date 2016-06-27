package fr.openwide.alfresco.component.model.repository.model.qshare;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.QShare;

public class Shared extends AspectModel {

		public Shared() {
			super(NameReference.create(QShare.NAMESPACE, "shared"));
		}

		protected Shared(NameReference nameReference) {
			super(nameReference);
		}

		public final TextPropertyModel sharedId = PropertyModels.newText(this, QShare.NAMESPACE, "sharedId");
		
		public final TextPropertyModel sharedBy = PropertyModels.newText(this, QShare.NAMESPACE, "sharedBy");


}
