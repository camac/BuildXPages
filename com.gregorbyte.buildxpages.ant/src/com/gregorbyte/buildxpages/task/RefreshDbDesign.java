package com.gregorbyte.buildxpages.task;

import com.gregorbyte.buildxpages.DesignRefreshMessageCallback;
import com.gregorbyte.buildxpages.NotesNativeLibrary;
import com.gregorbyte.buildxpages.StringByReference;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public class RefreshDbDesign extends AbstractBxTask {
	
	private String server			= null;
	private String destDb 			= null;
	private String templateServer 	= null;
	
	private DesignRefreshMessageCallback cb = null;
	
	public RefreshDbDesign(String destDb) {
		this.destDb = destDb;
		initDefaultCallback();
	}
	
	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getDestDb() {
		return destDb;
	}

	public void setDestDb(String destDb) {
		this.destDb = destDb;
	}

	public String getTemplateServer() {
		return templateServer;
	}

	public void setTemplateServer(String templateServer) {
		this.templateServer = templateServer;
	}

	public void setCallback(DesignRefreshMessageCallback cb) {
		this.cb = cb;
	}
	
	public DesignRefreshMessageCallback getCallback() {		
		return this.cb;
	}
	
	public void initDefaultCallback() {

		this.cb = new DesignRefreshMessageCallback() {

			@Override
			public void proc(StringByReference message, short mType) {
				System.out.println(message.getValue());
				System.out.println(mType);
			}

		};

	}
	
	@Override
	protected void doTask() {

		NotesNativeLibrary notes = NotesNativeLibrary.SYNC_INSTANCE;
		
		String pathnet = pathNetConstruct(server, destDb);
		
		IntByReference dbHandle = new IntByReference();
		boolean dbOpen = false;
		short errorint = 0;

		try {

			errorint = notes.NSFDbOpen(pathnet, dbHandle);
			checkError(errorint);
			dbOpen = true;

			
			notes.DesignRefresh(this.templateServer, dbHandle.getValue(), 0,
					Pointer.NULL, getCallback());

		} finally {
			
			if (dbOpen) {
				errorint = notes.NSFDbClose(dbHandle.getValue());
				checkError(errorint);
				dbOpen = false;
			}
			
		}

	}

}
