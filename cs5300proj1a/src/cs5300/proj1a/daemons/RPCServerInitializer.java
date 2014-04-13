package cs5300.proj1a.daemons;

import java.net.SocketException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import cs5300.proj1b.rpc.RPCServer;

public class RPCServerInitializer implements ServletContextListener{

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub

	}
	@Override
	public void contextInitialized(ServletContextEvent arg0) {

		try {
			Thread t = new Thread(new RPCServer());
			t.start();
		} catch (SocketException e) {
			e.printStackTrace();
		}

	}
}
