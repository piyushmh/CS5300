package cs5300.proj1a.utils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;


public class Utils {

	public static long getCurrentTimeInMillis(){
		Date date= new Date();
        Timestamp currentTimestamp= new Timestamp(date.getTime());
        return currentTimestamp.getTime();
	}
	
	public static boolean hasSessionExpired(Date d){
		long currenttime = getCurrentTimeInMillis();
		Timestamp dateinnumber = new Timestamp(d.getTime());
		if ( currenttime  > dateinnumber.getTime()){
			return true;
		}else{
			return false;
		}
	}
	
	public static String generateDelimitedStringFromList(
			char delimited, ArrayList<String> l){
		
		if( l.size() == 0)
			return " ";
		String retvalString = l.get(0);
		
		for( int i = 1; i < l.size(); i++){
			retvalString += delimited + l.get(i);
		}
		return retvalString;
	}
}

