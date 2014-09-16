package com.gregorbyte.designer.headless.socket;

import java.io.PrintWriter;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

public class DeleteProjectJobChangeAdapter extends JobChangeAdapter {

	private PrintWriter writer;
	
	public DeleteProjectJobChangeAdapter(PrintWriter writer) {
		this.writer = writer;
	}

	@Override
	public void aboutToRun(IJobChangeEvent event) {
		// TODO Auto-generated method stub
		super.aboutToRun(event);
	}

	@Override
	public void awake(IJobChangeEvent event) {
		// TODO Auto-generated method stub
		super.awake(event);
	}

	@Override
	public void done(IJobChangeEvent event) {

		if (event.getResult().isOK())
			writer.println("DELETE PROJECT STATUS: SUCCESS");
		else
			writer.println("DELETE PROJECT STATUS: FAIL");
		
		super.done(event);
	}

	@Override
	public void running(IJobChangeEvent event) {
		// TODO Auto-generated method stub
		super.running(event);
	}

	@Override
	public void scheduled(IJobChangeEvent event) {
		// TODO Auto-generated method stub
		super.scheduled(event);
	}

	@Override
	public void sleeping(IJobChangeEvent event) {
		// TODO Auto-generated method stub
		super.sleeping(event);
	}
	
	
	
}
