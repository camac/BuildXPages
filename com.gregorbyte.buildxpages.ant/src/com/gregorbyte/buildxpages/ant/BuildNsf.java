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

public class BuildNsf extends Task {

	private static final String MSG_TERM = "END.";

	private static final String CMD_REFRESHIMPORTBUILD = "refreshImportBuild";
	private static final String CMD_IMPORTANDBUILD = "importandbuild";

	private String projectname;
	private String project;

	private String server;
	private String nsf;

	private boolean failonerror = true;
	private String port;

	public void setProject(String project) {
		this.project = project;
	}

	public void setProjectname(String projectname) {
		this.projectname = projectname;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public void setNsf(String nsf) {
		this.nsf = nsf;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setFailonerror(boolean failonerror) {
		this.failonerror = failonerror;
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

		if (project == null || "".equals(project))
			throw new BuildException("No OnDiskProject specified");

		File file = new File(project);

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
			String msg = String.format("Attempt to Create Socket with Designer on Port '%s'...", portNumber);
			log(msg);
			s = new Socket(InetAddress.getLocalHost(), portNumber);
			log("...Socket Created");
		} catch (IOException e) {
			getProject().setProperty("buildnsf.failed", "true");
			if (failonerror) {
				throw new BuildException("... Could not Connect to Headless Server using port: " + port, e);
			} else {
				log("...Could not connect to Headless server using port: " + port);
				return;
			}
		}

		try {
			// Set up Input/Output Stream
			BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
			PrintWriter out = new PrintWriter(s.getOutputStream(), true);

			// Read welcome message (max ten lines)
			for (int i = 0; i < 10; i++) {

				String line = input.readLine();
				if (line.equalsIgnoreCase(MSG_TERM))
					break;

				log(line);
			}

			// Send instruction to Build NSf
			log("Sending Refresh Import Build Command for " + project);
			
			out.println(CMD_REFRESHIMPORTBUILD);
			out.println(project);
			out.println(projectname);
			out.println(nsf);
			out.println(server);
			out.println(MSG_TERM);

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

				log(response);
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

			log("Exception when building");
			if (failonerror) {
				throw new BuildException(e);
			}
		}

		if (!nsfBuilt || !noProblems) {
			getProject().setProperty("buildnsf.failed", "true");
		}

		if (!nsfBuilt && failonerror) {
			throw new BuildException("Build Failed: NSF Not built properly");
		}

		if (!noProblems && failonerror) {
			throw new BuildException("Build Failed: Problems found after build");
		}

	}

}
