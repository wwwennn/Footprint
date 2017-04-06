package db;

/**
*
* @author Wen Zhong
*/
import com.sleepycat.persist.model.*;
import static com.sleepycat.persist.model.Relationship.*;

@Entity
public class Footprint {
	@PrimaryKey(sequence="footPrintPKey")
	private long autoPrimaryKey;
	
	// Not sure about it
	@SecondaryKey(relate=MANY_TO_ONE)
	private String username;
	
	private String siteName;

	public long getAutoPrimaryKey() {
		return autoPrimaryKey;
	}

	public void setAutoPrimaryKey(long autoPrimaryKey) {
		this.autoPrimaryKey = autoPrimaryKey;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	
}
