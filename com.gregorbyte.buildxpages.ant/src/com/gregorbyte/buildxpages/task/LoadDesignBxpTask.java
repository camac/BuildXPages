package com.gregorbyte.buildxpages.task;

import com.gregorbyte.buildxpages.NotesNativeLibrary;
import com.sun.jna.ptr.IntByReference;

public class LoadDesignBxpTask extends AbstractBxTask {

	private String server;
	private String directory;

	public LoadDesignBxpTask(String server, String directory) {
		this.server = server;
		this.directory = directory;
	}

	public String getConsoleCommand() {

		if (directory == null || "".equals(directory)) {
			return null;
		}

		StringBuilder sb = new StringBuilder("load design -d ");

		sb.append(directory);

		return sb.toString();
	}

	@Override
	protected void doTask() {

		String command = getConsoleCommand();

		if (command == null)
			throw new RuntimeException(
					"Could not determine directory to refresh");

		NotesNativeLibrary notes = NotesNativeLibrary.SYNC_INSTANCE;

		IntByReference retInfo = new IntByReference();
		notes.NSFRemoteConsole(server, command, retInfo);

	}

}
