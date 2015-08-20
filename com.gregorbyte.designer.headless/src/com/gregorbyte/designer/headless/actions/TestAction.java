package com.gregorbyte.designer.headless.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.gregorbyte.designer.headless.jobs.RefreshImportBuildJob;

public class TestAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;

	@Override
	public void run(IAction action) {

		RefreshImportBuildJob job = RefreshImportBuildJob.createFromOdpProjectName("DoraHeadless");
		
		job.schedule();
		
		System.out.println("Finishing Action");
		return;

	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}

	@Override
	public void dispose() {

	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;

	}

	

}
