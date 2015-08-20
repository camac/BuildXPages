package com.gregorbyte.buildxpages.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.gregorbyte.buildxpages.task.MaintenanceWarningBxpTask;

public class MaintenanceWarningTask extends Task {

	private String server;
	private String minutes;

	private boolean cancelwarning;

	public void setServer(String server) {
		this.server = server;
	}

	public void setMinutes(String minutes) {
		this.minutes = minutes;
	}

	public void setCancelwarning(boolean cancelwarning) {
		this.cancelwarning = cancelwarning;
	}

	@Override
	public void execute() throws BuildException {

		MaintenanceWarningBxpTask task = new MaintenanceWarningBxpTask(server);

		if (this.cancelwarning) {
			task.setCancelWarning(true);
		} else {

			if (minutes == null || minutes.equals("")) {
				throw new BuildException("Minutes Argument not supplied");
			}

			task.setMinutes(minutes);
		}

		task.execute();

	}

}
