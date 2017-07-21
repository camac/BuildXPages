package com.gregorbyte.designer.headless.jobs;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
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

	private String nsfServer;
	private String nsfName;

	private String onDiskProjectFile;
	private String onDiskProjectName;

	private IProject diskProject;
	private IDominoDesignerProject desProject;

	public RefreshImportBuildJob() {

		super("Refresh Import and Build Job");
	}

	public String getNsfName() {
		return nsfName;
	}

	public void setNsfName(String nsfName) {
		this.nsfName = nsfName;
	}

	public String getNsfServer() {
		return nsfServer;
	}

	public void setNsfServer(String nsfServer) {
		this.nsfServer = nsfServer;
	}

	public String getOnDiskProjectFile() {
		return onDiskProjectFile;
	}

	public void setOnDiskProjectFile(String onDiskProjectFile) {
		this.onDiskProjectFile = onDiskProjectFile;
	}

	public String getOnDiskProjectName() {
		return onDiskProjectName;
	}

	public void setOnDiskProjectName(String onDiskProjectName) {
		this.onDiskProjectName = onDiskProjectName;
	}

	public IProject getDiskProject() {
		return diskProject;
	}

	public void setDiskProject(IProject diskProject) {
		this.diskProject = diskProject;
	}

	public IDominoDesignerProject getDesProject() {
		return desProject;
	}

	public void setDesProject(IDominoDesignerProject desProject) {
		this.desProject = desProject;
	}

	public static RefreshImportBuildJob createFromOdpProjectName(String odpProjectName) {

		RefreshImportBuildJob job = new RefreshImportBuildJob();
		job.onDiskProjectName = odpProjectName;
		return job;

	}

	public static RefreshImportBuildJob createFromOdpProjectFile(String onDiskProjectFile) {

		RefreshImportBuildJob job = new RefreshImportBuildJob();
		job.onDiskProjectFile = onDiskProjectFile;
		return job;

	}

	public static RefreshImportBuildJob createFromBoth(String odpFilePath, String nsfpath) {

		RefreshImportBuildJob job = new RefreshImportBuildJob();
		job.onDiskProjectFile = odpFilePath;
		job.nsfName = nsfpath;
		return job;

	}

	private void findOnDiskProject() {

		File odpProjectFile = new File(this.onDiskProjectFile);
		if (!odpProjectFile.exists()) {
			return;
		}

		IPath diskProjectPath = new Path(odpProjectFile.getPath());
		IPath diskFolderPath = diskProjectPath.removeLastSegments(1);

		IWorkspace localIWorkspace = ResourcesPlugin.getWorkspace();

		for (IProject p : localIWorkspace.getRoot().getProjects()) {

			IPath rawpath = p.getLocation();
			IPath odpfolder = diskFolderPath;

			if (rawpath.equals(odpfolder)) {
				diskProject = p;
				break;
			}

		}

	}

	private void findDesignerProject() throws CoreException {

		if (StringUtil.isEmpty(nsfName) && diskProject != null) {
			desProject = SyncUtil.getAssociatedNsfProject(diskProject);
		} else {
			desProject = ProjectUtilities.getDesignerProject(nsfName);
		}
	}

	private void checkAssociation() throws CoreException {

		if (desProject != null && diskProject != null) {

			IDominoDesignerProject assoc = SyncUtil.getAssociatedNsfProject(diskProject);

			if (assoc != null && !assoc.equals(desProject)) {
				throw new IllegalStateException("Disk Project is associated with a different NSF: " + assoc.getDatabaseName());
			}

			IProject assocDisk = SyncUtil.getAssociatedDiskProject(desProject, false);

			if (assocDisk != null && !assocDisk.equals(diskProject)) {
				throw new IllegalStateException("NSF Project is associated with a different DISK project");
			}

			if (assoc == null && assocDisk == null) {

				System.out.println("Creating Association");
				SyncUtil.createAssociation(desProject, diskProject);

			}

		}

	}

	protected void checkSetup(IProgressMonitor monitor) {

		if (StringUtil.isNotEmpty(nsfName) && StringUtil.isNotEmpty(onDiskProjectFile)) {

			// Check if ODProject exists

			// Check if NSF exists

			// Check if they are synced

		} else if (StringUtil.isNotEmpty(onDiskProjectFile)) {

		} else if (StringUtil.isNotEmpty(nsfName)) {

		}

	}

	private Status error(String msg) {
		return new Status(Status.ERROR, "com.gregorbyte.designer.headless", msg);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		checkSetup(monitor);

		findOnDiskProject();

		if (diskProject == null) {
			ImportOnDiskProjectJob job = new ImportOnDiskProjectJob(onDiskProjectFile, onDiskProjectName);
			job.run(monitor);
			diskProject = job.getDiskProject();
		}

		CreateNSFJob create = new CreateNSFJob(nsfServer, nsfName);
		create.run(monitor);
		desProject = create.getNewProject();

		if (diskProject == null || !diskProject.exists()) {
			return error("Could not find Disk Project");
		} else if (desProject == null) {
			return error("Could not find Designer Project");
		}

		try {
			checkAssociation();
		} catch (Exception e) {
			return error(e.getMessage());
		}

		try {

			if (!diskProject.isOpen()) {
				diskProject.open(monitor);
				System.out.println("Opening project");
			}

			boolean autoBuild = NsfUtil.isAutoBuilding();

			if (autoBuild) {
				NsfUtil.setAutoBuilding(false);
			}

			diskProject.refreshLocal(IResource.DEPTH_INFINITE, monitor);

			// Why is it not deleting java?
			diskProject.build(IncrementalProjectBuilder.CLEAN_BUILD, monitor);

			diskProject.build(IncrementalProjectBuilder.FULL_BUILD, monitor);

			NoUISyncAction syncaction = new NoUISyncAction();
			syncaction.setSyncProjects(desProject, diskProject);
			syncaction.doExecute();

			IProject desProj = desProject.getProject();

			if (!desProj.isOpen()) {
				desProj.open(monitor);
			}

			desProj.build(IncrementalProjectBuilder.FULL_BUILD, monitor);

			System.out.println("About to Finish");

		} catch (CoreException e) {
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
						// AssociateOnDiskWithNSFJob.joblogger.fine(StringUtil.format("--
						// executing {0} syncOps",
						// new Object[] { Integer.valueOf(localList.size()) }));
						RefreshImportBuildJob.NoUISyncAction.this.executeSyncOps(syncOps, monitor, true);
					}
					RefreshImportBuildJob.NoUISyncAction.this.performPostImportProcessing(monitor);

					Iterator<ConflictSyncOperation> it = RefreshImportBuildJob.NoUISyncAction.this.conflictOps
							.iterator();
					if (it.hasNext()) {
						ConflictSyncOperation conflictOp = it.next();
						conflictOp.setSyncDirection(1);
					}
					RefreshImportBuildJob.NoUISyncAction.this.doHandleConflictOps(monitor);

					// TODO set project name back
					Object localObject = new StateMonitor.Key("sync", "DoraHeadless");// RefreshImportBuildJob.this.nsfProjectName);

					int i = 1;
					if (RefreshImportBuildJob.NoUISyncAction.this.deferOps.size() > 0) {
						i = 0;
						// AssociateOnDiskWithNSFJob.joblogger.fine("NoUISyncAction
						// has postProcessing Jobs");
					}
					if (i != 0) {
						CommandLineJobManager.instance().arm((StateMonitor.Key) localObject);
					}
					RefreshImportBuildJob.NoUISyncAction.this.scheduleTimestampUpdateJobForImports(
							RefreshImportBuildJob.NoUISyncAction.this.importSyncs);
					if (i == 0) {
						CommandLineJobManager.instance().arm((StateMonitor.Key) localObject);
					}
					RefreshImportBuildJob.NoUISyncAction.this.postProcessing(monitor);
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
