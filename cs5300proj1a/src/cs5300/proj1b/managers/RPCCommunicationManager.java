package cs5300.proj1b.managers;

import java.io.IOException;
import java.util.UUID;

import cs5300.proj1a.objects.SessionObject;
import cs5300.proj1a.servelets.WebServer;
import cs5300.proj1b.rpc.RPCClient;

public class RPCCommunicationManager {

	private static String NETWORK_DELIMITER = "|";

	private static int RPC_SERVER_PORT = 5300;

	private static int SESSION_READ_OPCODE = 1;

	private static int SESSION_WRITE_OPCODE = 2;

	private static int CONFIRMATION_CODE = 400;

	public boolean replicateSession(
			SessionObject object, 
			String replicaserver){

		System.out.println("Replication session id : " + object.getSessionId() + 
				" on server : " + replicaserver);

		boolean retval = false;
		String callId = UUID.randomUUID().toString(); //callID
		String serverstring = 
				callId + 				NETWORK_DELIMITER +
				SESSION_WRITE_OPCODE +  NETWORK_DELIMITER + 
				object.getSessionId() + NETWORK_DELIMITER +
				object.getMessage() + 	NETWORK_DELIMITER + 
				object.getVersion() + 	NETWORK_DELIMITER + 
				object.getExpirationTimeMilliSecond();

		try {

			String reply = new RPCClient().callServer(replicaserver, RPC_SERVER_PORT, serverstring, callId);	
			if( reply!= null){
				String[] list = reply.split(NETWORK_DELIMITER);
				if( 400 == Integer.parseInt(list[1]));
				retval = true;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return retval;
	}

	/* Returns NULL if can't connect
	 * Returns NULL if exception if thrown
	 */
	public SessionObject sessionRead( String hostname, String sessionid, int version){

		SessionObject object = null;
		try{
			String callID = UUID.randomUUID().toString();
			String send= callID + NETWORK_DELIMITER + SESSION_READ_OPCODE + NETWORK_DELIMITER + 
					sessionid +  NETWORK_DELIMITER + version;
			String outputString = new RPCClient().callServer(hostname, RPC_SERVER_PORT, send, callID);

			if( outputString != null){
				//EXPECTING FORMAT - CALLID|VERSION|MESSAGE|EXPIRATIONDATE
				String[] list = outputString.split(NETWORK_DELIMITER);
				object = new SessionObject();
				assert( version == Integer.parseInt(list[1]));
				object.setVersion(version);
				object.setSessionId(sessionid);
				object.setMessage(list[2]);
				object.setExpirationTime(Long.parseLong(list[3].trim()));
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return object;
	}

	public String replyToClient( String s){

		System.out.println("String received from client : " + s);
		String[] list = s.split(NETWORK_DELIMITER);
		String retvalString = list[0]; //return the same call id

		int opcode = Integer.parseInt(list[1]);

		if( opcode == SESSION_READ_OPCODE){

			//EXPECTED FORMAT - CALLID|OPCODE|SESSIONID|VERSION

			SessionObject object = WebServer.sessionTable.concurrentHashMap.get(list[2]);
			if(object != null && (object.getVersion() == Integer.parseInt(list[3]))){

				retvalString += NETWORK_DELIMITER + object.getVersion() + NETWORK_DELIMITER + object.getMessage()
						+ NETWORK_DELIMITER + object.getExpirationTimeMilliSecond();

			}else{

				retvalString += NETWORK_DELIMITER + -1 + NETWORK_DELIMITER + " " + NETWORK_DELIMITER + -1;
			}

		}else if ( opcode == SESSION_WRITE_OPCODE){

			//EXPECTED FORMAT - CALLID|OPCODE|SESSIONID|MESSAGE|VERSION|EXPIRATIONDATE

			SessionObject sessionObject = new SessionObject();
			sessionObject.setSessionId(list[2].trim());
			sessionObject.setMessage(list[3].trim());
			sessionObject.setVersion(Integer.parseInt(list[4].trim()));
			sessionObject.setExpirationTime(Long.parseLong(list[5].trim()));

			WebServer.sessionManager.addSessionLocally(sessionObject, WebServer.sessionTable);
			retvalString += NETWORK_DELIMITER + CONFIRMATION_CODE;
		}

		System.out.println("String returning to client : " + retvalString);
		return retvalString;
	}
}
