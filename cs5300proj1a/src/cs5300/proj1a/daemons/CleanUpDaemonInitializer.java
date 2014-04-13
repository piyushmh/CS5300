package cs5300.proj1a.daemons;

import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class CleanUpDaemonInitializer implements ServletContextListener{

	private static int CLEAN_UP_DAEMON_FREQUENCY = 5*60*1000;
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		
		//Timer time = new Timer();
		//CleanUpDaemon daemontask =  new CleanUpDaemon();
		//time.schedule(daemontask, 0, CLEAN_UP_DAEMON_FREQUENCY);
	}

}
