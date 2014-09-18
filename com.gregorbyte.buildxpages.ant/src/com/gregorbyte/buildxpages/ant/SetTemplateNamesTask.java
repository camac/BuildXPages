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
			throw new BuildException(
					"You cannot have clearinherit=true and inheritfrom set as well");

		if (clearmastername && mastername != null)
			throw new BuildException(
					"You cannot have clearmaster=true and mastername set as well");

		log(this.server);
		log(this.database);
		log("-------");
		log("Inherit From");
		log("Clear: " + this.clearinheritfrom);
		log(this.inheritfrom);
		log("-------");
		log("Is Master");
		log("Clear: " + this.clearmastername);
		log(this.mastername);

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
