package servlets;

import java.io.IOException;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import db.DBWrapper;
import intermediate.HtmlDealer;

/**
*
* @author Wen Zhong
*/

@SuppressWarnings("serial")
public class ExploreServlet extends HttpServlet {
	HtmlDealer dealer;
	StringBuilder webPage;
	@Override
	public void init() throws ServletException {
		super.init();
		// get the singleton DBWrapper
		DBWrapper wrapper = DBWrapper.getDB(getServletContext().getInitParameter("dbroot"));
		wrapper.setupStore();
		webPage = new StringBuilder();
	}
	
	@Override
	public void destroy() {
		super.destroy();
		// close DBWrapper
		DBWrapper wrapper = DBWrapper.getDB(getServletContext().getInitParameter("dbroot"));
		wrapper.closeStore();
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String approot = getServletContext().getInitParameter("approot");
		dealer = new HtmlDealer(approot + "/webpage/explore.html");
		ServletOutputStream out = response.getOutputStream();
		HttpSession session = request.getSession(false);
		String pathinfo = request.getPathInfo();
		if (session != null) {
			out.println(dealer.getWebPage());
		} else {
			response.sendRedirect("/login"); // send redirect to LoginServlet
		}
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String pathinfo = request.getPathInfo();
		System.out.println("pathinfo: " + pathinfo);
		DBWrapper wrapper = DBWrapper.getDB(getServletContext().getInitParameter("dbroot"));
		HttpSession session = request.getSession(false);
		String username = (String)session.getAttribute("username");
		
	}
}
