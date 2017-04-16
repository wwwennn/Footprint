import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class JsonUtility {
	public static void main(String[] args) {
		StringBuilder sb = new StringBuilder();
		Scanner in = null;
		try {
			in = new Scanner(new File("/Users/zhongwen/Dropbox/Spring2017/594/project/data/test.json"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			in.close();
		}
		
	}
}