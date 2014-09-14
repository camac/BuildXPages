package com.gregorbyte.designer.headless;

import com.ibm.designer.domino.tools.userlessbuild.jobs.ImportAndBuildJob;


public class TestingOnly {

	public TestingOnly() {

	}

	public void wouldThisWork() {

		String onDiskPath = "V:\\DominoGit\\Headless\\com.gregorbyte.headless.nsf\\.project";
		String nsfName = "testhead25.nsf";
		
		ImportAndBuildJob myJob = new ImportAndBuildJob(onDiskPath, nsfName);

	}
	
	public void whataboutthis() {
		
		
	}

}
