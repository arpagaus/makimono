package net.makimono;

import net.makimono.activity.HomeActivity.FileExtractorTask;

public class Application extends android.app.Application {

	private FileExtractorTask fileExtractorTask;

	public FileExtractorTask getFileExtractorTask() {
		return fileExtractorTask;
	}

	public void setFileExtractorTask(FileExtractorTask fileExtractorTask) {
		this.fileExtractorTask = fileExtractorTask;
	}

}
