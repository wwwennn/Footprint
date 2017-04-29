package servlets;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.annotation.MultipartConfig;

import db.DBWrapper;
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
		ServletOutputStream out = response.getOutputStream();
		HttpSession session = request.getSession(false);
		String pathinfo = request.getPathInfo();
		if (session != null) {
			if("/create".equals(pathinfo)) {
				System.out.println("user is going to write a new article");
				StringBuilder createPage = dealer.serveCreatePage(approot + "/webpage/create.html");
				out.println(createPage.toString());
			} else {
				DBWrapper wrapper = DBWrapper.getDB(getServletContext().getInitParameter("dbroot"));
				String username = (String)session.getAttribute("username");
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
//		String pathinfo = request.getPathInfo();
//		System.out.println("pathinfo: " + pathinfo);
//		DBWrapper wrapper = DBWrapper.getDB(getServletContext().getInitParameter("dbroot"));
		HttpSession session = request.getSession(false);
		String username = (String)session.getAttribute("username");
		ServletOutputStream out = response.getOutputStream();
//		String title = (String)request.getParameter("title");
//		String article = (String)request.getParameter("article");
//		System.out.println("title: " + title);
//		System.out.println("article: " + article);
		
		System.out.println("username: " + username);
		ServletFileUpload sf = new ServletFileUpload(new DiskFileItemFactory());
		try {
			List<FileItem> multifiles = sf.parseRequest(request);
			System.out.println("size: " + multifiles.size());
			for(FileItem item : multifiles) {
				if(!item.isFormField()) {
					System.out.println(item.getName());
					String fileFolder = "/Users/zhongwen/Dropbox/Spring2017/594/project/Footprint/webpage/blogImg/" + username;
					File userFolder = new File(fileFolder);
					if(!userFolder.exists()) {
						userFolder.mkdirs();
					}
					item.write(new File(fileFolder + "/" + item.getName()));
				} else {
					String name = item.getFieldName();
				    String value = item.getString();
				    System.out.println("name: " + name);
				    System.out.println("value: " + value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		out.println(webPage.toString());
	}
}
