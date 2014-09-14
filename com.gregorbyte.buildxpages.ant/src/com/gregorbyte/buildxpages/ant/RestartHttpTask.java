package com.gregorbyte.buildxpages.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class RestartHttpTask extends Task {

	private String server;
	
	public void setServer(String server) {
		this.server = server;
	}
	
	@Override
	public void execute() throws BuildException {

		//com.gregorbyte.buildxpages.task.RestartHttpTask task = new com.gregorbyte.buildxpages.task.RestartHttpTask(server);
		// TODO Actually do stuff
		//task.execute();
		
		log(this.server);
		
	}

	
	
}
