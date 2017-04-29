package servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import db.DBWrapper;
import db.User;
import intermediate.FacebookUser;
import intermediate.HtmlDealer;

@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {
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
		String pathInfo = request.getPathInfo();
		
		
		if ("/register".equals(pathInfo)) { // user tries to register
			register(wrapper, request, response);
		} else if("/facebook".equals(pathInfo)) {
			facebook(wrapper, request, response);
		} else {
			login(wrapper, request, response);
		}
	}
	
	private void facebook(DBWrapper wrapper, HttpServletRequest request, HttpServletResponse response) throws IOException {
		System.out.println("enter: facebook");
		String json = request.getParameter("info");
		if(json != null) {
			FacebookUser fbUser = parseJson(json);
			if(fbUser != null) {
				String firstname = fbUser.getUser().substring(0, fbUser.getUser().lastIndexOf("_"));
				String lastname = fbUser.getUser().substring(fbUser.getUser().lastIndexOf("_") + 1);
				String username = wrapper.containsUser(firstname, lastname);
				if(username != null) {
					System.out.println("-------------------------");
					System.out.println("the user is in our database");
					System.out.println("-------------------------");
					HttpSession session = request.getSession();
					session.setAttribute("username", username);
					session.setAttribute("firstname", firstname);
					session.setAttribute("lastname", lastname);
					session.setAttribute("friends", fbUser.getFriends());
					System.out.println("Being redirect to home");
					response.sendRedirect("/home");
				} else {
					response.sendRedirect("/home");
					System.out.println("the user is not in our database");
				}
			}
		}
	}

	private void login(DBWrapper wrapper, HttpServletRequest request, HttpServletResponse response) throws IOException {
		ServletOutputStream out = response.getOutputStream();
		String username = (String)request.getParameter("username");
		String pwd = (String)request.getParameter("pwd");
		String approot = getServletContext().getInitParameter("approot");
		if (!wrapper.containsUser(username)) { // user not registered yet
			String added = "<form action=\"/login\" method=\"get\">" + 
					"<input type = \"submit\" class=\"btn btn-default\" value = \"You are not registered yet! Click to register\" > </form>";
			String webPage = dealer.serveErrorPage(approot + "/webpage/error.html", added);
			out.println(webPage);
		} else { // existing user trying to log in
			User storedUser = wrapper.getUser(username);
			if (!pwd.equals(storedUser.getPwd())) { // password wrong
				String added = "<form action=\"/login\" method=\"get\">" + 
						"<input type = \"submit\" class=\"btn btn-default\" value = \"Wrong password! Click to try again\" > </form>";
				String webPage = dealer.serveErrorPage(approot + "/webpage/error.html", added);
				out.println(webPage);
			} else { // password correct
				HttpSession session = request.getSession();
				session.setAttribute("username", username);
				session.setAttribute("firstname", storedUser.getFirstname());
				session.setAttribute("lastname", storedUser.getLastname());
				session.setAttribute("pwd", pwd);
				response.sendRedirect("/home");
			}
		}
	}
	
	private void register(DBWrapper wrapper, HttpServletRequest request, HttpServletResponse response) throws IOException {
		ServletOutputStream out = response.getOutputStream();
		String approot = getServletContext().getInitParameter("approot");
		String username = (String)request.getParameter("username");
		String pwd = (String)request.getParameter("pwd");
		if (wrapper.containsUser(username)) { // already registered
			String added = "<form action=\"/login\" method=\"get\">" + 
					"<input type = \"submit\" class=\"btn btn-default\" value = \"Username is taken, try another one.\" > </form>";
			String webPage = dealer.serveErrorPage(approot + "/webpage/error.html", added);
			out.println(webPage);
		} else {
			String lastname = request.getParameter("last");
			String firstname = request.getParameter("first");
			User user = new User();
			user.setup(username, lastname, firstname, pwd);
			wrapper.putUser(user);
			String added = "<form action=\"/login?username=" + username + "&pwd=" + pwd + "\" method=\"post\">" + 
					"<input type = \"submit\" class=\"btn btn-default\" value = \"Welcome! Click to continue\" > </form>";
			String webPage = dealer.serveErrorPage(approot + "/webpage/error.html", added);
			out.println(webPage);
		}
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String approot = getServletContext().getInitParameter("approot");
		dealer = new HtmlDealer(approot + "/webpage/index.html");
		ServletOutputStream out = response.getOutputStream();
		out.println(dealer.getWebPage());
	}
	
	private FacebookUser parseJson(String json) {
		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(json);
			FacebookUser fbUser = new FacebookUser();
			if(obj != null) {
				JSONObject obj1 = (JSONObject)obj;
				if(obj1 != null) {
					JSONArray friends = (JSONArray)obj1.get("friends");
					ArrayList<String> userFriends = new ArrayList<String>();
					Iterator<String> iterator = friends.iterator();
					while(iterator.hasNext()) {
						userFriends.add(iterator.next());
					}
					fbUser.setFriends(userFriends);
					fbUser.setUser((String)obj1.get("user"));
					return fbUser;
				}
				
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
