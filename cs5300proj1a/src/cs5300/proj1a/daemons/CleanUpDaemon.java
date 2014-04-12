package cs5300.proj1a.daemons;

import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import cs5300.proj1a.objects.SessionObject;
import cs5300.proj1a.servelets.WebServer;
import cs5300.proj1a.utils.Utils;

public class CleanUpDaemon extends TimerTask {

	@Override
	public void run() {
		
		System.out.println("Clean up daemon running");
		
		ConcurrentHashMap<String, SessionObject> map = WebServer.sessionTable.concurrentHashMap;
		Iterator<Map.Entry<String,SessionObject>> it = map.entrySet().iterator();
		long currenttime = Utils.getCurrentTimeInMillis();
		while( it.hasNext()){
			Map.Entry<String, SessionObject> entry = it.next();
			System.out.println("Iterating over session with session id  : " + entry.getValue().getSessionId());
			if( currenttime >= entry.getValue().getExpirationTimeMilliSecond()){
				System.out.println("Cleaning up session : " + entry.getValue().getSessionId());
				it.remove();
			}
		}
		
	}

}
