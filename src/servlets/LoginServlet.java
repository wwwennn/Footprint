package servlets;

import java.io.BufferedReader;
import java.io.FileReader;
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

@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {
	
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
		String pathInfo = request.getPathInfo();
		String pwd = (String)request.getParameter("pwd");
		DBWrapper wrapper = DBWrapper.getDB(getServletContext().getInitParameter("dbroot"));
		ServletOutputStream out = response.getOutputStream();
//		System.out.println("path:" + pathInfo);
//		System.out.println(request);
//		System.out.println("Get 'post' a request from login");
		
		out.println("<html>");
		
//		if("/Wen_Zhong".equals(pathInfo)) {
//			System.out.println(request.getParameter("name"));
//		}
		
		if ("/register".equals(pathInfo)) { // user tries to register
			String username = (String)request.getParameter("username");
			System.out.println("enter: register");
			if (wrapper.containsUser(username)) { // already registered
				out.println("<form action=\"/login\" method=\"get\">" + 
						"<input type = \"submit\" value = \"You have already registered! Click yo login\" > </form>");
			} else {
				String lastname = request.getParameter("last");
				String firstname = request.getParameter("first");
				User user = new User();
				user.setup(username, lastname, firstname, pwd);
				wrapper.putUser(user);
				out.println("<form action=\"/login?username=" + username + "&pwd=" + pwd + "\" method=\"post\">" + 
						"<input type = \"submit\" value = \"Welcome! Click to continue\" > </form>");
			}
		} else if("/facebook".equals(pathInfo)) {
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
		} else { // user tries to log in
			System.out.println("enter: the other");
			String username = (String)request.getParameter("username");
			if (!wrapper.containsUser(username)) { // user not registered yet
				out.println("<form action=\"/login\" method=\"get\">" + 
						"<input type = \"submit\" value = \"You are not registered yet! Click to register\" > </form>");
			} else { // existing user trying to log in
				User storedUser = wrapper.getUser(username);
				if (!pwd.equals(storedUser.getPwd())) { // password wrong
					out.println("<form action=\"/login\" method=\"get\">" + 
							"<input type = \"submit\" value = \"Wrong password! Click to try again\" > </form>");
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
		out.println("</html>");
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String approot = getServletContext().getInitParameter("approot");
//		String pathInfo = request.getPathInfo();
//		DBWrapper wrapper = DBWrapper.getDB(getServletContext().getInitParameter("dbroot"));
		ServletOutputStream out = response.getOutputStream();
//		System.out.println("Get 'get' a request from login");
//		System.out.println("******************************");
//		System.out.println(pathInfo);
//		System.out.println("******************************");
//		
		out.println(serveLoginPage(approot));
//		
//		return;
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
	
	private String serveLoginPage(String approot) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(approot + "/webpage/index.html"));
		StringBuilder builder = new StringBuilder();
		String line = in.readLine();
		while (line != null) {
			builder.append(line);
			line = in.readLine();
		}
		in.close();
		return builder.toString();
	}

}
