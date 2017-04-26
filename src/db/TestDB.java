package db;

/**
*
* @author Wen Zhong
*/

public class TestDB {

	public static void main(String[] args) throws Exception {
//		DbRead dbRead = new DbRead();
//		DbPut dbPut = new DbPut();
//		dbPut.insertPlace("dummy place", 100, 100);
//		System.out.println("test: " + dbRead.getPlaceInfo("Schuylkill River Park").getSiteName());
		
		DBWrapper wrapper = DBWrapper.getDB("/Users/zhongwen/Dropbox/Spring2017/594/project/db");
		wrapper.setupStore();
		System.out.println(wrapper.getPlace("Schuylkill River Park"));
		System.out.println(wrapper.getSearchPlaces("park"));
		
		
		
		wrapper.closeStore();;
	}

}
