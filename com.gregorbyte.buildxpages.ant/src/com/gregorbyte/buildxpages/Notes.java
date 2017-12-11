package com.gregorbyte.buildxpages;

import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;

public enum Notes {

	INSTANCE;
	
	private boolean initialised = false;
	private ThreadLocal<Boolean> threadInitialised = new ThreadLocal<Boolean>();

	private synchronized void init() {
		
		if (!initialised) {
			
			NotesNativeLibrary notes = NotesNativeLibrary.SYNC_INSTANCE;
			Native.setProtected(true);

			short error = notes.NotesInitExtended(0, null);
			checkError(error);
			this.initialised = true;
			
		}
	}
	
	public synchronized void term() {
		if (initialised) {
			NotesNativeLibrary notes = NotesNativeLibrary.SYNC_INSTANCE;
			notes.NotesTerm();
			initialised = false;
		}
	}
	
	public synchronized void initThread() {
		
		init();
		
		if (!isThreadInitialised()) {
			//System.out.println("Initialising thread");
			NotesNativeLibrary.SYNC_INSTANCE.NotesInitThread();
			getThreadInit().set(true);
		}
		
	}
	
	public synchronized void termThread() {
		
		if (isThreadInitialised()) {
			NotesNativeLibrary.SYNC_INSTANCE.NotesTermThread();
			getThreadInit().set(false);
		}
		
	}
	
	public synchronized boolean isInitialised() {
		return initialised;
	}

	public ThreadLocal<Boolean> getThreadInit() {
		return threadInitialised;
	}

	public void setThreadInit(ThreadLocal<Boolean> threadInit) {
		this.threadInitialised = threadInit;
	}
	
	public boolean isThreadInitialised() {
		if (getThreadInit().get() == null) return false;
		return getThreadInit().get();
	}
	

	
	public void checkError(short error) {

		if (error > 0) {
			printApiError(error);
			throw new RuntimeException("Something went Wrong");
		}
	}

	public void printApiError(short api_error) {

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
