package com.gregorbyte.buildxpages.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.gregorbyte.buildxpages.DesignRefreshMessageCallback;
import com.gregorbyte.buildxpages.StringByReference;

public class RefreshDbDesign extends Task {

	private String server			= null;
	private String database 		= null;
	private String templateserver 	= null;
	
	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getTemplateserver() {
		return templateserver;
	}

	public void setTemplateserver(String templateserver) {
		this.templateserver = templateserver;
	}

	@Override
	public void execute() throws BuildException {
		
		log(this.server);
		log(this.database);
		log(this.templateserver);
				
		com.gregorbyte.buildxpages.task.RefreshDbDesign task = new com.gregorbyte.buildxpages.task.RefreshDbDesign(
				this.database);
		
		task.setServer(this.server);
		task.setTemplateServer(this.templateserver);
		
		task.setCallback(getCallback());		
		
		task.execute();

	}
	
	public DesignRefreshMessageCallback getCallback() {
		
		
		return new DesignRefreshMessageCallback() {
			
			@Override
			public void proc(StringByReference message, short mType) {

				log(message.getValue());
				
			}
		};
		
	}

}
