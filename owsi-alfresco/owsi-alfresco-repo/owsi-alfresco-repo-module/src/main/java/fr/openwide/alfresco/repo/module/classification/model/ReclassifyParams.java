package fr.openwide.alfresco.repo.module.classification.model;

import java.time.temporal.TemporalUnit;
import java.util.Date;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.module.model.OwsiModel;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.component.model.search.model.restriction.RestrictionBuilder;
import fr.openwide.alfresco.repo.module.classification.service.ClassificationService;

public class ReclassifyParams {

	private NameReference container;
	private Integer batchSize = ClassificationService.DEFAULT_RECLASSIFY_BATCH_SIZE;
	private RestrictionBuilder restrictions = new RestrictionBuilder();
	private boolean useCmis = false;
	
	public NameReference getContainer() {
		return container;
	}
	public ReclassifyParams container(NameReference container) {
		this.container = container;
		return this;
	}
	public ReclassifyParams container(ContainerModel model) {
		return container(model.getNameReference());
	}	
	
	public Integer getBatchSize() {
		return batchSize;
	}
	public ReclassifyParams batchSize(Integer batchSize) {
		this.batchSize = batchSize;
		return this;
	}
	
	public RestrictionBuilder getRestrictions() {
		return restrictions;
	}
	
	public boolean isUseCmis() {
		return useCmis;
	}
	public ReclassifyParams useCmis() {
		this.useCmis = true;
		return this;
	}
	
	public ReclassifyParams olderThan(Date date) {
		getRestrictions()
			.lt(OwsiModel.classifiable.classificationDate, date).of();
		return this;
	}
	public ReclassifyParams olderThan(Integer nb, TemporalUnit unit) {
		getRestrictions()
			.lt(OwsiModel.classifiable.classificationDate, nb, unit).of();
		return this;
	}
	public ReclassifyParams withoutClassificationDate() {
		getRestrictions()
			.isUnset(OwsiModel.classifiable.classificationDate).of();
		return this;
	}
	
	
}
