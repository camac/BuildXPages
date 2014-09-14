package com.gregorbyte.buildxpages.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.gregorbyte.buildxpages.task.DeleteDatabase;

public class DeleteNsfTask extends Task {

	private String server;
	private String filename;

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	@Override
	public void execute() throws BuildException {

		DeleteDatabase task = new DeleteDatabase(server, filename);
		task.execute();

	}

}
