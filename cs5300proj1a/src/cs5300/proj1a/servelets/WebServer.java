package cs5300.proj1a.servelets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cs5300.proj1a.objects.HostInformation;
import cs5300.proj1a.objects.Metadata;
import cs5300.proj1a.objects.SessionObject;
import cs5300.proj1a.sessiontable.SessionTable;
import cs5300.proj1a.utils.Utils;
import cs5300.proj1b.managers.CookieManager;
import cs5300.proj1b.managers.RPCCommunicationManager;
import cs5300.proj1b.managers.ServerViewManager;
import cs5300.proj1b.managers.SessionManager;

/**
 * Servlet implementation class SessionManager
 * Author - Piyush
 */
@WebServlet("/SessionManager")
public class WebServer extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/*String used to represent parameter*/
	private static final String PARAM_STRING = "param";

	/*Cookie*/
	private static final String COOKIE_STRING = "CS5300PROJ1SESSIONPM489";

	private static final String PAGE_LOAD = "pageload";

	private static final String REFRESH = "refresh";

	private static final String REPLACE = "replace";

	private static final String LOGOUT = "logout";
	
	private static String MESSAGE = "This is a default CS 5300 message";

	private static int COOKIE_AGE = 60*5;

	public static SessionTable sessionTable = new SessionTable();

	public static HostInformation hostInfo  = new HostInformation();

	public static ServerViewManager viewManager = new ServerViewManager();

	public static RPCCommunicationManager rpcManager = new RPCCommunicationManager();

	public static SessionManager sessionManager = new SessionManager();

	public static CookieManager cookieManager = new CookieManager();

	public WebServer() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter responsewriter = response.getWriter();
		String param = request.getParameter(PARAM_STRING);
		String cookiecontent = null;
		Cookie [] cookies = request.getCookies();
		for( Cookie cookie : cookies){
			if( cookie.getName().equals(COOKIE_STRING)){
				cookiecontent = cookie.getValue();
				break;
			}
		}
		System.out.println("Size of the session table is :" + sessionTable.getSize());
		System.out.println("Cookie content from the browser cookie is : " + cookiecontent);

		String servermessage = "";
		long newTime = Utils.getCurrentTimeInMillis() + COOKIE_AGE * 1000;
		int newcookieAge = COOKIE_AGE; 
		Metadata metadataobject = sessionManager.fetchSession(cookiecontent, rpcManager, viewManager, cookieManager, sessionTable, hostInfo);
		
		SessionObject sessionobject = null;
		int servernum = -1;
		
		if( metadataobject!= null){
			sessionobject = metadataobject.getSessionObject();
			servernum = metadataobject.getServernum();
		}else{
			if( cookiecontent!= null){
				servermessage = "SESSIONTIMEDOUT";
			}
		}
		
		//ArrayList<String> replicatedServers = metadataobject.getReplicatedServers();
		
		System.out.println("Retrieved session from session table : " + sessionobject);
		if( sessionobject!= null){
			if ( Utils.hasSessionExpired(sessionobject.getExpirationTime())){
				sessionManager.deleteSession(sessionobject.getSessionId(), sessionTable);
				sessionobject = null;
				servermessage = "SESSIONTIMEDOUT";
			}
		}

		if( sessionobject != null){

			if( param.equalsIgnoreCase(PAGE_LOAD)){

				System.out.println("Inside page load");		
				sessionobject.incrementVersionNumber();
				sessionobject.setExpirationTime(newTime);

			}else if (param.equalsIgnoreCase(REFRESH)){

				System.out.println("Inside page refresh");
				sessionobject.incrementVersionNumber();
				sessionobject.setExpirationTime(newTime);

			}else if (param.equalsIgnoreCase(REPLACE)){

				System.out.println("Inside replace");
				sessionobject.incrementVersionNumber();
				sessionobject.setExpirationTime(newTime);
				String replacementmessage = request.getParameter("message");
				sessionobject.setMessage(replacementmessage);
			
			}else if (param.equalsIgnoreCase(LOGOUT)){
				System.out.println("Inside logout");
				sessionManager.deleteSession(sessionobject.getSessionId(), sessionTable);
				newcookieAge = 0;
			}else{
				System.out.println("Shouldn't be here");
			}
			
		}else{
			
			if( ! param.equalsIgnoreCase(LOGOUT)){
				sessionobject = new SessionObject(MESSAGE, newTime, hostInfo);
				System.out.println("Constructed new session object with session id : " 
						+ sessionobject.getSessionId());
			}
		}
		
		if( param == null || !param.equalsIgnoreCase(LOGOUT)){
			
			assert(sessionobject!=null);
			ArrayList<String> replicatedservers = sessionManager.storeSessionWithReplication(
					sessionobject, sessionTable, hostInfo, viewManager, rpcManager);
		
			String cookiestring = cookieManager.generateCookieString(sessionobject, replicatedservers);
			Cookie c = new Cookie(COOKIE_STRING, cookiestring);
			c.setMaxAge(newcookieAge);
			response.addCookie(c);
			
			System.out.println("2 : " + Utils.generateDelimitedStringFromList(',', replicatedservers));
			String retstring = sessionobject.getMessage() + "|" + sessionobject.getExpirationTime().toString() 
					+ "|" + COOKIE_AGE + "|" + sessionobject.getVersion()
					+ "|" + hostInfo.getIPAddress() + "|" + Utils.generateDelimitedStringFromList(',', replicatedservers)
					+ "|" + Utils.generateDelimitedStringFromList(',', new ArrayList<String>(viewManager.getServerView())) 
					+ "|" + servernum + "|" + servermessage;
			
			responsewriter.write(retstring);
			
			System.out.println(retstring);
		}

}// end of doGet

/**
 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
 */
protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	// TODO Auto-generated method stub
}

}
