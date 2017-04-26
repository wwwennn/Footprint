package servlets;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import db.DBWrapper;

/**
 * This is the landing page.
 *
 */
@SuppressWarnings("serial")
public class HomeServlet extends HttpServlet {
	
	@Override
	public void init() throws ServletException {
		super.init();
		// get the singleton DBWrapper
		DBWrapper wrapper = DBWrapper.getDB(getServletContext().getInitParameter("dbroot"));
		wrapper.setupStore();
	}
	
	@Override
	public void destroy() {
		super.destroy();
		// close DBWrapper
		DBWrapper wrapper = DBWrapper.getDB(getServletContext().getInitParameter("dbroot"));
		wrapper.closeStore();
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String pathinfo = request.getPathInfo();
		DBWrapper wrapper = DBWrapper.getDB(getServletContext().getInitParameter("dbroot"));

		if ("/logout".equals(pathinfo)) {
			request.getSession().invalidate();
			response.sendRedirect("/home");
			return;
		}
		
		if("/search".equals(pathinfo)) {
			String searchKey = request.getParameter("searchKey");
			System.out.println("The search key is: " + searchKey);
			
			if(wrapper != null) { 
				ArrayList<String> siteNames = wrapper.getSearchPlaces(searchKey.toLowerCase());
				// TODO get the result of search
				System.out.println(siteNames);
			}
		}
		
		HttpSession session = request.getSession(false);

		if (session != null) { // user logged in
			System.out.println("Session is not null");
			String approot = getServletContext().getInitParameter("approot");
			String username = (String)session.getAttribute("username");
			String firstname = (String)session.getAttribute("firstname");
			ServletOutputStream out = response.getOutputStream();
			System.out.println("approot:" + approot);
			StringBuilder webPage = serveHomePage(approot);
			webPage = addUserName(webPage, firstname);
			if(session.getAttribute("friends") != null) {
				ArrayList<String> friends = (ArrayList<String>)session.getAttribute("friends");
				webPage = addFriends(webPage, friends);
			}
			out.println(webPage.toString());
		} else { // user not logged in
			response.sendRedirect("/login"); // send redirect to LoginServlet
		}
		
	}
	
	/**
	 * add user's friends to the html string
	 * @param webPage the whole web page
	 * @param friends the friends' list
	 * @return a html string with user's friends' names
	 */
	private StringBuilder addFriends(StringBuilder webPage, ArrayList<String> friends) {
		int startIndex = webPage.indexOf("Mark");
		for(int i = 0; i < friends.size(); i++) {
			webPage.replace(startIndex, startIndex + 4, "<p>" + friends.get(i) + "</p>");
		}
		
		return webPage;
	}
	
	/**
	 * add the user's name to the returned html file
	 * @param webPage the whole web page
	 * @param name user's first name
	 * @return a html string with user's first name
	 */
	private StringBuilder addUserName(StringBuilder webPage, String name) {
		int index = webPage.indexOf("Welcome");
		webPage.insert(index + 7, ", " + name);
		return webPage;
	}
	
	/**
	 * read home.html
	 * @param approot: file path
	 * @return a string which contains the file
	 * @throws IOException
	 */
	private StringBuilder serveHomePage(String approot) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(approot + "/webpage/home.html"));
		StringBuilder builder = new StringBuilder();
		String line = in.readLine();
		while (line != null) {
			builder.append(line);
			line = in.readLine();
		}
		in.close();
		return builder;
	}

}
