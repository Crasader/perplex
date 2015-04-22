package editoradd;


import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javafx.util.Pair;

import javax.swing.JOptionPane;

import editoradd.XImage;
import editoradd.XUtil;


class SIGroup {

	private int id;
	private String name;
	private int layer;

	public SIGroup(int id, String name, int layer) {
		this.id = id;
		this.name = name;
		this.layer = layer;
	}

	public int getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getLayer() {
		return layer;
	}
}

class SIManager {
	
	public final static int MAX_IMAGE_ID = 60000;
	
	public static void main(String[] args) throws Exception {
		StringBuffer s = new StringBuffer();

		String siFolder = XUtil.getDefPropStr("MapImageFolder");
		String pakFolder = XUtil.getDefPropStr("PakFolder");

		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(
		        XUtil.getDefPropStr("MapImageIniFile"))));
		ArrayList data = new ArrayList();
		String sLine;

		sLine = in.readLine();
		while (sLine != null) {
			data.add(sLine);
			sLine = in.readLine();
		}
		in.close();

		int groupID = -1;
		String groupName = "";

		int maxId = 0;

		// 生成图片列表
		for (int i = 0; i < data.size(); ++i) {
			sLine = ((String) (data.get(i))).trim();
			if (sLine == null) continue;
			if (sLine.length() < 2) continue;
			if (sLine.startsWith("$") && sLine.endsWith(";") && sLine.length() > 2) { // group
				String infos[] = sLine.substring(1, sLine.length() - 1).split(",", 0);
				if (infos != null) {
					if (infos.length >= 1) {
						groupName = infos[0].trim();
						++groupID;
						if (groupID > 0) {
							s.append("\r\n\r\n");
						}
					}
				}
			}
			if (sLine.startsWith("@") && sLine.endsWith(";") && sLine.length() > 2) { // image
				String infos[] = sLine.substring(1, sLine.length() - 1).split(",", 0);
				if (infos.length >= 2) {
					int siID = Integer.parseInt(infos[0].trim());
					String siName = infos[1].trim();
					Color maskColor = SingleImage.DEF_MASK_COLOR;
					int xmgType = 1;
					String sXmgType = "";
					if (infos.length >= 3) {
						String sColor = infos[2].trim();
						if (sColor.length() > 2) {
							if (sColor.substring(0, 2).equalsIgnoreCase("0x")) {
								sColor = sColor.substring(2);
							}
						}
						maskColor = new Color(Integer.parseInt(sColor, 16));
						//infos[4] = defaultLayer
						if(infos.length >= 5) {
							sXmgType = infos[4].trim();
							try {
								xmgType = Integer.parseInt(sXmgType);
							}
							catch(Exception e) {
							}
						}
					}
					String fileFullName;
					if (groupName.equals("")) {
						fileFullName = siFolder + "\\" + siName;
					}
					else {
						fileFullName = siFolder + "\\" + groupName + "\\" + siName;
					}
					File imageFile = new File(fileFullName);
					String ext = FileExtFilter.getExtension(imageFile).trim().toLowerCase();
					String listName = "";
					if(ext.equals("jpg")) {
						listName = siID + ".jpg";
						XUtil.copyFile(imageFile.getAbsolutePath(), new File(pakFolder + "\\" + listName).getAbsolutePath());
						if(!sXmgType.equals("")) {
							listName = listName + "/" + sXmgType;
						}
					}
					else if (ext.equals("png")) {
						listName = siID + ".png";
						XUtil.copyFile(imageFile.getAbsolutePath(), new File(pakFolder + "\\" + listName).getAbsolutePath());
						if(!sXmgType.equals("")) {
							listName = listName + "/" + sXmgType;
						}
					}
					else {
//						XImage image = new XImage(imageFile, maskColor);
//						image.saveXMG(new File(pakFolder + "\\" + siID + ".png"), xmgType);
//						listName = siID + ".xmg";
					}
					if (siID > maxId) maxId = siID;
					s.append(listName + "\r\n");
					System.out.println(groupName + "\\" + siName);
				}
			}
		}
		s.append("\r\n\r\n");

		// 在最前面加上存档文件的文件名
		// s.insert(0, "anim.dat\r\n" + "\r\n\r\n");

		if (maxId > MAX_IMAGE_ID) {
			JOptionPane.showMessageDialog(null, "图片ID大于最大值" + MAX_IMAGE_ID + "。", "ID过大",
			        JOptionPane.ERROR_MESSAGE);
			return;
		}

		// 生成文件list.txt
		DataOutputStream out;

		String output = "list.txt";
		if (args != null) {
			if (args.length >= 1) {
				if (args[0] != null) {
					if (!args[0].trim().equals("")) {
						output = args[0] + ".txt";
					}
				}
			}
		}
		output = pakFolder + "\\" + output;

		out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(output)));
		byte[] bytes = s.toString().getBytes();
		out.write(bytes);
		out.flush();
		out.close();
	}

	private String name;
	private SIGroup[] groups;
	private SingleImage[] sis;

	public SIManager(String name) throws Exception {
		this.name = name;
		readIniFile();
		for (int i = 0; i < sis.length; ++i) {
			try {
				sis[i].load();
			}
			catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "加载图片 " + sis[i].toString() + " 失败/n" + e,
				        "加载错误", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public SIGroup[] getGroups() {
		return groups;
	}

	public SingleImage getSI(int siID) {
		for (int i = 0; i < sis.length; ++i) {
			if (sis[i].getID() == siID) { return sis[i]; }
		}
		return null;
	}

	public SingleImage[] getSIs() {
		return sis;
	}

	public SingleImage[] getSIs(int groupID) {
		ArrayList tmp = new ArrayList();
		for (int i = 0; i < sis.length; ++i) {
			if (sis[i] != null) {
				if (sis[i].getGroup().getID() == groupID) {
					tmp.add(sis[i]);
				}
			}
		}

		SingleImage[] result = new SingleImage[tmp.size()];
		for (int i = 0; i < tmp.size(); ++i) {
			result[i] = (SingleImage) (tmp.get(i));
		}
		return result;
	}

	private void readIniFile() throws Exception {
		String siFolder = XUtil.getDefPropStr(name + "ImageFolder");
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(
		        XUtil.getDefPropStr(name + "ImageIniFile"))));
		ArrayList data = new ArrayList();
		String sLine;

		sLine = in.readLine();
		while (sLine != null) {
			data.add(sLine);
			sLine = in.readLine();
		}
		in.close();

		int groupID = -1;
		int groupLayer = SingleImage.DEF_LAYER;
		String groupName = "";
		SIGroup group = new SIGroup(groupID, groupName, groupLayer);
		ArrayList tmpGroups = new ArrayList();
		ArrayList tmpSIs = new ArrayList();

		for (int i = 0; i < data.size(); ++i) {
			sLine = ((String) (data.get(i))).trim();
			if (sLine == null) continue;
			if (sLine.length() < 2) continue;
			if (sLine.startsWith("$") && sLine.endsWith(";") && sLine.length() > 2) { // group
				String infos[] = sLine.substring(1, sLine.length() - 1).split(",", 0);
				if (infos != null) {
					if (infos.length >= 1) {
						groupName = infos[0].trim();
						++groupID;
						if (infos.length >= 2) {
							groupLayer = Integer.parseInt(infos[1].trim());
						}
						else {
							groupLayer = SingleImage.DEF_LAYER;
						}
						group = new SIGroup(groupID, groupName, groupLayer);
						tmpGroups.add(group);
					}
				}
			}
			if (sLine.startsWith("@") && sLine.endsWith(";") && sLine.length() > 2) { // image
				String infos[] = sLine.substring(1, sLine.length() - 1).split(",", 0);
				if (infos.length >= 2) {
					int siID = Integer.parseInt(infos[0].trim());
					String siName = infos[1].trim();
					String fileFullName;
					if (groupName.equals("")) {
						fileFullName = siFolder + "\\" + siName;
					}
					else {
						fileFullName = siFolder + "\\" + groupName + "\\" + siName;
					}
					Color maskColor = SingleImage.DEF_MASK_COLOR;
					if (infos.length >= 3) {
						String sColor = infos[2].trim();
						if (sColor.length() > 2) {
							if (sColor.substring(0, 2).equalsIgnoreCase("0x")) {
								sColor = sColor.substring(2);
							}
						}
						maskColor = new Color(Integer.parseInt(sColor, 16));
					}
					int defLayer = groupLayer;
					if (infos.length >= 4) {
						defLayer = Integer.parseInt(infos[3].trim());
					}
					tmpSIs.add(new SingleImage(group, siID, fileFullName, maskColor, defLayer));
				}
			}
		}

		groups = new SIGroup[tmpGroups.size()];
		for (int i = 0; i < tmpGroups.size(); ++i) {
			groups[i] = (SIGroup) (tmpGroups.get(i));
		}

		sis = new SingleImage[tmpSIs.size()];
		for (int i = 0; i < tmpSIs.size(); ++i) {
			sis[i] = (SingleImage) (tmpSIs.get(i));
		}
	}
}

public class SingleImage {

	public final static int DEF_LAYER = 50;
	public final static Color DEF_MASK_COLOR = new Color(0xFF00FF);

	private BufferedImage image;
	private BufferedImage flippedImage;
	private BufferedImage rotationImage;
	
	private SIGroup group;
	private int id;
	private File f;
	private Color maskColor;
	private String name;
	private int defLayer;

	private int width;
	private int height;
	
	public SingleImage(SIGroup group, int id, String fileFullName, Color maskColor, int defLayer) {
		this.group = group;
		this.id = id;
		this.f = new File(fileFullName);
		this.maskColor = maskColor;
		this.defLayer = defLayer;
		this.name = FileExtFilter.getName(f);
		this.width = 0;
		this.height = 0;
	}

	public int getDefLayer() {
		return defLayer;
	}

	public SIGroup getGroup() {
		return group;
	}

	public int getHeight() {
		return height;
	}

	public int getID() {
		return id;
	}

	public int getLeft(int originX) {
		return originX - getWidth() / 2;
	}

	public Color getMaskColor() {
		return this.maskColor;
	}

	public String getName() {
		return name;
	}

	public int getTop(int originY) {
		return originY - getHeight() / 2;
	}

	public int getWidth() {
		return width;
	}

	public void load() throws Exception {
		image = null;

		XImage ximage = new XImage(f, maskColor);
		if (ximage.isChanged()) {
			System.out.println("切割" + f);
			ximage.save();
		}
		image = ximage.getImage();

		if (image == null) { throw new Exception("无法读取图片：" + f.getAbsolutePath()); }

		AffineTransform at = new AffineTransform(-1, 0, 0, 1, image.getWidth(), 0);
		AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		flippedImage = ato.filter(image, null);
		
		height = image.getHeight();
		width = image.getWidth();
	}
	public String toString() {
		return name;
	}
}

class CheckDuplicateImage {

	public static void main(String[] args) throws Exception {
		String siFolder = XUtil.getDefPropStr("MapImageFolder");
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(
		        XUtil.getDefPropStr("MapImageIniFile"))));
		ArrayList data = new ArrayList();
		String sLine;
		String sOut;

		sLine = in.readLine();
		while (sLine != null) {
			data.add(sLine);
			sLine = in.readLine();
		}
		in.close();

		DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(
		        ".\\相同的图片.txt")));

		String groupName = "";
//		boolean groupDuplicate = false;
		ArrayList images = new ArrayList();

		for (int i = 0; i < data.size(); ++i) {
			sLine = ((String) (data.get(i))).trim();
			if (sLine == null) continue;
			if (sLine.length() < 2) continue;
			if (sLine.startsWith("$") && sLine.endsWith(";") && sLine.length() > 2) { // group
				String infos[] = sLine.substring(1, sLine.length() - 1).split(",", 0);
				if (infos != null) {
					if (infos.length >= 1) {
						groupName = infos[0].trim();
//						images.clear();
//						groupDuplicate = false;
					}
				}
			}
			if (sLine.startsWith("@") && sLine.endsWith(";") && sLine.length() > 2) { // image
				String infos[] = sLine.substring(1, sLine.length() - 1).split(",", 0);
				if (infos.length >= 2) {
					String imageName = infos[1].trim();
					String fileFullName;
					if (groupName.equals("")) {
						fileFullName = siFolder + "\\" + imageName;
					}
					else {
						fileFullName = siFolder + "\\" + groupName + "\\" + imageName;
					}
					Color maskColor = SingleImage.DEF_MASK_COLOR;
					if (infos.length >= 3) {
						String sColor = infos[2].trim();
						if (sColor.length() > 2) {
							if (sColor.substring(0, 2).equalsIgnoreCase("0x")) {
								sColor = sColor.substring(2);
							}
						}
						maskColor = new Color(Integer.parseInt(sColor, 16));
					}
					XImage image = new XImage(new File(fileFullName), maskColor);
					boolean duplicate = false;
					Pair p = null;
					XImage prev = null;
					for (int j = 0; j < images.size(); ++j) {
						p = (Pair)(images.get(j));
						prev = (XImage)(p.getValue());
						if (image.equals(prev)) {
							duplicate = true;
							break;
						}
					}
					if (duplicate) {
						XImage img2 = (XImage)(p.getValue());
						sOut = (String)(p.getKey()) + "\\" + (img2.getName() + "." + img2.getExtension()) + "\t" + 
								groupName + "\\" + image.getName() + "." + image.getExtension() + "\r\n"; 
								
						System.out.print(sOut);
						out.write(sOut.getBytes());
					}
					else {
						images.add(new Pair(groupName, image));
					}
				}
			}
		}

		sOut = "Done.";
		System.out.print(sOut);
		out.write(sOut.getBytes());
		out.flush();
		out.close();
	}
}
