package com.gregorbyte.designer.headless.socket;

import java.io.PrintWriter;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

public class CloseDesignerJobChangeAdapter extends JobChangeAdapter {

	private PrintWriter writer;
	
	public CloseDesignerJobChangeAdapter(PrintWriter writer) {
		
		this.writer = writer;
		
	}
	
	@Override
	public void scheduled(IJobChangeEvent event) {
	
		if (event.getResult().isOK())
			writer.println("CLOSE DESIGNER JOB SCHEDULED");
		else
			writer.println("CLOSE DESIGNER JOB SCHEDULING NOT SO GOOD");
		
		super.scheduled(event);
	}
	
	@Override
	public void aboutToRun(IJobChangeEvent event) {
		
		writer.println("CLOSE DESIGNER JOB ABOUT TO RUN");		
		super.aboutToRun(event);
		
	}

	@Override
	public void awake(IJobChangeEvent event) {

		writer.println("CLOSE DESIGNER JOB AWAKE");		
		super.awake(event);
		
	}

	@Override
	public void running(IJobChangeEvent event) {

		writer.println("CLOSE DESIGNER JOB RUNNING");		
		super.running(event);
	}

	@Override
	public void sleeping(IJobChangeEvent event) {

		writer.println("CLOSE DESIGNER JOB SLEEPING");		
		super.sleeping(event);
		
	}

	@Override
	public void done(IJobChangeEvent event) {

		if (event.getResult().isOK())
			writer.println("CLOSE DESIGNER JOB STATUS: SUCCESS");
		else
			writer.println("CLOSE DESIGNER JOB STATUS: FAIL");
		
		super.done(event);
	}

}
