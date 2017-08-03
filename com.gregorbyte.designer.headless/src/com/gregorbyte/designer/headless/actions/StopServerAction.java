package com.gregorbyte.designer.headless.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.gregorbyte.designer.headless.HeadlessServerActivator;

public class StopServerAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;

	public StopServerAction() {
		super();

	}

	@Override
	public void run(IAction action) {

		String msg = "";

		if (HeadlessServerActivator.INSTANCE.isServerRunning()) {
			HeadlessServerActivator.INSTANCE.stopServer();
			msg = "Headless Server Stopped";
		} else {
			msg = "Headless Server is not running, no action taken.";
		}

		MessageDialog.openConfirm(window.getShell(), "Headless Server", msg);

	}

	@Override
	public void selectionChanged(IAction arg0, ISelection arg1) {
		if (HeadlessServerActivator.INSTANCE != null) {

			if (HeadlessServerActivator.INSTANCE.isServerRunning()) {
				arg0.setEnabled(true);
			} else {
				arg0.setEnabled(false);
			}
		}
	}

	@Override
	public void dispose() {

	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

}
