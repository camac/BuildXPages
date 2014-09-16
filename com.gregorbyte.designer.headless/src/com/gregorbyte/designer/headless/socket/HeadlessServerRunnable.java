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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import com.gregorbyte.designer.headless.HeadlessServerActivator;
import com.gregorbyte.designer.headless.preferences.PreferenceConstants;
import com.ibm.designer.domino.tools.userlessbuild.ProjectUtilities;
import com.ibm.designer.domino.tools.userlessbuild.jobs.DeleteProjectJob;
import com.ibm.designer.domino.tools.userlessbuild.jobs.ImportAndBuildJob;

public class HeadlessServerRunnable implements Runnable {

	private static final String MSG_PROBLEMSSTATUS = "PROBLEMS STATUS: ";

	private static final String MSG_SUCCESS = "SUCCESS";
	private static final String MSG_FAIL = "FAIL";

	private static final String MSG_TERM = "END.";

	private boolean threadShouldStop = false;
	private ServerSocket socket = null;

	public void stopThread() {
		threadShouldStop = true;
	}

	private void importAndBuild(String onDiskPath, String nsfName, PrintWriter out) throws InterruptedException {
	
		ImportAndBuildJob myJob = new ImportAndBuildJob(
				onDiskPath, nsfName);

		BuildJobChangeAdapter listener = new BuildJobChangeAdapter(
				out);

		myJob.addJobChangeListener(listener);
		myJob.schedule();
		myJob.join();
		
	}
	
	private void reportMarkers(String projName, PrintWriter out) {

		IProject project = ProjectUtilities.getProject(projName);

		if (!project.isOpen()) {
			out.println(MSG_PROBLEMSSTATUS + MSG_FAIL);
			out.println("Project was not open");
			return;
		}

		try {

			IMarker[] problems = null;
			problems = project.findMarkers(IMarker.PROBLEM, true,
					IResource.DEPTH_INFINITE);

			if (problems.length == 0) {
				out.println(MSG_PROBLEMSSTATUS + MSG_SUCCESS);
				out.println("No Problems found after Building");
			} else {

				out.println(MSG_PROBLEMSSTATUS + MSG_FAIL);
				out.println("The following problems were present after building");

				for (IMarker marker : problems) {

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

	private void deleteProject(String projName, PrintWriter out) {

		IProject project = ProjectUtilities.getProject(projName);

		DeleteProjectJob job = new DeleteProjectJob(project);
				
		job.addJobChangeListener(new DeleteProjectJobChangeAdapter(out));
		job.schedule();
		
		try {
			job.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	@Override
	public void run() {

		try {
			
			String portNumber = HeadlessServerActivator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_PORT);
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

						BufferedReader in = new BufferedReader(
								new InputStreamReader(
										connSocket.getInputStream()));
						PrintWriter out = new PrintWriter(
								connSocket.getOutputStream(), true);

						out.println("CONNECTED TO DESIGNER! What can we do for you?");
						out.println("Expected Protocol");
						out.println("Line 1: On Disk Project Path (Path to .project file)");
						out.println("Line 2: Name of NSF to build");
						out.println(MSG_TERM);

						boolean argsvalid = true;
						String onDiskPath 	= in.readLine();
						String nsfName 		= in.readLine();

						File file = new File(onDiskPath);
							
						if (!file.exists()) {
							out.println("The supplied on disk Path could not be found: ");
							out.println("    ODP: " + onDiskPath);
							argsvalid = false;
						}
													
						IProject existing = ProjectUtilities.getProject(nsfName);
						if (existing != null) {
							out.println("The Supplied NSF project name already exists in the workspace:");
							out.println("    NSF: " + nsfName);
							argsvalid = false;								
						}
						
						if (!argsvalid) {
							out.println(MSG_TERM);
							break;
						}

						// Run the Import and Build
						importAndBuild(onDiskPath, nsfName, out);

						// Report on the Markers
						reportMarkers(nsfName, out);

						// Delete the Project
						deleteProject(nsfName, out);

						out.println(MSG_TERM);

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						System.out.println("Closing Connection Socket");
						connSocket.close();
						System.out.println("Closed Connection Socket");
					}

				} catch (InterruptedIOException e) {
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			socket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
