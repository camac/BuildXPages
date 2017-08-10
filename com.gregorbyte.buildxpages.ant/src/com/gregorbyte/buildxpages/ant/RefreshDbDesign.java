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
		
		String dbpath = null;
		String servername = null;
		
		if (this.server  == null) {
			dbpath = "local!!" + this.database;
		} else {
			dbpath = this.server + "!!" + this.database;
		}
		
		if (this.templateserver == null) {
			servername = "local";
		} else {
			servername = this.templateserver;
		}
		
		log("Refreshing Design of Db : " + dbpath);
		log("Refreshing From Server  : " + servername);		
				
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
