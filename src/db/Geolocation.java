package db;

/**
*
* @author Wen Zhong
*/

import java.io.*;
import java.net.*;
import java.util.*;
import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Geolocation {
	private static DbPut dbPut; 
	
	public static void main(String[] args) {
//		System.out.println("Start...");
		ArrayList<String> siteNames = getSitenames();
//		System.out.println(siteNames.size());
//		System.out.println("Step1...");
		dbPut = new DbPut();
//		System.out.println(siteNames);
		try {
//			System.out.println("Step2...");
			getGeolocation(siteNames);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void getGeolocation(ArrayList<String> siteNames) throws Exception {
		String USER_AGENT = "Mozilla/5.0";
		for(int i = 0; i < siteNames.size(); i++) {
			System.out.println(siteNames.get(i));
			String[] parts = siteNames.get(i).split(" ");
			StringBuilder place = new StringBuilder();
			for(int j = 0; j < parts.length; j++) {
				place.append(parts[j] + "+");
			}
			place.deleteCharAt(place.length() - 1);
			String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + place.toString() + ",+Philadelphia,+PA&key=AIzaSyDQpPuKYTaXyE4FF6aMS6FFC6LFysc2JzI";
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection)obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User_Agent", USER_AGENT);
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while((inputLine = in.readLine()) != null) {
				response.append(inputLine.trim());
			}
			in.close();
//			System.out.println(response.toString());
			JSONObject info = parseJson(response.toString());
			if(info != null) {
				double latitude = (Double)info.get("lat");
				double longitude = (Double)info.get("lng");
	//			System.out.println("latitude: " + latitude);
	//			System.out.println("longitude: " + longitude);
				dbPut.insertPlace(siteNames.get(i), latitude, longitude);
			}
		}
	}

	private static JSONObject parseJson(String json) {
		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(json);
			if(obj != null) {
				JSONObject obj1 = (JSONObject)obj;
				if(obj1 != null) {
					JSONArray results = (JSONArray)obj1.get("results");
					if(results.size() != 0) {
						JSONObject resObj = (JSONObject)results.get(0);
						JSONObject geometry = (JSONObject)resObj.get("geometry");
						JSONObject location = (JSONObject)geometry.get("location");
						return location;
					}
				}
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	private static ArrayList<String> getSitenames() {
		ArrayList<String> res = new ArrayList<>();
		try {
			Scanner in = new Scanner(new File("/Users/zhongwen/Dropbox/Spring2017/594/project/data/PPR_Assets.csv"));
			
			while(in.hasNextLine()) {
				res.add(in.nextLine());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return res;
	}
}
