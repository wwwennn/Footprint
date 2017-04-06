package db;

/**
*
* @author Wen Zhong
*/

import java.io.*;
import com.sleepycat.je.*;
import com.sleepycat.persist.*;

public class DbEnv {
	private Environment env;
	private EntityStore store;
	
	public DbEnv() {
		
	}
	
	public void setup(File envHome, boolean readOnly) throws DatabaseException {
		EnvironmentConfig envConfig = new EnvironmentConfig();
		StoreConfig storeConfig = new StoreConfig();
		
		envConfig.setReadOnly(readOnly);
		storeConfig.setReadOnly(readOnly);
		
		envConfig.setAllowCreate(!readOnly);
		storeConfig.setAllowCreate(!readOnly);
		
		env = new Environment(envHome, envConfig);
		store = new EntityStore(env, "EntityStore", storeConfig);
	}
	
	public EntityStore getEntityStore() {
		return store;
	}
	
	public Environment getEnv() {
		return env;
	}
	
	public void close() {
		if(store != null) {
			try {
				store.close();
			} catch(DatabaseException dbe) {
				System.err.println("Error closing store: " + dbe.toString());
				System.exit(-1);
			}
		}
		
		if(env != null) {
			try {
				env.close();
			} catch(DatabaseException dbe) {
				System.err.println("Error closing environment: " + dbe.toString());
				System.exit(-1);
			}
		}
	}
}
