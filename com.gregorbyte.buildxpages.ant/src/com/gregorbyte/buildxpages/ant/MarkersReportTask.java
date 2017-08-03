package com.gregorbyte.buildxpages.ant;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class MarkersReportTask extends Task {

	private static final String MSG_TERM = "END.";
	private static final String COMMAND_MARKERS = "markersreport";
	
	private String ondiskproject;

	private boolean failonerror = true;
	private String port;

	public void setOndiskproject(String ondiskproject) {
		this.ondiskproject = ondiskproject;
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

	public void validateProperties() throws BuildException {

		if (ondiskproject == null || "".equals(ondiskproject))
			throw new BuildException("No OnDiskProject specified");

		File file = new File(ondiskproject);

		if (!file.exists())
			throw new BuildException("Supplied OnDiskProject does not exists");

	}

	@Override
	public void execute() throws BuildException {

		if (port == null || port.equals(""))
			port = "8282";

		int portNumber = Integer.parseInt(port);

		validateProperties();

		boolean nsfBuilt = false;
		boolean noProblems = false;
		Socket s = null;

		try {
			s = new Socket(InetAddress.getLocalHost(), portNumber);
		} catch (IOException e) {
			throw new BuildException(
					"Could not Connect to Headless Server using port: " + port,
					e);
		}

		try {
			// Set up Input/Output Stream
			BufferedReader input = new BufferedReader(new InputStreamReader(
					s.getInputStream()));
			PrintWriter out = new PrintWriter(s.getOutputStream(), true);

			// Read welcome message (max ten lines)
			for (int i = 0; i < 10; i++) {

				String line = input.readLine();
				if (line.equalsIgnoreCase(MSG_TERM))
					break;

				System.out.println(line);
			}

			// Send instruction to Build NSf
			out.println(COMMAND_MARKERS);
			out.println(ondiskproject);

			// Output Confirmation Message

			String response;
			response = input.readLine();

			// Output Progress if possible

			while (response != null && !response.equals(MSG_TERM)) {

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

			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (IOException e) {
			throw new BuildException(e);
		}

//		if (!nsfBuilt)
//			throw new BuildException("Build Failed: NSF Not built properly");

		if (!noProblems && failonerror)
			throw new BuildException("Build Failed: Problems found after build");

	}

}
