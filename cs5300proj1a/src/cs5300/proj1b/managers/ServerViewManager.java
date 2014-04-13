package cs5300.proj1b.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import cs5300.proj1a.objects.ServerView;
import cs5300.proj1a.servelets.WebServer;
import cs5300.proj1a.utils.Utils;

//Author - Piyush

public class ServerViewManager {

	public static final String SIMPLE_DB_DELIMITER = "_";
	public static final String REGEX_SIMPLE_DB_DELIMITER = "_";
	public static final String NETWORK_VIEW_DELIMITER = "_";
	public static final String REGEX_NETWORK_VIEW_DELIMITER = "_";
	public static final int VIEW_SIZE = 5;
	public static final String NULL_SERVER = "0.0.0.0";
	private ServerView serverView;

	private Logger LOGGER = Logger.getLogger(ServerViewManager.class.getName());
	
	public ServerViewManager() {
		this.serverView = new ServerView();
	}

	public Set<String> getServerViewSet(){
		return this.serverView.getServerSet();
	}

	public ArrayList<String> getServerViewList(){
		return new ArrayList<String>(this.serverView.getServerSet());
	}

	public Set<String> shrinkView(Set<String> s, int size){

		List<String> list = new ArrayList<String>(s);
		Collections.shuffle(list);
		if ( list.size() > size){
			list = list.subList(0, size);
		}
		return new HashSet<String>(list);    
	}

	public Set<String> removeFromView(Set<String> s, String str){
		Set<String> retSet = new HashSet<String>();
		for (String server : s) {
			if( ! server.equalsIgnoreCase(str))
				retSet.add(server);
		}
		return retSet;
	}

	public Set<String> addToView(Set<String> view, String server){
		Set<String> serverSet =  new HashSet<String>();
		serverSet.add(server);
		return unionView(view, serverSet);
	}

	public Set<String> unionView(Set<String> a, Set<String> b){
		Set<String> retset = new HashSet<String>();
		retset.addAll(a);
		retset.addAll(b);
		return retset;
	}

	public void addSelfToBootStrapServer(){

		LOGGER.info("Adding own IP to bootstrap server");
		String bootstrapcontentString  = WebServer.simpleDBManager.getValue();
		List<String> servers = Utils.splitAndTrim(bootstrapcontentString, REGEX_SIMPLE_DB_DELIMITER);		
		logger.info("Received servers : " + Utils.printStringList(servers));
		Set<String> serverSet = new HashSet<String>(servers);
		serverSet = removeFromView(serverSet, NULL_SERVER);
		logger.info("Null removed servers : " + Utils.printStringList(servers));
		serverSet = shrinkView(serverSet, VIEW_SIZE - 1);
		logger.info("Shrunk servers : " + Utils.printStringList(servers));
		serverSet = addToView(serverSet, WebServer.hostInfo.getIPAddress());
		logger.info("Own IP added servers : " + Utils.printStringList(servers));
		assert( serverSet.size() <= VIEW_SIZE);
		
		String write = Utils.generateDelimitedStringFromList(SIMPLE_DB_DELIMITER, new ArrayList<String>(serverSet));
		WebServer.simpleDBManager.putValue(write);
	}

	private Set<String> getHostView(String hostname){
		
		String viewstring = WebServer.rpcManager.gossipViewWithHost(hostname);
		if( viewstring!= null){
			List<String> viewarray = Utils.splitAndTrim(viewstring, REGEX_SIMPLE_DB_DELIMITER);
			return new HashSet<String>(viewarray);
		}else{
			LOGGER.info("Inside ServerViewManager:getHostView : Operating getting "
					+ "host view failed, returning empty set");
			return new HashSet<String>();
		}
	}
	
	private void gossipViewWithHost(String hostname){
		
		LOGGER.info("Gossiping with host : " + hostname);
		Set<String> viewSet = getHostView(hostname);
		if( viewSet.size() == 0 ){
			return;
		}
		
		Set<String> combinedViewSet = unionView(
				viewSet, this.serverView.getServerSet());
		
		combinedViewSet = removeFromView(
				combinedViewSet, WebServer.hostInfo.getIPAddress());
		
		Set<String> shrunkSet = shrinkView(combinedViewSet, VIEW_SIZE);
		this.serverView.setServerSet(shrunkSet);
		LOGGER.info("Updating self view as :" + Utils.printStringSet(
				this.serverView.getServerSet()));
	}
	
	private void gossipWithBootStrapServer(){
		
		LOGGER.info("Gossiping with boot strap server");
		String bootstrapcontentString  = WebServer.simpleDBManager.getValue();
		List<String> servers = Utils.splitAndTrim(bootstrapcontentString, REGEX_SIMPLE_DB_DELIMITER);		
		Set<String> bootServerSet = new HashSet<String>(servers);
		bootServerSet = removeFromView(bootServerSet, WebServer.hostInfo.getIPAddress());
		
		Set<String> combinedviewSet = unionView(bootServerSet, this.serverView.getServerSet());
		combinedviewSet = shrinkView(combinedviewSet, VIEW_SIZE);
		this.serverView.setServerSet(combinedviewSet);
		
		LOGGER.info("Updating self view as :" + Utils.printStringSet(
				this.serverView.getServerSet()));
		
		combinedviewSet = addToView(combinedviewSet, WebServer.hostInfo.getIPAddress());
		combinedviewSet = shrinkView(combinedviewSet, VIEW_SIZE);
		
		assert( combinedviewSet.size() <= VIEW_SIZE);
		
		String write = Utils.generateDelimitedStringFromList(
				SIMPLE_DB_DELIMITER, new ArrayList<String>(combinedviewSet));
		
		WebServer.simpleDBManager.putValue(write);
		
	}
	
	public void initiateViewGossip(){
		
		LOGGER.info("Initiating periodic view gossip");
		
		int size = this.serverView.getServerSet().size();
		int randomindex =  (int) (Math.random() * (size + 1) );
		
		if( size > 0 && randomindex < size){
			gossipViewWithHost(new ArrayList<String>(
					this.serverView.getServerSet()).get(randomindex));
		}else{
			gossipWithBootStrapServer();
		}
	}
}

