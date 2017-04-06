package db;

/**
*
* @author Wen Zhong
*/

import com.sleepycat.persist.model.*;
import static com.sleepycat.persist.model.Relationship.*;

@Entity
public class Place {
	@PrimaryKey
	private String siteName;
	
	private double lat;
	private double lon;
	
	
	public String getSiteName() {
		return siteName;
	}
	
	
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	
	
	public double getLat() {
		return lat;
	}
	
	
	public void setLat(double lat) {
		this.lat = lat;
	}
	
	
	public double getLon() {
		return lon;
	}
	
	
	public void setLon(double lon) {
		this.lon = lon;
	}	
}
