package com.gregorbyte.buildxpages.task;

import com.gregorbyte.buildxpages.NotesLibrary;
import com.gregorbyte.buildxpages.NotesNativeLibrary;
import com.gregorbyte.buildxpages.StringByReference;

public abstract class AbstractBxTask implements BxTask {

	public static short NSF_INFO_SIZE = 128;

	public static short INFOPARSE_TITLE = 0;
	public static short INFOPARSE_CATEGORIES = 1;
	public static short INFOPARSE_CLASS = 2;
	public static short INFOPARSE_DESIGN_CLASS = 3;

	public static int NOTE_ID_SPECIAL = 0xFFFF0000;
	public static int NOTE_CLASS_ICON 	= 0x00000010;
	
	public static int NOTE_CLASS_DOCUMENT	= 0x00000001;
	public static int NOTE_CLASS_ALL 		= 0x00007FFF;
	public static int NOTE_CLASS_ALLNONDATA	= 0x7FFE;
	
	public static short MAXUSERNAME = 256;
	
	@Override
	abstract public void execute();

	public String pathNetConstruct(String server, String fileName) {
		
		StringByReference retPathName = new StringByReference(MAXUSERNAME);
		
		short error = NotesNativeLibrary.INSTANCE.OSPathNetConstruct(null, server, fileName, retPathName);		
		checkError(error);
		
		return retPathName.getValue();
		
	}
	
	public void checkError(short error) {
		NotesLibrary.checkError(error);
	}

	public static void printApiError(short api_error) {
		NotesLibrary.printApiError(api_error);
	}
	
}
