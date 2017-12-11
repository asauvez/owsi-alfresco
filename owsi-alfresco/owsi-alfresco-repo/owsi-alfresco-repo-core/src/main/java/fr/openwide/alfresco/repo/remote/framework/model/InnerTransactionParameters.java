package fr.openwide.alfresco.repo.remote.framework.model;

import java.io.Serializable;

import org.springframework.extensions.webscripts.Description.RequiredTransactionParameters;
import org.springframework.extensions.webscripts.TransactionParameters;

/**
 * Serializable extension of TransactionParameters
 */
public class InnerTransactionParameters extends TransactionParameters implements Serializable {

	private static final long serialVersionUID = 6760201659274310403L;

	public static InnerTransactionParameters build(RequiredTransactionParameters params) {
		InnerTransactionParameters inner = new InnerTransactionParameters();
		inner.setRequired(params.getRequired());
		inner.setCapability(params.getCapability());
		inner.setBufferSize(params.getBufferSize());
		return inner;
	}

}
