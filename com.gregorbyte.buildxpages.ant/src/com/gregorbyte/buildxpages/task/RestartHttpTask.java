package com.gregorbyte.buildxpages.task;

import com.gregorbyte.buildxpages.NotesNativeLibrary;
import com.sun.jna.ptr.IntByReference;

public class RestartHttpTask implements BxTask {

	private static final String CMD_TELL_HTTP_RESTART 	= null;
	//private static final String CMD_SHOW_HTTP_USERS 	= null;

	private String server;
	
	public RestartHttpTask(String server) {
		this.server = server;
	}
	
	@Override
	public void execute() {

		NotesNativeLibrary notes = NotesNativeLibrary.INSTANCE;

		IntByReference retInfo = new IntByReference();
		notes.NSFRemoteConsole(server, CMD_TELL_HTTP_RESTART, retInfo);

		// TODO OSLOCK STUFF

	}

}
