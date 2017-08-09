package com.gregorbyte.designer.headless.socket;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.gregorbyte.designer.headless.HeadlessServerActivator;
import com.gregorbyte.designer.headless.jobs.RefreshImportBuildJob;
import com.gregorbyte.designer.headless.preferences.PreferenceConstants;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject;
import com.ibm.designer.domino.team.util.SyncUtil;
import com.ibm.designer.domino.tools.userlessbuild.ProjectUtilities;
import com.ibm.designer.domino.tools.userlessbuild.jobs.CleanUpJob;
import com.ibm.designer.domino.tools.userlessbuild.jobs.CloseDesignerJob;
import com.ibm.designer.domino.tools.userlessbuild.jobs.DeleteProjectJob;
import com.ibm.designer.domino.tools.userlessbuild.jobs.ImportAndBuildJob;

public class HeadlessServerRunnable implements Runnable {

	private static final String MSG_PROBLEMSSTATUS = "PROBLEMS STATUS: ";

	private static final String MSG_SUCCESS = "SUCCESS";
	private static final String MSG_FAIL = "FAIL";

	private static final String MSG_TERM = "END.";

	private static final String CMD_SHUTDOWN = "shutdown";
	private static final String CMD_REPORTMARKERS = "markersreport";
	private static final String CMD_REFRESHIMPORTBUILD = "refreshimportbuild";
	private static final String CMD_IMPORTANDBUILD = "importandbuild";

	private boolean threadShouldStop = false;
	private ServerSocket socket = null;

	public void stopThread() {
		threadShouldStop = true;
	}

	private RefreshImportBuildJob refreshImportBuild(String onDiskProjectFile, String projectName, String nsfName,
			String server, PrintWriter out) throws InterruptedException {

		RefreshImportBuildJob job = new RefreshImportBuildJob();
		
		job.setWriter(out);

		job.setOnDiskProjectFile(onDiskProjectFile);
		job.setOnDiskProjectName(projectName);
		job.setNsfName(nsfName);
		job.setNsfServer(server);

		BuildJobChangeAdapter listener = new BuildJobChangeAdapter(out);

		job.addJobChangeListener(listener);
		job.schedule();
		job.join();

		return job;
	}

	private void closeDesigner(PrintWriter out) throws InterruptedException {

		CloseDesignerJob job = new CloseDesignerJob();
		CloseDesignerJobChangeAdapter listener = new CloseDesignerJobChangeAdapter(out);
		job.addJobChangeListener(listener);
		
		out.write("Scheduling Close Designer Job");
		
		job.schedule();
		

	}

	@SuppressWarnings("unused")
	private void importAndBuild(String onDiskPath, String nsfName, PrintWriter out) throws InterruptedException {

		ImportAndBuildJob myJob = new ImportAndBuildJob(onDiskPath, nsfName);

		BuildJobChangeAdapter listener = new BuildJobChangeAdapter(out);

		myJob.addJobChangeListener(listener);
		myJob.schedule();
		myJob.join();

	}

	public IProject getAssociatedProject(String onDiskProjectFile) {

		if (StringUtil.isEmpty(onDiskProjectFile)) {
			return null;
		}

		File file = new File(onDiskProjectFile);

		if (!file.exists()) {
			return null;
		}

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IPath path = new Path(file.getPath());

		try {

			IProjectDescription pd = workspace.loadProjectDescription(path);

			String onDiskProjectName = pd.getName();

			IProject proj = ProjectUtilities.getProject(onDiskProjectName);

			IDominoDesignerProject dproj = SyncUtil.getAssociatedNsfProject(proj);

			if (dproj == null) {
				return null;
			}

			return dproj.getProject();

		} catch (CoreException e) {
			e.printStackTrace();
			return null;
		}

	}

	private void reportMarkers(IProject project, PrintWriter out) {

		if (!project.isOpen()) {
			out.println(MSG_PROBLEMSSTATUS + MSG_FAIL);
			out.println("Project was not open");
			return;
		}

		try {

			IMarker[] problems = null;
			problems = project.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);

			boolean haserrors = false;

			for (IMarker marker : problems) {

				int severity = marker.getAttribute(IMarker.SEVERITY, 0);

				if (severity >= 2) {
					haserrors = true;
				}
			}

			// Report status
			if (haserrors) {
				out.println(MSG_PROBLEMSSTATUS + MSG_FAIL);
			} else {
				out.println(MSG_PROBLEMSSTATUS + MSG_SUCCESS);
			}

			// Output the Errors
			if (problems.length == 0) {
				out.println("No ProblemMarkers found after Building");
			} else {

				out.println("The following ProblemMarkers were present after building");

				for (IMarker marker : problems) {

					int severity = marker.getAttribute(IMarker.SEVERITY, 0);

					if (severity >= 2) {
						out.print("Error: ");
					} else if (severity == 1) {
						out.print("Warning: ");
					} else {
						out.print("Info: ");
					}

					// Need to check if the marker actually exists
					if (marker.exists()) {
						out.print(marker.getResource().getName() + ": ");
						String msg = (String) marker.getAttribute("message");
						out.println(msg);
					}
				}

			}

		} catch (CoreException e) {
			System.out.println("Something went wrong getting markers");
		} catch (Exception e) {
			e.printStackTrace(out);
		}

	}

	@SuppressWarnings("unused")
	private void cleanup(PrintWriter out) {

		CleanUpJob job = new CleanUpJob();

		job.addJobChangeListener(new DeleteProjectJobChangeAdapter(out));
		job.schedule();

		try {
			job.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unused")
	private void deleteProject(String projName, PrintWriter out) {

		IProject project = ProjectUtilities.getProject(projName);

		DeleteProjectJob job = new DeleteProjectJob(project);

		job.addJobChangeListener(new DeleteProjectJobChangeAdapter(out));
		job.schedule();

		try {
			job.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {

		try {

			String portNumber = HeadlessServerActivator.getDefault().getPreferenceStore()
					.getString(PreferenceConstants.P_PORT);
			Integer port = null;
			try {
				port = Integer.parseInt(portNumber);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return;
			}

			socket = new ServerSocket(port, 0, InetAddress.getLocalHost());

			socket.setSoTimeout(500);

			while (!threadShouldStop) {

				try {

					// Wait for a connection
					Socket connSocket = socket.accept();

					try {

						BufferedReader in = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));
						PrintWriter out = new PrintWriter(connSocket.getOutputStream(), true);

						out.println("CONNECTED TO DESIGNER! What can we do for you?");
						out.println(MSG_TERM);

						boolean argsvalid = true;
						String command = in.readLine();

						List<String> args = new ArrayList<String>();
						String line = in.readLine();
						while (!StringUtil.equalsIgnoreCase(MSG_TERM, line)) {
							args.add(line);
							line = in.readLine();
						}

						if (StringUtil.equalsIgnoreCase(command, CMD_SHUTDOWN)) {
							closeDesigner(out);
						} else if (StringUtil.equalsIgnoreCase(command, CMD_REFRESHIMPORTBUILD)) {

							if (args.size() < 4) {
								out.println("Wrong number of arguments");
							} else {

								String onDiskPath = args.get(0);
								String onDiskProjectName = args.get(1);
								String nsfPath = args.get(2);
								String server = args.get(3);

								if (StringUtil.equalsIgnoreCase("null", onDiskPath)) {
									onDiskPath = null;
								}
								if (StringUtil.equalsIgnoreCase("null", onDiskProjectName)) {
									onDiskProjectName = null;
								}
								if (StringUtil.equalsIgnoreCase("null", nsfPath)) {
									nsfPath = null;
								}
								if (StringUtil.equalsIgnoreCase("null", server)) {
									server = null;
								}

								File file = new File(onDiskPath);

								if (!file.exists()) {
									out.println("The supplied on disk Path could not be found: ");
									out.println("    ODP: " + onDiskPath);
									argsvalid = false;
								}

								if (!argsvalid) {
									out.println(MSG_TERM);
									break;
								}

								RefreshImportBuildJob job = refreshImportBuild(onDiskPath, onDiskProjectName, nsfPath,
										server, out);

								// Report on the Markers
								IProject proj = job.getDesProject().getProject();
								reportMarkers(proj, out);
							}

						} else if (StringUtil.equalsIgnoreCase(command, CMD_REPORTMARKERS)) {

							if (args.size() < 1) {
								out.println("Wrong number of arguments");
							} else {

								String onDiskPath = args.get(0);

								IProject proj = getAssociatedProject(onDiskPath);
								reportMarkers(proj, out);

							}

						} else {
							out.println("Unknown Command '" + command + "'");
						}

						out.println(MSG_TERM);

					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						System.out.println("Closing Connection Socket");
						connSocket.close();
						System.out.println("Closed Connection Socket");
					}

				} catch (InterruptedIOException e) {

				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			socket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
