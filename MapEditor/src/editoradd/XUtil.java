package editoradd;


import java.awt.Font;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

interface Copyable {

	public Copyable copy();
}

class DefaultTheme extends DefaultMetalTheme {

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

	public FontUIResource getControlTextFont() {
		return font;
	}

	public FontUIResource getDefaultFont() {
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

	public final static int[] MOVE_DIRS = { D, DL, L, LU, U };
	public final static int[] FULL_MOVE_DIRS = { D, DL, L, LU, U, UR, R, RD };
	public final static int[] STAND_DIRS = { D, DDL, DL, DLL, L, LLU, LU, LUU, U };
	public final static int[] FULL_STAND_DIRS = { D, DDL, DL, DLL, L, LLU, LU, LUU, U, UUR, UR,
	        URR, R, RRD, RD, RDD };
	public final static int[] FIRE_DIRS = STAND_DIRS;
	public final static String[] DESCRIPTIONS = { "下", "左下下", "左下", "左下上", "左", "左上下", "左上", "左上上",
	        "上", "右上上", "右上", "右上下", "右", "右下上", "右下", "右下下" };

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

class DoublePair implements Copyable, Saveable {

	public final static DoublePair[] copyArray(DoublePair[] array) throws Exception {
		DoublePair[] result = null;
		if (array != null) {
			result = new DoublePair[array.length];
			for (int i = 0; i < array.length; ++i) {
				result[i] = array[i].copyDoublePair();
			}
		}
		return result;
	}

	public final static DoublePair[] createArrayViaFile(DataInputStream in) throws Exception {
		DoublePair[] result = null;
		int length = in.readInt();
		if (length > 0) {
			result = new DoublePair[length];
			for (int i = 0; i < length; ++i) {
				result[i] = DoublePair.createViaFile(in);
			}
		}
		return result;
	}

	public final static DoublePair createViaFile(DataInputStream in) throws Exception {
		DoublePair result = new DoublePair();
		result.load(in);
		return result;
	}

	public double x, y;

	public DoublePair() {
		this.x = 0;
		this.y = 0;
	}

	public DoublePair(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Copyable copy() {
		return copyDoublePair();
	}

	public DoublePair copyDoublePair() {
		return new DoublePair(x, y);
	}

	public void copyFrom(DoublePair source) {
		this.x = source.x;
		this.y = source.y;
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

	public void load(DataInputStream in) throws Exception {
		x = in.readDouble();
		y = in.readDouble();
	}

	public void save(DataOutputStream out) throws Exception {
		out.writeDouble(x);
		out.writeDouble(y);
	}

	public void saveMobile(DataOutputStream out) throws Exception {
		SL.writeDouble(x, out);
		SL.writeDouble(y, out);
	}
}

class FileExtFilter implements java.io.FileFilter {

	public static String getExtension(File f) {
		if (!f.isFile()) { return ""; }
		String ext = "";
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	public static String getName(File f) {
		if (!f.isFile()) { return ""; }
		String name = "";
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			name = s.substring(0, i).toLowerCase();
		}
		return name;
	}

	String ext;

	public FileExtFilter(String ext) {
		this.ext = ext;
	}

	public boolean accept(File f) {
		if (f.isFile()) { return getExtension(f).equalsIgnoreCase(ext); }
		return false;
	}

	public String toString() {
		return ext.toUpperCase() + "FileFilter";
	}
}

class Grid {

	public final static int W = 4, H = 4;

	public static IntPair getGridXY(int x, int y) {
		// int tmpX = x >> 4; //ax / W
		// int tmpY = y >> 3; //aY / H
		//
		// int offsetX = (x - (tmpX << 4)) - (W >> 1); //x * W;
		// int offsetY = (y - (tmpY << 3)) - (H >> 1); //y * H;
		//
		// int gridX = tmpY + tmpX;
		// int gridY = tmpY - tmpX;
		//
		// int tmp = ( (W >> 1) - Math.abs(offsetX)) - (Math.abs(offsetY) << 1);
		//
		// if (tmp >= 0) { //在中心菱形以内或者就在中心菱形上
		// gridX += 1;
		// }
		// else { //在中心菱形以外
		// if (offsetX < 0) {
		// if (offsetY < 0) { //中心菱形的左上角
		// //do nothing
		// }
		// else { //offsetY > 0 //中心菱形的左下角
		// gridX += 1;
		// gridY += 1;
		// }
		// }
		// else { //offsetX > 0
		// if (offsetY < 0) { //中心菱形的右上角
		// gridX += 1;
		// gridY -= 1;
		// }
		// else { //offsetY > 0 //中心菱形的右下角
		// gridX += 2;
		// }
		// }
		// }

		int gridX = x >> 2;
		int gridY = y >> 2;
		return new IntPair(gridX, gridY);
	}

	public static IntPair getScreenXY(int gridX, int gridY) {
		// int x = ( (gridX - gridY) << 3);
		// int y = ( (gridX + gridY) << 2);

		int x = (gridX << 2) + (W >> 1);
		int y = (gridY << 2) + (H >> 1);
		return new IntPair(x, y);
	}
}

class IntPair implements Copyable, Saveable {

	public final static IntPair[] copyArray(IntPair[] array) {
		IntPair[] result = null;
		if (array != null) {
			result = new IntPair[array.length];
			for (int i = 0; i < array.length; ++i) {
				result[i] = array[i].copyIntPair();
			}
		}
		return result;
	}

	public final static IntPair[] createArrayViaFile(DataInputStream in) throws Exception {
		IntPair[] result = null;
		int length = in.readInt();
		if (length > 0) {
			result = new IntPair[length];
			for (int i = 0; i < length; ++i) {
				result[i] = createViaFile(in);
			}
		}
		return result;
	}

	public final static IntPair createViaFile(DataInputStream in) throws Exception {
		IntPair result = new IntPair();
		result.load(in);
		return result;
	}

	public int x, y;

	public IntPair() {
		this.x = 0;
		this.y = 0;
	}

	public IntPair(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Copyable copy() {
		return copyIntPair();
	}

	public void copyFrom(IntPair source) {
		this.x = source.x;
		this.y = source.y;
	}

	public final IntPair copyIntPair() {
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

	public void load(DataInputStream in) throws Exception {
		x = in.readInt();
		y = in.readInt();
	}

	public void save(DataOutputStream out) throws Exception {
		out.writeInt(x);
		out.writeInt(y);
	}

	public void saveMobile(DataOutputStream out) throws Exception {
		SL.writeInt(x, out);
		SL.writeInt(y, out);
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

// class Rect implements Copyable, Saveable {
//
// public final static int CORNER_NONE = 0;
// public final static int CORNER_LEFT_TOP = 1; // 左上
// public final static int CORNER_LEFT_MIDDLE = 2; // 左中
// public final static int CORNER_LEFT_BOTTOM = 3; // 左下
// public final static int CORNER_CENTER_TOP = 4; // 中上
// public final static int CORNER_CENTER_MIDDLE = 5; // 正中
// public final static int CORNER_CENTER_BOTTOM = 6; // 中下
// public final static int CORNER_RIGHT_TOP = 7; // 右上
// public final static int CORNER_RIGHT_MIDDLE = 8; // 右中
// public final static int CORNER_RIGHT_BOTTOM = 9; // 右下
//
// public final static int CORNER_SIZE = 5;
//
// public final static int[] CORNERS = { CORNER_LEFT_TOP, CORNER_LEFT_MIDDLE,
// CORNER_LEFT_BOTTOM,
// CORNER_CENTER_TOP, CORNER_CENTER_MIDDLE, CORNER_CENTER_BOTTOM,
// CORNER_RIGHT_TOP,
// CORNER_RIGHT_MIDDLE, CORNER_RIGHT_BOTTOM };
//
// public final static double[][] CORNER_OFFSETS = { { 0, 0 }, { 0, 0 }, { 0,
// 0.5 }, { 0, 1 },
// { 0.5, 0 }, { 0.5, 0.5 }, { 0.5, 1 }, { 1, 0 }, { 1, 0.5 }, { 1, 1 } };
//
// public final static int[] CORNER_CURSORS = { Cursor.DEFAULT_CURSOR,
// Cursor.NW_RESIZE_CURSOR,
// Cursor.W_RESIZE_CURSOR, Cursor.SW_RESIZE_CURSOR, Cursor.N_RESIZE_CURSOR,
// Cursor.MOVE_CURSOR, Cursor.S_RESIZE_CURSOR, Cursor.NE_RESIZE_CURSOR,
// Cursor.E_RESIZE_CURSOR, Cursor.SE_RESIZE_CURSOR };
//
// public final static Rect[] copyArray(Rect[] array) {
// Rect[] result = null;
// if (array != null) {
// result = new Rect[array.length];
// for (int i = 0; i < array.length; ++i) {
// result[i] = array[i].copyRect();
// }
// }
// return result;
// }
//
// public final static Rect[] createArrayViaFile(DataInputStream in) throws
// Exception {
// Rect[] result = null;
// int length = in.readInt();
// if (length > 0) {
// result = new Rect[length];
// for (int i = 0; i < length; ++i) {
// result[i] = createViaFile(in);
// }
// }
// return result;
// }
//
// public final static Rect createViaFile(DataInputStream in) throws Exception {
// Rect result = new Rect();
// result.load(in);
// return result;
// }
//
// private final static int reverseX(int corner) {
// switch (corner) {
// case CORNER_LEFT_TOP:
// return CORNER_RIGHT_TOP;
// case CORNER_LEFT_MIDDLE:
// return CORNER_RIGHT_MIDDLE;
// case CORNER_LEFT_BOTTOM:
// return CORNER_RIGHT_BOTTOM;
// case CORNER_RIGHT_TOP:
// return CORNER_LEFT_TOP;
// case CORNER_RIGHT_MIDDLE:
// return CORNER_LEFT_MIDDLE;
// case CORNER_RIGHT_BOTTOM:
// return CORNER_LEFT_BOTTOM;
// default:
// return corner;
// }
// }
//
// private final static int reverseY(int corner) {
// switch (corner) {
// case CORNER_LEFT_TOP:
// return CORNER_LEFT_BOTTOM;
// case CORNER_LEFT_BOTTOM:
// return CORNER_LEFT_TOP;
// case CORNER_CENTER_TOP:
// return CORNER_CENTER_BOTTOM;
// case CORNER_CENTER_BOTTOM:
// return CORNER_CENTER_TOP;
// case CORNER_RIGHT_TOP:
// return CORNER_RIGHT_BOTTOM;
// case CORNER_RIGHT_BOTTOM:
// return CORNER_RIGHT_TOP;
// default:
// return corner;
// }
// }
//
// public int x, y, width, height;
//
// private Rect() {
// this.x = this.y = this.width = this.height = 0;
// }
//
// public Rect(int x, int y, int width, int height) {
// this.x = x;
// this.y = y;
// this.width = width;
// this.height = height;
// }
//
// public boolean contains(int x, int y) {
// return this.x <= x && this.x + this.width >= x && this.y <= y && this.y +
// this.height >= y;
// }
//
// public Copyable copy() {
// return copyRect();
// }
//
// public void copyFrom(Rect source) {
// this.x = source.x;
// this.y = source.y;
// this.width = source.width;
// this.height = source.height;
// }
//
// public final Rect copyRect() {
// return new Rect(x, y, width, height);
// }
//
// public Rect getCopy() {
// return new Rect(x, y, width, height);
// }
//
// public Cursor getCornerCursor(int corner) {
// if (corner >= 0 && corner < CORNER_CURSORS.length) {
// return Cursor.getPredefinedCursor(CORNER_CURSORS[corner]);
// }
// else {
// return Cursor.getDefaultCursor();
// }
// }
//
// private IntPair getCornerXY(int corner, double scaleX, double scaleY, int
// cornerSize) {
// int cornerX = (int) ((this.x + CORNER_OFFSETS[corner][0] * this.width) *
// scaleX)
// - (cornerSize / 2);
// int cornerY = (int) ((this.y + CORNER_OFFSETS[corner][1] * this.height) *
// scaleY)
// - (cornerSize / 2);
// return new IntPair(cornerX, cornerY);
// }
//
// public int getSelectCorner(int x, int y, double scaleX, double scaleY) {
// return getSelectCorner(x, y, scaleX, scaleY, CORNER_SIZE);
// }
//
// public int getSelectCorner(int x, int y, double scaleX, double scaleY, int
// cornerSize) {
// int result = CORNER_NONE;
//
// for (int i = 0; i < CORNERS.length; ++i) {
// IntPair cornerXY = getCornerXY(CORNERS[i], scaleX, scaleY, cornerSize);
// int cornerX = cornerXY.x;
// int cornerY = cornerXY.y;
// if (x >= cornerX && x <= cornerX + cornerSize && y >= cornerY
// && y <= cornerY + cornerSize) {
// result = CORNERS[i];
// break;
// }
//
// }
// return result;
// }
//
// public void load(DataInputStream in) throws Exception {
// x = in.readInt();
// y = in.readInt();
// width = in.readInt();
// height = in.readInt();
// }
//
// public void paintCorner(Graphics g) {
// paintCorner(g, CORNER_SIZE);
// }
//
// public void paintCorner(Graphics g, int cornerSize) {
// Graphics2D g2 = (Graphics2D) g;
// AffineTransform oldTransform = g2.getTransform();
// double scaleX = oldTransform.getScaleX();
// double scaleY = oldTransform.getScaleY();
// g2.scale(1 / scaleX, 1 / scaleY);
// for (int i = 0; i < CORNERS.length; ++i) {
// IntPair cornerXY = getCornerXY(CORNERS[i], scaleX, scaleY, cornerSize);
// int cornerX = cornerXY.x;
// int cornerY = cornerXY.y;
// g2.fillRect(cornerX, cornerY, cornerSize, cornerSize);
// }
// g2.setTransform(oldTransform);
// }
//
// public int resizeByCorner(int corner, int offsetX, int offsetY) {
// if (corner == CORNER_NONE) { return corner; }
//
// int left = this.x;
// int top = this.y;
// int right = this.x + this.width;
// int bottom = this.y + this.height;
//
// switch (corner) {
// case CORNER_LEFT_TOP:
// left += offsetX;
// top += offsetY;
// break;
// case CORNER_LEFT_MIDDLE:
// left += offsetX;
// break;
// case CORNER_LEFT_BOTTOM:
// left += offsetX;
// bottom += offsetY;
// break;
// case CORNER_CENTER_TOP:
// top += offsetY;
// break;
// case CORNER_CENTER_MIDDLE:
// left += offsetX;
// right += offsetX;
// top += offsetY;
// bottom += offsetY;
// break;
// case CORNER_CENTER_BOTTOM:
// bottom += offsetY;
// break;
// case CORNER_RIGHT_TOP:
// right += offsetX;
// top += offsetY;
// break;
// case CORNER_RIGHT_MIDDLE:
// right += offsetX;
// break;
// case CORNER_RIGHT_BOTTOM:
// right += offsetX;
// bottom += offsetY;
// break;
// }
//
// this.x = Math.min(left, right);
// this.y = Math.min(top, bottom);
// this.width = Math.abs(right - left);
// this.height = Math.abs(bottom - top);
//
// int newCorner = corner;
// if (left >= right) {
// newCorner = reverseX(newCorner);
// }
// if (top >= bottom) {
// newCorner = reverseY(newCorner);
// }
// return newCorner;
// }
//
// public void save(DataOutputStream out) throws Exception {
// out.writeInt(x);
// out.writeInt(y);
// out.writeInt(width);
// out.writeInt(height);
// }
//
// public void saveMobile(DataOutputStream out) throws Exception {
// SL.writeInt(x, out);
// SL.writeInt(y, out);
// SL.writeInt(width, out);
// SL.writeInt(height, out);
// }
// }

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

	public final static int[][] RELATIONS = { { LESS, LORE, EQUAL, GORE, GREATER },
	        { SET, PLUS, REDUCE } };

	public final static String[] DESCRIPTIONS = { " < ", " <= ", " == ", " >= ", " > ", " = ",
	        " + ", " - " };
}

interface Saveable {

	public void save(DataOutputStream out) throws Exception;

	public void saveMobile(DataOutputStream out) throws Exception;
}

class SL {

	private final static void basicWriteByte(int data, DataOutputStream out) throws Exception {
		out.writeByte(data & 0xFF);
	}

	public final static int[] readIntArray(DataInputStream in) throws Exception {
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

	public final static String readString(DataInputStream in) throws Exception {
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

	public final static void saveArray(Saveable[] array, DataOutputStream out) throws Exception {
		if (array == null) {
			out.writeInt(0);
		}
		else {
			out.writeInt(array.length);
			for (int i = 0; i < array.length; ++i) {
				array[i].save(out);
			}
		}
	}

	public final static void saveArrayMobile(Saveable[] array, DataOutputStream out)
	        throws Exception {
		if (array == null) {
			writeInt(0, out);
		}
		else {
			writeInt(array.length, out);
			for (int i = 0; i < array.length; ++i) {
				array[i].saveMobile(out);
			}
		}
	}

	public final static void saveArray(ArrayList array, DataOutputStream out) throws Exception {
		if (array == null) {
			out.writeInt(0);
		}
		else {
			out.writeInt(array.size());
			for (int i = 0; i < array.size(); ++i) {
				Object obj = array.get(i);
				if(obj instanceof Saveable) {
					((Saveable)obj).save(out);
				}
				else if(obj instanceof Integer) {
					out.writeInt(((Integer)obj).intValue());
				}
			}
		}
	}

	public final static void saveArrayMobile(ArrayList array, DataOutputStream out)
	        throws Exception {
		if (array == null) {
			writeInt(0, out);
		}
		else {
			writeInt(array.size(), out);
			for (int i = 0; i < array.size(); ++i) {
				Object obj = array.get(i);
				if(obj instanceof Saveable) {
					((Saveable)obj).saveMobile(out);
				}
				else if(obj instanceof Integer) {
					writeInt(((Integer)obj).intValue(), out);
				}
			}
		}
	}

	public final static void writeArray(int[] data, DataOutputStream out) throws Exception {
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

	public final static void writeArrayMobile(int[] data, DataOutputStream out) throws Exception {
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

	public final static void writeBoolean(boolean data, DataOutputStream out) throws Exception {
		basicWriteByte(data ? 1 : 0, out);
	}

	public final static void writeByte(int data, DataOutputStream out) throws Exception {
		basicWriteByte(data, out);
	}

	public final static void writeDouble(double data, DataOutputStream out) throws Exception {
		writeInt((int) (data * 100), out);
	}

	public final static void writeInt(int data, DataOutputStream out) throws Exception {
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

	public final static void writeShort(int data, DataOutputStream out) throws Exception {
		if (XUtil.getDefPropInt("Type") == 0) {// Symbian
			basicWriteByte((data & 0xFF), out);
			basicWriteByte(((data & 0xFF00) >> 8), out);
		}
		else {// Java
			basicWriteByte(((data & 0xFF00) >> 8), out);
			basicWriteByte((data & 0xFF), out);
		}
	}

	public final static void writeString(String data, DataOutputStream out) throws Exception {
		if (data == null) {
			out.writeInt(0);
		}
		else {
			byte[] bytes = data.getBytes();
			if (bytes == null) {
				out.writeInt(0);
			}
			else {
				int len = bytes.length;
				out.writeInt(bytes.length);
				out.write(bytes);
			}
		}
	}

	public final static void writeStringUnic(String data, DataOutputStream out) throws Exception {
		if (data == null) {
			writeInt(0, out);
			return;
		}
		data = data.replaceAll("\r\n", "\n");
		data = data.replaceAll("\r", "\n");
		// String strUnic = new String(data.getBytes("UTF8"), "UTF8");
		writeInt(data.length(), out);
		for (int i = 0; i < data.length(); ++i) {
			writeShort(data.charAt(i), out);
		}
	}
}

public class XUtil {

	public final static int SCREEN_W = 176, SCREEN_H = 208;
	public final static int LEFT_BUTTON = MouseEvent.BUTTON1, RIGHT_BUTTON = MouseEvent.BUTTON3;

	private static Properties DEF_PROP_STR;
	private static Properties DEF_PROP_INT;
	private static boolean PROP_LOADED = false;

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

	public static String[] copyArray(String[] data) {
		String[] result = null;
		if (data != null) {
			result = new String[data.length];
			for (int i = 0; i < data.length; ++i) {
				result[i] = data[i];
			}
		}
		return result;
	}

	public static boolean copyFile(String source, String dest) {
		boolean result = false;
		try {
			File sourceFile = new File(source);
			File destFile = new File(dest);
			if (sourceFile.exists()) {
				// DataInputStream in =
				// new DataInputStream(
				// new BufferedInputStream(
				// new FileInputStream(sourceFile)));
				FileInputStream in = new FileInputStream(sourceFile);
				// DataOutputStream out =
				// new DataOutputStream(
				// new BufferedOutputStream(
				// new FileOutputStream(destFile)));
				FileOutputStream out = new FileOutputStream(destFile);
				byte[] bytes = new byte[1024 * 4];
				int length;
				while ((length = in.read(bytes)) != -1) {
					out.write(bytes, 0, length);
				}
				out.flush();
				out.close();
				in.close();
				result = true;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static String readFile(File f) {
		if(!f.exists()) return "";
		StringBuffer s = new StringBuffer();
		
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			String sLine;
			sLine = in.readLine();
			while (sLine != null) {
				s.append(sLine);
				s.append("\r\n");
				sLine = in.readLine();
			}
			in.close();

		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return s.toString();
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

	public static String getNumberString(int value) {
		return getNumberString(value, 4);
	}

	public static String getNumberString(int value, int numberLength) {
		if (value == 0) {
			String result = "";
			for (int i = 0; i < numberLength; ++i) {
				result += "0"; // 零
			}
			return result;
		}
		for (int i = 0; i < numberLength - 1; ++i) {
			if (value >= Math.pow(10, i) && value < Math.pow(10, (i + 1))) {
				String result = "";
				for (int j = i + 1; j < numberLength; ++j) {
					result += "0"; // 零
				}
				result += value + "";
				return result;
			}
		}
		return value + "";
	}

	private static void initDefProp() throws IOException {
		if (PROP_LOADED) {
			// return;
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

}
