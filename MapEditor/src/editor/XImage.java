package editor;

import java.io.*;
import javax.imageio.*;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

/**
 * 用于读取图片文件以及作为Animation的基础单位，不能用于别处。
 */
public class XImage {
	private int left, top, width, height; //这是相对于原始图左上角的偏移量
	private int basicWidth, basicHeight;
	private BufferedImage image;
	private BufferedImage flippedImage;
//	private Color[][] argbColor;
	private String name;
	private String ext;
	private Color maskColor;
	private int layer;
	private int id;

	public XImage(File f, Color maskColor, int layer) throws Exception {
		id = 0;
		read(f, maskColor, layer);
	}

	public int getLeft() {
		return left;
	}

	public int getTop() {
		return top;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getBasicWidth() {
		return basicWidth;
	}

	public int getBasicHeight() {
		return basicHeight;
	}

	public BufferedImage getImage() {
		return image;
	}

//	public Color[][] getData() {
//		return argbColor;
//	}

	public String getName() {
		return name;
	}

	public String getExtension() {
		return ext;
	}

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public int getLayer() {
		return layer;
	}

	public void read(File f, Color maskColor, int layer) throws Exception {
		Color[][] argbColor;
		name = FileExtFilter.getName(f) + Animation.FILE_MARK + "_" + layer;
		ext = FileExtFilter.getExtension(f);
		if (ext.equalsIgnoreCase("PNG")) {
			ext = "rwa";
		}
		this.maskColor = maskColor;
		this.layer = layer;

		BufferedImage basicImage = null;
		if (ext.equalsIgnoreCase("rwa")) {
			basicImage = readPNG(f);
		}
		else if (ext.equalsIgnoreCase("bmp")) {
			basicImage = readBMP(f, maskColor);
		}
		if (basicImage == null) {
			throw new Exception("无法读取源文件");
		}
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
			if (!found) {
				++minX;
			}
		}
		if (minX >= basicImage.getWidth()) {
			minX = basicImage.getWidth() - 1;

		}
		int maxX = basicImage.getWidth() - 1;
		found = false;
		while (maxX >= minX && !found) {
			for (int y = 0; y < basicImage.getHeight(); ++y) {
				if (colors[maxX][y].getAlpha() != 0) {
					found = true;
					break;
				}
			}
			if (!found) {
				--maxX;
			}
		}
		if (maxX < minX) {
			maxX = minX;

		}
		int minY = 0;
		found = false;
		while (minY < basicImage.getHeight() && !found) {
			for (int x = 0; x < basicImage.getWidth(); ++x) {
				if (colors[x][minY].getAlpha() != 0) {
					found = true;
					break;
				}
			}
			if (!found) {
				++minY;
			}
		}
		if (minY >= basicImage.getHeight()) {
			minY = basicImage.getHeight() - 1;

		}
		int maxY = basicImage.getHeight() - 1;
		found = false;
		while (maxY >= minY && !found) {
			for (int x = 0; x < basicImage.getWidth(); ++x) {
				if (colors[x][maxY].getAlpha() != 0) {
					found = true;
					break;
				}
			}
			if (!found) {
				--maxY;
			}
		}
		if (maxY < minY) {
			maxY = minY;

			//create real data
		}
		left = minX;
		top = minY;
		width = maxX - minX + 1;
		height = maxY - minY + 1;
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.createGraphics();
		g.drawImage(basicImage, -left, -top, null);
		image.flush();
		
//			ImageIO.write(result, "png", new File(".\\tmp.png"));
//			result = ImageIO.read(new File(".\\tmp.png"));
		
		AffineTransform at = new AffineTransform( -1, 0, 0, 1, width, 0);
		AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		flippedImage = ato.filter(image, null);

		argbColor = new Color[width][height];
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				argbColor[x][y] = colors[left + x][top + y];
			}
		}
//		if(name.equals("0100000")) {
//			Color c = argbColor[33][26];
//			System.out.println(name + "  " + c.getRed() + "  " + c.getGreen() + "  " + c.getBlue());
//		}
	}

//	public void save(File parent) throws Exception {
//		if (ext.equalsIgnoreCase("rwa")) {
//			saveRWA(parent);
//		}
//		else if (ext.equalsIgnoreCase("bmp")) {
//			saveBMP(parent);
//		}
//	}

//	private void saveRWA(File parent) throws Exception {
//		String fileFullName = parent.getPath() + "\\" + name + ".rwa";
//		DataOutputStream out =
//			new DataOutputStream(
//			new BufferedOutputStream(
//			new FileOutputStream(fileFullName)));
//
//		SL.writeInt(width, out);
//		SL.writeInt(height, out);
//		for (int y = 0; y < height; ++y) {
//			for (int x = 0; x < width; ++x) {
//				SL.writeByte(argbColor[x][y].getAlpha(), out);
//				SL.writeByte(argbColor[x][y].getRed(), out);
//				SL.writeByte(argbColor[x][y].getGreen(), out);
//				SL.writeByte(argbColor[x][y].getBlue(), out);
//			}
//		}
//		out.flush();
//		out.close();
//	}
//
//	private void saveBMP(File parent) throws Exception {
//		String fileFullName = parent.getPath() + "\\" + name + ".bmp";
//		DataOutputStream out =
//			new DataOutputStream(
//			new BufferedOutputStream(
//			new FileOutputStream(fileFullName)));
//
//		int pad = (4 - ( (width * 3) % 4)) % 4; //每个像素是3个byte的数据，但是一行的数据总和必须是4的倍数，如果不是则使用pad个空数据补齐
//
//		//14字节的文件头
//		SL.writeByte(0x42, out); //'B'
//		SL.writeByte(0x4D, out); //'M'
//		int fileSize = 14 + 40 + ( (width * 3) + pad) * height + 2; //最后的2是为了使整个文件大小能够被4整除
//		SL.writeInt(fileSize, out);
//		SL.writeInt(0, out);
//		SL.writeInt(0x36, out); //14 + 40 = 54 = 0x36
//
//		//40字节信息头
//		SL.writeInt(40, out); //信息头长度
//		SL.writeInt(width, out);
//		SL.writeInt(height, out);
//		SL.writeShort(1, out);
//		SL.writeShort(24, out); //24位BMP
//		SL.writeInt(0, out); //无压缩
//		SL.writeInt(0, out); //无压缩的话此处必为0
//		SL.writeInt(0xB12, out); //horizontal resolution: Pixels/meter
//		SL.writeInt(0xB12, out); //vertical resolution: Pixels/meter
//		SL.writeInt(0, out); //本图像实际用到的颜色数，如果该值为0，则用到的颜色数为2的(颜色位数)次幂
//		SL.writeInt(0, out); //指定本图像中重要的颜色数，如果该值为0，则认为所有的颜色都是重要的
//
//		//数据
//		for (int y = height - 1; y >= 0; --y) {
//			for (int x = 0; x < width; ++x) {
//				if (argbColor[x][y].getAlpha() == 0) { //如果是透明的则存为maskColor
//					SL.writeByte(maskColor.getBlue(), out);
//					SL.writeByte(maskColor.getGreen(), out);
//					SL.writeByte(maskColor.getRed(), out);
//				}
//				else { //否则存为原始颜色
//					SL.writeByte(argbColor[x][y].getBlue(), out);
//					SL.writeByte(argbColor[x][y].getGreen(), out);
//					SL.writeByte(argbColor[x][y].getRed(), out);
//				}
//			}
//			for (int p = 0; p < pad; ++p) { //补0
//				SL.writeByte(0, out);
//			}
//		}
//
//		SL.writeByte(0, out);
//		SL.writeByte(0, out);
//		out.flush();
//		out.close();
//	}

	public boolean equals(XImage other) {
//		if (other == null) {
//			return false;
//		}
//		if (other.width != this.width || other.height != this.height) {
//			return false;
//		}
//
//		for (int x = 0; x < width; ++x) {
//			for (int y = 0; y < height; ++y) {
//				Color
//					selfColor = this.argbColor[x][y],
//								otherColor = other.argbColor[x][y];
//
//				if (selfColor.getAlpha() != otherColor.getAlpha()
//					|| selfColor.getRed() != otherColor.getRed()
//					|| selfColor.getGreen() != otherColor.getGreen()
//					|| selfColor.getBlue() != otherColor.getBlue()
//					) {
//					return false;
//				}
//			}
//		}

		return false;
	}

	//readPNG
	public static BufferedImage readJPG(File f) {
		BufferedImage result;
		try {
			if (!FileExtFilter.getExtension(f).equalsIgnoreCase("jpg")) {
				throw new Exception("不是jpg图片");
			}
			result = ImageIO.read(f);
		}
		catch (Exception e) {
			result = null;
			e.printStackTrace();
		}
		return result;
	}

		
	//readPNG
	public static BufferedImage readPNG(File f) {
		BufferedImage result;
		try {
			if (!FileExtFilter.getExtension(f).equalsIgnoreCase("png")) {
				throw new Exception("不是PNG图片");
			}
			result = ImageIO.read(f);
		}
		catch (Exception e) {
			result = null;
			e.printStackTrace();
		}
		return result;
	}

	//readBMP
	public static BufferedImage readBMP(File f, Color maskColor) {
		BufferedImage result = null;
		try {
			FileInputStream fs = new FileInputStream(f);

			//读取14字节BMP文件头
			int fileHeadLen = 14;
			byte fileHeadData[] = new byte[fileHeadLen];
			fs.read(fileHeadData, 0, fileHeadLen);
			if (fileHeadData[0] != 'B' || fileHeadData[1] != 'M') {
				throw new Exception("不是BMP文件");
			}

			//读取40字节BMP信息头
			int fileInfoLen = 40;
			byte fileInfoData[] = new byte[fileInfoLen];
			fs.read(fileInfoData, 0, fileInfoLen);

			//获取一些重要数据
			int width = ( ( (int) fileInfoData[7] & 0xFF) << 24) //源图宽度
						| ( ( (int) fileInfoData[6] & 0xFF) << 16)
						| ( ( (int) fileInfoData[5] & 0xFF) << 8)
						| ( (int) fileInfoData[4] & 0xFF);
//			System.out.println("宽：" + width);

			int height = ( ( (int) fileInfoData[11] & 0xFF) << 24) //源图高度
						 | ( ( (int) fileInfoData[10] & 0xFF) << 16)
						 | ( ( (int) fileInfoData[9] & 0xFF) << 8)
						 | ( (int) fileInfoData[8] & 0xFF);
//			System.out.println("高："+height);

			//位数
			int bitCount = ( ( (int) fileInfoData[15] & 0xFF) << 8) |
						   ( (int) fileInfoData[14] & 0xFF);
//			System.out.println("位数：" + bitCount);

			int[] argbData = new int[width * height];

			if (bitCount == 24) { //对24位BMP进行解析
				int pad = (4 - ( (width * 3) % 4)) % 4; //每个像素是3个byte的数据，但是一行的数据总和必须是4的倍数，如果不是则使用pad个空数据补齐
//				System.out.println("pad = " + pad);
				byte[] fileData = new byte[ (width + pad) * 3 * height];
				fs.read(fileData, 0, fileData.length);
				int index = 0;
				int argb;
				for (int y = 0; y < height; ++y) {
					for (int x = 0; x < width; ++x) {
						argb = (0xFF << 24) //Alpha
							   | ( ( (byte) fileData[index + 2] & 0xFF) << 16) //Red
							   | ( ( (byte) fileData[index + 1] & 0xFF) << 8) //Green
							   | ( (byte) fileData[index] & 0xFF); //Blue
						if (equalsColor(new Color(argb, true), maskColor)) {
							argb = 0;
						}
						argbData[width * (height - 1 - y) + x] = argb;
						index += 3;
					}
					index += pad;
				}
			}
			else if (bitCount == 1) { //对1位BMP进行解析
				int bytesPerRow = ( (width + 31) >> 5) << 2; //Bytes Per Row,一个byte包含8个像素的数据
				byte[] fileData = new byte[bytesPerRow * height];
				fs.skip(8); //skip FF FF FF 00 00 00 00 00 黑色和白色
				fs.read(fileData, 0, fileData.length);
				for (int y = 0; y < height; ++y) {
					for (int x = 0; x < width; ++x) {
						int bitColor = (~ (fileData[y * bytesPerRow + (x >> 3)]) >> (7 - (x % 8))) &
									   1;
						int argb = (bitColor == 1 ? 0 : 0xFF000000);
						argbData[width * (height - 1 - y) + x] = argb;
					}
				}
			}
			else {
				throw new Exception("不是 24 位或者 1 位的BMP图。");
			}

			//create Image
			Toolkit kit = Toolkit.getDefaultToolkit();
			Image tmp = kit.createImage(new MemoryImageSource(width, height, argbData, 0, width));
			result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics g = result.createGraphics();
			g.drawImage(tmp, 0, 0, null);
			result.flush();
			tmp = null;
			fs.close(); //关闭输入流
			
//			ImageIO.write(result, "png", new File(".\\tmp.png"));
//			result = ImageIO.read(new File(".\\tmp.png"));
		}
		catch (Exception e) {
			result = null;
			e.printStackTrace();
		}
		return result;
	}

	private static boolean equalsColor(Color color, Color maskColor) {
		if (maskColor.getAlpha() != 0xFF) {
			return false;
		}
		if (get4BitRGB(color.getRed()) == get4BitRGB(maskColor.getRed())
			&& get4BitRGB(color.getBlue()) == get4BitRGB(maskColor.getBlue())
			&& get4BitRGB(color.getGreen()) == get4BitRGB(maskColor.getGreen())) {
			return true;
		}
		return false;
	}

	public static int get4BitRGB(int value) {
		return ( (value & 0xF0) >> 4);
	}

	public void paint(Graphics g, int left, int top, boolean flip) { //这里的left,top是（裁减后的）该图片的左上角
		BufferedImage img = image;
		if (flip) {
			img = flippedImage;
		}
		g.drawImage(img, left, top, null);
	}
}