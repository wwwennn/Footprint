package db;

/**
*
* @author Wen Zhong
*/
import com.sleepycat.persist.model.*;
import static com.sleepycat.persist.model.Relationship.*;

import java.util.*;


@Entity
public class User {
	@PrimaryKey
	private String username;
	
	private String lastname;
	private String firstname;
	private String pwd;
	
//	private ArrayList<String> siteNames; // the keys of the places this user has been to
	private HashSet<String> siteNames;
	
	private ArrayList<Blog> articles;
	
	public void setup(String username, String lastname, String firstname, String pwd) {
		this.username = username;
		this.lastname = lastname;
		this.firstname = firstname;
		this.pwd = pwd;
		siteNames = new HashSet<String>();
		articles = new ArrayList<Blog>();
	}
	
	public String getUsername() {
		return username;
	}
	
	
	public HashSet<String> getSiteNames() {
		return siteNames;
	}

	public void setSiteNames(HashSet<String> siteNames) {
		this.siteNames = siteNames;
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
	
	
	
	public ArrayList<Blog> getArticles() {
		return articles;
	}

	public void setArticles(ArrayList<Blog> articles) {
		this.articles = articles;
	}

	public void addPlaces(ArrayList<String> places) {
		siteNames.addAll(places);
	}
	
	public void addBlog(Blog blog) {
		articles.add(blog);
	}
	
	public void deletePlace(String place) {
		siteNames.remove(place);
	}
}
