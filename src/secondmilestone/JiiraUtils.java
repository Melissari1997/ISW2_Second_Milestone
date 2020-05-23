package secondmilestone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JiiraUtils {
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
}
