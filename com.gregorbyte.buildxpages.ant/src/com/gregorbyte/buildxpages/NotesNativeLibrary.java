package com.gregorbyte.buildxpages;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.win32.StdCallLibrary;

/**
 * 
 * 
 * WORD = typedef unsigned short WORD DWORD = typedef unsigned 32-bit integer
 * (int)
 * 
 * @author Cameron
 *
 */
public interface NotesNativeLibrary extends StdCallLibrary {

	NotesNativeLibrary INSTANCE = (NotesNativeLibrary) Native.loadLibrary(
			"nnotes", NotesNativeLibrary.class);

	// STATUS LNPUBLIC NotesInitExtended(int argc, char far * far *argv);
	short NotesInitExtended(int argc, Pointer p);

	// TODO Not sure what to do here

	short NSFDbOpen(String dbName, IntByReference dbHandle);

	// STATUS LNPUBLIC NSFDbClose(DBHANDLE hDB);
	short NSFDbClose(int dbHandle);

	short NSFDbSpaceUsage(int dbHandle, IntByReference retAllocatedBytes,
			IntByReference retFreeBytes);

	// Must be Listed in the Server Document Administrators Field or in the
	// Admin Variable in the server's notes.ini
	short NSFRemoteConsole(String server, String command,
			IntByReference responseText);

	// void LNPUBLIC NotesTerm(void);
	void NotesTerm();

	// STATUS LNPUBLIC NSFDbInfoGet( DBHANDLE hDB, char far *retBuffer);
	short NSFDbInfoGet(int hDB, StringByReference retBuffer);

	// void LNPUBLIC NSFDbInfoParse(char far *Info, WORD What, char far
	// *Buffer,WORD Length);
	void NSFDbInfoParse(String info, short what, StringByReference buffer,
			short length);

	// void LNPUBLIC NSFDbInfoModify(char far *Info, WORD What, const char far
	// *Buffer);
	void NSFDbInfoModify(StringByReference info, short what, String buffer);

	// STATUS LNPUBLIC NSFDbInfoSet(DBHANDLE hDB, char far *Buffer);
	short NSFDbInfoSet(int hDB, String buffer);

	// STATUS LNPUBLIC NSFNoteOpen(DBHANDLE db_handle, NOTEID note_id, WORD
	// open_flags, NOTEHANDLE far *note_handle);
	short NSFNoteOpen(int db_handle, int note_id, short open_flags,
			NativeLongByReference note_handle);

	// TODO NOTEHANDLE

	/*
	 * STATUS LNPUBLIC NSFItemInfo( NOTEHANDLE note_handle, const char far
	 * *item_name, WORD name_len, BLOCKID far *item_blockid, WORD far
	 * *value_datatype, BLOCKID far *value_blockid, DWORD far *value_len);
	 * 
	 * I think value_datatype and value_len should be ShortByReference and
	 * IntByReference, but for now I need to pass in NULL so I am using Pointer
	 */
	short NSFItemInfo(NativeLong note_handle, StringByReference item_name,
			short name_len, Pointer item_blockid, Pointer value_datatype,
			Pointer value_blockid, Pointer value_len);

	// NSFItemIsPresent
	// BOOL NSFItemIsPresent(NOTEHANDLE note_handle, char far *item_name, WORD
	// item_name_length);
	int NSFItemIsPresent(NativeLongByReference note_handle,
			StringByReference item_name, short item_name_length);

	// TODO NOTEHANDLE

	// WORD LNPUBLIC NSFItemGetText(NOTEHANDLE  note_handle,	const char far *item_name,
	// 		char far *item_text, WORD  text_len);
	short NSFItemGetText(NativeLong note_handle, String item_name, StringByReference item_text, short text_len);
	
	// NSFItemSetText
	// STATUS LNPUBLIC NSFItemSetText( NOTEHANDLE hNote, const char far
	// *ItemName, const char far *ItemText, WORD TextLength);
	short NSFItemSetText(NativeLong hNote, String itemName, String itemText,
			short textLength);

	// TODO NOTEHANDLE

	// STATUS LNPUBLIC NSFNoteUpdate(NOTEHANDLE note_handle, WORD update_flags);
	short NSFNoteUpdate(NativeLong note_handle, short update_flags);

	// TODO NOTEHANDLE

	// STATUS LNPUBLIC NSFNoteClose(NOTEHANDLE note_handle);
	short NSFNoteClose(NativeLong note_handle);

	/*
	 * STATUS LNPUBLIC DesignRefresh(char *Server, DBHANDLE hDB, DWORD dwFlags,
	 * ABORTCHECKPROC AbortCheck, OSSIGMSGPROC MessageProc);
	 * 
	 * TODO investigate callback
	 */
	//short DesignRefresh(String server, int hDB, int dwFlags, Pointer abortCheck, Pointer messageProc);
	
	short DesignRefresh(String server, int hDb, int dwFlags, Pointer abortCheck, DesignRefreshMessageCallback messageProc);

	// WORD LNPUBLIC OSLoadString(HMODULE hModule, STATUS StringCode, char far
	// *retBuffer, WORD BufferLength);
	short OSLoadString(IntByReference hModule, short StringCode,
			StringByReference retBuffer, short BufferLength);

	//	STATUS LNPUBLIC OSPathNetConstruct(
	//			const char far *PortName,
	//			const char far *ServerName,
	//			const char far *FileName,
	//			char far *retPathName);
	short OSPathNetConstruct(Pointer portName, String serverName, String fileName, StringByReference retPathName);

	//	STATUS LNPUBLIC NSFDbCreateAndCopy(
	//			const char far *srcDb,
	//			const char far *dstDb,
	//			WORD  NoteClass,
	//			WORD  limit,
	//			DWORD  flags,
	//			DBHANDLE far *retHandle);
	short NSFDbCreateAndCopy(String srcDb, String dstDb, short NoteClass, short limit, int flags, IntByReference retHandle);
	
	//	STATUS LNPUBLIC NSFDbDelete(
	//			cost char far *PathName);
	short NSFDbDelete(String pathName);
	
	
}
