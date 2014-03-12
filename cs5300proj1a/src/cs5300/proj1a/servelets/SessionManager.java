package cs5300.proj1a.servelets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.activity.InvalidActivityException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cs5300.proj1a.objects.SessionObject;
import cs5300.proj1a.sessiontable.ConcurrentHashMapSessionTable;
import cs5300.proj1a.sessiontable.SessionTable;
import cs5300.proj1a.utils.Utils;

/**
 * Servlet implementation class SessionManager
 * Author - Piyush
 */
@WebServlet("/SessionManager")
public class SessionManager extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	/*String used to represent parameter*/
	private static final String PARAM_STRING = "param";
	
	/*Cookie*/
	private static final String COOKIE_STRING = "CS5300PROJ1SESSIONPM489";
	
	private static final String PAGE_LOAD = "pageload";
	
	private static final String REFRESH = "refresh";
	
	private static final String REPLACE = "replace";
	
	private static String MESSAGE = "This is a default CS 5300 message";
	
	private static int COOKIE_AGE = 60*1;
	
	public static SessionTable sessionTable = new ConcurrentHashMapSessionTable();
	
	public SessionManager() {
        super();
    }
 
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		PrintWriter responsewriter = response.getWriter();
		String param = request.getParameter(PARAM_STRING);
		String sessionId = null;
		Cookie [] cookies = request.getCookies();
		for( Cookie cookie : cookies){
			if( cookie.getName().equals(COOKIE_STRING)){
				sessionId = cookie.getValue();
				break;
			}
		}
		System.out.println("Size of the session table is :" + sessionTable.getSize());
		System.out.println("Session id from the browser cookie is : " + sessionId);
		
		if( param.equalsIgnoreCase(PAGE_LOAD)){
			
			SessionObject sessionobject = null;
			
			if( sessionId == null){
				System.out.println("Inside page load with null session id ");
				sessionobject = new SessionObject(MESSAGE, COOKIE_AGE * 1000);
				System.out.println("Constructed new session object with session id : " 
						+ sessionobject.getSessionId());
			
			}else{
				System.out.println("Inside page load with not null session id ");		
				sessionobject = sessionTable.getSession(sessionId);
				System.out.println("Retrieved session from session table : " + sessionobject);
							
				if( sessionobject == null){	
					sessionobject = new SessionObject(MESSAGE, COOKIE_AGE * 1000);
					System.out.println("Constructed new session object with session id : " 
							+ sessionobject.getSessionId());
				}else{
					if ( Utils.hasSessionExpired(sessionobject.getExpirationTime())){
						sessionTable.deleteObject(sessionId);
						sessionobject = new SessionObject(MESSAGE, COOKIE_AGE * 1000);
					}else{
						sessionobject.incrementVersionNumber();
						sessionobject.setExpirationTime(Utils.getCurrentTimeInMillis() + COOKIE_AGE * 1000);
					}
				}		
			}
			
			Cookie c = new Cookie(COOKIE_STRING, sessionobject.getSessionId());
			c.setMaxAge(COOKIE_AGE);
			response.addCookie(c);
			
			sessionTable.putSession(sessionobject.getSessionId(), sessionobject);
			
			responsewriter.write(sessionobject.getMessage() + "|" + sessionobject.getExpirationTime().toString() 
					+ "|" + sessionobject.getVersion());
			System.out.println("Page load : " + sessionobject.getMessage() + 
					"|" + sessionobject.getExpirationTime().toString() + "|" + sessionobject.getVersion());
			
		}else if (param.equalsIgnoreCase(REFRESH)){
			
			SessionObject sessionobject ;
			
			if( sessionId != null){
				 sessionobject = sessionTable.getSession(sessionId);
				if ( Utils.hasSessionExpired(sessionobject.getExpirationTime())){
					sessionTable.deleteObject(sessionId);
					sessionobject = new SessionObject(MESSAGE, COOKIE_AGE * 1000);
				}else{
					sessionobject.incrementVersionNumber();
					sessionobject.setExpirationTime(Utils.getCurrentTimeInMillis() + COOKIE_AGE*1000);
				}
			}else{
				sessionobject = new SessionObject(MESSAGE, COOKIE_AGE * 1000); 
			}
			
			sessionTable.putSession(sessionobject.getSessionId(), sessionobject);
			Cookie c = new Cookie(COOKIE_STRING, sessionobject.getSessionId());
			c.setMaxAge(COOKIE_AGE);
			response.addCookie(c);
			responsewriter.write(MESSAGE + "|" + sessionobject.getExpirationTime() 
					+ "|" + sessionobject.getVersion());
			System.out.println("Session refresh : " + sessionobject.getMessage() + 
					"|" + sessionobject.getExpirationTime().toString() + "|" + sessionobject.getVersion());
			
		}else if (param.equalsIgnoreCase(REPLACE)){
		
			SessionObject sessionobject ;
			if( sessionId != null){
				sessionobject  = sessionTable.getSession(sessionId);
				if ( Utils.hasSessionExpired(sessionobject.getExpirationTime())){
					sessionTable.deleteObject(sessionId);
					sessionobject = new SessionObject(MESSAGE, COOKIE_AGE * 1000);
				}else{
					sessionobject.incrementVersionNumber();
					sessionobject.setExpirationTime(Utils.getCurrentTimeInMillis() + COOKIE_AGE*1000);
					String replacementmessage = request.getParameter("message");
					sessionobject.setMessage(replacementmessage);
					//MESSAGE = replacementmessage;
				}
			}else{
				sessionobject = new SessionObject(MESSAGE, COOKIE_AGE * 1000);
			}
			
			sessionTable.putSession(sessionobject.getSessionId(), sessionobject);
			Cookie c = new Cookie(COOKIE_STRING, sessionobject.getSessionId());
			c.setMaxAge(COOKIE_AGE);
			response.addCookie(c);
			System.out.println(sessionobject.getMessage() + "|" + sessionobject.getExpirationTime().toString() 
					+ "|" + sessionobject.getVersion());
			responsewriter.write(sessionobject.getMessage() + "|" + sessionobject.getExpirationTime().toString() 
					+ "|" + sessionobject.getVersion());
			
			System.out.println("Text replace : " + sessionobject.getMessage() + 
					"|" + sessionobject.getExpirationTime().toString() + "|" + sessionobject.getVersion());
			
		
		}else {//logout request	
			Cookie c = new Cookie(COOKIE_STRING, sessionId);
			c.setMaxAge(0);
			sessionTable.deleteObject(sessionId);
			System.out.println("Logging out of session:"+ sessionId);
			response.addCookie(c);
			System.out.println("Size of the session table is :" + sessionTable.getSize());
		}
		
	}// end of doGet

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
}
