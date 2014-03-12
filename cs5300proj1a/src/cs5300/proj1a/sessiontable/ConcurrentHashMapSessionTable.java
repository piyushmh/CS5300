package cs5300.proj1a.sessiontable;

import java.util.concurrent.ConcurrentHashMap;

import cs5300.proj1a.objects.SessionObject;

public class ConcurrentHashMapSessionTable implements SessionTable {

	public ConcurrentHashMap<String, SessionObject> concurrentHashMap;
	
	public ConcurrentHashMapSessionTable(){
		super();
		this.concurrentHashMap = new ConcurrentHashMap<String, SessionObject>();
	}

	@Override
	public SessionObject putSession(String key, SessionObject value) {
		return this.concurrentHashMap.put(key, value);
	}

	@Override
	public SessionObject getSession(String key) {
		return this.concurrentHashMap.get(key);
	}

	@Override
	public void deleteObject(String key) {
		this.concurrentHashMap.remove(key);		
	}

	@Override
	public int getSize() {
		return this.concurrentHashMap.size();
	}

}
