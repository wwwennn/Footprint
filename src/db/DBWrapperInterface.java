package db;

import java.util.ArrayList;
import java.util.HashSet;

public interface DBWrapperInterface {
	
	/**
	 * Store a user in the database.
	 * @param user the user to be stored.
	 */
	public void putUser(User user);
	
	
	/**
	 * Setup database environment and entity store.
	 */
	public void setupStore();
	
	
	/**
	 * Store a site into the database.
	 * @param siteName site name.
	 * @param latitude site latitude.
	 * @param longitude site longitude.
	 * @throws Exception
	 */
	public void putPlace(String siteName, double latitude, double longitude) throws Exception;
	
	
	/**
	 * Find all sites whose name contains the search key.
	 * @param searchKey search key word.
	 * @return a list of sites.
	 */
	public ArrayList<String> getSearchPlaces(String searchKey);
	
	
	/**
	 * Given a user, get all sites that this user has marked.
	 * @param username user name.
	 * @return all sites that this user has marked.
	 */
	public HashSet<String> getUserPlaces(String username);
	
	
	/**
	 * Check if database contains a user.
	 * @param username the name of the user.
	 * @return true if database contains the specified user, false otherwise.
	 */
	public boolean containsUser(String username);
	
	
	/**
	 * Add a list of sites to a user.
	 * @param username user name,e
	 * @param places to be added to the user.
	 */
	public void addPlacesToUser(String username, ArrayList<String> places);
	
	
	/**
	 * Check if database contains a user.
	 * @param firstname user's first name.
	 * @param lastname user' last name.
	 * @return true if database contains this user, false otherwise.
	 */
	public String containsUser(String firstname, String lastname);
	
	
	/**
	 * Given a user name, get the user.
	 * @param username user name.
	 * @return the user object.
	 */
	public User getUser(String username);
	
	
	/**
	 * Delete a site that is marked by a user.r
	 * @param username user name.
	 * @param placename site name.
	 */
	public void deletePlaceFromUser(String username, String placename);
	
	
	/**
	 * Given a site name, get the site.
	 * @param placename site name.
	 * @return the site object.
	 */
	public Place getPlace(String placename);
	
	
	/**
	 * Add a blog for the specified user.
	 * @param username user name.
	 * @param blog the blog to be added to the user.
	 */
	public void addBlogToUser(String username, Blog blog);
	
	
	/**
	 * Get all blogs that belong to the specified user.
	 * @param username user name.
	 * @return a list of blogs.
	 */
	public ArrayList<Blog> getUserBlogs(String username);
	
	
	/**
	 * Flush buffer, write data to disk, release resources, close database.
	 */
	public void closeStore();

}
