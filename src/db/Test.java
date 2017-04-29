package db;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;

/**
*
* @author Wen Zhong
*/

public class Test {
	public static void main(String[] args) {
		DBWrapper wrapper = DBWrapper.getDB("/Users/zhongwen/Dropbox/Spring2017/594/project/db");
		wrapper.setupStore();
		ArrayList<String> temp = new ArrayList<String>();
		temp.add("Laura Sims Rink");
		temp.add("Rizzo Ice Rink");
		wrapper.addPlacesToUser("wen123", temp);
		HashSet<String> places = wrapper.getUserPlaces("wen123");
		for(String str : places) {
			System.out.println(str);
		}
		wrapper.closeStore();
	}
}
