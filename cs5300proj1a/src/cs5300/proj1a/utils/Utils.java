package cs5300.proj1a.utils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;


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

	public static String[] splitAndTrim(String arg,String delRegex){

		String[] list = arg.split(delRegex);
		for( int i = 0; i < list.length ; i++){
			list[i] = list[i].trim();
		}
		return list;
	} 

	public static String generateDelimitedStringFromList(
			char delimiter, ArrayList<String> l){

		if( l.size() == 0)
			return " ";
		String retvalString = l.get(0);

		for( int i = 1; i < l.size(); i++){
			retvalString += delimiter + l.get(i);
		}
		return retvalString;
	}

	public static String generateDelimitedStringFromList(
			String delimiter, ArrayList<String> l){

		if( l.size() == 0)
			return " ";
		String retvalString = l.get(0);

		for( int i = 1; i < l.size(); i++){
			retvalString += delimiter + l.get(i);
		}
		return retvalString;
	}

	public static String printStringList( String[] s){

		String retString = "[";
		for (String string : s) {
			retString += string+",";
		} 
		retString += "]";
		return retString;
	}

	public static String printStringSet(Set<String> s){
		String retString = "[";
		for (String string : s) {
			retString += string + ",";
		}
		retString += "]";
		return retString;
	}
}

