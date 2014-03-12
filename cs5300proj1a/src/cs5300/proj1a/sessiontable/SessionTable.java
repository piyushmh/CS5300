package cs5300.proj1a.sessiontable;

import cs5300.proj1a.objects.SessionObject;

public interface SessionTable {

	public SessionObject putSession(String key, SessionObject value);
	
	public SessionObject getSession(String key);
	
	public void deleteObject(String key);
	
	public int getSize();
}
