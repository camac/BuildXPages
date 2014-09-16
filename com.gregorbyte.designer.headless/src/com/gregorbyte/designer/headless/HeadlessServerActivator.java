package com.gregorbyte.designer.headless;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.gregorbyte.designer.headless.socket.HeadlessServerRunnable;

/**
 * The activator class controls the plug-in life cycle
 */
public class HeadlessServerActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.gregorbyte.designer.headless"; //$NON-NLS-1$

	public static HeadlessServerActivator INSTANCE = null; 
	
	private Thread serverThread;
	private HeadlessServerRunnable headlessServer;
	
	// The shared instance
	private static HeadlessServerActivator plugin;
	
	/**
	 * The constructor
	 */
	public HeadlessServerActivator() {
	}

	public void startServer() {
		if (!isServerRunning()) {
			headlessServer = new HeadlessServerRunnable();
			serverThread = new Thread(headlessServer);
			serverThread.setName("Headless Nsf Server");
			serverThread.start();
		}
	}
	
	public void stopServer() {
		if (isServerRunning())
			headlessServer.stopThread();
	}
	
	public boolean isServerRunning() {

		if (serverThread == null) return false;
		return serverThread.isAlive();
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {

		INSTANCE = this;
		
		System.out.println("Starting Headless Bundle");
				
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		
		System.out.println("Stopping Headless Bundle");

		stopServer();			
				
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static HeadlessServerActivator getDefault() {
		return plugin;
	}

}
