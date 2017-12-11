package com.gregorbyte.buildxpages.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.gregorbyte.buildxpages.task.SetTemplateNames;

public class SetTemplateNamesTask extends Task {

	private String server = null;
	private String database = null;

	private boolean clearinheritfrom = false;
	private String inheritfrom = null;

	private boolean clearmastername = false;
	private String mastername = null;

	public void setServer(String server) {
		this.server = server;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public void setClearinheritfrom(boolean clearinheritfrom) {
		this.clearinheritfrom = clearinheritfrom;
	}

	public void setInheritfrom(String inheritfrom) {
		this.inheritfrom = inheritfrom;
	}

	public void setClearmastername(boolean clearmastername) {
		this.clearmastername = clearmastername;
	}

	public void setMastername(String mastername) {
		this.mastername = mastername;
	}

	@Override
	public void execute() throws BuildException {

		if (clearinheritfrom && inheritfrom != null)
			throw new BuildException("You cannot have clearinherit=true and inheritfrom set as well");

		if (clearmastername && mastername != null)
			throw new BuildException("You cannot have clearmaster=true and mastername set as well");

		String dbpath = null;

		if (this.server == null) {
			dbpath = "local!!" + this.database;
		} else {
			dbpath = this.server + "!!" + this.database;
		}

		log("Setting Template Names for: " + dbpath);
		if (this.clearinheritfrom) {
			log("... Inherit From:       <clear>");
		} else if (this.inheritfrom != null) {
			log("... Inherit From:       '" + this.inheritfrom + "'");
		} else {
			log("... Inherit From:       <no change>");
		}

		if (this.clearmastername) {
			log("... Is Master Template: <clear>'");
		} else if (this.mastername != null) {
			log("... Is Master Template: '" + this.mastername + "'");
		} else {
			log("... Is Master Template: <no change>");
		}

		SetTemplateNames task = new SetTemplateNames(this.server, this.database);

		if (this.clearinheritfrom)
			task.clearInherit();
		else if (inheritfrom != null && !inheritfrom.isEmpty())
			task.setInheritFrom(this.inheritfrom);

		if (this.clearmastername)
			task.clearMaster();
		else if (mastername != null && !mastername.isEmpty())
			task.setAsMaster(mastername);

		task.execute();

	}

}
