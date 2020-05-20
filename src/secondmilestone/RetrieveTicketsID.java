package secondmilestone;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.net.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class RetrieveTicketsID {

	static String projName ="Mahout";
	static String organization = "apache";
	static int threshold = 7;
	private static String readAll(Reader rd) throws IOException {
		      StringBuilder sb = new StringBuilder();
		      int cp;
		      while ((cp = rd.read()) != -1) {
		         sb.append((char) cp);
		      }
		      return sb.toString();
		   }

	   public static JSONArray readJsonArrayFromUrl(String url) throws IOException, JSONException {
		   InputStream is = new URL(url).openStream();
		      try 
		         (BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
		         ){
		         return new JSONArray(readAll(rd));
		       } finally {
		         is.close();
		       }
		   }

   public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
      InputStream is = new URL(url).openStream();
      try 
         (BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
         ){
         return new JSONObject(readAll(rd));
       } finally {
         is.close();
       }
   }
   
   
   public static List<String> getTicketID(String projName) throws IOException, JSONException{
	 
	   Integer j = 0;
	   Integer i = 0; 
	   Integer total = 1;
	   List<String> ticketList =  new ArrayList<>();
      //Get JSON API for closed bugs w/ AV in the project
      do {
         //Only gets a max of 1000 at a time, so must do this multiple times if bugs >1000
         j = i + 1000;
         String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
                + projName + "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR"
                + "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,versions,created&startAt="
                + i.toString() + "&maxResults=" + j.toString();
         JSONObject json = readJsonFromUrl(url);
         JSONArray issues = json.getJSONArray("issues");
         total = json.getInt("total");
         for (; i < total && i < j; i++) {
            //Iterate through each bug
            String key = issues.getJSONObject(i%1000).get("key").toString();
            ticketList.add(key);
         }  
      } while (i < total);
      return ticketList;
   }
   public static int[] findStartEnd(String projName, String commitMessage) {
	   
	   int[] resultArray = new int[2];
	   int start = commitMessage.substring(0, projName.length()+threshold).toLowerCase().indexOf(projName.toLowerCase()+ "-");
	   int end = -1;

		 if (start != -1) {		   
			   String startString = commitMessage.substring(0, projName.length()+threshold).substring(start).toLowerCase();			   
			   if (startString.matches(projName.toLowerCase()+ "-[0-9][0-9][0-9][0-9].*")) {
				   	end = start + projName.length()+5;
			   }
			   else if (startString.matches(projName.toLowerCase()+ "-[0-9][0-9][0-9].*")) {
				   end = start + projName.length()+4;
			   }
			   else if (startString.matches(projName.toLowerCase()+ "-[0-9][0-9].*")) {
				   end = start + projName.length()+3;
			   }
			   else if (startString.matches(projName.toLowerCase()+ "-[0-9].*")) {
				   end = start + projName.length()+2;
			   }
		   }
	   
	   resultArray[0] = start;
	   resultArray[1] = end;	   
	return resultArray;
	   
   }
   
   /*
    * Se il commit di Github esiste nella lista dei commit di Jiira, ritorna il Anno/Mese in cui ne è stato fatto il commit
    * 
    */
   public static String checkEsistence(List<String> ticketsID, String ticketMessage, String date) throws ParseException {
	   if(ticketsID.contains(ticketMessage)) {
		   Date data = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date); 
           Calendar cal = Calendar.getInstance();
           cal.setTime(data);
           Integer month= cal.get(Calendar.MONTH) +1;
           Integer year = cal.get(Calendar.YEAR);
		   return String.valueOf(year) + "/" + String.format("%02d",month);
			  
	  }else {
		  return null;
	  }
   }

}