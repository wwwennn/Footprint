package servlets;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import db.DBWrapper;
import db.User;

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
		String username = (String)request.getParameter("username");
		String pwd = (String)request.getParameter("pwd");
		DBWrapper wrapper = DBWrapper.getDB(getServletContext().getInitParameter("dbroot"));
		ServletOutputStream out = response.getOutputStream();
		out.println("<html>");
		if ("/register".equals(pathInfo)) { // user tries to register
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
		} else { // user tries to log in
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
					session.setAttribute("pwd", pwd);
					response.sendRedirect("/home");
				}
			}
		}
		out.println("</html>");
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String approot = getServletContext().getInitParameter("approot");
		ServletOutputStream out = response.getOutputStream();
//		out.println("<html>");
//		out.println("<form action=\"/login\" method=\"post\">" +
//				"User Name: <input type = \"text\" name=\"username\"><br>" +
//				"Password: <input type = \"text\" name=\"password\"><br>" + 
//				"<input type = \"submit\" value = \"Login\" > </form>");
//		FileInputStream in = new FileInputStream("./webpage/index.html");
		out.println(serveLoginPage(approot));
//		out.println("</html>");
		return;
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
