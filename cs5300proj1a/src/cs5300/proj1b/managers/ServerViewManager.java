package cs5300.proj1b.managers;

import java.util.HashSet;
import java.util.Set;

public class ServerViewManager {

	private Set<String> serverView;
	
	public ServerViewManager() {
		super();
		this.serverView = new HashSet<String>();
		this.serverView.add("192.168.0.15");
	}
	
	public Set<String> getServerView(){
		return this.serverView;
	}
	public String generateHostFromView(){
		return null;
	}
}
