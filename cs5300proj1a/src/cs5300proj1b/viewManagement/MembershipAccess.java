package cs5300proj1b.viewManagement;
import cs5300.proj1a.utils.Server;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.DeleteAttributesRequest;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.SelectRequest;
/**
 * @author adityagaitonde
 */
public class MembershipAccess implements Runnable{

	private static final String simpleDBDomain = "Project1b";
	AmazonSimpleDB sdb; 
	Server current;
	
	public MembershipAccess(Server s) throws IOException{
		System.out.println("Creating domain called " + simpleDBDomain + ".\n");
		sdb = new AmazonSimpleDBClient(new PropertiesCredentials(
	           MembershipAccess.class.getResourceAsStream("AwsCredentials.properties")));
		current = s;
		checkServers();
	}
	
	public void updateView(){
		List<ReplaceableAttribute> replaceAbleAttributes = new ArrayList<ReplaceableAttribute>();
		replaceAbleAttributes.add(new ReplaceableAttribute("IP", current.getIp().toString(), true));
		replaceAbleAttributes.add(new ReplaceableAttribute("port", ""+ current.getPort(), true));
		replaceAbleAttributes.add(new ReplaceableAttribute("serverID", current.getServerID() , true));
		sdb.putAttributes(new PutAttributesRequest(simpleDBDomain, current
		        .toString(), replaceAbleAttributes));
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			try {
				Thread.sleep(10000);
				checkServers();
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	private void checkServers() {
		// TODO Auto-generated method stub
		updateView();
		SelectRequest sr = new SelectRequest("select * from "+simpleDBDomain);
		List<Server> ss = new ArrayList<Server>();			
		for(Item item : sdb.select(sr).getItems()){
			ss.add(new Server(item.getName()));
		}
		if(ss.size() > 0){
			int k = ss.size();
			int serverIndex = getRandomInRange(0, k-1);
			if(!checkAlive(ss.get(serverIndex))){
				Server s = ss.remove(serverIndex);
				sdb.deleteAttributes(new DeleteAttributesRequest(simpleDBDomain, s.getIp().toString()));
				sdb.deleteAttributes(new DeleteAttributesRequest(simpleDBDomain, ""+s.getPort()));
				sdb.deleteAttributes(new DeleteAttributesRequest(simpleDBDomain, s.getServerID()));
			}
		}
	}
	
	private boolean checkAlive(Server server) {
		// TODO Auto-generated method stub
		/*RPC call and return here */ 
		return true;
	}

	private int getRandomInRange(int min, int max) {
		return min + (int)(Math.random() * ((max - min) + 1));
	}

}



