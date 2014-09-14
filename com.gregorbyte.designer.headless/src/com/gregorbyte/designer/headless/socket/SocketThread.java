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

public class SocketThread implements Runnable {

	private static final String MSG_TERM = "END.";
	
	private boolean threadShouldStop = false;
	private ServerSocket socket = null;

	public void stopThread() {
		threadShouldStop = true;
	}

	private void reportMarkers(PrintWriter out) {
		
		String projName = "testhead25.nsf";
		
		//IDominoDesignerProject dproject =ProjectUtilities.getDesignerProject(projName);
		IProject project = ProjectUtilities.getProject(projName);
		
		try {
			
			IMarker[] problems = null;
			problems = project.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE); 
			
			for (IMarker marker : problems) {
				
				// Need to check if the marker actually exists
				if (marker.exists()) {

					//out.println(marker.getType());

					out.print(marker.getResource().getName() + ": ");
					
					String msg = (String) marker.getAttribute("message");					
					out.println(msg);
					
//					for (Object key : marker.getAttributes().keySet() ) {
//						out.print(key + ": ");						
//						out.println(marker.getAttributes().get(key));						
//					}
										
				}
				
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

			socket = new ServerSocket(8098,0,InetAddress.getLocalHost());
			//socket = new ServerSocket(8098);
			socket.setSoTimeout(500);

			while (!threadShouldStop) {

				try {

					Socket connSocket = socket.accept();
					
					try {
						
						BufferedReader in = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));
						PrintWriter out = new PrintWriter(connSocket.getOutputStream(),true);
						
						out.println("Hello there.");
						out.println("Enter a line with only a period to quit\n");
						
						while (true) {
							String input = in.readLine();
							if (input == null || input.equals(".")) {
								break;
							}
							
							if (input.contains("BUILDMEANNSF")) {
								
								String onDiskPath = "V:\\DominoGit\\Headless\\com.gregorbyte.headless.nsf\\.project";
								String nsfName = "testhead25.nsf";
								
								ImportAndBuildJob myJob = new ImportAndBuildJob(onDiskPath, nsfName);

								myJob.addJobChangeListener(new BuildJobChangeAdapter());
								out.println("I am building you something");
								myJob.schedule();							
								
							} else if (input.contains("PROBLEMS")) {
								
								reportMarkers(out);
								
							} else {
								out.println(input.toUpperCase());
							}
							
							out.println(MSG_TERM);
						}
						
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
