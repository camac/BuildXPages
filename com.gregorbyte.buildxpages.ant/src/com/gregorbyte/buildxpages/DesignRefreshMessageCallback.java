package com.gregorbyte.buildxpages;

import com.sun.jna.win32.StdCallLibrary;

public interface DesignRefreshMessageCallback extends StdCallLibrary.StdCallCallback {

	void proc(StringByReference message, short mType);
	
}
