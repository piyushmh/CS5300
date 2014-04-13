package cs5300.proj1a.daemons;

import java.net.SocketException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import cs5300.proj1b.rpc.RPCServer;

@WebListener
public class RPCServerInitializer implements ServletContextListener{

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

	}
	@Override
	public void contextInitialized(ServletContextEvent arg0) {

		try {
			System.out.println("RPCServerInitializer started");
			Thread t = new Thread(new RPCServer());
			t.start();
		} catch (SocketException e) {
			e.printStackTrace();
		}

	}
}
