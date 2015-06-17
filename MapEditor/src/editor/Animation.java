package editor;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

class AnimGroup {
	private int id;
	private String name;
	private int AnimaCount;
	private HashMap<String, Integer> animaIDs;
	public AnimGroup(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getID() {
		return id;
	}

	public String getName() {
		return name;
	}
	
}

public class Animation {
	public final static String BASE_FILE_NAME = "原点.txt";
	public final static String FILE_MARK = "_x";
	public final static int DEFAULT_INTERVAL = 120;
	public final static int MAX_INT = 100000;

	public File animFolder;
	private Color maskColor;
	private int originX, originY; //这是相对于原始图左上角的偏移量
	private int[] intervals;
	private AnimFrame[] frames;
	private ArrayList images;
	private int left, top, width, height; //这是相对于原始图左上角的偏移量
	private int groupID;
	private int id;
	private String name;
	private boolean loaded;
	private int AnimaCount;
	private HashMap<String, Integer> animaIDs;
	
	public Animation(int id, String name) {
		init( -1, id, name, 0);
	}

	public Animation(int groupID, int id, String folder, int animaCount) {
		init(groupID, id, folder, animaCount);
	}

	private void init(int groupID, int id, String folder, int animaCount) {
		this.groupID = groupID;
		this.id = id;
		this.animFolder = new File(folder);
		this.name = animFolder.getName();
		loaded = false;
		AnimaCount = animaCount;
		animaIDs = new HashMap<>();
		for (int i = 0; i < animaCount; i++) {
			String key = "00" + i;
			if (i > 9) {
				key = "0" + i;
			}
			animaIDs.put(key, i);
		}
	}

	public int getGroupID() {
		return groupID;
	}

	public int getID() {
		return id;
	}

	public int getAnimaCount()
	{
		return AnimaCount;
	}
	
	public Integer[] getAnimaID()
	{
		return (Integer[])animaIDs.values().toArray();
	}
	
	public String[] getAnimaName() {
		return (String[]) animaIDs.keySet().toArray();
	}
	
	public int getInterval(int frameIndex) {
		if (frameIndex < 0 || frameIndex >= intervals.length) {
			return 0;
		}
		return intervals[frameIndex];
	}

	public int getInterval(int startFrame, int endFrame) {
		if (endFrame < startFrame) {
			return -1;
		}
		int result = 0;
		for (int i = startFrame; i < endFrame; ++i) {
			result += getInterval(i);
		}
		return result;
	}

	public int getFrameIndex(int startFrame, int interval) {
		if (interval < 0) {
			return -1;
		}
		int result = startFrame;
		int tmp = 0;
		for (int i = startFrame; i < intervals.length; ++i) {
			if (tmp == interval || (tmp < interval && tmp + getInterval(i) > interval)) {
				tmp += getInterval(i);
				result = i;
				break;
			}
			else {
				tmp += getInterval(i);
			}
		}
		if (tmp <= interval) {
			result = -1;
		}
		return result;
	}

//
	public void setInterval(int frameIndex, int interval) {
		if (frameIndex < 0 || frameIndex >= intervals.length) {
			return;
		}
		intervals[frameIndex] = interval;
	}

//	private int getOriginX() {
//		return originX;
//	}
//
//	private int getOriginY() {
//		return originY;
//	}

//	public void setOriginY(int originY) {this.originY = originY;}

	public int getFrameCount() {
		if (frames == null) {
			return 0;
		}
		return frames.length;
	}

//	private int getLeft() {
//		return left;
//	}

	public int getLeft(int originX) {
		return originX - this.originX + left;
	}

//	private int getTop() {
//		return top;
//	}

	public int getTop(int originY) {
		return originY - this.originY + top;
	}

//	public int getWidth() {
//		return width;
//	}

	public int getFullWidth() {
		return Math.max(Math.abs(this.left - this.originX) * 2,
						Math.abs(this.left + this.width - this.originX) * 2);
	}

	public int getHeight() {
		return height;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return getName();
	}

	public synchronized void load() throws Exception {
		if (loaded) {
			return;
		}
		loaded = true;
		//读取基本信息
		loadBasicInfo(animFolder);

		String animPath = animFolder.getPath();
		File[] layerFolders = { //所有的层
							  new File(animPath + "\\" + "00"),
							  new File(animPath + "\\" + "01"),
							  new File(animPath + "\\" + "02"),
							  new File(animPath + "\\" + "03"),
							  new File(animPath + "\\" + "04")
		};
		File mainLayerFolder = new File(animPath + "\\" + "02"); //主层
		if (mainLayerFolder.exists()) { //分层动画
			//获得主层的图片文件
			File[] mainLayerFiles = getImageFiles(mainLayerFolder);

			//读取动画
			frames = new AnimFrame[mainLayerFiles.length];
			images = new ArrayList();
			images.clear();
			for (int frameIndex = 0; frameIndex < mainLayerFiles.length; ++frameIndex) { //循环帧
				String frameName = FileExtFilter.getName(mainLayerFiles[frameIndex]);
				XImage[] frameImages = new XImage[layerFolders.length];
				for (int layerIndex = 0; layerIndex < layerFolders.length; ++layerIndex) { //循环层
					File f = new File(layerFolders[layerIndex].getPath() + "\\" +
									  mainLayerFiles[frameIndex].getName());
					if (f.exists()) {
						frameImages[layerIndex] = new XImage(f, maskColor, layerIndex);
					}
					else {
						frameImages[layerIndex] = null;
					}
				}
				//insertFrameImages(frameImages);
				frames[frameIndex] = new AnimFrame(frameImages, frameName);
			}

		}
		else { //不分层动画
			File[] files = getImageFiles(animFolder);
			frames = new AnimFrame[files.length];
			for (int frameIndex = 0; frameIndex < files.length; ++frameIndex) {
				String frameName = FileExtFilter.getName(files[frameIndex]);
				XImage image = new XImage(files[frameIndex], maskColor, 2);
				XImage[] frameImages = new XImage[1];
				frameImages[0] = image;
				//insertFrameImages(frameImages);
				frames[frameIndex] = new AnimFrame(frameImages, frameName);
			}
		}

		//重新设置intervals
		int[] oldIntervals = intervals;
		intervals = new int[frames.length];
		for (int i = 0; i < intervals.length; ++i) {
			if (i < oldIntervals.length) {
				intervals[i] = oldIntervals[i];
			}
			else {
				intervals[i] = oldIntervals[oldIntervals.length - 1];
			}
		}

		//求宽高

		left = -MAX_INT;
		top = -MAX_INT;
		int right = MAX_INT;
		int bottom = MAX_INT;
		int tmp;

		for (int i = 0; i < frames.length; ++i) {
			AnimFrame frame = frames[i];
			if (frame == null) {
				continue;
			}
			//left
			tmp = frame.getLeft();
			if (tmp < left || left == -MAX_INT) {
				left = tmp;
			}
			//top
			tmp = frame.getTop();
			if (tmp < top || top == -MAX_INT) {
				top = tmp;
			}
			//right
			tmp = frame.getLeft() + frame.getWidth();
			if (tmp > right || right == MAX_INT) {
				right = tmp;
			}
			//bottom
			tmp = frame.getTop() + frame.getHeight();
			if (tmp > bottom || bottom == MAX_INT) {
				bottom = tmp;
			}
		}

		width = right - left;
		height = bottom - top;
		if (originX == -MAX_INT && originY == -MAX_INT) {
			originX = width / 2;
			originY = height / 2;
		}
	}

	private void loadBasicInfo(File animFolder) throws Exception {
		maskColor = new Color(0xFF00FF);
		originX = -MAX_INT;
		originY = -MAX_INT;
		intervals = new int[1];
		intervals[0] = DEFAULT_INTERVAL;

		File originFile = new File(animFolder.getPath() + "\\" + BASE_FILE_NAME);
		if (!originFile.exists()) {
			return;
		}
		BufferedReader in = new BufferedReader(
			new InputStreamReader(
			new FileInputStream(originFile)));

		String sLine = in.readLine();
		if (sLine == null) {
			return;
		}
		sLine = sLine.trim();
		if (sLine.substring(0, 2).equalsIgnoreCase("0x")) {
			int color = Integer.parseInt(sLine.substring(2), 16);
			maskColor = new Color(color);
			sLine = in.readLine();
			if (sLine == null) {
				return;
			}
			sLine = sLine.trim();
		}
		originX = Integer.parseInt(sLine);

		sLine = in.readLine();
		if (sLine == null) {
			return;
		}
		sLine = sLine.trim();
		originY = Integer.parseInt(sLine);

		ArrayList tmp = new ArrayList();
		sLine = in.readLine();
		while (sLine != null) {
			sLine = sLine.trim();
			if (sLine.indexOf("*") > 0) {
				String value = sLine.substring(0, sLine.indexOf("*")).trim();
				int count = Integer.parseInt(sLine.substring(sLine.indexOf("*") + 1).trim());
				for (int i = 0; i < count; ++i) {
					tmp.add(value);
				}
			}
			else {
				tmp.add(sLine);
			}
			sLine = in.readLine();
		}
		if (tmp.size() > 0) {
			intervals = new int[tmp.size()];
			for (int i = 0; i < tmp.size(); ++i) {
				intervals[i] = Integer.parseInt( (String) (tmp.get(i)));
			}
		}

		in.close();
	}

	private File[] getImageFiles(File parent) {
		//获得图片文件
		File[] files = parent.listFiles(new java.io.FileFilter() {
			public boolean accept(File f) {
				return (!FileExtFilter.getName(f).endsWith(FILE_MARK)) &&
					(FileExtFilter.getExtension(f).equalsIgnoreCase("BMP") ||
					 FileExtFilter.getExtension(f).equalsIgnoreCase("PNG"));
			}
		});

		//按文件名排序
		for (int i = 0; i < files.length - 1; ++i) {
			for (int j = i + 1; j < files.length; ++j) {
				if (FileExtFilter.getName(files[i]).
					compareTo(FileExtFilter.getName(files[j])) > 0) {
					File tmp = files[i];
					files[i] = files[j];
					files[j] = tmp;
				}
			}
		}
		return files;
	}

	private void insertFrameImages(XImage[] frameImages) {
		for (int check = 0; check < frameImages.length; ++check) {
			if (frameImages[check] == null) {
				continue;
			}
			boolean hasSameImage = false;
			for (int basic = 0; basic < images.size(); ++basic) {
				if (images.get(basic) == null) {
					continue;
				}
				XImage basicImage = (XImage) (images.get(basic));
				if (basicImage.equals(frameImages[check])) {
					hasSameImage = true;
					frameImages[check].setID(basicImage.getID());
					break;
				}
			}
			if (!hasSameImage) {
				frameImages[check].setID(images.size());
				images.add(frameImages[check]);
			}
		}
	}

//	public void paintLeftTop(int frameIndex, Graphics g, int left, int top) {
//		paintLeftTop(frameIndex, g, left, top, true);
//	}

	public void paintLeftTop(int frameIndex, Graphics g, int left, int top, boolean flip) { //这里的left,top是（裁减后的）该动画的左上角的坐标
		if (frameIndex < 0 || frameIndex >= frames.length) {
			return;
		}
		AnimFrame frame = frames[frameIndex];
		int tmpx = frame.getLeft() - this.left;
		int tmpy = frame.getTop() - this.top;
		if (flip) {
			tmpx = this.originX * 2 - (frame.getLeft() + frame.getWidth()) - this.left;
		}
		
		frame.paint(g,
					left + tmpx,
					top + tmpy,
					flip);
	}

//	public void paintOrigin(int frameIndex, Graphics g, int originX, int originY) {
//		paintOrigin(frameIndex, g, originX, originY, true);
//	}

	public void paintOrigin(int frameIndex, Graphics g, int originX, int originY, boolean flip) {
		paintLeftTop(frameIndex,
					 g,
					 originX - this.originX + this.left,
					 originY - this.originY + this.top,
					 flip);
	}
}

class AnimFrame {
	public final static int MAX_INT = 100000;

	private String name;
	private XImage[] images;
	private int left, top, width, height; //这是这一帧对应原始图的左上角的偏移量

	public AnimFrame(XImage[] images, String name) throws Exception {
		this.name = name;
		this.images = images;

		left = -MAX_INT;
		top = -MAX_INT;
		int right = MAX_INT;
		int bottom = MAX_INT;
		int tmp;

		for (int i = 0; i < images.length; ++i) {
			XImage image = images[i];
			if (image == null) {
				continue;
			}
			//left
			tmp = image.getLeft();
			if (tmp < left || left == -MAX_INT) {
				left = tmp;
			}
			//top
			tmp = image.getTop();
			if (tmp < top || top == -MAX_INT) {
				top = tmp;
			}
			//right
			tmp = image.getLeft() + image.getWidth();
			if (tmp > right || right == MAX_INT) {
				right = tmp;
			}
			//bottom
			tmp = image.getTop() + image.getHeight();
			if (tmp > bottom || bottom == MAX_INT) {
				bottom = tmp;
			}
		}

		width = right - left;
		height = bottom - top;
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

	public void paint(Graphics g, int left, int top, boolean flip) { //这里的left, top是这一帧的（裁减后的）整体画片的左上角坐标
		for (int i = 0; i < images.length; ++i) {
			XImage image = images[i];
			if (image == null) {
				continue;
			}

			int tmpx = image.getLeft() - this.left;
			int tmpy = image.getTop() - this.top;
			if (flip) {
				tmpx = this.width - (tmpx + image.getWidth());
			}
			
			image.paint(g,
						left + tmpx,
						top + tmpy,
						flip);
		}
	}
}

class ARManager {
	private File iniFile;
	private AnimGroup[] groups;
	private Animation[] anims;

	public ARManager() throws Exception {
		iniFile = new File(XUtil.getDefPropStr("AnimationIniFile"));
		readIniFile();
		for (int i = 0; i < anims.length; ++i) {
			try {
				anims[i].load();
			}
			catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
											  "加载动画 " + anims[i].toString() + " 失败/n" + e, "加载错误",
											  JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public AnimGroup[] getGroups() {
		return groups;
	}

	public Animation[] getAnims() {
		return anims;
	}

	public Animation[] getAnims(int groupID) {
		ArrayList tmp = new ArrayList();
		for (int i = 0; i < anims.length; ++i) {
			if (anims[i] != null) {
				if (anims[i].getGroupID() == groupID) {
					tmp.add(anims[i]);
				}
			}
		}

		Animation[] result = new Animation[tmp.size()];
		for (int i = 0; i < tmp.size(); ++i) {
			result[i] = (Animation) (tmp.get(i));
		}
		return result;
	}

	public Animation getAnim(int animID) {
		for (int i = 0; i < anims.length; ++i) {
			if (anims[i].getID() == animID) {
				return anims[i];
			}
		}
		return null;
	}

	private void readIniFile() throws Exception {
		String animFolder = XUtil.getDefPropStr("AnimationFolder");
		BufferedReader in = new BufferedReader(
			new InputStreamReader(
			new FileInputStream(iniFile)));
		ArrayList data = new ArrayList();
		String sLine;

		sLine = in.readLine();
		while (sLine != null) {
			data.add(sLine);
			sLine = in.readLine();
		}
		in.close();

		int groupID = -1;
		ArrayList tmpGroups = new ArrayList();
		ArrayList tmpAnims = new ArrayList();

		for (int i = 0; i < data.size(); ++i) {
			sLine = ( (String) (data.get(i))).trim();
			if (sLine == null) {
				continue;
			}
			if (sLine.length() < 2) {
				continue;
			}
			if (sLine.startsWith("$") && sLine.endsWith(";") && sLine.length() > 2) { //anim group line
				String infos[] = sLine.substring(1, sLine.length() - 1).split(",", 0);
				if (infos != null) {
					if (infos.length >= 1) {
						String groupName = infos[0].trim();
						++groupID;
						tmpGroups.add(new AnimGroup(groupID, groupName));
					}
				}
			}
			if (sLine.startsWith("@") && sLine.endsWith(";") && sLine.length() > 2) { //anim line
				String infos[] = sLine.substring(1, sLine.length() - 1).split(",", 0);
				if (infos != null) {
					if (infos.length >= 3) {
						int animID = Integer.parseInt(infos[0].trim());
						String animName = infos[1].trim();
						int animCount = Integer.parseInt(infos[2].trim());
						tmpAnims.add(new Animation(groupID, animID, animFolder + "\\" + animName, animCount));
					}
				}
			}
		}

		groups = new AnimGroup[tmpGroups.size()];
		for (int i = 0; i < tmpGroups.size(); ++i) {
			groups[i] = (AnimGroup) (tmpGroups.get(i));
		}

		anims = new Animation[tmpAnims.size()];
		for (int i = 0; i < tmpAnims.size(); ++i) {
			anims[i] = (Animation) (tmpAnims.get(i));
		}
	}

	private void load() throws Exception {
		readIniFile();
	}
}

class AnimPainterGroup
	implements PainterGroup {
	public final static AnimPainterGroup[] getGroups(AnimGroup[] groups) {
		AnimPainterGroup[] result = null;
		if (groups != null) {
			result = new AnimPainterGroup[groups.length];
			for (int i = 0; i < groups.length; ++i) {
				result[i] = new AnimPainterGroup(groups[i]);
			}
		}
		return result;
	}

	private AnimGroup animGroup;
	private boolean selected;

	public AnimPainterGroup(AnimGroup animGroup) {
		this.animGroup = animGroup;
		this.selected = false;
	}

	public int getID() {
		return animGroup.getID();
	}

	public String getName() {
		return animGroup.getName();
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}

class AnimPainter
	extends PainterPanel {
	public final static AnimPainter[] getPainters(Animation[] anims) {
		AnimPainter[] result = null;
		if (anims != null) {
			result = new AnimPainter[anims.length];
			for (int i = 0; i < anims.length; ++i) {
				result[i] = new AnimPainter(anims[i]);
				result[i].computeSize();
			}
		}
		return result;
	}

	private Animation anim;
	private String name;

	public AnimPainter(Animation anim) {
		super();
		this.anim = anim;
		this.name = anim.getName();
	}

	public AnimPainter(Animation anim, String name) {
		super();
		this.anim = anim;
		this.name = name;
	}

	public Animation getAnim() {
		return anim;
	}

	public int getGroupID() {
		return anim.getGroupID();
	}

	public int getID() {
		return anim.getID();
	}

	public int getImageWidth() {
		return anim.getFullWidth();
	}

	public int getImageHeight() {
		return anim.getHeight();
	}

	public String getName() {
		return name;
	}

	protected int getDefFrameIndex() {
		return 0;
	}

	protected boolean isFlip() {
		return false;
	}

	public void paintLeftTop(Graphics g, int left, int top) {
		anim.paintLeftTop(getDefFrameIndex(), g, left, top, isFlip());
	}

	public void paintOrigin(Graphics g, int originX, int originY) {
		anim.paintOrigin(getDefFrameIndex(), g, originX, originY, isFlip());
	}
}

class AnimSprite
	extends BasicSprite {

	private Animation anim;

	public AnimSprite(Animation anim, int id, int originX, int originY) {
		super(id, originX, originY, anim.getName());
		this.anim = anim;
	}

	public AnimSprite(Animation anim, int id, int originX, int originY, String name) {
		super(id, originX, originY, name);
		this.anim = anim;
	}

	public Animation getAnim() {
		return anim;
	}

	public void setAnim(Animation anim) {
		this.anim = anim;
	}

	public int getLeft() {
		return anim.getLeft(getX());
	}

	public int getTop() {
		return anim.getTop(getY());
	}

	public int getWidth() {
		return anim.getFullWidth();
	}

	public int getHeight() {
		return anim.getHeight();
	}

	public void paintIdle(Graphics g, int x, int y) {
		paintIdle(getDefFrameIndex(), g, x, y);
	}

	public int getDefFrameIndex() {
		return 0;
	}

	public boolean isFlip() {
		return false;
	}

	public void paintIdle(int frameIndex, Graphics g) {
		paintIdle(frameIndex, g, getX(), getY());
	}

	public void paintIdle(int frameIndex, Graphics g, int x, int y) {
		anim.paintOrigin(frameIndex, g, x, y, isFlip());
	}

	public void paintSelected(int frameIndex, Graphics g) {
		paintIdle(frameIndex, g);
		paintBorder(g, getSelectedBorderColor());
	}

	public void paintMoving(int frameIndex, Graphics g) {
		paintIdle(frameIndex, g, getMovingX(), getMovingY());
		paintBorder(g, getMovingBorderColor());
	}

	public void paint(int frameIndex, Graphics g) {
		if (isSelected()) {
			if (!isMoving()) {
				paintSelected(frameIndex, g);
			}
			else {
				paintMoving(frameIndex, g);
			}
		}
		else {
			paintIdle(frameIndex, g);
		}
	}
}

class AnimSelecter
	extends OKCancelDialog {
	private DefaultListModel animModel;
	private JList animList;
	private AnimPlayPanel animPlayPanel;
	private ARManager arManager;

	public AnimSelecter(JDialog owner, int animID, ARManager arManager) {
		super(owner);
		this.arManager = arManager;
		setTitle("选择动画");
		Container cp = this.getContentPane();

		animModel = new DefaultListModel();
		Animation[] anims = arManager.getAnims();
		for (int i = 0; i < anims.length; ++i) {
			animModel.addElement(anims[i]);
		}
		animList = new JList(animModel);
		animList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		animList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				animChanged();
			}
		});
		animList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				animListMouseClicked(e);
			}
		});
		JScrollPane animScroll = new JScrollPane(animList);
		SwingUtil.setDefScrollIncrement(animScroll);

		animPlayPanel = new AnimPlayPanel(true) {
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (!isVisible()) {
					return;
				}
				paintAnim(g);
				paintAnimOrigin(g, false);
			}
		};
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, animScroll,
											  animPlayPanel.getBackPanel());
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(200);

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		cp.add(splitPane, BorderLayout.CENTER);
		cp.add(buttonPanel, BorderLayout.SOUTH);

		setSelectedAnim(animID);
	}

	private void animChanged() {
		Animation animation = null;
		Object obj = animList.getSelectedValue();
		if (obj != null) {
			if (obj instanceof Animation) {
				animation = (Animation) obj;
			}
		}
		animPlayPanel.setAnim(animation);
	}

	public int getSelectedAnimID() {
		Object obj = animList.getSelectedValue();
		if (obj != null) {
			if (obj instanceof Animation) {
				return ( (Animation) obj).getID();
			}
		}
		return -1;
	}

	public Animation getSelectedAnim() {
		Object obj = animList.getSelectedValue();
		if (obj != null) {
			if (obj instanceof Animation) {
				return (Animation) obj;
			}
		}
		return null;
	}

	private void setSelectedAnim(int animID) {
		animList.clearSelection();
		Animation anim = arManager.getAnim(animID);
		if (anim != null) {
			animList.setSelectedValue(anim, true);
		}
	}

	private void animListMouseClicked(MouseEvent e) {
		if (e.getButton() == XUtil.LEFT_BUTTON && e.getClickCount() == 2) {
			okPerformed();
		}
	}

	public void okPerformed() {
		closeType = OK_PERFORMED;
		dispose();
	}

	public void cancelPerformed() {
		dispose();
	}

	public void dispose() {
		animPlayPanel.stop();
		super.dispose();
	}
}
