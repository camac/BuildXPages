package com.gregorbyte.buildxpages.ant;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.gregorbyte.buildxpages.task.GenerateSiteXml;

public class GenerateSiteXmlTask extends Task {

	private String eclipseDir;

	public void setEclipseDir(String eclipseDir) {
		this.eclipseDir = eclipseDir;
	}

	@Override
	public void execute() throws BuildException {

		if (this.eclipseDir == null || this.eclipseDir.isEmpty())
			throw new BuildException("eclipseDir not Specified");
		
		File file = new File(eclipseDir);
		
		if (!file.exists())
			throw new BuildException("specified eclipseDir does not exists");
		
		if (!file.isDirectory())
			throw new BuildException("eclipseDir must be a directory");
		
		try {

			GenerateSiteXml gen = new GenerateSiteXml(this.eclipseDir);
			gen.execute();
			
		} catch (Exception e) {
			log(e.getMessage());
			throw new BuildException("Problem Executing Generate Site Xml", e);
		}
		
		super.execute();
	}
	
	
	
}
