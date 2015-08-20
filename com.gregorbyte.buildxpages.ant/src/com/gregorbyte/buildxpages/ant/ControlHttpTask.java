package com.gregorbyte.buildxpages.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.gregorbyte.buildxpages.task.ControlHttpBxpTask;

public class ControlHttpTask extends Task {

	private String server;
	private String action;

	public void setServer(String server) {
		this.server = server;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@Override
	public void execute() throws BuildException {

		ControlHttpBxpTask task = new ControlHttpBxpTask(server, action);
		task.execute();
		log(this.server);

	}

}
