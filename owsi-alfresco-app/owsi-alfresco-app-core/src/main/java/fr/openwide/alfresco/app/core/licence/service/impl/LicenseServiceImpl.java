package fr.openwide.alfresco.app.core.licence.service.impl;

import fr.openwide.alfresco.app.core.licence.model.LicenseRestrictions;
import fr.openwide.alfresco.app.core.licence.service.LicenseService;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteBinding;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.WebScriptMethod;
import fr.openwide.alfresco.repo.wsgenerator.annotation.WebScriptEndPoint;
import fr.openwide.alfresco.repo.wsgenerator.model.WebScriptParam;

public class LicenseServiceImpl implements LicenseService {
	
	@WebScriptEndPoint(method=WebScriptMethod.GET, url="/api/admin/restrictions")
	private static class GET_RESTRICTIONS extends WebScriptParam<LicenseRestrictions> {
	}

	private RepositoryRemoteBinding remoteBinding;

	public LicenseServiceImpl(RepositoryRemoteBinding remoteBinding) {
		this.remoteBinding = remoteBinding;
	}
	
	@Override
	public LicenseRestrictions getRestrictions() {
		GET_RESTRICTIONS payload = new GET_RESTRICTIONS();
		return remoteBinding.builder(payload).call();
	}	
}
