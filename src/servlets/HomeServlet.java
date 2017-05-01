package servlets;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;

import db.DBWrapper;
import intermediate.FacebookUser;
import intermediate.HtmlDealer;

/**
 * This is the landing page.
 *
 */
@SuppressWarnings("serial")
public class HomeServlet extends HttpServlet {
	ArrayList<String> items = new ArrayList<String>();
	StringBuilder webPage = new StringBuilder();
	HtmlDealer dealer;
	
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
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		DBWrapper wrapper = DBWrapper.getDB(getServletContext().getInitParameter("dbroot"));
		String pathinfo = request.getPathInfo();
		
		HttpSession session = request.getSession(false);
		if("/add".equals(pathinfo)) {
			String json = request.getParameter("items");
			ArrayList<String> itemsToBeAdded = parseJson(json);
			ArrayList<String> newItems = new ArrayList<String>();
			for(int i = 0; i < itemsToBeAdded.size(); i++) {
				String temp = itemsToBeAdded.get(i);
				temp = temp.replace('-', ' ');
				newItems.add(temp.trim());
			}
			
			String username = (String)session.getAttribute("username");
			wrapper.addPlacesToUser(username, newItems);
			HashSet<String> allPlaces = wrapper.getUserPlaces(username);
			// return newItems back to ajax
			response.setContentType("application/json");
			new Gson().toJson(allPlaces, response.getWriter());
			return;
		}
		
		if("/search".equals(pathinfo)) {
			String searchKey = request.getParameter("searchKey");
			ArrayList<String> siteNames = wrapper.getSearchPlaces(searchKey.toLowerCase());
			System.out.println("size is " + siteNames.size());
			response.setContentType("application/json");
			new Gson().toJson(siteNames, response.getWriter());
			return;
		}
		
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		DBWrapper wrapper = DBWrapper.getDB(getServletContext().getInitParameter("dbroot"));
		String pathinfo = request.getPathInfo();
		
		
		String approot = getServletContext().getInitParameter("approot");
		dealer = new HtmlDealer(approot + "/webpage/home.html");
		if ("/logout".equals(pathinfo)) {
			request.getSession().invalidate();
			response.sendRedirect("/home");
			return;
		}
		
		
		HttpSession session = request.getSession(false);

		if (session != null) { // user logged in
			String firstname = (String)session.getAttribute("firstname");
			String username = (String)session.getAttribute("username");
			ServletOutputStream out = response.getOutputStream();
			HashSet<String> visitedPlaces = wrapper.getUserPlaces(username);

			if(visitedPlaces != null) {
				webPage = dealer.serveHomePageWithName(approot + "/webpage/home.html", firstname, visitedPlaces);
			}
			if(session.getAttribute("friends") != null) {
				ArrayList<String> friends = (ArrayList<String>)session.getAttribute("friends");
				webPage = dealer.addFriends(webPage, friends);
			}
			out.println(webPage.toString());
		} else { // user not logged in
			response.sendRedirect("/login"); // send redirect to LoginServlet
		}
		
	}
	
	public void doDelete(HttpServletRequest request, HttpServletResponse response) {
		String item = request.getPathInfo().substring(1).replace('-', ' ');
		items.add(item);
	}
	
	private ArrayList<String> parseJson(String json) {
		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(json);
			if(obj != null) {
				JSONArray items = (JSONArray)obj;
				if(items != null) {
					ArrayList<String> res = new ArrayList<String>();
					Iterator<String> iterator = items.iterator();
					while(iterator.hasNext()) {
						res.add(iterator.next());
					}
					return res;
				}
				
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}	

}
