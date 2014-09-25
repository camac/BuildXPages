package com.gregorbyte.buildxpages.task;

import com.gregorbyte.buildxpages.NotesNativeLibrary;

public class DeleteDatabase extends AbstractBxTask {

	private String server;
	private String filename;
	
	public DeleteDatabase(String server, String filename) {
	
		this.server 	= server;
		this.filename 	= filename;
		
	}
	
	@Override
	protected void doTask() {

		NotesNativeLibrary notes = NotesNativeLibrary.SYNC_INSTANCE;
		
		String path = pathNetConstruct(server, filename);

		short error = notes.NSFDbDelete(path);		
		checkError(error);
		
	}

}
