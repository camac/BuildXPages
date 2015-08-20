package com.gregorbyte.buildxpages.task;

import com.gregorbyte.buildxpages.NotesNativeLibrary;
import com.sun.jna.ptr.IntByReference;

public class MaintenanceWarningBxpTask extends AbstractBxTask {

	private String server;
	private String minutes;

	private boolean cancelWarning = false;

	public MaintenanceWarningBxpTask(String server) {
		this.server = server;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getMinutes() {
		return minutes;
	}

	public void setMinutes(String minutes) {
		this.minutes = minutes;
	}

	public boolean isCancelWarning() {
		return cancelWarning;
	}

	public void setCancelWarning(boolean cancelWarning) {
		this.cancelWarning = cancelWarning;
	}

	public String getConsoleCommand() {

		StringBuilder sb = new StringBuilder("tell http osgi ");

		if (cancelWarning) {
			sb.append(" goingdowncancel");
		} else {
			sb.append(" goingdown " + getMinutes());
		}

		return sb.toString();

	}

	@Override
	protected void doTask() {

		NotesNativeLibrary notes = NotesNativeLibrary.SYNC_INSTANCE;

		String command = getConsoleCommand();

		if (command == null || command.equals("")) {
			throw new RuntimeException("No Console Command");
		}

		IntByReference retInfo = new IntByReference();
		notes.NSFRemoteConsole(server, command, retInfo);

	}

}
