package cs5300.proj1b.managers;


import java.io.IOException;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.SelectRequest;

public class SimpleDBInteractionManager {

	private static final String simpleDBDomain = "Project1b";
	private static final String itemKey = "bootstrapItem";
	private static final String attributeKey = "bootstrapView";
	private AmazonSimpleDB sdb; 

	public SimpleDBInteractionManager(){

		try {
			sdb = new AmazonSimpleDBClient(new PropertiesCredentials(
					SimpleDBInteractionManager.class.getResourceAsStream("AwsCredentials.properties")));
		} catch (IOException e) {
			e.printStackTrace();
			sdb = null;
		}
	}

	public boolean putValue(String arg){

		System.out.println("Writing to simple DB : " + arg);
		ReplaceableAttribute replaceableAttribute = new ReplaceableAttribute()
		.withName(attributeKey)
		.withValue(arg)
		.withReplace(true);

		sdb.putAttributes(new PutAttributesRequest().withDomainName(simpleDBDomain)
				.withItemName(itemKey)
				.withAttributes(replaceableAttribute));
		
		return true;
	}

	public String getValue(){
		
		String retval = "";
		String selectExpression = "select * from `" + simpleDBDomain + "`";
		SelectRequest selectRequest = new SelectRequest(selectExpression);
		for(Item item : sdb.select(selectRequest).getItems()){
			for (Attribute attribute : item.getAttributes()) {
				if(attribute.getName().equals(attributeKey)){
					retval = attribute.getValue();
					break;
				}
			}
		}
		System.out.println("Read from simple DB : " + retval);
		return retval;
	}
}
