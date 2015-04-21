package editor;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

class FileExtFilter
	implements java.io.FileFilter {

	public static String getName(File f) {
		if (!f.isFile()) {
			return "";
		}
		String name = "";
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			name = s.substring(0, i).toLowerCase();
		}
		return name;
	}

	public static String getExtension(File f) {
		if (!f.isFile()) {
			return "";
		}
		String ext = "";
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	String ext;

	public FileExtFilter(String ext) {
		this.ext = ext;
	}

	public boolean accept(File f) {
		if (f.isFile()) {
			String s = f.getName();
			return getExtension(f).equalsIgnoreCase(ext);
		}
		return false;
	}

	public String toString() {
		return ext.toUpperCase() + "FileFilter";
	}
}

class DefaultTheme
	extends DefaultMetalTheme {
	private FontUIResource font;
	Properties properties = new Properties();
	public DefaultTheme() {
		super();
		try {
			FileInputStream in = new FileInputStream("DefaultLAF.ini");
			properties.load(in);
			in.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		String fontName = properties.getProperty("fontName", "Dialog");
		int fontSize = 12;
		try {
			fontSize = Integer.parseInt(properties.getProperty("fontSize"));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		font = new FontUIResource(fontName, Font.PLAIN, fontSize);
	}

	public FontUIResource getDefaultFont() {
		return font;
	}

	public FontUIResource getControlTextFont() {
		return font;
	}

	public FontUIResource getMenuTextFont() {
		return font;
	}

	public FontUIResource getSubTextFont() {
		return font;
	}

	public FontUIResource getSystemTextFont() {
		return font;
	}

	public FontUIResource getUserTextFont() {
		return font;
	}

	public FontUIResource getWindowTitleFont() {
		return font;
	}
}

class Pair {
	public Object first, second;
	public Pair(Object first, Object second) {
		this.first = first;
		this.second = second;
	}

	public String toString() {
		if (second == null) {
			return "";
		}
		else {
			return second.toString();
		}
	}
}

class IntPair {
	public int x, y;

	public IntPair() {
		this.x = 0;
		this.y = 0;
	}

	public IntPair(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public IntPair getCopy() {
		return new IntPair(x, y);
	}

	public boolean equals(Object obj) {
		if (obj != null) {
			if (obj instanceof IntPair) {
				IntPair pair = (IntPair) obj;
				return (pair.x == this.x && pair.y == this.y);
			}
			else if (obj instanceof DoublePair) {
				DoublePair pair = (DoublePair) obj;
				return (pair.x == this.x && pair.y == this.y);
			}
		}
		return false;
	}
}

class DoublePair {
	public double x, y;

	public DoublePair() {
		this.x = 0;
		this.y = 0;
	}

	public DoublePair(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public DoublePair getCopy() {
		return new DoublePair(x, y);
	}

	public boolean equals(Object obj) {
		if (obj != null) {
			if (obj instanceof IntPair) {
				IntPair pair = (IntPair) obj;
				return (pair.x == this.x && pair.y == this.y);
			}
			else if (obj instanceof DoublePair) {
				DoublePair pair = (DoublePair) obj;
				return (pair.x == this.x && pair.y == this.y);
			}
		}
		return false;
	}
}

class Relation {
	public final static int TYPE_COMPARE = 0;
	public final static int TYPE_MODIFY = 1;

	public final static int LESS = 0;
	public final static int LORE = 1;
	public final static int EQUAL = 2;
	public final static int GORE = 3;
	public final static int GREATER = 4;
	public final static int SET = 5;
	public final static int PLUS = 6;
	public final static int REDUCE = 7;

	public final static int[][] RELATIONS = { {
											LESS,
											LORE,
											EQUAL,
											GORE,
											GREATER
	}
		, {
		SET,
		PLUS,
		REDUCE
	}
	};

	public final static String[] DESCRIPTIONS = {
												" < ", " <= ", " == ", " >= ", " > ", " = ", " + ", " - "
	};
}

class Dir {
	public final static int LENGTH = 16;

	public final static int U = 8;
	public final static int UUR = 9;
	public final static int RUU = 9;
	public final static int UR = 10;
	public final static int RU = 10;
	public final static int URR = 11;
	public final static int RRU = 11;
	public final static int R = 12;
	public final static int RRD = 13;
	public final static int DRR = 13;
	public final static int RD = 14;
	public final static int DR = 14;
	public final static int RDD = 15;
	public final static int DDR = 15;
	public final static int D = 0;
	public final static int DDL = 1;
	public final static int LDD = 1;
	public final static int DL = 2;
	public final static int LD = 2;
	public final static int DLL = 3;
	public final static int LLD = 3;
	public final static int L = 4;
	public final static int LLU = 5;
	public final static int ULL = 5;
	public final static int LU = 6;
	public final static int UL = 6;
	public final static int UUL = 7;
	public final static int LUU = 7;

	public final static int[] MOVE_DIRS = {D, DL, L, LU, U};
	public final static int[] FULL_MOVE_DIRS = {D, DL, L, LU, U, UR, R, RD};
	public final static int[] STAND_DIRS = {D, DDL, DL, DLL, L, LLU, LU, LUU, U};
	public final static int[] FULL_STAND_DIRS = {D, DDL, DL, DLL, L, LLU, LU, LUU,
												U, UUR, UR, URR, R, RRD, RD, RDD};
	public final static int[] FIRE_DIRS = STAND_DIRS;
	public final static String[] DESCRIPTIONS = {
		"下", "左下下", "左下", "左下上", "左", "左上下", "左上", "左上上", 
		"上", "右上上", "右上", "右上下", "右", "右下上", "右下", "右下下"};

	public final static int flip(int dir) {
		int result = dir;
		switch (dir) {
			case UUR:
				result = UUL;
				break;
			case UR:
				result = UL;
				break;
			case URR:
				result = ULL;
				break;
			case R:
				result = L;
				break;
			case DRR:
				result = DLL;
				break;
			case DR:
				result = DL;
				break;
			case DDR:
				result = DDL;
				break;
		}
		return result;
	}

	public final static boolean isFlip(int dir) {
		return dir != flip(dir);
	}
}

class Rect {
	public final static int CORNER_NONE = 0;
	public final static int CORNER_LEFT_TOP = 1; //左上
	public final static int CORNER_LEFT_MIDDLE = 2; //左中
	public final static int CORNER_LEFT_BOTTOM = 3; //左下
	public final static int CORNER_CENTER_TOP = 4; //中上
	public final static int CORNER_CENTER_MIDDLE = 5; //正中
	public final static int CORNER_CENTER_BOTTOM = 6; //中下
	public final static int CORNER_RIGHT_TOP = 7; //右上
	public final static int CORNER_RIGHT_MIDDLE = 8; //右中
	public final static int CORNER_RIGHT_BOTTOM = 9; //右下

	public final static int CORNER_SIZE = 5;

	public final static int[] CORNERS = {
										CORNER_LEFT_TOP, CORNER_LEFT_MIDDLE, CORNER_LEFT_BOTTOM,
										CORNER_CENTER_TOP, CORNER_CENTER_MIDDLE, CORNER_CENTER_BOTTOM,
										CORNER_RIGHT_TOP, CORNER_RIGHT_MIDDLE, CORNER_RIGHT_BOTTOM
	};

	public final static double[][] CORNER_OFFSETS = { {0, 0}
		, {0, 0}
		, {0, 0.5}
		, {0, 1}
		, {0.5, 0}
		, {0.5, 0.5}
		, {0.5, 1}
		, {1, 0}
		, {1, 0.5}
		, {1, 1}
	};

	public final static int[] CORNER_CURSORS = {
											   Cursor.DEFAULT_CURSOR,
											   Cursor.NW_RESIZE_CURSOR, Cursor.W_RESIZE_CURSOR, Cursor.SW_RESIZE_CURSOR,
											   Cursor.N_RESIZE_CURSOR, Cursor.MOVE_CURSOR, Cursor.S_RESIZE_CURSOR,
											   Cursor.NE_RESIZE_CURSOR, Cursor.E_RESIZE_CURSOR, Cursor.SE_RESIZE_CURSOR
	};

	private final static int reverseX(int corner) {
		switch (corner) {
			case CORNER_LEFT_TOP:
				return CORNER_RIGHT_TOP;
			case CORNER_LEFT_MIDDLE:
				return CORNER_RIGHT_MIDDLE;
			case CORNER_LEFT_BOTTOM:
				return CORNER_RIGHT_BOTTOM;
			case CORNER_RIGHT_TOP:
				return CORNER_LEFT_TOP;
			case CORNER_RIGHT_MIDDLE:
				return CORNER_LEFT_MIDDLE;
			case CORNER_RIGHT_BOTTOM:
				return CORNER_LEFT_BOTTOM;
			default:
				return corner;
		}
	}

	private final static int reverseY(int corner) {
		switch (corner) {
			case CORNER_LEFT_TOP:
				return CORNER_LEFT_BOTTOM;
			case CORNER_LEFT_BOTTOM:
				return CORNER_LEFT_TOP;
			case CORNER_CENTER_TOP:
				return CORNER_CENTER_BOTTOM;
			case CORNER_CENTER_BOTTOM:
				return CORNER_CENTER_TOP;
			case CORNER_RIGHT_TOP:
				return CORNER_RIGHT_BOTTOM;
			case CORNER_RIGHT_BOTTOM:
				return CORNER_RIGHT_TOP;
			default:
				return corner;
		}
	}

	public int x, y, width, height;

	Rect(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public Rect getCopy() {
		return new Rect(x, y, width, height);
	}

	public boolean contains(int x, int y) {
		return this.x <= x && this.x + this.width >= x &&
			this.y <= y && this.y + this.height >= y;
	}

	public int getSelectCorner(int x, int y, double scaleX, double scaleY) {
		return getSelectCorner(x, y, scaleX, scaleY, CORNER_SIZE);
	}

	public int getSelectCorner(int x, int y, double scaleX, double scaleY, int cornerSize) {
		int result = CORNER_NONE;

		for (int i = 0; i < CORNERS.length; ++i) {
			IntPair cornerXY = getCornerXY(CORNERS[i], scaleX, scaleY, cornerSize);
			int cornerX = cornerXY.x;
			int cornerY = cornerXY.y;
			if (x >= cornerX && x <= cornerX + cornerSize && y >= cornerY &&
				y <= cornerY + cornerSize) {
				result = CORNERS[i];
				break;
			}

		}
		return result;
	}

	private IntPair getCornerXY(int corner, double scaleX, double scaleY, int cornerSize) {
		int cornerX = (int) ( (this.x + CORNER_OFFSETS[corner][0] * this.width) * scaleX) -
					  (int) (cornerSize / 2);
		int cornerY = (int) ( (this.y + CORNER_OFFSETS[corner][1] * this.height) * scaleY) -
					  (int) (cornerSize / 2);
		return new IntPair(cornerX, cornerY);
	}

	public Cursor getCornerCursor(int corner) {
		if (corner >= 0 && corner < CORNER_CURSORS.length) {
			return Cursor.getPredefinedCursor(CORNER_CURSORS[corner]);
		}
		else {
			return Cursor.getDefaultCursor();
		}
	}

	public int resizeByCorner(int corner, int offsetX, int offsetY) {
		if (corner == CORNER_NONE) {
			return corner;
		}

		int left = this.x;
		int top = this.y;
		int right = this.x + this.width;
		int bottom = this.y + this.height;

		switch (corner) {
			case CORNER_LEFT_TOP:
				left += offsetX;
				top += offsetY;
				break;
			case CORNER_LEFT_MIDDLE:
				left += offsetX;
				break;
			case CORNER_LEFT_BOTTOM:
				left += offsetX;
				bottom += offsetY;
				break;
			case CORNER_CENTER_TOP:
				top += offsetY;
				break;
			case CORNER_CENTER_MIDDLE:
				left += offsetX;
				right += offsetX;
				top += offsetY;
				bottom += offsetY;
				break;
			case CORNER_CENTER_BOTTOM:
				bottom += offsetY;
				break;
			case CORNER_RIGHT_TOP:
				right += offsetX;
				top += offsetY;
				break;
			case CORNER_RIGHT_MIDDLE:
				right += offsetX;
				break;
			case CORNER_RIGHT_BOTTOM:
				right += offsetX;
				bottom += offsetY;
				break;
		}

		this.x = Math.min(left, right);
		this.y = Math.min(top, bottom);
		this.width = Math.abs(right - left);
		this.height = Math.abs(bottom - top);

		int newCorner = corner;
		if (left >= right) {
			newCorner = reverseX(newCorner);
		}
		if (top >= bottom) {
			newCorner = reverseY(newCorner);
		}
		return newCorner;
	}

	public void paintCorner(Graphics g) {
		paintCorner(g, CORNER_SIZE);
	}

	public void paintCorner(Graphics g, int cornerSize) {
		Graphics2D g2 = (Graphics2D) g;
		AffineTransform oldTransform = g2.getTransform();
		double scaleX = oldTransform.getScaleX();
		double scaleY = oldTransform.getScaleY();
		g2.scale(1 / scaleX, 1 / scaleY);
		for (int i = 0; i < CORNERS.length; ++i) {
			IntPair cornerXY = getCornerXY(CORNERS[i], scaleX, scaleY, cornerSize);
			int cornerX = cornerXY.x;
			int cornerY = cornerXY.y;
			g2.fillRect(cornerX, cornerY, cornerSize, cornerSize);
		}
		g2.setTransform(oldTransform);
	}
}

class Grid {
	public final static int W = 4, H = 4;
	
	public static IntPair getSortXY(int x, int y) {
		int tmpX = x >> 2;
		int tmpY = y >> 1;

		int offsetX = (x - (tmpX << 2)) - (4 >> 1);
		int offsetY = (y - (tmpY << 1)) - (2 >> 1);

		int sortX = tmpY + tmpX;
		int sortY = tmpY - tmpX;

		int tmp = ( (4 >> 1) - Math.abs(offsetX)) - (Math.abs(offsetY) << 1);

		if (tmp >= 0) { //在中心菱形以内或者就在中心菱形上
			sortX += 1;
		}
		else { //在中心菱形以外
			if (offsetX < 0) {
				if (offsetY < 0) { //中心菱形的左上角
					//do nothing
				}
				else { //offsetY > 0 //中心菱形的左下角
					sortX += 1;
					sortY += 1;
				}
			}
			else { //offsetX > 0
				if (offsetY < 0) { //中心菱形的右上角
					sortX += 1;
					sortY -= 1;
				}
				else { //offsetY > 0 //中心菱形的右下角
					sortX += 2;
				}
			}
		}
		return new IntPair(sortX, sortY);
	}

	public static IntPair getGridXY(int x, int y) {
		int gridX = x >> 2;
		int gridY = y >> 2;
		return new IntPair(gridX, gridY);
	}

	public static IntPair getScreenXY(int gridX, int gridY) {
//		int x = ( (gridX - gridY) << 3);
//		int y = ( (gridX + gridY) << 2);

		int x = (gridX << 2) + (W >> 1);
		int y = (gridY << 2) + (H >> 1);
		return new IntPair(x, y);
	}
}

public class XUtil {
	public final static int SCREEN_W = 176, SCREEN_H = 208;
	public final static int LEFT_BUTTON = MouseEvent.BUTTON1, RIGHT_BUTTON = MouseEvent.BUTTON3;

	private static Properties DEF_PROP_STR;
	private static Properties DEF_PROP_INT;
	private static boolean PROP_LOADED = false;

	private static void initDefProp() throws IOException {
		if (PROP_LOADED) {
//			return;
		}
		DEF_PROP_STR = new Properties();
		DEF_PROP_INT = new Properties();
		File f;
		FileInputStream in;
		f = new File(".\\DefaultPropStr.ini");
		if (f.exists()) {
			in = new FileInputStream(f);
			DEF_PROP_STR.load(in);
			in.close();
		}
		f = new File(".\\DefaultPropInt.ini");
		if (f.exists()) {
			in = new FileInputStream(f);
			DEF_PROP_INT.load(in);
			in.close();
		}
		PROP_LOADED = true;
	}

	public static String getDefPropStr(String name) {
		String result = null;
		try {
			initDefProp();
			result = DEF_PROP_STR.getProperty(name);
		}
		catch (Exception e) {
			result = null;
		}
		if (result == null) {
			result = "";
		}
		return result;
	}

	public static int getDefPropInt(String name) {
		int result = 0;
		try {
			initDefProp();
			String value = DEF_PROP_INT.getProperty(name);
			result = Integer.parseInt(value);
		}
		catch (Exception e) {
			result = 0;
		}
		return result;
	}

	public static String getClassName(Class c) {
		String result = "";
		if (c != null) {
			result = c.getName();
			int i = result.lastIndexOf(".");
			if (i > 0) {
				result = result.substring(i + 1, result.length());
			}
			i = result.lastIndexOf("$");
			if (i > 0) {
				result = result.substring(i + 1, result.length());
			}
		}
		return result;
	}

	public static int[] copyArray(int[] data) {
		int[] result = null;
		if (data != null) {
			result = new int[data.length];
			for (int i = 0; i < data.length; ++i) {
				result[i] = data[i];
			}
		}
		return result;
	}

	public static IntPair[] copyArray(IntPair[] data) {
		IntPair[] result = null;
		if (data != null) {
			result = new IntPair[data.length];
			for (int i = 0; i < data.length; ++i) {
				result[i] = data[i].getCopy();
			}
		}
		return result;
	}

	public static DoublePair[] copyArray(DoublePair[] data) {
		DoublePair[] result = null;
		if (data != null) {
			result = new DoublePair[data.length];
			for (int i = 0; i < data.length; ++i) {
				result[i] = data[i].getCopy();
			}
		}
		return result;
	}

	public static Rect[] copyArray(Rect[] data) {
		Rect[] result = null;
		if (data != null) {
			result = new Rect[data.length];
			for (int i = 0; i < data.length; ++i) {
				result[i] = data[i].getCopy();
			}
		}
		return result;
	}
	
	public static boolean copyFile(String source, String dest) {
		boolean result = false;
		try {
			File sourceFile = new File(source);
			File destFile = new File(dest);
			if(sourceFile.exists()) {
//				DataInputStream in = 
//								new DataInputStream(
//								new BufferedInputStream(
//							    new FileInputStream(sourceFile)));
				FileInputStream in = new FileInputStream(sourceFile);
//				DataOutputStream out = 
//								new DataOutputStream(
//								new BufferedOutputStream(
//								new FileOutputStream(destFile)));
				FileOutputStream out = new FileOutputStream(destFile);
				byte[] bytes = new byte[1024 * 4];
				int length;
				while((length = in.read(bytes)) != -1) {
					out.write(bytes, 0, length);
				}
			    out.flush();
			    out.close();
				in.close();
				result = true;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String getNumberString(int value) {
		return getNumberString(value, 4);
	}

	public static String getNumberString(int value, int numberLength) {
		if (value == 0) {
			String result = "";
			for (int i = 0; i < numberLength; ++i) {
				result += "0"; //零
			}
			return result;
		}
		for (int i = 0; i < numberLength - 1; ++i) {
			if (value >= Math.pow(10, i) && value < Math.pow(10, (i + 1))) {
				String result = "";
				for (int j = i + 1; j < numberLength; ++j) {
					result += "0"; //零
				}
				result += value + "";
				return result;
			}
		}
		return value + "";
	}

}

class SL {
	private static void basicWriteByte(int data, DataOutputStream out) throws Exception {
		out.writeByte(data & 0xFF);
	}

	public static void writeByte(int data, DataOutputStream out) throws Exception {
		basicWriteByte(data, out);
	}

	public static void writeShort(int data, DataOutputStream out) throws Exception {
		if (XUtil.getDefPropInt("Type") == 0) {// Symbian
			basicWriteByte((data & 0xFF), out);
			basicWriteByte(((data & 0xFF00) >> 8), out);
		}
		else {// Java
			basicWriteByte(((data & 0xFF00) >> 8), out);
			basicWriteByte((data & 0xFF), out);
		}
	}

	public static void writeInt(int data, DataOutputStream out) throws Exception {
		if (XUtil.getDefPropInt("Type") == 0) {// Symbian
			basicWriteByte((data & 0xFF), out);
			basicWriteByte(((data & 0xFF00) >> 8), out);
			basicWriteByte(((data & 0xFF0000) >> 16), out);
			basicWriteByte(((data & 0xFF000000) >> 24), out);
		}
		else {// Java
			basicWriteByte(((data & 0xFF000000) >> 24), out);
			basicWriteByte(((data & 0xFF0000) >> 16), out);
			basicWriteByte(((data & 0xFF00) >> 8), out);
			basicWriteByte((data & 0xFF), out);
		}
	}

	public static void writeDouble(double data, DataOutputStream out) throws Exception {
		writeInt( (int) (data * 100), out);
	}

	public static void writeString(String data, DataOutputStream out) throws Exception {
		if (data == null) {
			out.writeInt(0);
		}
		else {
			byte[] bytes = data.getBytes();
			if (bytes == null) {
				out.writeInt(0);
			}
			else {
				out.writeInt(bytes.length);
				out.write(bytes);
			}
		}
	}

	public static String readString(DataInputStream in) throws Exception {
		int length = in.readInt();
		if (length <= 0) {
			return "";
		}
		else {
			byte[] bytes = new byte[length];
			in.read(bytes, 0, length);
			return new String(bytes, 0, length);
		}
	}

	public static void writeIntArrayMobile(int[] data, DataOutputStream out) throws Exception {
		if (data == null) {
			writeInt(0, out);
		}
		else {
			writeInt(data.length, out);
			for (int i = 0; i < data.length; ++i) {
				writeInt(data[i], out);
			}
		}
	}

	public static void writeIntArray(int[] data, DataOutputStream out) throws Exception {
		if (data == null) {
			out.writeInt(0);
		}
		else {
			out.writeInt(data.length);
			for (int i = 0; i < data.length; ++i) {
				out.writeInt(data[i]);
			}
		}
	}

	public static int[] readIntArray(DataInputStream in) throws Exception {
		int[] result = null;
		int length = in.readInt();
		if (length > 0) {
			result = new int[length];
			for (int i = 0; i < length; ++i) {
				result[i] = in.readInt();
			}
		}
		return result;
	}

	public static void writeIntPairMobile(IntPair p, DataOutputStream out) throws Exception {
		writeInt(p.x, out);
		writeInt(p.y, out);
	}

	public static void writeIntPair(IntPair p, DataOutputStream out) throws Exception {
		out.writeInt(p.x);
		out.writeInt(p.y);
	}

	public static IntPair readIntPair(DataInputStream in) throws Exception {
		IntPair result = new IntPair();
		result.x = in.readInt();
		result.y = in.readInt();
		return result;
	}

	public static void writeIntPairArrayMobile(IntPair[] data, DataOutputStream out) throws Exception {
		if (data == null) {
			writeInt(0, out);
		}
		else {
			writeInt(data.length, out);
			for (int i = 0; i < data.length; ++i) {
				writeIntPairMobile(data[i], out);
			}
		}
	}

	public static void writeIntPairArray(IntPair[] data, DataOutputStream out) throws Exception {
		if (data == null) {
			out.writeInt(0);
		}
		else {
			out.writeInt(data.length);
			for (int i = 0; i < data.length; ++i) {
				writeIntPair(data[i], out);
			}
		}
	}

	public static IntPair[] readIntPairArray(DataInputStream in) throws Exception {
		IntPair[] result = null;
		int length = in.readInt();
		if (length > 0) {
			result = new IntPair[length];
			for (int i = 0; i < length; ++i) {
				result[i] = readIntPair(in);
			}
		}
		return result;
	}

	public static void writeDoublePairMobile(DoublePair p, DataOutputStream out) throws Exception {
		writeDouble(p.x, out);
		writeDouble(p.y, out);
	}

	public static void writeDoublePair(DoublePair p, DataOutputStream out) throws Exception {
		out.writeDouble(p.x);
		out.writeDouble(p.y);
	}

	public static DoublePair readDoublePair(DataInputStream in) throws Exception {
		DoublePair result = new DoublePair();
		result.x = in.readDouble();
		result.y = in.readDouble();
		return result;
	}

	public static void writeRectMobile(Rect r, DataOutputStream out) throws Exception {
		writeInt(r.x, out);
		writeInt(r.y, out);
		writeInt(r.width, out);
		writeInt(r.height, out);
	}

	public static void writeRect(Rect r, DataOutputStream out) throws Exception {
		out.writeInt(r.x);
		out.writeInt(r.y);
		out.writeInt(r.width);
		out.writeInt(r.height);
	}

	public static Rect readRect(DataInputStream in) throws Exception {
		Rect result = new Rect(0, 0, 0, 0);
		result.x = in.readInt();
		result.y = in.readInt();
		result.width = in.readInt();
		result.height = in.readInt();
		return result;
	}

	public static void writeRectArrayMobile(Rect[] data, DataOutputStream out) throws Exception {
		if (data == null) {
			writeInt(0, out);
		}
		else {
			writeInt(data.length, out);
			for (int i = 0; i < data.length; ++i) {
				writeRectMobile(data[i], out);
			}
		}
	}

	public static void writeRectArray(Rect[] data, DataOutputStream out) throws Exception {
		if (data == null) {
			out.writeInt(0);
		}
		else {
			out.writeInt(data.length);
			for (int i = 0; i < data.length; ++i) {
				writeRect(data[i], out);
			}
		}
	}

	public static Rect[] readRectArray(DataInputStream in) throws Exception {
		Rect[] result = null;
		int length = in.readInt();
		if (length > 0) {
			result = new Rect[length];
			for (int i = 0; i < length; ++i) {
				result[i] = readRect(in);
			}
		}
		return result;
	}
}
