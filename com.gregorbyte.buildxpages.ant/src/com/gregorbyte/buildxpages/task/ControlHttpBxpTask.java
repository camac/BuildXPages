package com.gregorbyte.buildxpages.task;

import com.gregorbyte.buildxpages.NotesNativeLibrary;
import com.sun.jna.ptr.IntByReference;

public class ControlHttpBxpTask extends AbstractBxTask {

	private static final String ACTION_START = "start";
	private static final String ACTION_STOP = "stop";
	private static final String ACTION_RESTART = "restart";

	private static final String CMD_START = "load http";
	private static final String CMD_STOP = "tell http quit";
	private static final String CMD_RESTART = "restart task http";

	private String server;
	private String action;

	public ControlHttpBxpTask(String server, String action) {
		this.server = server;
		this.action = action;
	}

	public String getConsoleCommand() {

		if (action == null)
			return null;

		if (action.equalsIgnoreCase(ACTION_START)) {
			return CMD_START;
		} else if (action.equalsIgnoreCase(ACTION_STOP)) {
			return CMD_STOP;
		} else if (action.equalsIgnoreCase(ACTION_RESTART)) {
			return CMD_RESTART;
		}

		return null;
	}

	@Override
	protected void doTask() {

		String command = getConsoleCommand();

		if (command == null)
			throw new RuntimeException(
					"Could not determine action: must be start, stop or restart");

		NotesNativeLibrary notes = NotesNativeLibrary.SYNC_INSTANCE;

		IntByReference retInfo = new IntByReference();
		notes.NSFRemoteConsole(server, command, retInfo);

	}

}
