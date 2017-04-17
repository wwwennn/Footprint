package servlets;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * This is the landing page.
 *
 */
@SuppressWarnings("serial")
public class HomeServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String pathinfo = request.getPathInfo();
		if ("/logout".equals(pathinfo)) {
			request.getSession().invalidate();
			response.sendRedirect("/home");
			return;
		}
		HttpSession session = request.getSession(false);
		if (session != null) { // user logged in
			String approot = getServletContext().getInitParameter("approot");
			String username = (String)session.getAttribute("username");
			ServletOutputStream out = response.getOutputStream();
			out.println("<html>");
			out.println(username + " logged in now!");
			
			// logout option
			out.println("<form action=\"/home/logout\" method=\"get\">" + 
					"<input type = \"submit\" value = \"Logout\" > </form>");
			out.println("</html>");
			out.println(serveHomePage(approot));
		} else { // user not logged in
			response.sendRedirect("/login"); // send redirect to LoginServlet
		}
	}
	
	private String serveHomePage(String approot) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(approot + "/webpage/home.html"));
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
