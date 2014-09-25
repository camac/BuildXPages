package com.gregorbyte.buildxpages.task;

import com.gregorbyte.buildxpages.NotesNativeLibrary;
import com.sun.jna.ptr.IntByReference;

public class CreateAndCopyDatabase extends AbstractBxTask {

	private String srcServer;
	private String srcFilename;

	private String dstServer;
	private String dstFilename;

	public CreateAndCopyDatabase() {

	}

	public CreateAndCopyDatabase(String srcServer, String srcFilename,
			String dstServer, String dstFilename) {

		this.srcServer = srcServer;
		this.srcFilename = srcFilename;

		this.dstServer = dstServer;
		this.dstFilename = dstFilename;

	}

	public String getSrcServer() {
		return srcServer;
	}

	public void setSrcServer(String srcServer) {
		this.srcServer = srcServer;
	}

	public String getSrcFilename() {
		return srcFilename;
	}

	public void setSrcFilename(String srcFilename) {
		this.srcFilename = srcFilename;
	}

	public String getDstServer() {
		return dstServer;
	}

	public void setDstServer(String dstServer) {
		this.dstServer = dstServer;
	}

	public String getDstFilename() {
		return dstFilename;
	}

	public void setDstFilename(String dstFilename) {
		this.dstFilename = dstFilename;
	}

	@Override
	protected void doTask() {

		NotesNativeLibrary notes = NotesNativeLibrary.SYNC_INSTANCE;
		
		String srcPath = pathNetConstruct(srcServer, srcFilename);
		String dstPath = pathNetConstruct(dstServer, dstFilename);

		System.out.println(srcPath);
		System.out.println(dstPath);

		IntByReference retHandle = new IntByReference();
		short zero = 0;

		short error = notes.NSFDbCreateAndCopy(srcPath, dstPath,
				(short) NOTE_CLASS_ALLNONDATA, zero, 0, retHandle);
		checkError(error);

		error = notes.NSFDbClose(retHandle.getValue());
		checkError(error);
		
	}
}
