package servlets;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import javax.servlet.ServletContext;
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
import com.google.gson.reflect.TypeToken;

import db.Blog;
import db.DBWrapper;
import intermediate.FacebookUser;
import intermediate.HtmlDealer;

/**
 * This is the landing page.
 *
 */
@SuppressWarnings("serial")
public class ArticleServlet extends HttpServlet {
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
		
		System.out.println("article got a post request");
		dealer = new HtmlDealer("/Users/zhongwen/Dropbox/Spring2017/594/project/Footprint/webpage/article.html");
//		ServletOutputStream out = response.getOutputStream();
//		out.println(dealer.getWebPage());
//		response.setStatus(response.SC_MOVED_TEMPORARILY);
//		response.setHeader("Location", "article.html");
		String site = "http://localhost:8080/article" ;
		response.setStatus(response.SC_MOVED_TEMPORARILY);
		response.setHeader("Location", site); 
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(false);
		if(session == null) {
			response.sendRedirect("/home");
		}
		DBWrapper wrapper = DBWrapper.getDB(getServletContext().getInitParameter("dbroot"));
		System.out.println("article got a get request");
		dealer = new HtmlDealer("/Users/zhongwen/Dropbox/Spring2017/594/project/Footprint/webpage/article.html");
		String articleName = request.getParameter("article");
		String username = (String)session.getAttribute("username");
		String timestamp = request.getParameter("timestamp");
		System.out.println("username: " + username);
		System.out.println("timestamp: " + timestamp);
		ArrayList<Blog> blogs = wrapper.getUserBlogs(username);
		Blog returnedBlog=null;
		for(Blog b : blogs) {
			if(b.getTitle().equals(articleName) && b.getTimestamp().equals(timestamp)) {
				returnedBlog = b;
				break;
			}
		}
		webPage = new StringBuilder(dealer.getWebPage());
		webPage = dealer.addArticle(webPage, returnedBlog);
		ServletOutputStream out = response.getOutputStream();
		out.println(webPage.toString());
	}

}
