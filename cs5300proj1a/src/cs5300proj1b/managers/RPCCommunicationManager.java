package cs5300proj1b.managers;

import cs5300.proj1a.objects.SessionObject;

public class RPCCommunicationManager {

	public boolean replicateSession(
			SessionObject object, 
			String replicaserver){
		
		System.out.println("Replication session id : " + object.getSessionId() + 
				" on server : " + replicaserver);
		return true;
	}
}
