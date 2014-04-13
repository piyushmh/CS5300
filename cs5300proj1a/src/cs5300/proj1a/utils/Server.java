package cs5300.proj1a.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

/**
 * 
 */

/**
 * @author adityagaitonde
 *
 */
public class Server {
	/* Assuming server ID is an IP address */
	String serverID;
	InetAddress ip;
	int port;
	
	public Server(InetAddress sip, int sport){
		ip = sip;
		port = sport;
		serverID = ip+"-"+UUID.randomUUID().toString();
	}
	
	public Server() throws UnknownHostException{
		ip = InetAddress.getByName("127.0.0.1");
		port = 0;
		serverID = ip+"-"+UUID.randomUUID().toString();
	}
	/**
	 * @return the ip
	 */
	public InetAddress getIp() {
		return ip;
	}

	/**
	 * @param ip the ip to set
	 */
	public void setIp(InetAddress ip) {
		this.ip = ip;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	public Server(String serverID) {
		// TODO Auto-generated constructor stub
		this.serverID = serverID;
	}

	/**
	 * @return the serverID
	 */
	public String getServerID() {
		return serverID;
	}

	/**
	 * @param serverID the serverID to set
	 */
	public void setServerID(String serverID) {
		this.serverID = serverID;
	}
}
