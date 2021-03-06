package secondmilestone;


import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
  
public class GetTicketInfo {
	   private static double p= 0;
	   private static double totalP =0;
	   private static int count = 1;
	   private static String formatDate = "yyyy-MM-dd";
	   private static String fieldsStr = "fields";
	   private static String fixVersionsStr = "fixVersions";
	   private static String releaseDateStr = "releaseDate";
	   private static String affectedVersions = "versions";
	   
	  
      public static String proportion(String fixedVersion, String openingVersion){
  	      int injectedVersion =  (int) (Double.valueOf(fixedVersion) -((Double.valueOf(fixedVersion)-Double.valueOf(openingVersion))*p));
  	      
  	      if (injectedVersion <= 0)
  	    	  injectedVersion = 1;
  	      return String.valueOf(injectedVersion);
      }
      public static void computeP(int fixedVersion, int openingVersion, int injectedVersion) {
    	  Integer numerator = Integer.valueOf(fixedVersion)-Integer.valueOf(injectedVersion);
    	  Integer denominator = Integer.valueOf(fixedVersion)-Integer.valueOf(openingVersion);
    	  double newP = 0.0;
    	  if(denominator != 0) {
    		  newP = numerator/denominator;
    	  }
       	  totalP += newP;
       	  count++;
       	  p = totalP/count;

      }
      
      public static void addAffectedVersions(String injectedVersion, String fixedVersion, JSONArray affectedVersion) throws JSONException {
    	  for(int i = Integer.parseInt(injectedVersion); i < Integer.valueOf(fixedVersion); i++) {
    		  JSONObject affectedV = new JSONObject();
    		  affectedV.put("name", String.valueOf(i));
    		  affectedVersion.put(affectedV);
    	  }
      }
      
      public static boolean checkIv(String fixedVersion, String openingVersion, String injectedVersion) {
    	  return (fixedVersion != null && openingVersion!= null && injectedVersion != null && Integer.valueOf(fixedVersion)-Integer.valueOf(openingVersion) >= 1 && Integer.valueOf(openingVersion)-Integer.valueOf(injectedVersion) >= 0);
    	  
      }
      public static JSONObject computeInjectedVersion(JSONObject key, String projName) throws ParseException, JSONException, IOException {
    	   JSONArray affectedVersion = key.getJSONObject(fieldsStr).getJSONArray(affectedVersions);
    	   Date iv = null;
		   List<Date> affectedVersionList =  new ArrayList<>(); //lista delle AV trovate quando ho risolto un bug
		   String injectedVersion = null;
		   VersionParser vp = new VersionParser();
	  	   vp.setProgress(1.0);
	  	   Date fixedDate = null;
	       String fixedVersion = null;
	       
	       if(key.getJSONObject(fieldsStr).getJSONArray(fixVersionsStr).getJSONObject(0).has(releaseDateStr)) {
	  	     fixedDate = new SimpleDateFormat(formatDate).parse(key.getJSONObject(fieldsStr).getJSONArray(fixVersionsStr).getJSONObject(0).getString(releaseDateStr));
	  	     fixedVersion = vp.getVersionName(fixedDate, projName);
	       }else {
	      		fixedVersion = (key.getJSONObject(fieldsStr).getJSONArray(fixVersionsStr).getJSONObject(0).getString("name"));
	      	}
	        
	  	   Date openingDate = new SimpleDateFormat(formatDate).parse(key.getJSONObject(fieldsStr).getString("created"));
	  	   String openingVersion = vp.getVersionName(openingDate, projName);
		   
      	   for(int index = 0; index< affectedVersion.length();index++) {
      		if(affectedVersion.getJSONObject(index).has(releaseDateStr))
      			affectedVersionList.add(new SimpleDateFormat(formatDate).parse(affectedVersion.getJSONObject(index).getString(releaseDateStr)));  
      	   }     	
      	   if(!affectedVersionList.isEmpty()) {
      		iv = Collections.min(affectedVersionList, (o1,o2) ->  o1.compareTo(o2));	
      		injectedVersion = vp.getVersionName(iv, projName);
      		if(iv.compareTo(new SimpleDateFormat(formatDate).parse(key.getJSONObject(fieldsStr).getString("created"))) > 0 || openingVersion == null){
      			return null;
      	    }
      		computeP(Integer.valueOf(fixedVersion), Integer.valueOf(openingVersion), Integer.valueOf(injectedVersion));
      		key.getJSONObject(fieldsStr).put("injectedversion", injectedVersion);
      		return key;
      	   }
	  	    injectedVersion = proportion(fixedVersion,openingVersion);
	  	    boolean checkIv = checkIv(fixedVersion, openingVersion,injectedVersion);
	  	    if(!checkIv) {
	  	    	return null;
	  	    }
	      	if(affectedVersionList.isEmpty()) {
	      		computeP(Integer.valueOf(fixedVersion), Integer.valueOf(openingVersion), Integer.valueOf(injectedVersion));
	      	}
	      	key.getJSONObject(fieldsStr).put("injectedversion", injectedVersion); 
	      	
	      	addAffectedVersions(injectedVersion,fixedVersion, affectedVersion);
	        return key;
      }
      
      public static JSONArray takeOnlyLastFix(JSONArray fixVersions) throws ParseException, JSONException {
    	  List<Date> fixVersionsList = new ArrayList<>();
    	  JSONArray result = new JSONArray();
    	  for(int i = 0; i< fixVersions.length(); i++) {
    		  fixVersionsList.add(new SimpleDateFormat(formatDate).parse(fixVersions.getJSONObject(i).getString(releaseDateStr))); 
    	  }
    	  Date fixVersion = Collections.max(fixVersionsList, (o1,o2) ->  o1.compareTo(o2));
    	  for(int i = 0; i< fixVersions.length(); i++) {
    		  if(fixVersion.compareTo(new SimpleDateFormat(formatDate).parse(fixVersions.getJSONObject(i).getString(releaseDateStr))) == 0){
    			  return result.put(fixVersions.getJSONObject(i));
    		  }
    	  }
		return null;
      }
      
     public static JSONArray deleteNonReleasedVersions(JSONArray versions) throws JSONException, ParseException, IOException {
    	 JSONArray result = new JSONArray();
    	 if(versions.length() >0) {
    		 for(int i = 0; i< versions.length();i++) {
    			 if(versions.getJSONObject(i).has("released") && versions.getJSONObject(i).getBoolean("released")) {
        			 result.put(versions.getJSONObject(i));
    			 }
        	 }
    	 }
    	 return result;
     }
     
     public static void removeOldFixVersion(JSONObject key)throws JSONException, ParseException {
    	 if(key.getJSONObject(fieldsStr).getJSONArray(fixVersionsStr).length()>1) {
         	JSONArray lastFix = takeOnlyLastFix(key.getJSONObject(fieldsStr).getJSONArray(fixVersionsStr));
         	key.getJSONObject(fieldsStr).remove(fixVersionsStr);
         	key.getJSONObject(fieldsStr).put(fixVersionsStr, lastFix);
         }
     }
     public static Date getFixVersion(String commitMessage, String projName) throws IOException, JSONException, ParseException {
    	 String token = new String(Files.readAllBytes(Paths.get(projName + "_Extended_Commits_Sha.JSON")));
         JSONArray object = new JSONArray(token);
         for(int i = 0; i< object.length(); i++) {
        	 
        	 if(object.getJSONObject(i).getString("FixCommit").equals(commitMessage)) {
        		 return new SimpleDateFormat(formatDate).parse(object.getJSONObject(i).getJSONObject("commit").getJSONObject("committer").getString("date"));
        	 }
         }
         return null;
     }
      
	public static JSONArray getTicketID(String projName) throws IOException, JSONException, ParseException{
		 
		  Integer j = 0;
		  Integer i = 0; 
		  Integer total = 1;
		  VersionParser vp = new VersionParser();
		  JSONArray ticketList = new JSONArray();
	     do {
	         j = i + 1000;
	         String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project%20%3D%20"+projName+"%20AND%20issuetype%20%3D%20Bug%20AND%20status%20in%20(Resolved,%20Closed)%20AND%20resolution%20=%20Fixed%20&&fields=resolutiondate,fixVersions,versions,created&startAt="+ 
		         		i.toString() + "&maxResults=" + j.toString();
	         JSONObject json = JiiraUtils.readJsonFromUrl(url);
	         JSONArray issues = json.getJSONArray("issues");
	         total = json.getInt("total");
	         for (; i < total && i < j; i++) {
	            //Iterate through each bug
	        	 
	            JSONObject key = issues.getJSONObject(i%1000);
	            
	            JSONArray fixedV = deleteNonReleasedVersions(key.getJSONObject(fieldsStr).getJSONArray(fixVersionsStr));
	            key.getJSONObject(fieldsStr).remove(fixVersionsStr);
	            key.getJSONObject(fieldsStr).put(fixVersionsStr, fixedV);
	            JSONArray fixedAv = deleteNonReleasedVersions(key.getJSONObject(fieldsStr).getJSONArray(affectedVersions));
	            key.getJSONObject(fieldsStr).remove(affectedVersions);
	            key.getJSONObject(fieldsStr).put(affectedVersions, fixedAv);
	            String versionName = null;
            	
	            if(key.getJSONObject(fieldsStr).getJSONArray(fixVersionsStr).length() == 0) {
	            	
	            	JSONObject jsonresolutionDate = new JSONObject();
	            	Date resolutionDate = getFixVersion(key.getString("key"), projName);
	            	versionName = vp.getVersionName(resolutionDate, projName);
	            	
	            	if(versionName == null) {
	            		continue; // non considero date di ticket creati nel periodo di una versione recente non ancora rilasciata
	            	}
	            	
	            	jsonresolutionDate.put("name", versionName);
	            	key.getJSONObject(fieldsStr).getJSONArray(fixVersionsStr).put(jsonresolutionDate);
	            } 
	            
	            removeOldFixVersion(key);
	            putInTicketList(key,ticketList,projName);
	         } 
	         }while(i<total);
	     
	      return ticketList;
	   }
	public static void putInTicketList(JSONObject key,JSONArray ticketList, String projName) throws ParseException, JSONException, IOException {
		if(computeInjectedVersion(key, projName) != null) {
			ticketList.put(key);
    	}
	}
	public static void main(String[] args) throws IOException, JSONException, ParseException {
		String projName = "OPENJPA";
		FileWriter file = new FileWriter(projName +"_TicketInfo.JSON");
	    JSONArray resultJSONArray = getTicketID(projName);
	      try {
	    		  file.write(resultJSONArray.toString());
	              file.flush();
	      }catch(Exception e){
	    	 e.printStackTrace();
	      }finally{
	    	     file.close();
	      }

	}

}
