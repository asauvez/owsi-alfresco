package fr.openwide.alfresco.test.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExifProperties extends PropertiesModelIT {

	@JsonProperty("exif:pixelXDimension")
	public Integer pixelXDimension;

	@JsonProperty("exif:pixelYDimension")
	public Integer pixelYDimension;
}
