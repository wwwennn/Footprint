package db;

import java.io.File;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;

/**
*
* @author Wen Zhong
*/

public class Test {
	private static File myDbEnvPath = new File("/Users/zhongwen/Dropbox/Spring2017/594/project/db");
	private static DataAccessor da;
	private static DbEnv myDbEnv = new DbEnv();
	
	public static void main(String[] args) {
		DbRead read = new DbRead();
		try {
			myDbEnv.setup(myDbEnvPath, true);
			da = new DataAccessor(myDbEnv.getEntityStore());
			EntityCursor<Place> places = da.placeBySiteName.entities();
			for(Place p : places) {
				if(p.getSiteName().toLowerCase().contains("park"))
					System.out.println(p.getSiteName() + ": latitude: " + p.getLat() + " longitude: " + p.getLon());
			}
			places.close();
		} catch(DatabaseException dbe) {
			System.err.println("ExampleInventoryRead: " + dbe.toString());
	        dbe.printStackTrace();
		} finally {
			myDbEnv.close();
		}
		System.out.println("All done.");
	}
}
