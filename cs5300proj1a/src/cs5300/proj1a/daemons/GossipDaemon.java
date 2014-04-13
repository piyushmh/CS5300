package cs5300.proj1a.daemons;

import cs5300.proj1a.servelets.WebServer;

//Author - Piyush
public class GossipDaemon implements Runnable{

	
	private static final int GOSSIP_INTERVAL_SEC = 60;
	
	@Override
	public void run() {
		
		System.out.println("Starting gossip thread");
		WebServer.viewManager.addSelfToBootStrapServer();
		
		while(true){
			
			try {
				WebServer.viewManager.initiateViewGossip();
				int interval = (int) (GOSSIP_INTERVAL_SEC/2 + 
						(Math.random()* GOSSIP_INTERVAL_SEC) );
				
				System.out.println("Next gossip interval is :" + interval);
				Thread.sleep(interval);
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

}
