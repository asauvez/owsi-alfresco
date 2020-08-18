package fr.openwide.alfresco.component.model.repository.model.exif;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.BooleanPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.DateTimePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.DoublePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.IntegerPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.ExifModel;

public class ExifExif extends AspectModel {

	public ExifExif() {
		super(NameReference.create(ExifModel.NAMESPACE, "exif"));
	}

	protected ExifExif(NameReference nameReference) {
		super(nameReference);
	}

	public final DateTimePropertyModel dateTimeOriginal = PropertyModels.newDateTime(this, ExifModel.NAMESPACE, "dateTimeOriginal");
	public final IntegerPropertyModel pixelXDimension = PropertyModels.newInteger(this, ExifModel.NAMESPACE, "pixelXDimension");
	public final IntegerPropertyModel pixelYDimension = PropertyModels.newInteger(this, ExifModel.NAMESPACE, "pixelYDimension");
	public final DoublePropertyModel exposureTime = PropertyModels.newDouble(this, ExifModel.NAMESPACE, "exposureTime");
	public final DoublePropertyModel fNumber = PropertyModels.newDouble(this, ExifModel.NAMESPACE, "fNumber");
	public final BooleanPropertyModel flash = PropertyModels.newBoolean(this, ExifModel.NAMESPACE, "flash");
	public final DoublePropertyModel focalLength = PropertyModels.newDouble(this, ExifModel.NAMESPACE, "focalLength");
	public final TextPropertyModel isoSpeedRatings = PropertyModels.newText(this, ExifModel.NAMESPACE, "isoSpeedRatings");
	public final TextPropertyModel manufacturer = PropertyModels.newText(this, ExifModel.NAMESPACE, "manufacturer");
	public final TextPropertyModel model = PropertyModels.newText(this, ExifModel.NAMESPACE, "model");
	public final TextPropertyModel software = PropertyModels.newText(this, ExifModel.NAMESPACE, "software");
	public final IntegerPropertyModel orientation = PropertyModels.newInteger(this, ExifModel.NAMESPACE, "orientation");
	public final DoublePropertyModel xResolution = PropertyModels.newDouble(this, ExifModel.NAMESPACE, "xResolution");
	public final DoublePropertyModel yResolution = PropertyModels.newDouble(this, ExifModel.NAMESPACE, "yResolution");
	public final TextPropertyModel resolutionUnit = PropertyModels.newText(this, ExifModel.NAMESPACE, "resolutionUnit");
}
