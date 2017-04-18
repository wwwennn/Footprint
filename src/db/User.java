package db;

/**
*
* @author Wen Zhong
*/
import com.sleepycat.persist.model.*;
import static com.sleepycat.persist.model.Relationship.*;

@Entity
public class User {
	@PrimaryKey
	private String username;
	
	private String lastname;
	private String firstname;
	private String pwd;
	
	public void setup(String username, String lastname, String firstname, String pwd) {
		this.username = username;
		this.lastname = lastname;
		this.firstname = firstname;
		this.pwd = pwd;
	}
	
	public String getUsername() {
		return username;
	}
	
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	
	public String getLastname() {
		return lastname;
	}
	
	
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
	
	public String getFirstname() {
		return firstname;
	}
	
	
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	
	
	public String getPwd() {
		return pwd;
	}
	
	
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	
}
