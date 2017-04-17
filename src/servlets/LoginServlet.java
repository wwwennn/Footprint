package servlets;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String username = (String)request.getParameter("username");
		String password = (String)request.getParameter("password");
		HttpSession session = request.getSession();
		session.setAttribute("username", username);
		session.setAttribute("password", password);
		response.sendRedirect("/home");
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
