package secondmilestone;


import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.opencsv.CSVWriter;
 
public class GetVersionInfo {

		   private static Map<LocalDateTime, String> releaseNames;
		   private static Map<LocalDateTime, String> releaseID;
		   private static ArrayList<LocalDateTime> releases;
		   static Logger logger = Logger.getLogger(GetVersionInfo.class.getName());
		   //private static double progress = 0.5; // percentuale di progresso. 0.5 indica il   50% del progresso, ovvero il primo 50% delle versioni
		   public static JSONObject getCommitFromVersionName(String fileName, String version) throws IOException, JSONException {
				String token = new String(Files.readAllBytes(Paths.get(fileName)));
		        JSONArray object = new JSONArray(token);
		        int total = object.length();
		        JSONArray commitsOfVersion = new JSONArray();     
		        int i;
		        for (i=0; i < total; i++) {
		        	if( object.getJSONObject(i).getString("Version").equals(version)) {
		        		
		        		commitsOfVersion.put(object.getJSONObject(i));
		        		return commitsOfVersion.getJSONObject(0);
		        	}
		        }
		             
				return null;
			}
		   
		   public static void writeOnFile(String projName, JSONArray releasesCommit ) throws IOException {
			   CSVWriter csvWriter = null;
				 try {
		
			            //Name of CSV for output
			            csvWriter = new CSVWriter(new FileWriter(projName + "VersionInfo.csv"),';',
			                    CSVWriter.NO_QUOTE_CHARACTER,
			                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
			                    CSVWriter.DEFAULT_LINE_END);

			            csvWriter.writeNext(new String[] {"Index","Version ID","Version Name","Date", "VersionSha"});
			            Integer index = 0;
			            for ( int i = 0; i < releases.size(); i++) {
			               String releaseSha = null;
			               
			               String versionName = releaseNames.get(releases.get(i));
			               for(int j = 0; j < releasesCommit.length(); j++) {
			            	   String releaseName = releasesCommit.getJSONObject(j).getString("name");
			            	   
			            	   if(releaseName.equals(versionName) || releaseName.equals("release-"+versionName) || releaseName.equals(versionName+"-incubating")) {
			            		   releaseSha = releasesCommit.getJSONObject(j).getJSONObject("commit").getString("sha");
			            	   }
			               }
			               if(versionName == null) {
			            	   JSONObject computedReleaseSha = getCommitFromVersionName(projName + "_Extended_Commits_Sha.JSON", versionName);
			            	   if(computedReleaseSha != null)
			            		   releaseSha = computedReleaseSha.getString("sha");
			               }
			               if(releaseSha != null) {
			            	index++;
			                csvWriter.writeNext(new String[] {index.toString(),releaseID.get(releases.get(i)),releaseNames.get(releases.get(i)),releases.get(i).toString(), releaseSha});
			               }
			               
			            }

			         } catch (Exception e) {
			          
			            logger.log(Level.INFO, "context", e);
			         } 
				 
					 if (csvWriter != null) {
						 csvWriter.close();
					 }
		   }
		   public static void main(String[] args) throws IOException, JSONException {
			   
		   String projName ="OPENJPA";
			 //Fills the arraylist with releases dates and orders them
			   //Ignores releases with missing dates
		   releases = new ArrayList<>();
	       Integer i;
	       String jiiraURL = "https://issues.apache.org/jira/rest/api/2/project/" + projName;
	       JSONObject jiiraJson = readJsonFromUrl(jiiraURL);
	       JSONArray versions = jiiraJson.getJSONArray("versions");
	       releaseNames = new HashMap<>();
	       releaseID = new HashMap<> ();
	       for (i = 0; i < versions.length(); i++ ) {
            String name = "";
            String id = "";
            if(versions.getJSONObject(i).has("releaseDate")) {
	               if (versions.getJSONObject(i).has("name"))
	                  name = versions.getJSONObject(i).get("name").toString();
	               if (versions.getJSONObject(i).has("id"))
	                  id = versions.getJSONObject(i).get("id").toString();
	            	   addRelease(versions.getJSONObject(i).get("releaseDate").toString(),
	                          name,id);
	            }
	       }
	         // order releases by date
	         Collections.sort(releases, (o1,o2) ->  o1.compareTo(o2));
	         
	         if (releases.size() < 6)
	            return;
	         
	         String githubURL = "https://api.github.com/repos/apache/"+projName+ "/tags";
	         int total = 0;
	         Integer page = 0;
	         Integer perPage = 100;
	         JSONArray releasesCommit = new JSONArray();
	         do {
	         	
	         	JSONArray jsonArray = GithubConnector.readJsonArrayFromUrl(githubURL+ "?page="+ page.toString()+"&per_page=" + perPage.toString());
	         	
	         	
	         	total = jsonArray.length();
	         
	         	for (i = 0; i< total; i++) {
	         		JSONObject commit = jsonArray.getJSONObject(i%1000);
	         		releasesCommit.put(commit);
	         	}
	             page++;
	             
	        
	         }while(total>0);
	         writeOnFile(projName, releasesCommit);		 
	       }
			 
		         
		   

	
	   public static void addRelease(String strDate, String name, String id) {
		      LocalDate date = LocalDate.parse(strDate);
		      LocalDateTime dateTime = date.atStartOfDay();
		      if (!releases.contains(dateTime))
		         releases.add(dateTime);
	         releaseNames.put(dateTime, name);
	         releaseID.put(dateTime, id);
	        
		 }


	   public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
	      InputStream is = new URL(url).openStream();
	      try (
	         BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
	         ){
	         return new JSONObject(readAll(rd));
	       } finally {
	         is.close();
	       }
	   }
	   
	   private static String readAll(Reader rd) throws IOException {
		      StringBuilder sb = new StringBuilder();
		      int cp;
		      while ((cp = rd.read()) != -1) {
		         sb.append((char) cp);
		      }
		      return sb.toString();
		   }
}
