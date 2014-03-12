package cs5300.proj1a.objects;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import cs5300.proj1a.utils.Utils;

public class SessionObject {

	private String sessionId;
	private int version;
	private String message;
	private long expirationTime;
	
	public SessionObject(String message, long expirationTime) throws UnknownHostException {
		super();
		this.message = message;
		this.expirationTime = Utils.getCurrentTimeInMillis() + expirationTime;
		
		String uid = new java.rmi.server.UID().toString();
		uid += InetAddress.getLocalHost().hashCode();
		this.sessionId = uid;
		this.setVersion(0);
	}

	
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getExpirationTime() {
		Date date=new Date(this.expirationTime);
		return date;
	}
	
	public long getExpirationTimeMilliSecond(){
		return this.expirationTime;
	}

	public void setExpirationTime(long expirationTime) {
		this.expirationTime = expirationTime;
	}
	
	public void incrementVersionNumber(){
		this.version += 1;
	}
	
	
}
