package cs5300proj1b.viewManagement;
import cs5300.proj1a.utils.View;
import cs5300.proj1a.utils.Server;

/**
 * @author adityagaitonde
 *
 */
public class ViewManagement {
	
	/* Shrink the view uniformly at random */
	public View shrink(View view, int k){
		if(view != null){
			while(view.getServers().size() > k){
				int randomServerNo = getRandomInRange(0, k-1);
				view.getServers().remove(randomServerNo);
			}
		}
		return view;
	}
   
	/* Insert the server into the view if 
	 * the server is not already present
	 */
	public View insert(View view, String serverID){
		if(view != null){
			boolean serverPresent = false;
			for(Server s : view.getServers()){
				if(s.getServerID() == serverID){
					serverPresent = true;
				}
			}
			if(!serverPresent){
				view.getServers().add(new Server(serverID));
			}
		}else{
			View newView  = new View();
			newView.getServers().add(new Server(serverID));
			return newView;
		}
		return view;
	}
	
    /*
     * Remove the server from the view if the server is
     * present 
     */
	public View remove(View view, String serverID){
		Server removeServer = null;
		if(view != null){
			for(Server server : view.getServers()){
				if(server.getServerID() == serverID){
					removeServer = server;
				}	
			}	
			if(removeServer != null){
				view.getServers().remove(removeServer);
			}
		}	
		return view;
	}	
	/*
	 * Choose a server which is assigned uniformly at random
	 * 
	 */
	public Server choose(View view){
		int k = getRandomInRange(0, view.getServers().size()-1);
		return view.getServers().get(k);
	}	
	
    /*
     * Union of two views Eliminating duplicates
     */
	public View union(View v, View w){
		for(Server server : v.getServers()){
			String serverID = server.getServerID();
			boolean serverPresent = false;
			for(Server chkServer : w.getServers()){
				if(serverID == chkServer.getServerID()){
					serverPresent = true;
				}
			}
			if(!serverPresent){
				v.getServers().add(server);
			}
		}
		return v;
	}
	
	private int getRandomInRange(int min, int max) {
		return min + (int)(Math.random() * ((max - min) + 1));
	}
}