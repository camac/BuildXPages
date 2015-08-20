package com.gregorbyte.buildxpages.task;

import lotus.domino.Agent;
import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.NotesFactory;
import lotus.domino.NotesThread;
import lotus.domino.Session;
import lotus.domino.Document;

public class ImportToOpenEclipseUpdateSite {

	private static boolean deleteFirst = false;
	private static final String AGENT_NAME = "(API)";
	private static String serverName	= null;
	private static String dbFilePath	= null;
	private static String sitexmlpath 	= null;
	
	private static boolean processArgs(String[] args) {
		
		int length = args.length;
		
		if (length < 3) {
			return false;
		}

		// Process Option
		for (int i = 0; i < (length - 3); i++) {
			
			if (args[i].equals("-d"))
				deleteFirst = true;
			else {
				System.out.println("unknown option: " + args[i]);
				return false;
			}
			
		}
		
		serverName = args[length - 3];
		dbFilePath = args[length - 2];
		sitexmlpath = args[length - 1];
				
		return true;
		
	}
	
	private static void usage() {
		
		System.out.println("OpenEclipseImport [-d] <server> <dbfilepath> <sitexml> ");
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		if (!processArgs(args)) {
			usage();
			System.exit(1);;
		} else {
			StringBuilder sb = new StringBuilder("Importing for:");

			sb.append("Server " + serverName);
			sb.append(", UpdateSite: " + dbFilePath);
			sb.append(", SiteXML: " + sitexmlpath);
			
			System.out.println(sb.toString());
		}
		
		try {
			
			NotesThread.sinitThread();
			
			Session s = NotesFactory.createSession(); 
			Database db = s.getDatabase(serverName, dbFilePath);
			
			if (db == null || !db.isOpen()) {
				throw new NullPointerException("Could not get the Update Site Database");
			}
			
			Agent agent = db.getAgent(AGENT_NAME);
			
			System.out.println(s.getEffectiveUserName());
			
			if (deleteFirst) {
				System.out.println("Deleting existing Plugins");
				Document doc = db.createDocument();			
				doc.replaceItemValue("command", "deleteall");
				doc.save();
				agent.run(doc.getNoteID());
				doc.recycle();
				doc = null;
			}

			Document doc = db.createDocument();
			
			doc.replaceItemValue("command", "importsite");
			doc.replaceItemValue("sitexmlpath", sitexmlpath);
			
			doc.save();			
			agent.run(doc.getNoteID());
			
		} catch (NotesException e) {
			e.printStackTrace();
		} finally {
			System.out.println("Terminating Notes Thread");
			NotesThread.stermThread();
		}
		
		
	}

}
