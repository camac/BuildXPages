package com.gregorbyte.designer.headless.jobs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import org.eclipse.core.internal.resources.ModelObjectWriter;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.IOverwriteQuery;

import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject;
import com.ibm.designer.domino.team.util.SyncUtil;
import com.ibm.designer.domino.tools.userlessbuild.HeadlessLoggerAdapter;
import com.ibm.designer.domino.tools.userlessbuild.controller.CommandLineJobManager;
import com.ibm.designer.domino.tools.userlessbuild.jobs.DeleteProjectJob;

public class ImportOnDiskProjectJob extends Job {

	Logger logger = HeadlessLoggerAdapter.joblogger;

	final String projectfile;

	String projectName = null;

	private IProject diskProject = null;
	
	public ImportOnDiskProjectJob(String filepath, String projectName) {
		super(StringUtil.format("Importing project {0}", new Object[] { filepath }));
		this.projectfile = filepath;
		this.projectName = projectName;
	}

	public String getProjectName() {
		return this.projectName;
	}
	
	public IProject getDiskProject() {
		return this.diskProject;
	}

	protected IStatus run(IProgressMonitor monitor) {
		IStatus status = Status.OK_STATUS;
		try {
			status = importProject(monitor);
		} catch (Exception e) {
			String str = StringUtil.format("unable to import {0}", new Object[] { this.projectfile });
			status = new Status(4, "com.ibm.designer.domino", -1, str, e);
		}
		return status;
	}

	private void reportError(String error) {
		this.logger.severe(error);
	}

	private IStatus importProject(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		// Find the Project File on thh File System
		File odpProjectFile = new File(this.projectfile);

		if (!odpProjectFile.exists()) {
			String localObject1 = StringUtil.format("Project file {0} does not exist", new Object[] { this.projectfile });
			return new Status(4, "com.ibm.designer.domino", CommandLineJobManager.STATUSCODE_ERROR,
					(String) localObject1, null);
		}

		Path path = new Path(odpProjectFile.getPath());
		IPath odpfolder = path.removeLastSegments(1);

		try {

			// Load Project Description from File System
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IProjectDescription pd = workspace.loadProjectDescription((IPath) path);

			// If we have a desired project name, lets change it now
			if (projectName != null) {
				pd.setName(projectName);
			} else {
				projectName = pd.getName();
			}

			IProject existingProject = null;
			
			for (IProject p : workspace.getRoot().getProjects()) {

				IPath rawpath = p.getLocation();

				if (rawpath.equals(odpfolder)) {
					existingProject = p;
					break;
				}

			}

			if (existingProject != null && existingProject.exists()) {

				if (!existingProject.isOpen()) {
					existingProject.open(monitor);
				}

				return Status.OK_STATUS;

			}
			if (existingProject == null) {				
				existingProject = workspace.getRoot().getProject(projectName);
			}

			if (!existingProject.exists()) {
				monitor.beginTask("Create project", 100);
				existingProject.create(pd, new SubProgressMonitor(monitor, 30));
				existingProject.open(128, new SubProgressMonitor(monitor, 70));
				existingProject.refreshLocal(2, monitor);

				CommandLineJobManager.instance().projectCreated(existingProject);
			} else {
				throw new IllegalStateException("There is already a another Project named " + projectName);
			}

			if (!existingProject.isOpen()) {
				existingProject.open(monitor);
			}
			diskProject = existingProject;

		} catch (CoreException localCoreException) {
			reportError(localCoreException.toString());
			throw new InvocationTargetException(localCoreException);
		} finally {
			monitor.done();
		}
		monitor.done();

		String str = StringUtil.format("Project {0} already exists", new Object[] { this.projectfile });
		return new Status(0, "com.ibm.designer.domino", CommandLineJobManager.STATUSCODE_NOOP, str, null);
	}

	Shell getShell() {
		return null;
	}
	
	@SuppressWarnings("unused")
	private void removeExisting(IProject existingProject, IProgressMonitor monitor) {
		// Lets remove it
		try {
			IDominoDesignerProject desProject = SyncUtil.getAssociatedNsfProject(existingProject);

			if (desProject != null) {

				if (!desProject.getProject().isOpen()) {
					desProject.getProject().open(monitor);
				}

				SyncUtil.clearAssociation(desProject);
			}

			DeleteProjectJob djob = new DeleteProjectJob(existingProject);
			djob.schedule();
			djob.join();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
