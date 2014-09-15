package com.gregorbyte.buildxpages.ant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class BuildNsf extends Task {

	private static final Object MSG_TERM = "END.";
	
	private String ondiskproject;
	private String targetfilename;
	private boolean failonerror = true;
	private String port;
	
	public void setOndiskproject(String ondiskproject) {
		this.ondiskproject = ondiskproject;
	}

	public void setTargetfilename(String targetfilename) {
		this.targetfilename = targetfilename;
	}

	public void setPort(String port) {
		this.port = port;
	}

	private boolean parseStatus(String message) {

		String[] bits = message.split(":");		
		String result = bits[1].trim();
		
		if (result.equals("SUCCESS"))
			return true;
		if (result.equals("FAIL"))
			return false;
		
		return false;
		
	}
	
	@Override
	public void execute() throws BuildException {
		
		if (port == null || port.equals(""))
			port = "8098";
		
		int portNumber = Integer.parseInt(port);
				
		if (ondiskproject == null || "".equals(ondiskproject))
			throw new BuildException("No OnDiskProject specified");
		
		if (targetfilename == null || "".equals(targetfilename))
			throw new BuildException("No targetfilename specified");
		
		
		boolean nsfBuilt = false;
		boolean noProblems = false;
		Socket s = null;
		
		try {			
			s = new Socket(InetAddress.getLocalHost(), portNumber);
		} catch (IOException e) {
			throw new BuildException("Could not Connect to Headless Server", e);
		}

		try {
			// Set up Input/Output Stream
			BufferedReader input = new BufferedReader(new InputStreamReader(
					s.getInputStream()));
			PrintWriter out = new PrintWriter(s.getOutputStream(), true);
			
			// Read welcome message
			for (int i = 0; i < 3; i++) {
				System.out.println(input.readLine());
			}
			
			// Send instruction to Build NSf
			out.println("Will you BUILDMEANNSF please?");
			//out.println(ondiskproject);
			out.println(targetfilename);

			// Output Confirmation Message
			
			String response;
			response = input.readLine();
			
			// Output Progress if possible
			
			while (!response.equals(MSG_TERM)) {
			
				if (response.startsWith("BUILD JOB STATUS:")) {
					if (parseStatus(response))
						nsfBuilt = true;
				}
				
				if (response.startsWith("PROBLEMS STATUS:")) {
					if (parseStatus(response))
						noProblems = true;
				}
				
				System.out.println(response);			
				response = input.readLine();
			}

			// Process Complete
			
			// Check Errors
			
			// If there are errors
			//throw new BuildException("Errors found in NSF after build");
			
			// otherwise report success
			
//			PrintStream output;
//			try {
//				output = new PrintStream(s.getOutputStream());
//				
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			
			try {
				input.close();
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			

		} catch (IOException e) {
			throw new BuildException(e);
		}

		if (!nsfBuilt)
			throw new BuildException("Build Failed: NSF Not built properly");
		
		if (!noProblems && failonerror)
			throw new BuildException("Build Failed: Problems found after build");		
	
	}

}
