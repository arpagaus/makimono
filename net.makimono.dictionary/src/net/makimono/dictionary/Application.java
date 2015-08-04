package net.makimono.dictionary;

import net.makimono.dictionary.activity.MainActivity.FileExtractorTask;

public class Application extends android.app.Application {

	private FileExtractorTask fileExtractorTask;

	public FileExtractorTask getFileExtractorTask() {
		return fileExtractorTask;
	}

	public void setFileExtractorTask(FileExtractorTask fileExtractorTask) {
		this.fileExtractorTask = fileExtractorTask;
	}

}
