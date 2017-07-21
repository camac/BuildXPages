package com.gregorbyte.designer.headless.jobs;

import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.ide.resources.extensions.DesignerException;
import com.ibm.designer.domino.ide.resources.extensions.NotesPlatform;
import com.ibm.designer.domino.ide.resources.ipc.DesignApp;
import com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject;
import com.ibm.designer.domino.ipc.exception.IPCException;
import com.ibm.designer.domino.napi.NotesAPIException;
import com.ibm.designer.domino.napi.util.NotesUtils;
import com.ibm.designer.domino.tools.userlessbuild.HeadlessLoggerAdapter;
import com.ibm.designer.domino.tools.userlessbuild.NSFPath;
import com.ibm.designer.domino.tools.userlessbuild.controller.CommandLineJobManager;
import com.ibm.designer.domino.ui.commons.extensions.DesignerResource;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.NotesFactory;
import lotus.domino.Session;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class CreateNSFJob extends Job {

	static final Logger logger = HeadlessLoggerAdapter.joblogger;

	final String nsfName;
	final String serverName;

	private boolean alreadyExists = false;

	IDominoDesignerProject _designerProject;

	public CreateNSFJob(String serverName, String nsfName) {

		super(StringUtil.format("creating NSF {0} [server={1}]", new Object[] { nsfName, serverName }));

		this.nsfName = nsfName;
		this.serverName = serverName;

	}

	public IDominoDesignerProject getNewProject() {
		return this._designerProject;
	}

	protected IStatus run(IProgressMonitor paramIProgressMonitor) {

		IStatus status = Status.OK_STATUS;

		try {

			if (StringUtil.isEmpty(this.nsfName)) {
				String msg = StringUtil.format("No name specified for new NSF [server:(1)]",
						new Object[] { this.nsfName, this.serverName });
				return new Status(4, "com.ibm.designer.domino", CommandLineJobManager.STATUSCODE_ERROR, msg, null);
			}

			status = createNSFIfNotExists();

			if (!status.isOK()) {
				return status;
			}

			String nsf = this.nsfName;

			int i = nsf.lastIndexOf("!!");
			String server = this.serverName;

			if (i != -1) {
				server = NotesUtils.DNAbbreviate(nsf.substring(0, i));
				nsf = nsf.substring(i + 2);
			}
			if ((server == null) || (server.length() == 0)) {
				server = "Local";
			}

			this._designerProject = ((IDominoDesignerProject) DesignerResource.openDesignerProject(server, nsf));

			// CommandLineJobManager.instance().projectCreated(this._designerProject.getProject());
			// CommandLineJobManager.instance().nsfCreated(new
			// NSFPath(this.serverName, this.nsfName,
			// this._designerProject.getProject()));
			if (logger.isLoggable(Level.FINEST)) {
				logger.finest("CreateNSFJob project created: " + nsf + ":" + server);
			}

		} catch (DesignerException localDesignerException) {
			logger.log(Level.SEVERE, "Designer error project creation for " + this.nsfName, localDesignerException);
		} catch (NotesAPIException localNotesAPIException) {
			logger.log(Level.SEVERE, "Notes API error project creation for " + this.nsfName, localNotesAPIException);
		}
		System.out.println(status);

		return status;
	}

	private IStatus createNSFIfNotExists() {
		IStatus status = Status.OK_STATUS;
		try {
			status = checkIfExists();
			if ((status.isOK()) && (!this.alreadyExists)) {
				status = createNewDatabase();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	private IStatus checkIfExists() {
		
		IStatus status = Status.OK_STATUS;

		final Status[] arrayOfStatus = new Status[1];

		Runnable runnable = new Runnable() {
			
			@SuppressWarnings("unused")
			public void run() {
				
				Session session = null;
				Database database = null;
				
				try {
					
					session = NotesFactory.createSession();

					database = session.getDatabase(CreateNSFJob.this.serverName, CreateNSFJob.this.nsfName, false);
					if (database != null) {
						CreateNSFJob.this.alreadyExists = true;
					}
					
				} catch (NotesException e) {

					String msg = StringUtil.format("NSF creation failed for {0} [server:{1}]",
							new Object[] { CreateNSFJob.this.nsfName, CreateNSFJob.this.serverName });
					
					arrayOfStatus[0] = new Status(4, "com.ibm.designer.domino", CommandLineJobManager.STATUSCODE_ERROR,
							msg, e);
					if (database != null) {
						try {
							database.recycle();
						} catch (Exception localException1) {
						}
					}
					if (session != null) {
						try {
							session.recycle();
						} catch (Exception localException2) {
						}
					}
				} finally {
					if (database != null) {
						try {
							database.recycle();
						} catch (Exception localException3) {
						}
					}
					if (session != null) {
						try {
							session.recycle();
						} catch (Exception localException4) {
						}
					}
				}
			}
		};
		
		try {
			NotesPlatform.getInstance().syncExec(runnable);
		} catch (Throwable localThrowable) {
			String str = StringUtil.format("NSF existance check failed for {0} [server:{1}]",
					new Object[] { this.nsfName, this.serverName });
			arrayOfStatus[0] = new Status(4, "com.ibm.designer.domino", CommandLineJobManager.STATUSCODE_ERROR, str,
					localThrowable);
		}
		if ((status.isOK()) && (arrayOfStatus[0] != null)) {
			status = arrayOfStatus[0];
		}
		return status;
	}

	private IStatus createNewDatabase() {
		IStatus status = Status.OK_STATUS;
		try {
			String server = this.serverName;
			if (StringUtil.equalsIgnoreCase(server, "Local")) {
				server = "";
			}
			String projectName = DesignApp.newApplication(server, this.nsfName);

			String msg = StringUtil.format("sucessfully created project {2} for NSF {0} [server:{1}]",
					new Object[] { this.nsfName, this.serverName, projectName });
			status = new Status(0, "com.ibm.designer.domino", CommandLineJobManager.STATUSCODE_ERROR, msg, null);
		} catch (IPCException localIPCException) {
			String str2 = StringUtil.format("NSF creation failed for {0} [server:{1}]",
					new Object[] { this.nsfName, this.serverName });
			status = new Status(4, "com.ibm.designer.domino", CommandLineJobManager.STATUSCODE_ERROR, str2,
					localIPCException);
		}
		return status;
	}
}
