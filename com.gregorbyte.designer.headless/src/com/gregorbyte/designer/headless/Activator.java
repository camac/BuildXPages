package com.gregorbyte.designer.headless;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpService;

import com.gregorbyte.designer.headless.servlet.DesignerBuildServlet;
import com.gregorbyte.designer.headless.servlet.DesignerHttpServiceTracker;
import com.gregorbyte.designer.headless.socket.SocketThread;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.gregorbyte.designer.headless"; //$NON-NLS-1$

	private DesignerHttpServiceTracker serviceTracker;
	private SocketThread myThread;
	
	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		System.out.println("Starting Headless Bundle");

		ServiceReference sr = context.getServiceReference(HttpService.class.getName());
		if (sr != null) {
			HttpService http = (HttpService)context.getService(sr);
			if (http != null) {
				http.registerServlet("/maybe", new DesignerBuildServlet(), null, null);
			}			
		}

		myThread = new SocketThread();
		(new Thread(myThread)).start();
		
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		
		System.out.println("Stopping Headless Bundle");

		myThread.stopThread();
		
		ServiceReference sr = context.getServiceReference(HttpService.class.getName());
		if (sr != null) {
			HttpService http = (HttpService)context.getService(sr);
			if (http != null) {
				http.unregister("/maybe");
			}			
		}

		
				
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

}
