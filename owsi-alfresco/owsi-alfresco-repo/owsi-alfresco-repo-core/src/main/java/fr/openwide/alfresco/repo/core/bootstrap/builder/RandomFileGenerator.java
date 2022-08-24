package fr.openwide.alfresco.repo.core.bootstrap.builder;

import java.util.function.Consumer;

public class RandomFileGenerator {
	
	private int number;
	private Consumer<FolderBootstrap> applyToFolder;
	private Consumer<FileBootstrap> applyToFile;
	private boolean generateFolder = true;

	public RandomFileGenerator(int number) {
		this.number = number;
	}
	
	public int getNumber() {
		return number;
	}
	
	public RandomFileGenerator applyToFolder(Consumer<FolderBootstrap> applyToFolder) {
		this.applyToFolder = applyToFolder;
		return this;
	}
	public Consumer<FolderBootstrap> getApplyToFolder() {
		return applyToFolder;
	}
	public RandomFileGenerator applyToFile(Consumer<FileBootstrap> applyToFile) {
		this.applyToFile = applyToFile;
		return this;
	}
	public Consumer<FileBootstrap> getApplyToFile() {
		return applyToFile;
	}

	public RandomFileGenerator generatorFolder(boolean generateFolder) {
		this.generateFolder = generateFolder;
		return this;
	}
	public boolean isGenerateFolder() {
		return generateFolder;
	}
}
