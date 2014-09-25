package com.gregorbyte.buildxpages.task;

import com.gregorbyte.buildxpages.NotesNativeLibrary;
import com.gregorbyte.buildxpages.StringByReference;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;

public class SetTemplateNames extends AbstractBxTask {

	public static final String FIELD_TITLE = "$Title";
	
	private String server = null;
	private String dbPath = null;
	
	private boolean modifyInherit 	= false;
	private boolean inherit 		= false;
	private String inheritFrom 		= null;
	
	private boolean modifyMaster 	= false;
	private boolean isMaster 		= false;
	private String masterName 		= null;
	
	public SetTemplateNames(String server, String dbPath) {
		this.server = server;
		this.dbPath = dbPath;
	}
	
	public SetTemplateNames clearMaster() {
		this.modifyMaster 	= true;
		this.isMaster 		= false;
		this.masterName		= null;
		return this;
	}
	
	public SetTemplateNames setAsMaster(String masterName) {
		this.modifyMaster 	= true;
		this.isMaster 		= true;
		this.masterName 	= masterName;
		return this;
	}
	
	public SetTemplateNames clearInherit() {
		this.modifyInherit 	= true;
		this.inherit 		= false;
		this.inheritFrom 	= null;
		return this;
	}
	
	public SetTemplateNames setInheritFrom(String inheritFrom) {
		this.modifyInherit 	= true;
		this.inherit		= true;
		this.inheritFrom 	= inheritFrom;
		return this;
	}
	
	@Override
	protected void doTask() {

		if (!(modifyInherit || modifyMaster)) return;
		
		NotesNativeLibrary notes = NotesNativeLibrary.SYNC_INSTANCE;

		IntByReference dbHandle = new IntByReference();
		NativeLongByReference note_handle = new NativeLongByReference();

		StringByReference db_info = new StringByReference(NSF_INFO_SIZE); // TODO
		StringByReference parsed = new StringByReference(NSF_INFO_SIZE);

		// Note Item field
		StringByReference titleField = new StringByReference(FIELD_TITLE);
		short item_name_length = (short) FIELD_TITLE.length();

		short open_flags = 0;

		// TODO IS THIS THE CORRECT OPERATION?
		int note_id = (NOTE_ID_SPECIAL | NOTE_CLASS_ICON);

		short errorint = 0;
		boolean dbOpen = false;
		boolean noteOpen = false;

		// OSPathNetConstruct - Needed?

		try {

			String path = this.pathNetConstruct(this.server, this.dbPath);			

			errorint = notes.NSFDbOpen(path, dbHandle);
			checkError(errorint);
			dbOpen = true;

			errorint = notes.NSFDbInfoGet(dbHandle.getValue(), db_info);
			checkError(errorint);

			notes.NSFDbInfoParse(db_info.getValue(), (short) 0, parsed,
					(short) 128);
			checkError(errorint);

			if (modifyMaster && isMaster)
				notes.NSFDbInfoModify(db_info, INFOPARSE_CLASS, masterName);

			// TODO clear master
			if (modifyMaster && !isMaster)
				System.out.println("TODO whatever it takes to clear master");
				

			if (modifyInherit && inherit)
				notes.NSFDbInfoModify(db_info, INFOPARSE_DESIGN_CLASS, inheritFrom);
			
			// TODO clear inherit
			if (modifyInherit && !inherit)
				System.out.println("TODO whatever it takes to clear inherit");

			errorint = notes.NSFDbInfoSet(dbHandle.getValue(),
					db_info.getValue());
			checkError(errorint);

			// Update the Note
			errorint = notes.NSFNoteOpen(dbHandle.getValue(), note_id,
					open_flags, note_handle);
			checkError(errorint);
			noteOpen = true;

			errorint = notes.NSFItemInfo(note_handle.getValue(), titleField,
					item_name_length, Pointer.NULL, Pointer.NULL, Pointer.NULL,
					Pointer.NULL);
			checkError(errorint);

			// see
			// http://www-10.lotus.com/ldd/nd6forum.nsf/d6091795dfaa5b1185256a7a0048a2d0/0b52a373458624fd852574f1006d39e9?OpenDocument
			// Look into the Header file, you can just pass null null null null
			// for the last args

			/* Update the FIELD_TITLE ("$TITLE") field if present */
			short infoLength = (short) db_info.getValue().length();
			errorint = notes.NSFItemSetText(note_handle.getValue(),
					FIELD_TITLE, db_info.getValue(), infoLength);
			checkError(errorint);

			errorint = notes.NSFNoteUpdate(note_handle.getValue(), (short) 0);
			checkError(errorint);

			/* Update the note in the database */
			errorint = notes.NSFNoteClose(note_handle.getValue());
			checkError(errorint);
			noteOpen = false;

			errorint = notes.NSFDbClose(dbHandle.getValue());
			checkError(errorint);
			dbOpen = false;

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
			
		}

	}

}
