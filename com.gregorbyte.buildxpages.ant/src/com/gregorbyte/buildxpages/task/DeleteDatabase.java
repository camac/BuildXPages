package com.gregorbyte.buildxpages.task;

import com.gregorbyte.buildxpages.NotesLibrary;
import com.gregorbyte.buildxpages.NotesNativeLibrary;

public class DeleteDatabase extends AbstractBxTask {

	private String server;
	private String filename;
	
	public DeleteDatabase(String server, String filename) {
	
		this.server 	= server;
		this.filename 	= filename;
		
	}
	
	@Override
	public void execute() {

		NotesNativeLibrary notes = NotesNativeLibrary.INSTANCE;
		
		NotesLibrary.init();
		
		String path = pathNetConstruct(server, filename);

		short error = notes.NSFDbDelete(path);		
		checkError(error);
		
		NotesLibrary.deinit();
		
	}

}
