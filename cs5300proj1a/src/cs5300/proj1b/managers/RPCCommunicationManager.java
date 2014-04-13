package cs5300.proj1b.managers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import cs5300.proj1a.objects.SessionObject;
import cs5300.proj1a.servelets.WebServer;
import cs5300.proj1a.utils.Utils;
import cs5300.proj1b.rpc.RPCClient;

import java.util.logging.*;

public class RPCCommunicationManager {

	private static String NETWORK_DELIMITER = "|";

	private static String REGEX_NETWORK_DELIMITER = "\\|";

	private static int RPC_SERVER_PORT = 5300;

	private static int SESSION_READ_OPCODE = 1;

	private static int SESSION_WRITE_OPCODE = 2;
	
	private static int VIEW_GOSSIP_OPCODE = 3;

	private static int CONFIRMATION_CODE = 400;

	private Logger logger = Logger.getLogger(RPCCommunicationManager.class.getName());
	
	public String gossipViewWithHost(String server){
		
		logger.info("Inside RPC Communication Manager: Gossiping with host : " + server);
		
		String callId = UUID.randomUUID().toString(); 
		String serverString = 
				callId + NETWORK_DELIMITER + VIEW_GOSSIP_OPCODE;
		
		String reply = null;
		try {

			reply = new RPCClient().callServer(server, RPC_SERVER_PORT, serverString, callId);	
			System.out.println("String received from server : " + reply);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return reply;
	}
	
	public boolean replicateSession(
			SessionObject object, 
			String replicaserver){

		System.out.println("Replicating session id : " + object.getSessionId() + 
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
			System.out.println("String received from server : " + reply);
			if( reply!= null){
				String[] list = reply.split(REGEX_NETWORK_DELIMITER);
				System.out.println(Utils.printStringList(Arrays.asList(list)));
				if( 400 == Integer.parseInt(list[1].trim()));
				retval = true;
			}else{
				System.out.println("Replication failed on server : " + replicaserver);
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
				
				String[] list = outputString.split(REGEX_NETWORK_DELIMITER);
				
				System.out.println("Inside session read : " + Utils.printStringList(Arrays.asList(list)));
				
				if( Integer.parseInt(list[1].trim()) != -1){
					
					object = new SessionObject();
					assert( version == Integer.parseInt(list[1]));
					object.setVersion(version);
					object.setSessionId(sessionid);
					object.setMessage(list[2]);
					object.setExpirationTime(Long.parseLong(list[3].trim()));
				}else {
					System.out.println("Inside session read : Invalid version number received from server");
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return object;
	}

	public String replyToClient( String s){

		System.out.println("String received from client : " + s);
		String[] list = s.split(REGEX_NETWORK_DELIMITER);
		System.out.println(Utils.printStringList(Arrays.asList(list)));
		String retvalString = list[0]; //return the same call id

		int opcode = Integer.parseInt(list[1].trim());

		if( opcode == SESSION_READ_OPCODE){
			
			//EXPECTED FORMAT - CALLID|OPCODE|SESSIONID|VERSION

			SessionObject object = WebServer.sessionTable.concurrentHashMap.get(list[2]);
			if(object != null && (object.getVersion() == Integer.parseInt(list[3].trim()))){
				retvalString += NETWORK_DELIMITER + object.getVersion() + NETWORK_DELIMITER + object.getMessage()
						+ NETWORK_DELIMITER + object.getExpirationTimeMilliSecond();
			
			}else{
			
				retvalString += NETWORK_DELIMITER + -1 + NETWORK_DELIMITER + "Dummy" + NETWORK_DELIMITER + -1;
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
		
		} else if( opcode == VIEW_GOSSIP_OPCODE){
			
			//EXPECTEDFORMAT = CALLID|OPCODE
			
			Set<String> selfviewSet = WebServer.viewManager.getServerViewSet();
			retvalString += NETWORK_DELIMITER + Utils.generateDelimitedStringFromList(
					ServerViewManager.NETWORK_VIEW_DELIMITER , new ArrayList<String>(selfviewSet));
 		}

		System.out.println("String returning to client : " + retvalString);
		return retvalString;
	}
}
