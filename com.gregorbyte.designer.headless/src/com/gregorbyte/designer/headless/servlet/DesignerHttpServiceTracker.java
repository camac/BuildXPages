package com.gregorbyte.designer.headless.servlet;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

public class DesignerHttpServiceTracker extends ServiceTracker {

	public DesignerHttpServiceTracker(BundleContext context) {
		super(context, HttpService.class.getName(), null);
	}

	@Override
	public Object addingService(ServiceReference reference) {

		HttpService httpService = (HttpService) super.addingService(reference);
		if (httpService == null)
			return null;
		
		try {
			System.out.println("Registering Servlet at /headless2");
			httpService.registerServlet("/headless2", new DesignerBuildServlet(), null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return httpService;
	}

	@Override
	public void removedService(ServiceReference reference, Object service) {

		HttpService httpService = (HttpService) service;
		
		System.out.println("Unregistering /headless2");
		httpService.unregister("/headless2");
		
		super.removedService(reference, service);
	}
	
	
	
}
