package editor;

import java.io.*;

public class Test {
	public Test() {
	}

	public static void main(String[] args) {
		try {
			Test t = new Test();
			t.test();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void test() throws Exception {
		File f = new File("c:\\test\\xxx.txt");
		f.createNewFile();
	}
}