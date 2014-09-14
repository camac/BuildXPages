package com.gregorbyte.buildxpages.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.gregorbyte.buildxpages.task.CreateAndCopyDatabase;

public class CopyNsfTask extends Task {

	private String srcserver = null;
	private String srcfilepath = null;
	private String dstserver = null;
	private String dstfilepath = null;

	public String getSrcserver() {
		return srcserver;
	}

	public void setSrcserver(String srcserver) {
		this.srcserver = srcserver;
	}

	public String getSrcfilepath() {
		return srcfilepath;
	}

	public void setSrcfilepath(String srcfilepath) {
		this.srcfilepath = srcfilepath;
	}

	public String getDstserver() {
		return dstserver;
	}

	public void setDstserver(String dstserver) {
		this.dstserver = dstserver;
	}

	public String getDstfilepath() {
		return dstfilepath;
	}

	public void setDstfilepath(String dstfilepath) {
		this.dstfilepath = dstfilepath;
	}

	@Override
	public void execute() throws BuildException {

		CreateAndCopyDatabase task = new CreateAndCopyDatabase();
		
		task.setSrcServer(this.srcserver);
		task.setSrcFilename(this.srcfilepath);
		task.setDstServer(this.dstserver);
		task.setDstFilename(this.dstfilepath);
		
		task.execute();
		
	}

}
