package com.gregorbyte.designer.headless.socket;

import java.io.PrintWriter;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

public class BuildJobChangeAdapter extends JobChangeAdapter {

	private PrintWriter writer;
	
	public BuildJobChangeAdapter(PrintWriter writer) {
		
		this.writer = writer;
		
	}
	
	@Override
	public void done(IJobChangeEvent event) {

		if (event.getResult().isOK())
			writer.println("BUILD JOB STATUS: SUCCESS");
		else
			writer.println("BUILD JOB STATUS: FAIL");
		
		super.done(event);
	}

}
