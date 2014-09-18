package com.gregorbyte.buildxpages.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class SetSingleCopyXPageTask extends Task {

	private String server 	= null;
	private String database = null;
	private String scxdpath = null;
	private boolean scxdflag = false;

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

	public String getScxdpath() {
		return scxdpath;
	}

	public void setScxdpath(String scxdpath) {
		this.scxdpath = scxdpath;
	}

	public boolean getScxdflag() {
		return scxdflag;
	}

	public void setScxdflag(boolean scxdflag) {
		this.scxdflag = scxdflag;
	}

	@Override
	public void execute() throws BuildException {
		// TODO Auto-generated method stub

		log(this.server);
		log(this.database);
		log("Flag: " + this.scxdflag);
		log("Path: " + this.scxdpath);

		com.gregorbyte.buildxpages.task.SetSingleCopyXPageTask task 
		= new com.gregorbyte.buildxpages.task.SetSingleCopyXPageTask();

		task.setServer(this.server);
		task.setDatabase(this.database);
		task.setFlag(this.scxdflag);
		task.setScxdPath(this.scxdpath);
		
		task.execute();
		
		
	}

}
