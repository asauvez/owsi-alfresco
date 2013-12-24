package fr.openwide.alfresco.app.web.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

public class CheckMultipartFileValidator implements ConstraintValidator<CheckMultipartFile, MultipartFile> {

	private String[] extension;
	private String[] mimetype;
	private long minSize;
	private long maxSize;

	// TODO message par défaut + verif size ????

	@Override
	public void initialize(CheckMultipartFile constraintAnnotation) {
		extension = constraintAnnotation.extension();
		mimetype = constraintAnnotation.mimetype();
		minSize = constraintAnnotation.size().min();
		maxSize = constraintAnnotation.size().max();
		if (minSize < 0 || maxSize < minSize) {
			throw new IllegalStateException(String.format("Invalid size parameters: min=%d, max=%d", minSize, maxSize));
		}
	}

	@Override
	public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}
		boolean isValid = true;
		// check extension
		if (isValid && extension != null && extension.length > 0) {
			isValid = false;
			String fileExt = FilenameUtils.getExtension(value.getOriginalFilename());
			for (String ext : extension) {
				if (ext.equals(fileExt)) {
					isValid = true;
					break;
				}
			}
		}
		// check mimetype
		if (isValid && mimetype != null && mimetype.length > 0) {
			isValid = false;
			String fileMimetype = value.getContentType();
			for (String mime : mimetype) {
				if (mime.equals(fileMimetype)) {
					isValid = true;
					break;
				}
			}
		}
		// check size
		if (isValid) {
			long length = value.getSize();
			isValid = length >= minSize && length <= maxSize;
		}
		return isValid;
	}


}
