package com.gregorbyte.designer.headless;

import org.eclipse.ui.IStartup;

public class HeadlessStartup implements IStartup {

	@Override
	public void earlyStartup() {

		System.out.println("Start me up Headless");
		
	}

}
