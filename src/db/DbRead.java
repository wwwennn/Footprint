package db;

/**
*
* @author Wen Zhong
*/

import java.io.*;
import com.sleepycat.je.*;
import com.sleepycat.persist.*;
import java.util.*;

public class DbRead {
	private DataAccessor da;
	private DbEnv dbEnv;
	
	public DbRead() {
		dbEnv = new DbEnv();
		dbEnv.setup(new File("/Users/zhongwen/Dropbox/Spring2017/594/project/db"), false);
		da = new DataAccessor(dbEnv.getEntityStore());
	}
	
	public User getUserInfo(String username) throws DatabaseException {
		return da.userByUsername.get(username);
	}
	
	public Place getPlaceInfo(String siteName) throws DatabaseException {
		return da.placeBySiteName.get(siteName);
	}
	
	public List<Footprint> getFootprint(String username) throws DatabaseException {
		EntityCursor<Footprint> fps = da.footprintSInd.entities();
		List<Footprint> res = new ArrayList<>();
		
		for(Footprint f : fps) {
			if(f.getUsername().equals(username)) {
				res.add(f);
			}
		}
		
		return res;
	}
}
