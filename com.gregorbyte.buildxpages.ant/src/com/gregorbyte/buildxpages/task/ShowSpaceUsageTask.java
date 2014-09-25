package com.gregorbyte.buildxpages.task;

import com.gregorbyte.buildxpages.NotesNativeLibrary;
import com.sun.jna.ptr.IntByReference;

public class ShowSpaceUsageTask extends AbstractBxTask {

	private String dbpath;
	
	public ShowSpaceUsageTask(String dbpath) {
		this.dbpath = dbpath;
	}
	
	
	
	@Override
	protected void doTask() {

		IntByReference dbHandle = new IntByReference();
		IntByReference retAllocatedBytes = new IntByReference();
		IntByReference retFreeBytes = new IntByReference();
		boolean dbOpen = false;
		short errorint = 0;
		
		NotesNativeLibrary notes = NotesNativeLibrary.SYNC_INSTANCE;
				
		try {
			
			errorint = notes.NSFDbOpen(this.dbpath, dbHandle);
			checkError(errorint);
			dbOpen = true;
		
			errorint = notes.NSFDbSpaceUsage(dbHandle.getValue(),
					retAllocatedBytes, retFreeBytes);
			checkError(errorint);
			
			errorint = notes.NSFDbClose(dbHandle.getValue());	
			checkError(errorint);
			dbOpen = false;
			
			float percentused = 100 * (float) retAllocatedBytes.getValue()
					/ retFreeBytes.getValue() + retAllocatedBytes.getValue();
				
			System.out.println(Float.toString(percentused));
			
		} finally {
			
			if (dbOpen) {
				errorint = notes.NSFDbClose(dbHandle.getValue());	
				checkError(errorint);				
			}
			
		} 
		
	}
	
}
