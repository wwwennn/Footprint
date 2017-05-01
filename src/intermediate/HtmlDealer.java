package intermediate;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import db.Blog;

/**
*
* @author Wen Zhong
*/

public class HtmlDealer {
	StringBuilder webPage;
	
	public HtmlDealer(String path) {
		webPage = readPage(path);
	}
	
	public StringBuilder readPage(String path) {
		BufferedReader in = null;
		StringBuilder res = new StringBuilder();
		try {
			in = new BufferedReader(new FileReader(path));
			String line = in.readLine();
			while (line != null) {
				res.append(line);
				line = in.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return res;
	}
	
	public StringBuilder serveBlogPage() {
		return webPage;
	}
	
	public StringBuilder serveHomePageWithName(String path, String firstname, HashSet<String> visitedPlaces) {
		StringBuilder sb = readPage(path);
		int index = sb.indexOf("Welcome");
		sb.insert(index + 7, ", " + firstname);
		if(visitedPlaces.size() > 0) {
			String original = "Oops, you have not marked any places...";
			int placeStartIdx = sb.indexOf(original);
			StringBuilder addedPlaces = new StringBuilder();
//			for(int i = 0; i < visitedPlaces.size(); i++) {
//				addedPlaces.append("<li>" + visitedPlaces.get(i) + "</li>");
//			}
			for(String str : visitedPlaces) {
				addedPlaces.append("<li>" + str + "</li>");
			}
			addedPlaces.insert(0, "<ul class=\"currentFP\">");
			addedPlaces.append("</ul><a href='http://localhost:8080/blog#manageFPTle' class='btn btn-default' id='manageFP'>Manage Your Footprints</a>");
			sb.replace(placeStartIdx, placeStartIdx + original.length(), addedPlaces.toString());
		}
		return sb;
	}
	
	public StringBuilder addFriends(StringBuilder page, ArrayList<String> friends) {
		int startIndex = page.indexOf("Mark");
		for(int i = 0; i < friends.size(); i++) {
			page.replace(startIndex, startIndex + 4, "<p>" + friends.get(i) + "</p>");
		}
		
		return page;
	}
	
	public StringBuilder addSearchResult(ArrayList<String> items, ArrayList<String> sitenames, StringBuilder page) {
		int searchIndex = page.indexOf("Search");
		String temp = "<ul id=\"searchResult\" style=\"overflow: scroll;\">";
		int insertIndex = page.indexOf(temp, searchIndex);
		StringBuilder res = new StringBuilder();
		for(int i = 0; i < sitenames.size(); i++) {
			if(!items.contains(sitenames.get(i)))
				res.append("<li>" + sitenames.get(i) + "</li>");
			
		}
		page.insert(insertIndex + temp.length(), res);
		return page;
	}
	
	public StringBuilder addPlacesToBlog(StringBuilder page, HashSet<String> places) {
		String notification = "<ul class='manageSection'>";
		StringBuilder toAdd = new StringBuilder();
		for(String str : places) {
			toAdd.append("<li>" + str + "</li>");
		}
		page.insert(page.indexOf(notification) + notification.length(), toAdd);
		return page;
	}
	
	public StringBuilder serveCreatePage(String path) {
		return readPage(path);
	}
	
	public String serveErrorPage(String path, String added) {
		StringBuilder original = readPage(path);
		String signal = "<div class=\"box text-center\">";
		int index = original.indexOf(signal);
		original.insert(index + signal.length(), added);
		return original.toString();
	}
	
	public String getWebPage() {
		return webPage.toString();
	}

	public void setWebPage(StringBuilder webPage) {
		this.webPage = webPage;
	}
	
	public StringBuilder addArticle(StringBuilder page, Blog blog) {
		int titleIdx = page.indexOf("Title");
		page.replace(titleIdx, titleIdx + "Title".length(), blog.getTitle());
		int timeIdx = page.indexOf("Timestamp");
		page.replace(timeIdx, timeIdx + "Timestamp".length(), blog.getTimestamp());
		int imgIdx = page.indexOf("imagePath");
		String imagePath = blog.getImagePath();
		if(blog.getImagePath().length() == 0) {
			imagePath = "img/default.png";
		} else {
			int idx=imagePath.indexOf("blogImg");
            imagePath=imagePath.substring(idx);
		}
		page.replace(imgIdx, imgIdx + "imagePath".length(), imagePath);
		int contentIdx = page.indexOf("Content");
		page.replace(contentIdx, contentIdx + "Content".length(), blog.getContent());
		
		return page;
	}
}
