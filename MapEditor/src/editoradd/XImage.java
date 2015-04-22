package editoradd;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.Raster;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;

public class XImage {

	private final static void basicWriteByte(int data, DataOutputStream out) throws Exception {
		out.writeByte(data & 0xFF);
	}

	private final static boolean equalsColor(Color color, Color maskColor) {
		if (get4BitValue(color.getRed()) == get4BitValue(maskColor.getRed())
		        && get4BitValue(color.getBlue()) == get4BitValue(maskColor.getBlue())
		        && get4BitValue(color.getGreen()) == get4BitValue(maskColor.getGreen())) { return true; }
		return false;
	}

	public final static int get4BitRGB(Color color) {
		return (get4BitValue(color.getRed()) << 8) | (get4BitValue(color.getGreen()) << 4)
		        | (get4BitValue(color.getBlue()));
	}

	public final static int get4BitValue(int value) {
		return ((value & 0xF0) >> 4);
	}

	// readBMP
	public final static BufferedImage readBMP(File f, Color maskColor) {
		return readBMP(f, maskColor, null);
	}

	public final static BufferedImage readBMP(File f, Color maskColor, XImage image) {
		BufferedImage result = null;
		try {
			FileInputStream fs = new FileInputStream(f);

			// 读取14字节BMP文件头
			int fileHeadLen = 14;
			byte fileHeadData[] = new byte[fileHeadLen];
			fs.read(fileHeadData, 0, fileHeadLen);
			if (fileHeadData[0] != 'B' || fileHeadData[1] != 'M') throw new Exception("不是BMP文件");

			// 读取40字节BMP信息头
			int fileInfoLen = 40;
			byte fileInfoData[] = new byte[fileInfoLen];
			fs.read(fileInfoData, 0, fileInfoLen);

			// 获取一些重要数据
			int width = ((fileInfoData[7] & 0xFF) << 24)// 源图宽度
			        | ((fileInfoData[6] & 0xFF) << 16)
			        | ((fileInfoData[5] & 0xFF) << 8)
			        | (fileInfoData[4] & 0xFF);
			// System.out.println("宽:" + width);

			int height = ((fileInfoData[11] & 0xFF) << 24)// 源图高度
			        | ((fileInfoData[10] & 0xFF) << 16)
			        | ((fileInfoData[9] & 0xFF) << 8)
			        | (fileInfoData[8] & 0xFF);
			// System.out.println("高:"+height);

			// 位数
			int bitCount = ((fileInfoData[15] & 0xFF) << 8) | (fileInfoData[14] & 0xFF);

			// System.out.println("位数:" + bitCount);

			int[] argbData = new int[width * height];

			if (bitCount == 24) {// 对24位BMP进行解析
				if (image != null) image.palettes = null;
				int pad = (4 - ((width * 3) % 4)) % 4;// 每个像素是3个byte的数据，但是一行的数据总和必须是4的倍数，如果不是则使用pad个空数据补齐
				// System.out.println("pad = " + pad);
				byte[] fileData = new byte[(width * 3 + pad) * height];
				fs.read(fileData, 0, fileData.length);
				int index = 0;
				int argb;
				for (int y = 0; y < height; ++y) {
					for (int x = 0; x < width; ++x) {
						argb = (0xFF << 24)// Alpha
						        | ((fileData[index + 2] & 0xFF) << 16)// Red
						        | ((fileData[index + 1] & 0xFF) << 8)// Green
						        | (fileData[index] & 0xFF);// Blue
						if (equalsColor(new Color(argb, true), maskColor)) argb = 0;
						argbData[width * (height - 1 - y) + x] = argb;
						index += 3;
					}
					index += pad;
				}
			}
			else if (bitCount == 1) {// 对1位BMP进行解析
				int bytesPerRow = ((width + 31) >> 5) << 2; // Bytes Per
				// Row,一个byte包含8个像素的数据
				byte[] fileData = new byte[bytesPerRow * height];
				fs.skip(8);// skip FF FF FF 00 00 00 00 00 黑色和白色
				fs.read(fileData, 0, fileData.length);
				for (int y = 0; y < height; ++y) {
					for (int x = 0; x < width; ++x) {
						int bitColor = (~(fileData[y * bytesPerRow + (x >> 3)]) >> (7 - (x % 8))) & 1;
						int argb = (bitColor == 1 ? 0 : 0xFF000000);
						argbData[width * (height - 1 - y) + x] = argb;
					}
				}
			}
			else if (bitCount == 8) {// 对8位BMP进行解析
				// 读取调色板
				int paletteCount = ((fileInfoData[35] & 0xFF) << 24)// 调色板数
				        | ((fileInfoData[34] & 0xFF) << 16)
				        | ((fileInfoData[33] & 0xFF) << 8)
				        | (fileInfoData[32] & 0xFF);
				if(paletteCount == 0) paletteCount = 256;
				byte[] paletteData = new byte[paletteCount * 4];
				fs.read(paletteData, 0, paletteData.length);
				int[] palettes = new int[paletteCount];
				for (int i = 0; i < paletteCount; ++i) {
					int di = i * 4;
					palettes[i] = ((paletteData[di + 2] & 0xFF) << 16)// 颜色
					        | ((paletteData[di + 1] & 0xFF) << 8) | (paletteData[di] & 0xFF);
					palettes[i] &= 0xFFFFFF;
				}
				if (image != null) image.palettes = palettes;
				// 读取像素
				int pad = (4 - (width % 4)) % 4;// 每个像素是1个byte的数据，但是一行的数据总和必须是4的倍数，如果不是则使用pad个空数据补齐
				byte[] fileData = new byte[(width + pad) * height];
				fs.read(fileData, 0, fileData.length);
				int index = 0;
				int argb;
				for (int y = 0; y < height; ++y) {
					for (int x = 0; x < width; ++x) {
						argb = (0xFF << 24) | palettes[(fileData[index] & 0xFF)];
						if (equalsColor(new Color(argb, true), maskColor)) argb = 0;
						argbData[width * (height - 1 - y) + x] = argb;
						++index;
					}
					index += pad;
				}
			}
			else {
				throw new Exception("不是 24 位、8位或者 1 位的BMP图。");
			}

			// create Image
			Toolkit kit = Toolkit.getDefaultToolkit();
			Image tmp = kit.createImage(new MemoryImageSource(width, height, argbData, 0, width));
			result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics g = result.createGraphics();
			g.drawImage(tmp, 0, 0, null);
			result.flush();
			fs.close(); // 关闭输入流
		}
		catch (Exception e) {
			result = null;
			e.printStackTrace();
		}
		return result;
	}

	// readPNG
	public final static BufferedImage readPNG(File f, Color maskColor) {
		BufferedImage img;
		try {
			if (!FileExtFilter.getExtension(f).equalsIgnoreCase("png")) { throw new Exception(
			        "不是PNG图片"); }
			img = ImageIO.read(f);
		}
		catch (Exception e) {
			img = null;
			e.printStackTrace();
		}
		
		Raster raster = img.getData();
		ColorModel model = img.getColorModel();
		int[] colors = new int[img.getWidth() * img.getHeight()];
		Object data;
		Color color;

		int index = 0;
		for (int y = 0; y < img.getHeight(); ++y) {
			for (int x = 0; x < img.getWidth(); ++x) {
				data = raster.getDataElements(x, y, null);
				color = new Color(model.getRGB(data), true);
				if(maskColor != null) {
					if(equalsColor(color, maskColor)) {
						color = new Color(0, true);
					}
				}
				colors[index++] = color.getRGB();
				
			}
		}

		Toolkit kit = Toolkit.getDefaultToolkit();
		Image tmp = kit.createImage(new MemoryImageSource(img.getWidth(), img.getHeight(), colors, 0, img.getWidth()));
		BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = result.createGraphics();
		g.drawImage(tmp, 0, 0, null);
		result.flush();
		
		return result;
	}

	private final static void writeByte(int data, DataOutputStream out) throws Exception {
		basicWriteByte(data, out);
	}

	private final static void writeInt(int data, DataOutputStream out) throws Exception {
		basicWriteByte((data & 0xFF), out);
		basicWriteByte(((data & 0xFF00) >> 8), out);
		basicWriteByte(((data & 0xFF0000) >> 16), out);
		basicWriteByte(((data & 0xFF000000) >> 24), out);
	}

	private final static void writeShort(int data, DataOutputStream out) throws Exception {
		basicWriteByte((data & 0xFF), out);
		basicWriteByte(((data & 0xFF00) >> 8), out);
	}

	private int left, top, width, height, basicWidth, basicHeight;
	private BufferedImage image;
	private Color[][] argbColor;
	private File fparent;
	private String name;

	private String ext;

	private Color maskColor;

	private int id;

	private boolean changed;

	private int[] palettes = null;
	
	private boolean solid = false;

	public XImage(File f, Color maskColor) throws Exception {
		id = 0;
		read(f, maskColor);
	}

	public boolean equals(XImage other) {
		if (other == null) return false;
		if (other.width != this.width || other.height != this.height) return false;

		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				Color selfColor = this.argbColor[x][y], otherColor = other.argbColor[x][y];

				if (selfColor.getAlpha() != otherColor.getAlpha()
				        || selfColor.getRed() != otherColor.getRed()
				        || selfColor.getGreen() != otherColor.getGreen()
				        || selfColor.getBlue() != otherColor.getBlue()) return false;

				if (!equalsColor(selfColor, otherColor)) return false;
			}
		}

		return true;
	}

	public int getBasicHeight() {
		return basicHeight;
	}

	public int getBasicWidth() {
		return basicWidth;
	}

	public Color[][] getData() {
		return argbColor;
	}

	public String getExtension() {
		return ext;
	}

	public int getHeight() {
		return height;
	}

	public int getID() {
		return id;
	}

	public BufferedImage getImage() {
		return image;
	}

	public int getLeft() {
		return left;
	}

	public String getName() {
		return name;
	}

	public int getTop() {
		return top;
	}

	public int getWidth() {
		return width;
	}

	public boolean isChanged() {
		return changed;
	}

	public void read(File f, Color maskColor) throws Exception {
		fparent = new File(f.getParent());
		name = FileExtFilter.getName(f);
		ext = FileExtFilter.getExtension(f);
		this.maskColor = maskColor;

		BufferedImage basicImage = null;
		if (ext.equalsIgnoreCase("png")) {
			basicImage = readPNG(f, maskColor);
		}
		else if (ext.equalsIgnoreCase("bmp")) {
			basicImage = readBMP(f, maskColor, this);
		}
		else if(ext.equalsIgnoreCase("jpg")) {
			basicImage = ImageIO.read(f);
		}
		if (basicImage == null) throw new Exception("无法读取源文件" + f.getAbsolutePath());
		basicWidth = basicImage.getWidth();
		basicHeight = basicImage.getHeight();

		Raster raster = basicImage.getData();
		ColorModel model = basicImage.getColorModel();
		Color[][] colors = new Color[basicImage.getWidth()][basicImage.getHeight()];
		Object data;
		Color color;

		for (int y = 0; y < basicImage.getHeight(); ++y) {
			for (int x = 0; x < basicImage.getWidth(); ++x) {
				data = raster.getDataElements(x, y, null);
				color = new Color(model.getRGB(data), true);
				colors[x][y] = color;
			}
		}

		boolean found;
		int minX = 0;
		found = false;
		while (minX < basicImage.getWidth() && !found) {
			for (int y = 0; y < basicImage.getHeight(); ++y) {
				if (colors[minX][y].getAlpha() != 0) {
					found = true;
					break;
				}
			}
			if (!found) ++minX;
		}
		if (minX >= basicImage.getWidth()) minX = basicImage.getWidth() - 1;

		int maxX = basicImage.getWidth() - 1;
		found = false;
		while (maxX >= minX && !found) {
			for (int y = 0; y < basicImage.getHeight(); ++y) {
				if (colors[maxX][y].getAlpha() != 0) {
					found = true;
					break;
				}
			}
			if (!found) --maxX;
		}
		if (maxX < minX) maxX = minX;

		int minY = 0;
		found = false;
		while (minY < basicImage.getHeight() && !found) {
			for (int x = 0; x < basicImage.getWidth(); ++x) {
				if (colors[x][minY].getAlpha() != 0) {
					found = true;
					break;
				}
			}
			if (!found) ++minY;
		}
		if (minY >= basicImage.getHeight()) minY = basicImage.getHeight() - 1;

		int maxY = basicImage.getHeight() - 1;
		found = false;
		while (maxY >= minY && !found) {
			for (int x = 0; x < basicImage.getWidth(); ++x) {
				if (colors[x][maxY].getAlpha() != 0) {
					found = true;
					break;
				}
			}
			if (!found) --maxY;
		}
		if (maxY < minY) maxY = minY;

		// create real data
		left = minX;
		top = minY;
		width = maxX - minX + 1;
		height = maxY - minY + 1;
		changed = !(left == 0 && top == 0 && width == basicWidth && height == basicHeight);
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.createGraphics();
		g.drawImage(basicImage, -left, -top, null);
		image.flush();

		argbColor = new Color[width][height];
		solid = true;
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				argbColor[x][y] = colors[left + x][top + y];
				int alpha = argbColor[x][y].getAlpha();
				if(alpha != 0 && alpha != 0xFF) {
					solid = false;
				}
			}
		}
		// if(name.equals("0100000")) {
		// Color c = argbColor[33][26];
		// System.out.println(name + " " + c.getRed() + " " + c.getGreen() + " "
		// + c.getBlue());
		// }
	}

	public void save() throws Exception {
		save(fparent);
	}

	public void save(File parent) throws Exception {
		if (ext.equalsIgnoreCase("png")) {
			savePNG(parent);
		}
		else if (ext.equalsIgnoreCase("bmp")) {
			saveBMP(parent);
		}
	}

	private void saveBMP(File parent) throws Exception {
		String fileFullName = parent.getPath() + "\\" + name + ".bmp";
		DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(
		        fileFullName)));
		if(palettes == null) {
			saveBMP24(out);
		}
		else {
			saveBMP8(out);
		}
		out.flush();
		out.close();
	}
	
	private void saveBMP24(DataOutputStream out) throws Exception {
		int pad = (4 - ((width * 3) % 4)) % 4;// 每个像素是3个byte的数据，但是一行的数据总和必须是4的倍数，如果不是则使用pad个空数据补齐

		// 14字节的文件头
		writeByte(0x42, out);// 'B'
		writeByte(0x4D, out);// 'M'
		int fileSize = 14 + 40 + ((width * 3) + pad) * height + 2;// 最后的2是为了使整个文件大小能够被4整除
		writeInt(fileSize, out);
		writeInt(0, out);
		writeInt(0x36, out);// 14 + 40 = 54 = 0x36

		// 40字节信息头
		writeInt(40, out);// 信息头长度
		writeInt(width, out);
		writeInt(height, out);
		writeShort(1, out);
		writeShort(24, out);// 24位BMP
		writeInt(0, out);// 无压缩
		writeInt(0, out);// 24位图无压缩的话此处必为0
		writeInt(0xB12, out);// horizontal resolution: Pixels/meter
		writeInt(0xB12, out);// vertical resolution: Pixels/meter
		writeInt(0, out);// 本图像实际用到的颜色数，如果该值为0，则用到的颜色数为2的(颜色位数)次幂
		writeInt(0, out);// 指定本图像中重要的颜色数，如果该值为0，则认为所有的颜色都是重要的

		// 数据
		for (int y = height - 1; y >= 0; --y) {
			for (int x = 0; x < width; ++x) {
				if (argbColor[x][y].getAlpha() == 0) {// 如果是透明的则存为maskColor
					writeByte(maskColor.getBlue(), out);
					writeByte(maskColor.getGreen(), out);
					writeByte(maskColor.getRed(), out);
				}
				else {// 否则存为原始颜色
					writeByte(argbColor[x][y].getBlue(), out);
					writeByte(argbColor[x][y].getGreen(), out);
					writeByte(argbColor[x][y].getRed(), out);
				}
			}
			for (int p = 0; p < pad; ++p) {// 补0
				writeByte(0, out);
			}
		}

		writeByte(0, out);
		writeByte(0, out);
	}
	
	private void saveBMP8(DataOutputStream out) throws Exception {
		int pad = (4 - (width % 4)) % 4;// 每个像素是1个byte的数据，但是一行的数据总和必须是4的倍数，如果不是则使用pad个空数据补齐
		
		// 14字节的文件头
		writeByte(0x42, out);// 'B'
		writeByte(0x4D, out);// 'M'
		int fileSize = 14 + 40 + palettes.length * 4 + (width + pad) * height + 2;// 最后的2是为了使整个文件大小能够被4整除
		writeInt(fileSize, out);
		writeInt(0, out);
		writeInt(54 + palettes.length * 4, out);// 14 + 40 = 54

		// 40字节信息头
		writeInt(40, out);// 信息头长度
		writeInt(width, out);
		writeInt(height, out);
		writeShort(1, out);
		writeShort(8, out);// 8位BMP
		writeInt(0, out);// 无压缩
		writeInt((width + pad) * height + 2, out);// 无压缩的话此处可为0，一般表示除掉头信息和调色板之后剩下的所有字节数
		writeInt(0xB12, out);// horizontal resolution: Pixels/meter
		writeInt(0xB12, out);// vertical resolution: Pixels/meter
		writeInt(((palettes.length == 256) ? 0 : palettes.length), out);// 本图像实际用到的颜色数，如果该值为0，则用到的颜色数为2的(颜色位数)次幂
		writeInt(((palettes.length == 256) ? 0 : palettes.length), out);// 指定本图像中重要的颜色数，如果该值为0，则认为所有的颜色都是重要的
		
		for(int i = 0; i < palettes.length; ++i) {
			writeInt(palettes[i], out);
		}
		
		// 数据
		for (int y = height - 1; y >= 0; --y) {
			for (int x = 0; x < width; ++x) {
				Color color;
				if (argbColor[x][y].getAlpha() == 0) {// 如果是透明的则存为maskColor
					color = maskColor;
				}
				else {// 否则存为原始颜色
					color = argbColor[x][y];
				}
				for(int i = 0; i < palettes.length; ++i) {
					if(color.equals(new Color(palettes[i]))) {
						writeByte(i, out);
						break;
					}
				}
			}
			for (int p = 0; p < pad; ++p) {// 补0
				writeByte(0, out);
			}
		}

		writeByte(0, out);
		writeByte(0, out);
	}
	
	public void saveXMG(File file, int xmgType) throws Exception {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = img.createGraphics();
		if(solid) {
			g.setColor(new Color(0xFFFF00FF));
			g.fillRect(0, 0, width, height);
		}
		g.drawImage(image, 0, 0, null);
		img.flush();
		
		ImageIO.write(img, "png", file);
		String xmgStr = XUtil.getDefPropStr("XMG" + xmgType).trim();
		if(xmgStr.equals("")) {
			xmgStr = "png2xmg.exe";
		}
		Runtime.getRuntime().exec(xmgStr + " " + file.getAbsolutePath());
		//System.out.println(file);
	}

	public void savePNG(File parent) throws Exception {
		if (XUtil.getDefPropInt("Type") == 0) {// Symbian
			//saveRWA(parent);
		}
		File f = new File(parent.getPath() + "\\" + name + ".png");
		ImageIO.write(image, "png", f);
	}

	private void saveRWA(File parent) throws Exception {
		String fileFullName = parent.getPath() + "\\" + name + ".rwa";
		DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(
		        fileFullName)));

		writeInt(width, out);
		writeInt(height, out);
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				writeByte(argbColor[x][y].getAlpha(), out);
				writeByte(argbColor[x][y].getRed(), out);
				writeByte(argbColor[x][y].getGreen(), out);
				writeByte(argbColor[x][y].getBlue(), out);
			}
		}
		out.flush();
		out.close();
	}

	public void setID(int id) {
		this.id = id;
	}
}