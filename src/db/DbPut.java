package db;

/**
*
* @author Wen Zhong
*/

import java.io.*;
import java.util.*;
import com.sleepycat.je.DatabaseException;;

public class DbPut {
	
//	private String envPath;
//	private String placeFile;
	private DataAccessor da;
	private DbEnv env;
	
	public DbPut() {
		env = new DbEnv();
		env.setup(new File("/Users/zhongwen/Dropbox/Spring2017/594/project/db"), false);
		da = new DataAccessor(env.getEntityStore());
	}
	
	public void insertPlace(String siteName, double latitude, double longitude) throws Exception {
		Place newPlace = new Place();
		newPlace.setSiteName(siteName);
		newPlace.setLat(latitude);
		newPlace.setLon(longitude);
		
		da.placeBySiteName.put(newPlace);
		env.flushBuffer();
		System.out.println("put a location: " + newPlace.getSiteName());
	}
	
	public void insertUser(String username, String firstname, String lastname, String pwd) throws Exception {
		User newUser = new User();
		newUser.setUsername(username);
		newUser.setFirstname(firstname);
		newUser.setLastname(lastname);
		newUser.setPwd(pwd);
		
		da.userByUsername.put(newUser);
		env.flushBuffer();
	}
	
	public void insertFootprint(String username, String siteName) throws Exception {
		Footprint newFP = new Footprint();
		newFP.setSiteName(siteName);
		newFP.setUsername(username);
		
		da.footprintPInd.put(newFP);
		env.flushBuffer();
	}
	
	public void closeDB() {
		env.close();
	}
}
