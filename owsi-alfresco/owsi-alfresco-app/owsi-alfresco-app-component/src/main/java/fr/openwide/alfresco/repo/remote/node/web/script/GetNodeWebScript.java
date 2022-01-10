package fr.openwide.alfresco.repo.remote.node.web.script;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.node.service.NodeRemoteService.GET_NODE_SERVICE;
import fr.openwide.alfresco.repo.core.swagger.web.script.OwsiSwaggerWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptFormatDefault;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptTransactionAllow;

@GenerateWebScript(
	paramClass=GET_NODE_SERVICE.class,
	shortName="get",
	description="Get a node by reference",
	formatDefaultEnum=GenerateWebScriptFormatDefault.JSON,
	transactionAllow=GenerateWebScriptTransactionAllow.READONLY,
	family=OwsiSwaggerWebScript.WS_FAMILY,
	visibleInSwagger=false,
	beanParent="webscript.owsi.node")
public class GetNodeWebScript extends AbstractNodeWebScript<RepositoryNode, GET_NODE_SERVICE> {

	@Override
	protected RepositoryNode execute(GET_NODE_SERVICE parameter) {
		return nodeService.get(
				Objects.requireNonNull(parameter.nodeReference, "NodeReference"), 
				Objects.requireNonNull(parameter.nodeScope, "NodeScope"));
	}

	@Override
	protected Collection<RepositoryNode> getOutputNodes(RepositoryNode result) {
		return Collections.singleton(result);
	}

	@Override
	protected Class<GET_NODE_SERVICE> getParameterType() {
		return GET_NODE_SERVICE.class;
	}

}
