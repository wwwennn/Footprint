package db;

import java.io.File;
import java.util.Iterator;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;

/**
 * This class provides all operations on the database.
 *
 */
public class DBWrapper {
	
private static String envDirectory = null;
	
	private static Environment myEnv;
	private static EntityStore store;
	
	private static DBWrapper wrapper = null;
	
	/**
	 * Private constructor, used by static method.
	 * @param envDirectory
	 */
	private DBWrapper(String envDirectory) {
		File dir = new File(envDirectory);
		if (!dir.exists()) dir.mkdir();
		DBWrapper.envDirectory = envDirectory;
	}
	
	/**
	 * Get the singleton DBWrapper object.
	 * @param envDirectory database root.
	 * @return
	 */
	public static DBWrapper getDB(String envDirectory) {
		if (DBWrapper.wrapper == null) {
			DBWrapper.wrapper = new DBWrapper(envDirectory);
			return DBWrapper.wrapper;
		} else {
			return DBWrapper.wrapper;
		}
	}
	
	/**
	 * Setup the database.
	 */
	public void setupStore() {
		try {
			EnvironmentConfig envConfig = new EnvironmentConfig();
			StoreConfig storeConfig = new StoreConfig();
			envConfig.setAllowCreate(true);
			storeConfig.setAllowCreate(true);
			DBWrapper.myEnv = new Environment(new File(DBWrapper.envDirectory), envConfig);
			DBWrapper.store = new EntityStore(DBWrapper.myEnv, "Store", storeConfig);
		} catch (DatabaseException dbe) {
			dbe.printStackTrace();
		}
	}
	
	/**
	 * Store a User to the database.
	 * @param user the User to be stored.
	 */
	synchronized public void putUser(User user) {
		PrimaryIndex<String, User> primaryIndex = store.getPrimaryIndex(String.class, User.class);
		primaryIndex.put(user);
		store.sync();
		myEnv.sync();
	}
	
	/**
	 * Check if the database contains a specified User.
	 * @param username of the User.
	 * @return true of database contains the User.
	 */
	synchronized public boolean containsUser(String username) {
		return store.getPrimaryIndex(String.class, User.class).contains(username);
	}
	
	synchronized public String containsUser(String firstname, String lastname) {
		PrimaryIndex<String, User> pi = store.getPrimaryIndex(String.class, User.class);
		EntityCursor<User> pi_cursor = pi.entities();
		try {
			Iterator<User> i = pi_cursor.iterator();
			while(i.hasNext()) {
				User u = i.next();
				if(u.getFirstname().equals(firstname) && u.getLastname().equals(lastname)) {
					return u.getUsername();
				}
			}
		} finally {
			pi_cursor.close();
		}
		
		return null;
	}
	
	/**
	 * Get a User from the database by username.
	 * @param username username of the target User.
	 * @return the User with the specified username, or null if such User doesn't exist.
	 */
	synchronized public User getUser(String username) {
		return store.getPrimaryIndex(String.class, User.class).get(username);
	}
	
	/**
	 * Flush log into stable storage, close the EntityStore, close the Environment. Therefore close the database.
	 */
	public void closeStore() {
		if (store != null) {
			try {
				store.sync();
				store.close();
			} catch(DatabaseException dbe) {
				System.err.println("Error closing store: " + dbe.toString());
			}
		}
		if (myEnv != null) {
			try {
				myEnv.sync();
				myEnv.cleanLog();
				myEnv.close();
			} catch(DatabaseException dbe) {
				System.err.println("Error closing environment" + dbe.toString());
			}
		}
	}

}
