package fr.openwide.alfresco.api.core.node.binding;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import fr.openwide.alfresco.api.core.node.model.RemoteCallParameters;

public class RemoteCallPayload<P> {
	
	private P payload;
	private RemoteCallParameters remoteCallParameters;
	
	public P getPayload() {
		return payload;
	}
	public void setPayload(P payload) {
		this.payload = payload;
	}
	
	@JsonInclude(Include.NON_EMPTY)
	public RemoteCallParameters getRemoteCallParameters() {
		return remoteCallParameters;
	}
	public void setRemoteCallParameters(RemoteCallParameters remoteCallParameters) {
		this.remoteCallParameters = remoteCallParameters;
	}
}
