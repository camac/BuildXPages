package com.gregorbyte.designer.headless.jobs;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject;
import com.ibm.designer.domino.ide.resources.util.NsfUtil;
import com.ibm.designer.domino.team.action.SyncAction;
import com.ibm.designer.domino.team.builder.ConflictSyncOperation;
import com.ibm.designer.domino.team.builder.ISyncOperation;
import com.ibm.designer.domino.team.util.SyncUtil;
import com.ibm.designer.domino.tools.userlessbuild.ProjectUtilities;
import com.ibm.designer.domino.tools.userlessbuild.controller.CommandLineJobManager;
import com.ibm.designer.domino.tools.userlessbuild.controller.StateMonitor;

public class RefreshImportBuildJob extends Job {

	private String onDiskProjectFile;
	private String onDiskProjectName;

	public RefreshImportBuildJob() {

		super("Refresh Import and Build Job");
	}

	public static RefreshImportBuildJob createFromOdpProjectName(
			String odpProjectName) {

		RefreshImportBuildJob job = new RefreshImportBuildJob();
		job.onDiskProjectName = odpProjectName;
		return job;

	}

	public static RefreshImportBuildJob createFromOdpProjectFile(
			String onDiskProjectFile) {

		RefreshImportBuildJob job = new RefreshImportBuildJob();
		job.onDiskProjectFile = onDiskProjectFile;
		return job;

	}

	public void findProjectName() {

	}

	private void findOnDiskProjectName() {

		if (StringUtil.isNotEmpty(onDiskProjectName)) {
			return;
		}

		if (StringUtil.isEmpty(onDiskProjectFile)) {
			return;
		}

		File file = new File(onDiskProjectFile);

		if (!file.exists()) {
			return;
		}

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IPath path = new Path(file.getPath());

		IProjectDescription pd;
		try {
			pd = workspace.loadProjectDescription(path);
		} catch (CoreException e) {
			e.printStackTrace();
			return;
		}

		this.onDiskProjectName = pd.getName();

	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		findOnDiskProjectName();

		if (StringUtil.isEmpty(onDiskProjectName)) {
			return Status.OK_STATUS;
		}

		try {

			IProject proj = ProjectUtilities.getProject(this.onDiskProjectName);

			if (!proj.exists()) {

				// TODO how to report error
				System.out.println("Project Not Existy");
				return Status.OK_STATUS;
			}

			if (!proj.isOpen()) {
				proj.open(monitor);
				System.out.println("Opening project");
			}

			boolean autoBuild = NsfUtil.isAutoBuilding();

			if (autoBuild) {
				NsfUtil.setAutoBuilding(false);
			}

			proj.refreshLocal(IResource.DEPTH_INFINITE, monitor);

			// Why is it not deleting java?
			proj.build(IncrementalProjectBuilder.CLEAN_BUILD, monitor);
			
			proj.build(IncrementalProjectBuilder.FULL_BUILD, monitor);

			IDominoDesignerProject dproj = SyncUtil
					.getAssociatedNsfProject(proj);

			NoUISyncAction syncaction = new NoUISyncAction();
			syncaction.setSyncProjects(dproj, proj);
			syncaction.doExecute();

			IProject desProj = dproj.getProject();

			if (!desProj.isOpen()) {
				desProj.open(monitor);
			}

			desProj.build(IncrementalProjectBuilder.FULL_BUILD, monitor);

			System.out.println("About to Finish");

		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Status.OK_STATUS;
	}

	class NoUISyncAction extends SyncAction {
		NoUISyncAction() {
			super();
		}

		protected void performSync(IProgressMonitor paramIProgressMonitor) {
			IWorkspaceRunnable local1 = new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					List<ISyncOperation> syncOps = RefreshImportBuildJob.NoUISyncAction.this
							.analyzeSyncOperations(monitor);
					if (syncOps.size() > 0) {
						// AssociateOnDiskWithNSFJob.joblogger.fine(StringUtil.format("-- executing {0} syncOps",
						// new Object[] { Integer.valueOf(localList.size()) }));
						RefreshImportBuildJob.NoUISyncAction.this
								.executeSyncOps(syncOps, monitor, true);
					}
					RefreshImportBuildJob.NoUISyncAction.this
							.performPostImportProcessing(monitor);

					Iterator<ConflictSyncOperation> it = RefreshImportBuildJob.NoUISyncAction.this.conflictOps
							.iterator();
					if (it.hasNext()) {
						ConflictSyncOperation conflictOp = it.next();
						conflictOp.setSyncDirection(1);
					}
					RefreshImportBuildJob.NoUISyncAction.this
							.doHandleConflictOps(monitor);

					// TODO set project name back
					Object localObject = new StateMonitor.Key("sync",
							"DoraHeadless");// RefreshImportBuildJob.this.nsfProjectName);

					int i = 1;
					if (RefreshImportBuildJob.NoUISyncAction.this.deferOps
							.size() > 0) {
						i = 0;
						// AssociateOnDiskWithNSFJob.joblogger.fine("NoUISyncAction has postProcessing Jobs");
					}
					if (i != 0) {
						CommandLineJobManager.instance().arm(
								(StateMonitor.Key) localObject);
					}
					RefreshImportBuildJob.NoUISyncAction.this
							.scheduleTimestampUpdateJobForImports(RefreshImportBuildJob.NoUISyncAction.this.importSyncs);
					if (i == 0) {
						CommandLineJobManager.instance().arm(
								(StateMonitor.Key) localObject);
					}
					RefreshImportBuildJob.NoUISyncAction.this
							.postProcessing(monitor);
				}
			};
			try {
				ResourcesPlugin.getWorkspace().run(local1, null);
			} catch (Exception localException) {
				localException.printStackTrace();
			}
		}
	}

}
