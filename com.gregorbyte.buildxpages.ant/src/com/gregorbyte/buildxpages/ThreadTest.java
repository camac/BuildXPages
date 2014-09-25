package com.gregorbyte.buildxpages;

import com.gregorbyte.buildxpages.task.ShowSpaceUsageTask;

public class ThreadTest {
	
	private static final String TEST_DB = "domino02!!Cameron\\plugindev.nsf";
	private static final String TEST_DB2 = "domino02!!temp\\SetTemplate.nsf";

	private static class MyRunnable implements Runnable {

		@Override
		public void run() {
			
			ShowSpaceUsageTask task = new ShowSpaceUsageTask(TEST_DB);			
			task.execute();

			task = new ShowSpaceUsageTask(TEST_DB2);			
			task.execute();

			
		}
		
	}

	/**
	 * We will test running the Notes C lib in 3 threads
	 * 
	 *  
	 * I want to see the thread local stuff
	 * @param args
	 */
	
	public static void main(String[] args) {

		try {
			
			Thread thread1 = new Thread(new MyRunnable());
			Thread thread2 = new Thread(new MyRunnable());
			Thread thread3 = new Thread(new MyRunnable());
	
			thread1.start();
			thread2.start();
			thread3.start();

		} finally {

			Notes.INSTANCE.term();
			
		}
		
	}

}
