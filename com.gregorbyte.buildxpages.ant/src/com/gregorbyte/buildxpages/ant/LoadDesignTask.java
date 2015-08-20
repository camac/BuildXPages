package com.gregorbyte.buildxpages.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.gregorbyte.buildxpages.task.LoadDesignBxpTask;

public class LoadDesignTask extends Task {

	private String server;
	private String directory;

	public void setServer(String server) {
		this.server = server;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	@Override
	public void execute() throws BuildException {

		LoadDesignBxpTask task = new LoadDesignBxpTask(server, directory);
		task.execute();
		log(this.server);

	}

}
