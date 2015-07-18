package com.gregorbyte.buildxpages.ant;

import lotus.domino.Agent;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.NotesFactory;
import lotus.domino.NotesThread;
import lotus.domino.Session;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class ImportPluginsTask extends Task {

	private static final String AGENT_NAME = "(API)";
	private static final String FORM_NAME = "HeadlessImport";

	private boolean deletefirst = false;

	private String server;
	private String database;
	private String sitexml;

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getSitexml() {
		return sitexml;
	}

	public void setSitexml(String sitexml) {
		this.sitexml = sitexml;
	}

	public boolean isDeletefirst() {
		return deletefirst;
	}

	public void setDeletefirst(boolean deletefirst) {
		this.deletefirst = deletefirst;
	}

	@Override
	public void execute() throws BuildException {

		log("Server is " + getServer());
		log("Database is " + getDatabase());
		log("SiteXml is " + getSitexml());

		try {

			NotesThread.sinitThread();

			Session s = NotesFactory.createSession();
			Database db = s.getDatabase(getServer(), getDatabase());

			if (db == null || !db.isOpen()) {
				throw new NullPointerException(
						"Could not get the Update Site Database");
			}

			Agent agent = db.getAgent(AGENT_NAME);

			log("Running " + AGENT_NAME + " as " + s.getEffectiveUserName());

			if (isDeletefirst()) {
				log("Deleting existing Plugins");
				Document doc = db.createDocument();
				doc.replaceItemValue("Form", FORM_NAME);
				doc.replaceItemValue("command", "deleteall");
				doc.save();
				agent.run(doc.getNoteID());
				doc.recycle();
				doc = null;
			}

			Document doc = db.createDocument();

			doc.replaceItemValue("Form", FORM_NAME);
			doc.replaceItemValue("command", "importsite");
			doc.replaceItemValue("sitexmlpath", getSitexml());

			doc.save();
			agent.run(doc.getNoteID());

			String errorCode = doc.getItemValueString("ErrorCode");
			String errorText = doc.getItemValueString("ErrorText");

			if (!"".equals(errorCode)) {
				throw new BuildException("Error importing Plugins " + errorCode
						+ " : " + errorText);
			}

		} catch (NotesException e) {
			e.printStackTrace();
		} finally {
			System.out.println("Terminating Notes Thread");
			NotesThread.stermThread();
		}

		super.execute();
	}

}
