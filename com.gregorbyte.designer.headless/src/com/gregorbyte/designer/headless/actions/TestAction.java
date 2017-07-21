package com.gregorbyte.designer.headless.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.gregorbyte.designer.headless.jobs.RefreshImportBuildJob;

public class TestAction implements IWorkbenchWindowActionDelegate {

	@Override
	public void run(IAction action) {

		String nsfname = "staging\\Slicetime.nsf";
		String testodp = "D:\\workspaces\\testworkpace\\WahWah\\.project";
		String odpname = "SliceTime-staging";

		RefreshImportBuildJob job = new RefreshImportBuildJob();

		job.setNsfServer("CameronWS");
		job.setNsfName(nsfname);
		job.setOnDiskProjectFile(testodp);
		job.setOnDiskProjectName(odpname);
		
		job.schedule();


	}

	@Override
	public void selectionChanged(IAction arg0, ISelection arg1) {

	}

	@Override
	public void dispose() {

	}

	@Override
	public void init(IWorkbenchWindow window) {
	}

}
