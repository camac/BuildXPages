package com.gregorbyte.designer.headless.socket;

import java.io.BufferedReader;
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

import com.ibm.designer.domino.tools.userlessbuild.ProjectUtilities;
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

	private void reportMarkers(String projName, PrintWriter out) {

		IProject project = ProjectUtilities.getProject(projName);

		if (!project.isOpen()) {
			out.println(MSG_PROBLEMSSTATUS + MSG_FAIL);
			out.println("Project was not open");
			out.println(MSG_TERM);
			return;
		}

		try {

			IMarker[] problems = null;
			problems = project.findMarkers(IMarker.PROBLEM, true,
					IResource.DEPTH_INFINITE);

			if (problems.length == 0) {
				out.println(MSG_PROBLEMSSTATUS + MSG_SUCCESS);
				out.println("No Problems found after Building");
				out.println(MSG_TERM);
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

				out.println(MSG_TERM);
			}

		} catch (CoreException e) {
			System.out.println("Something went wrong getting markers");
		} catch (Exception e) {
			e.printStackTrace(out);
		}

	}

	@Override
	public void run() {

		try {

			socket = new ServerSocket(8098, 0, InetAddress.getLocalHost());
			// socket = new ServerSocket(8098);
			socket.setSoTimeout(500);

			while (!threadShouldStop) {

				try {

					Socket connSocket = socket.accept();

					try {

						BufferedReader in = new BufferedReader(
								new InputStreamReader(
										connSocket.getInputStream()));
						PrintWriter out = new PrintWriter(
								connSocket.getOutputStream(), true);

						out.println("Hello there.");
						out.println("Enter a line with only a period to quit\n");

						String onDiskPath = "V:\\DominoGit\\Headless\\com.gregorbyte.headless.nsf\\.project";
						String nsfName = "testhead28.nsf";

						while (true) {
							String input = in.readLine();
							if (input == null || input.equals(".")) {
								break;
							}

							if (input.contains("BUILDMEANNSF")) {

								nsfName = in.readLine();
								
								ImportAndBuildJob myJob = new ImportAndBuildJob(
										onDiskPath, nsfName);

								BuildJobChangeAdapter listener = new BuildJobChangeAdapter(
										out);

								myJob.addJobChangeListener(listener);
								out.println("I am building you something");
								myJob.schedule();
								myJob.join();

								// Report on the Markers
								reportMarkers(nsfName, out);

							} else if (input.contains("PROBLEMS")) {

								reportMarkers(nsfName, out);

							} else {
								out.println(input.toUpperCase());
							}

							out.println(MSG_TERM);
						}

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						connSocket.close();
					}

				} catch (InterruptedIOException e) {

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			socket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
