package cs5300.proj1a.daemons;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

//Author - Piyush

@WebListener
public class GossipDaemonInitializer implements ServletContextListener{

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("Starting gossip daemon initializer");
		Thread t = new Thread(new GossipDaemon());
		t.start();
	}

}
