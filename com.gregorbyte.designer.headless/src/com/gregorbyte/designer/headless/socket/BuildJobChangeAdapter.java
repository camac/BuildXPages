package com.gregorbyte.designer.headless.socket;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

public class BuildJobChangeAdapter extends JobChangeAdapter {

	@Override
	public void done(IJobChangeEvent event) {

		if (event.getResult().isOK())
			System.out.println("IT Went Well");
		else
			System.out.println("Not so good");
		
		super.done(event);
	}

}
