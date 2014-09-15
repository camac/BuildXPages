package com.gregorbyte.designer.headless.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.gregorbyte.designer.headless.HeadlessServerActivator;

public class StartServerAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;
	
	@Override
	public void run(IAction action) {

		String msg = "";
		
		if ( HeadlessServerActivator.INSTANCE.isServerRunning() ) {
			
			msg = "Server is already running. No Action";
			
		} else {
			
			HeadlessServerActivator.INSTANCE.startServer();			
			msg = "Server Started";			
			
		}
		
		MessageDialog.openInformation(window.getShell(), "Headless Server", msg);
			
	}

	@Override
	public void selectionChanged(IAction arg0, ISelection arg1) {

	}

	@Override
	public void dispose() {

	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

}
