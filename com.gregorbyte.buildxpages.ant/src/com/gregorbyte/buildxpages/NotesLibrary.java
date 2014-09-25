package com.gregorbyte.buildxpages;

import com.gregorbyte.buildxpages.task.DeleteDatabase;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;

public class NotesLibrary {

	public static boolean initialised = false;
	
	public static final String TEST_SERVER = "domino02";
	public static final String TEST_DB = "domino02!!Cameron\\plugindev.nsf";
	public static final String TEST_REFRESHDB = "domino02!!JobFiles\\Z1001.nsf";
	public static final String TEST_SCXDDB = "domino02!!Gregorbyte\\DoraDeploy.nsf";
	
	public static final String CMD_RESTART_HTTP = "restart task http";
	public static final String CMD_SHOW_HTTP_USERS = "tell http show users";

	public static short NSF_INFO_SIZE = 128;

	public static short INFOPARSE_TITLE = 0;
	public static short INFOPARSE_CATEGORIES = 1;
	public static short INFOPARSE_CLASS = 2;
	public static short INFOPARSE_DESIGN_CLASS = 3;

	public static short MAXUSERNAME = 256;
	
	public static int NOTE_ID_SPECIAL = 0xFFFF0000;
	public static int NOTE_CLASS_ICON = 0x00000010;

	@Deprecated
	public static void init() {
		
		if (!initialised) {

			NotesNativeLibrary notes = NotesNativeLibrary.SYNC_INSTANCE;
			Native.setProtected(true);

			short error = notes.NotesInitExtended(0, null);
			checkError(error);
			initialised = true;
			
		}
		
	}
	
	@Deprecated
	public static void initThread() {
		init();
		
		if (initialised) {
			Notes.INSTANCE.initThread();
		}
		
	}
	
	@Deprecated
	public static void termThread() {
		
		// IF this thread is initialised terminate it
		Notes.INSTANCE.termThread();			
		
	}
	
	@Deprecated
	public static void deinit() {
		
		if (initialised) {
			NotesNativeLibrary notes = NotesNativeLibrary.SYNC_INSTANCE;
			notes.NotesTerm();	
			initialised = false;
		}
		
	}
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {

		NotesNativeLibrary notes = NotesNativeLibrary.SYNC_INSTANCE;
		short error = 0;
		boolean notesInitialised = false;

		Native.setProtected(true);

		try {
			
			error = notes.NotesInitExtended(0, null);
			checkError(error);
			notesInitialised = true;

			String srcFilename = "demo\\GitTest.nsf";
			String dstFilename = "demo\\DidItwork.nsf";
			
			//CreateAndCopyDatabase task = new CreateAndCopyDatabase(null, srcFilename, null, dstFilename);
			DeleteDatabase task = new DeleteDatabase(null, dstFilename);			
			task.execute();
			
			//ShowSpaceUsageTask task = new ShowSpaceUsageTask(TEST_DB);
			//task.execute();
			//setTemplateNames("time","tosleep");
			//refreshDesign();
			//setSingleCopyXPageDesign(true, "Something.nsf");
			//setSingleCopyXPageDesign(true, "WatchFootball.nsf");

		} catch (RuntimeException e) {
			
			e.printStackTrace();
			
		} finally {
			
			if (notesInitialised)
				notes.NotesTerm();
		}

	}

	@Deprecated
	public static void checkError(short error) {

		if (error > 0) {
			printApiError(error);
			throw new RuntimeException("Something went Wrong");
		}
	}

	@Deprecated
	public static void printApiError(short api_error) {

		System.out.println(api_error);

		NotesNativeLibrary notes = NotesNativeLibrary.SYNC_INSTANCE;

		IntByReference nullhandle = new IntByReference(0);

		short mask = 16383; // 2B3
		// short mask = 0x3FFF;

		short string_id = (short) (mask & api_error);
		System.out.println(string_id);

		short bufferLength = 200;
		StringByReference error_text = new StringByReference(bufferLength);

		short text_len = notes.OSLoadString(nullhandle, string_id, error_text,
				bufferLength);

		System.out.println(text_len);
		System.out.println(error_text.getValue());

	}
	
}
