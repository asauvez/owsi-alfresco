package fr.openwide.alfresco.demo.repo.service;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.repo.node.NodeServicePolicies.OnAddAspectPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnUpdatePropertiesPolicy;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.repository.model.ExifModel;
import fr.openwide.alfresco.repo.dictionary.policy.service.AbstractPolicyService;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateService;

@GenerateService
public class ImagePolicyService extends AbstractPolicyService implements OnAddAspectPolicy, OnUpdatePropertiesPolicy {

	public ImagePolicyService() {
		super(ExifModel.exif);
	}

	@Override
	public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		if (hasPropertiesChanged(before, after, ExifModel.exif.pixelXDimension, ExifModel.exif.pixelYDimension)) {
			System.out.println("Taille changé");
		}
	}

	@Override
	public void onAddAspect(NodeRef nodeRef, QName aspectTypeQName) {
		System.out.println("Image ajouté");
	}
}
