package db;

/**
*
* @author Wen Zhong
*/

import java.io.*;
import com.sleepycat.je.*;
import com.sleepycat.persist.*;

public class DataAccessor {
	public DataAccessor(EntityStore store) throws DatabaseException {
		placeBySiteName = store.getPrimaryIndex(String.class, Place.class);
		
		userByUsername = store.getPrimaryIndex(String.class, User.class);
		
		footprintPInd = store.getPrimaryIndex(Long.class, Footprint.class);
		footprintSInd = store.getSecondaryIndex(footprintPInd, String.class, "username");
	}
	
	// Place Accessors
	PrimaryIndex<String, Place> placeBySiteName;
	
	// User Accessors
	PrimaryIndex<String, User> userByUsername;
	
	// Footprint Accessors
	PrimaryIndex<Long, Footprint> footprintPInd;
	SecondaryIndex<String, Long, Footprint> footprintSInd;
}
