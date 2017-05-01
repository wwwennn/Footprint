package servlets;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.lang.reflect.Type;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.servlet.annotation.MultipartConfig;

import db.DBWrapper;
import db.Blog;
import intermediate.HtmlDealer;

/**
*
* @author Wen Zhong
*/

@SuppressWarnings("serial")
@MultipartConfig
public class BlogServlet extends HttpServlet {
	HtmlDealer dealer;
	StringBuilder webPage;
	int blogNum;
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
		dealer = new HtmlDealer(approot + "/webpage/blog.html");
		DBWrapper wrapper = DBWrapper.getDB(getServletContext().getInitParameter("dbroot"));
		ServletOutputStream out = response.getOutputStream();
		HttpSession session = request.getSession(false);
		String pathinfo = request.getPathInfo();
		if (session != null) {
			String username = (String)session.getAttribute("username");
			ArrayList<Blog> blogs = wrapper.getUserBlogs(username);
//			System.out.println("Here is the list of articles");
//			for(Blog b : blogs) {
//				System.out.println(b.getTitle());
//			}
			if("/articles".equals(pathinfo)) {
				System.out.println("get a request from /articles");
				Type listType = new TypeToken<ArrayList<Blog>>() {}.getType();
				String json = new Gson().toJson(blogs, listType);
				response.setContentType("application/json");
				out.println(json);
				return;
			}
			
			if("/create".equals(pathinfo)) {
				StringBuilder createPage = dealer.serveCreatePage(approot + "/webpage/create.html");
				out.println(createPage.toString());
			} else {
				webPage = dealer.serveBlogPage();
				HashSet<String> places = wrapper.getUserPlaces(username);
				webPage = dealer.addPlacesToBlog(webPage, places);
				out.println(webPage.toString());
			}
		} else {
			response.sendRedirect("/login"); // send redirect to LoginServlet
		}
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String pathinfo = request.getPathInfo();
		DBWrapper wrapper = DBWrapper.getDB(getServletContext().getInitParameter("dbroot"));
		HttpSession session = request.getSession(false);
		String username = (String)session.getAttribute("username");
		ServletOutputStream out = response.getOutputStream();

		if("/delete".equals(pathinfo)) {
			String deletedItem = request.getParameter("deletedItem");
			wrapper.deletePlaceFromUser(username, deletedItem);
			return;
		}
		
		ServletFileUpload sf = new ServletFileUpload(new DiskFileItemFactory());
		try {
			Blog blog = new Blog();
			List<FileItem> multifiles = sf.parseRequest(request);
			System.out.println("size: " + multifiles.size());
			String title = multifiles.get(0).getString();
			String article = multifiles.get(2).getString();
			String fileFolder = "/usr/local/apache-tomcat-8.5.13/webapps/blogImg/" + username;
			File userFolder = new File(fileFolder);
			if(!userFolder.exists()) {
				userFolder.mkdirs();
			}
			int dotIdx = multifiles.get(1).getName().indexOf(".");
			String imagePath;
			if(multifiles.get(1).getName() == null || multifiles.get(1).getName().length() == 0) {
				blog.setImagePath("");
			} else {
				imagePath = fileFolder + "/" + title + multifiles.get(1).getName().substring(dotIdx);
				multifiles.get(1).write(new File(imagePath));
				blog.setImagePath(imagePath);
			}
			
			blog.setTitle(title);
			blog.setContent(article);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String timestamp = sdf.format(System.currentTimeMillis());
			blog.setTimestamp(timestamp);
			System.out.println("SUCCESS");
			wrapper.addBlogToUser(username, blog);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		out.println(webPage.toString());
	}
}
