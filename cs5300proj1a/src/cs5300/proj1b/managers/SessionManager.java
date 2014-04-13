package cs5300.proj1b.managers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import cs5300.proj1a.objects.HostInformation;
import cs5300.proj1a.objects.Metadata;
import cs5300.proj1a.objects.SessionObject;
import cs5300.proj1a.sessiontable.SessionTable;

public class SessionManager {

	private static int replicationFactor = 2;

	public Metadata fetchSession(
			String cookiecontent,
			RPCCommunicationManager rpcmanager,
			ServerViewManager viewManager,
			CookieManager cookiemanager,
			SessionTable sessionTable,
			HostInformation hostInfo){

		if( cookiecontent == null)
			return null;

		String[] contentList = cookiemanager.parseCookieContent(cookiecontent);
		String sessionId = contentList[0];
		int versionNum = -1;
		
		if( contentList.length > 1){
			versionNum = Integer.parseInt(contentList[1].trim());
		}
		
		ArrayList<String> replicatedServers = new ArrayList<String>();

		for( int i = 2; i < contentList.length ; i++){
			replicatedServers.add(contentList[i]);
		}

		SessionObject sessionObject = null; 
		int servernumber = checkIfReplicatedOnCurrentHost(replicatedServers, hostInfo.getIPAddress());

		System.out.println("1. " + sessionId + " " + versionNum + " " + servernumber);

		if( servernumber != -1){
			sessionObject = sessionTable.getSession(sessionId, versionNum);
		}

		if( servernumber == -1 || sessionObject == null){		
			for( int i = 0; i < replicatedServers.size(); i++){
				if( ! replicatedServers.get(i).equals(hostInfo.getIPAddress())){
					SessionObject temp = rpcmanager.sessionRead(replicatedServers.get(i), sessionId, versionNum);
					if( temp != null){
						sessionObject = temp;
						servernumber = i;
						break;
					}
				}
			}
		}
		return new Metadata(sessionObject, servernumber, replicatedServers);

	}

	private int checkIfReplicatedOnCurrentHost( ArrayList<String> replicas, String host){

		int retval = -1;
		for( int i = 0; i < replicas.size(); i++ ){
			if( host.equalsIgnoreCase( replicas.get(i))){
				retval = i;
				break;
			}
		}
		return retval;
	}

	public ArrayList<String> storeSessionWithReplication(
			SessionObject object,
			SessionTable table,
			HostInformation hostInfo,
			ServerViewManager viewmanager,
			RPCCommunicationManager rpcmanager){

		addSessionLocally(object, table); //store locally first

		Set<String> view = viewmanager.getServerView();
		ArrayList<String> replicatedServers = new ArrayList<String>();
		replicatedServers.add(hostInfo.getIPAddress());

		for (String replica : view) {

			if( rpcmanager.replicateSession(object, replica)){
				replicatedServers.add(replica);
				if( replicatedServers.size() == replicationFactor)
					break;
			}
		}

		System.out.println("Replication successfully done on hosts :" + replicatedServers.size());		
		return replicatedServers;

	}

	public void addSessionLocally(
			SessionObject sessionobject,
			SessionTable table){

		table.putSession(sessionobject.getSessionId(), sessionobject);

	}


	public void deleteSession(
			String sessionid,
			SessionTable sessiontable){

		sessiontable.deleteObject(sessionid);
	}

}
