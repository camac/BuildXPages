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
	public void scheduled(IJobChangeEvent event) {
	
		if (event != null && event.getResult() != null && event.getResult().isOK())
			writer.println("BUILD JOB SCHEDULED");
		else
			writer.println("BUILD JOB SCHEDULING NOT SO GOOD");
		
		super.scheduled(event);
	}
	
	@Override
	public void aboutToRun(IJobChangeEvent event) {
		
		writer.println("BUILD JOB ABOUT TO RUN");		
		super.aboutToRun(event);
		
	}

	@Override
	public void awake(IJobChangeEvent event) {

		writer.println("BUILD JOB AWAKE");		
		super.awake(event);
		
	}

	@Override
	public void running(IJobChangeEvent event) {

		writer.println("BUILD JOB RUNNING");		
		super.running(event);
	}

	@Override
	public void sleeping(IJobChangeEvent event) {

		writer.println("BUILD JOB SLEEPING");		
		super.sleeping(event);
		
	}

	@Override
	public void done(IJobChangeEvent event) {

		if (event.getResult().isOK())
			writer.println("BUILD JOB STATUS: SUCCESS");
		else {
			writer.println("BUILD JOB STATUS: FAIL");
			writer.println(event.getResult().getMessage() + event.getResult().getCode());
			if (event.getResult().getException() != null) {
				writer.println(event.getResult().getException().getMessage());
			}
			
		}
		
		super.done(event);
	}

}
