package editor;

import java.io.*;
import java.util.*;

/**
 用来加载和保存字符串的一个管理器。
 */
public class StringManager {
	public final static String NO_STRING = "";

	ArrayList strings;
	public StringManager() {
		strings = new ArrayList();
		clear();
	}

	public void clear() {
		strings.clear();
		strings.add(NO_STRING);
	}

	public int add(String value) {
		if (value == null) {
			return 0;
		}
		for (int i = 0; i < strings.size(); ++i) {
			if (value.equals( (String) (strings.get(i)))) {
				return i;
			}
		}
		strings.add(value);
		return (strings.size() - 1);
	}

	public String get(int index) {
		if (index >= 0 && index < strings.size()) {
			return (String) (strings.get(index));
		}
		return NO_STRING;
	}

	public void save(String name) throws Exception {
		String fileFullName = XUtil.getDefPropStr("StringFilePath") + "\\" + name + ".txt";
		DataOutputStream out =
			new DataOutputStream(
			new BufferedOutputStream(
			new FileOutputStream(fileFullName)));
		String result = "";
		for (int i = 1; i < strings.size(); ++i) {
			String value = (String) (strings.get(i));
			result = result + "[" + i + "]\r\n" + value + "\r\n" + "%%\r\n\r\n";
		}
//		result = result.replaceAll("\r\n","\n").replaceAll("\n","\r\n");
		result = result.replaceAll("\r\n", "\n");
		result = result.replaceAll("\r", "");
		result = result.replaceAll("\n", "\r\n");

		byte[] bytes = result.getBytes();
		out.write(bytes, 0, bytes.length);
		out.flush();
		out.close();
	}

	public void load(String name) throws Exception {
		clear();
		String fileFullName = XUtil.getDefPropStr("StringFilePath") + "\\" + name + ".txt";
		if (! (new File(fileFullName)).exists()) {
			return;
		}
		BufferedReader stringReader =
			new BufferedReader(
			new InputStreamReader(
			new FileInputStream(fileFullName)));
		String sLine;
		sLine = stringReader.readLine();
		while (sLine != null) {
			if (sLine.startsWith("[") && sLine.endsWith("]")) {
//				int id = Integer.parseInt(sLine.substring(1,sLine.length() - 1));
				String result = "";
				sLine = stringReader.readLine();
				while (! (sLine.length() >= 2 && sLine.startsWith("%") && sLine.endsWith("%"))) {
					result = result + sLine + "\r\n";
					sLine = stringReader.readLine();
				}
				if (result.length() >= "\r\n".length()) {
					result = result.substring(0, result.length() - "\r\n".length());
				}
				strings.add(result);
			}
			sLine = stringReader.readLine();
		}
		stringReader.close();
	}
}