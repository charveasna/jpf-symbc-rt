/**
 * 
 */
package javax.scj;

import java.util.Iterator;


/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class RealtimeSystem {

	private static String targetThread;
	
	public RealtimeSystem() { }
	
	public static void start() { 
		
		Iterator<RealtimeThread> rtIter = RealtimeThread.threadList.iterator();
		while(rtIter.hasNext()) {
			final RealtimeThread rtThread = rtIter.next();
			if(rtThread.getClass().getCanonicalName().equals(targetThread)) {
				Thread threadWrapper = new Thread(new Runnable() {
					
					@Override
					public void run() {
						rtThread.run();
					}
				});
				
				threadWrapper.start();
			}
				
		}
		/*for (int i = 0; i < cnt; ++i) {
			RealtimeThread.threadList.elementAt(i);
			final RealtimeThread realtimeThread = (RealtimeThread)RealtimeThread.threadList.elementAt(i);
			
			Thread threadWrapper = new Thread(new Runnable() {
				
				@Override
				public void run() {
					realtimeThread.run();
				}
			});
			
			threadWrapper.start();
			
		}*/
	}
	
	public static void stop() {}
	
	public static void fire(int event) { }
	
	public static int currentTimeMicros() { return 0;}
	
}
