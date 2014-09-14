package com.gregorbyte.buildxpages.task;

import com.gregorbyte.buildxpages.NotesLibrary;
import com.gregorbyte.buildxpages.NotesNativeLibrary;
import com.gregorbyte.buildxpages.StringByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;

public class SetSingleCopyXPageTask extends AbstractBxTask {

	private String server 	= null;
	private String database = null;
	private boolean flag 	= false;
	private String scxdPath = null;
	
	public SetSingleCopyXPageTask() {
		
	}
	
	public SetSingleCopyXPageTask(String targetDb, boolean flag, String scxdPath) {
		this.database	= targetDb;
		this.flag 		= flag;
		this.scxdPath 	= scxdPath;
	}
	
	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public String getScxdPath() {
		return scxdPath;
	}

	public void setScxdPath(String scxdPath) {
		this.scxdPath = scxdPath;
	}

	@Override
	public void execute() {

		NotesNativeLibrary notes = NotesNativeLibrary.INSTANCE;
		NotesLibrary.init();
		
		IntByReference dbHandle = new IntByReference();
		NativeLongByReference note_handle = new NativeLongByReference();

		
		// Note Item field
		String FIELD_SCXD = "$XpageSharedDesign";
		//StringByReference scxdFlagField = new StringByReference(FIELD_SCXD);
		//short flag_field_length = (short) FIELD_SCXD.length();

		String flagSetting = (flag) ? "1" : "0";

		StringByReference flagSettingRef = new StringByReference(flagSetting);

		String FIELD_SCXDPATH = "$XpageSharedDesignTemplate";
		//StringByReference scxdPathField = new StringByReference(FIELD_SCXDPATH);
		//short path_field_length = (short) FIELD_SCXDPATH.length();
		StringByReference scxdFilePath = new StringByReference(this.scxdPath);

		short open_flags = 0;
		int note_id = (NOTE_ID_SPECIAL | NOTE_CLASS_ICON);

		short errorint = 0;
		boolean dbOpen = false;
		boolean noteOpen = false;

		try {

			String pathnet = pathNetConstruct(server, database);
			errorint = notes.NSFDbOpen(pathnet, dbHandle);
			
			checkError(errorint);
			dbOpen = true;

			// Update the Note With SCXD details
			errorint = notes.NSFNoteOpen(dbHandle.getValue(), note_id,
					open_flags, note_handle);
			checkError(errorint);
			noteOpen = true;

			// errorint = notes.NSFItemInfo(note_handle.getValue(),
			// scxdFlagField, flag_field_length, Pointer.NULL, Pointer.NULL,
			// Pointer.NULL, Pointer.NULL);
			// checkError(errorint);

			// Check the Existing setting for the Flag
			StringByReference existFlag = new StringByReference(128);
			short retLengthFlag = notes.NSFItemGetText(note_handle.getValue(),
					FIELD_SCXD, existFlag, (short) 128);
			checkError(errorint);
			System.out
					.println("Prev scxdflag: " + existFlag.getValue().substring(0, retLengthFlag));

			
			short flagLength = (short) flagSettingRef.getValue().length();
			errorint = notes.NSFItemSetText(note_handle.getValue(), FIELD_SCXD,
					flagSettingRef.getValue(), flagLength);
			checkError(errorint);
			System.out.println("New scxdflag: " + flagSettingRef.getValue());

			// Update Path
			// errorint = notes.NSFItemInfo(note_handle.getValue(),
			// scxdPathField, path_field_length, Pointer.NULL, Pointer.NULL,
			// Pointer.NULL, Pointer.NULL);
			// checkError(errorint);
			
			StringByReference me = new StringByReference(128);
			short retLength = notes.NSFItemGetText(note_handle.getValue(),
					FIELD_SCXDPATH, me, (short) 128);
			checkError(errorint);
			System.out.println("Previous scxdpath: " + me.getValue().substring(0, retLength));

			short pathLength = (short) scxdFilePath.getValue().length();
			errorint = notes.NSFItemSetText(note_handle.getValue(),
					FIELD_SCXDPATH, scxdFilePath.getValue(), pathLength);
			checkError(errorint);
			System.out.println("New scxdpath: " + scxdFilePath.getValue());

			errorint = notes.NSFNoteUpdate(note_handle.getValue(), (short) 0);
			checkError(errorint);
			System.out.println("Update complete");

		} finally {

			if (noteOpen) {
				errorint = notes.NSFNoteClose(note_handle.getValue());
				checkError(errorint);
				noteOpen = false;
			}

			if (dbOpen) {
				errorint = notes.NSFDbClose(dbHandle.getValue());
				checkError(errorint);
				dbOpen = false;
			}
			
			NotesLibrary.deinit();

		}

	}

}
