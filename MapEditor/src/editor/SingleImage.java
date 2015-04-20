package editor;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

import javax.swing.*;

import java.awt.event.*;

import javax.swing.event.*;

class SIGroup {
	private int id;
	private String name;

	public SIGroup(int id, String name) {
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

public class SingleImage {
	public final static int DEF_LAYER = 50;
	public final static Color DEF_MASK_COLOR = new Color(0xFF00FF);

	private BufferedImage image;
	private BufferedImage flippedImage;
	private int groupID;
	private int id;
	private File f;
	private Color maskColor;
	private String name;
	private int defLayer;

	public SingleImage(int groupID, int id, String fileFullName, Color maskColor, int defLayer) {
		this.groupID = groupID;
		this.id = id;
		this.f = new File(fileFullName);
		this.maskColor = maskColor;
		this.defLayer = defLayer;
		this.name = FileExtFilter.getName(f);
	}

	public void load() throws Exception {
		image = null;
		if(FileExtFilter.getExtension(f).equalsIgnoreCase("jpg")) {
			image = XImage.readJPG(f);
		}
		else if(FileExtFilter.getExtension(f).equalsIgnoreCase("png")) {
			image = XImage.readPNG(f);
		}
		else if (FileExtFilter.getExtension(f).equalsIgnoreCase("bmp")) {
			image = XImage.readBMP(f, maskColor);
		}
		if (image == null) {
			throw new Exception("Œﬁ∑®∂¡»°Õº∆¨£∫" + f.getAbsolutePath());
		}

		AffineTransform at = new AffineTransform( -1, 0, 0, 1, image.getWidth(), 0);
		AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		flippedImage = ato.filter(image, null);
	}

	public int getGroupID() {
		return groupID;
	}

	public int getID() {
		return id;
	}

	public int getDefLayer() {
		return defLayer;
	}

	public String getName() {
		return name;
	}

	public int getLeft(int originX) {
		return originX - image.getWidth() / 2;
	}

	public int getTop(int originY) {
		return originY - image.getHeight() / 2;
	}

	public int getWidth() {
		return image.getWidth();
	}

	public int getHeight() {
		return image.getHeight();
	}

	public String toString() {
		return name;
	}

	public void paintLeftTop(Graphics g, int left, int top, boolean flip) {
		BufferedImage img = image;
		if (flip) {
			img = flippedImage;
		}
		g.drawImage(img, left, top, null);
	}

	public void paintOrigin(Graphics g, int originX, int originY, boolean flip) {
		paintLeftTop(g, getLeft(originX), getTop(originY), flip);
	}
}

class SIManager {
	private File iniFile;
	private SIGroup[] groups;
	private SingleImage[] sis;

	public SIManager() throws Exception {
		iniFile = new File(XUtil.getDefPropStr("ImageIniFile"));
		readIniFile();
		for (int i = 0; i < sis.length; ++i) {
			try {
				sis[i].load();
			}
			catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
											  "º”‘ÿÕº∆¨ " + sis[i].toString() + "  ß∞‹/n" + e, "º”‘ÿ¥ÌŒÛ",
											  JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public SIGroup[] getGroups() {
		return groups;
	}

	public SingleImage[] getSIs() {
		return sis;
	}

	public SingleImage[] getSIs(int groupID) {
		ArrayList tmp = new ArrayList();
		for (int i = 0; i < sis.length; ++i) {
			if (sis[i] != null) {
				if (sis[i].getGroupID() == groupID) {
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

	public SingleImage getSI(int siID) {
		for (int i = 0; i < sis.length; ++i) {
			if (sis[i].getID() == siID) {
				return sis[i];
			}
		}
		return null;
	}

	private void readIniFile() throws Exception {
		String siFolder = XUtil.getDefPropStr("ImageFolder");
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
		int groupLayer = SingleImage.DEF_LAYER;
		String groupName = "";
		ArrayList tmpGroups = new ArrayList();
		ArrayList tmpSIs = new ArrayList();

		for (int i = 0; i < data.size(); ++i) {
			sLine = ( (String) (data.get(i))).trim();
			if (sLine == null) {
				continue;
			}
			if (sLine.length() < 2) {
				continue;
			}
			if (sLine.startsWith("$") && sLine.endsWith(";") && sLine.length() > 2) { //si group line
				String infos[] = sLine.substring(1, sLine.length() - 1).split(",", 0);
				if (infos != null) {
					if (infos.length >= 1) {
						groupName = infos[0].trim();
						++groupID;
						if (infos.length >= 2) {
							groupLayer = Integer.parseInt(infos[1].trim());
						}
						tmpGroups.add(new SIGroup(groupID, groupName));
					}
				}
			}
			if (sLine.startsWith("@") && sLine.endsWith(";") && sLine.length() > 2) { //si line
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
					tmpSIs.add(new SingleImage(groupID, siID, fileFullName, maskColor, defLayer));
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

	private void load() throws Exception {
		readIniFile();
	}
}

class SISelecter
	extends OKCancelDialog {
	private DefaultListModel siModel;
	private JList siList;
	private ScrollablePanel siPanel;
	private SIManager siManager;
	private SingleImage si;

	public SISelecter(JDialog owner, int siID, SIManager siManager) {
		super(owner);
		this.siManager = siManager;
		setTitle("—°‘ÒÕº∆¨");
		Container cp = this.getContentPane();
		si = null;

		siModel = new DefaultListModel();
		SingleImage[] sis = siManager.getSIs();
		for (int i = 0; i < sis.length; ++i) {
			siModel.addElement(sis[i]);
		}
		siList = new JList(siModel);
		siList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		siList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				siChanged();
			}
		});
		siList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				siListMouseClicked(e);
			}
		});
		JScrollPane siScroll = new JScrollPane(siList);
		SwingUtil.setDefScrollIncrement(siScroll);

		siPanel = new ScrollablePanel(XUtil.getDefPropInt("BRPanelWidth"), 
									  XUtil.getDefPropInt("BRPanelHeight")) {
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (!isVisible()) {
					return;
				}
				if(si != null) {
				    si.paintOrigin(g, getBasicWidth() / 2, getBasicHeight() / 2, false);
				}
			}
		};
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, siScroll,
											  siPanel.getBackPanel());
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(200);

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		cp.add(splitPane, BorderLayout.CENTER);
		cp.add(buttonPanel, BorderLayout.SOUTH);

		setSelectedSI(siID);
	}

	private void siChanged() {
		si = null;
		Object obj = siList.getSelectedValue();
		if (obj != null) {
			if (obj instanceof SingleImage) {
				si = (SingleImage) obj;
			}
		}
		repaint();
	}

	public int getSelectedSIID() {
		Object obj = siList.getSelectedValue();
		if (obj != null) {
			if (obj instanceof SingleImage) {
				return ( (SingleImage) obj).getID();
			}
		}
		return -1;
	}

	public SingleImage getSelectedSI() {
		Object obj = siList.getSelectedValue();
		if (obj != null) {
			if (obj instanceof SingleImage) {
				return (SingleImage) obj;
			}
		}
		return null;
	}

	private void setSelectedSI(int siID) {
		siList.clearSelection();
		SingleImage si = siManager.getSI(siID);
		if (si != null) {
			siList.setSelectedValue(si, true);
		}
	}

	private void siListMouseClicked(MouseEvent e) {
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
}

class SIPainterGroup
	implements PainterGroup {
	public final static SIPainterGroup[] getGroups(SIGroup[] groups) {
		SIPainterGroup[] result = null;
		if (groups != null) {
			result = new SIPainterGroup[groups.length];
			for (int i = 0; i < groups.length; ++i) {
				result[i] = new SIPainterGroup(groups[i]);
			}
		}
		return result;
	}

	private SIGroup siGroup;
	private boolean selected;

	public SIPainterGroup(SIGroup siGroup) {
		this.siGroup = siGroup;
		this.selected = false;
	}

	public int getID() {
		return siGroup.getID();
	}

	public String getName() {
		return siGroup.getName();
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}

class SIPainter
	extends PainterPanel {
	public final static SIPainter[] getPainters(SingleImage[] sis) {
		SIPainter[] result = null;
		if (sis != null) {
			result = new SIPainter[sis.length];
			for (int i = 0; i < sis.length; ++i) {
				result[i] = new SIPainter(sis[i]);
				result[i].computeSize();
			}
		}
		return result;
	}

	SingleImage si;

	public SIPainter(SingleImage si) {
		super();
		this.si = si;
	}

	public SingleImage getSI() {
		return si;
	}

	public int getGroupID() {
		return si.getGroupID();
	}

	public int getID() {
		return si.getID();
	}

	public int getImageWidth() {
		return si.getWidth();
	}

	public int getImageHeight() {
		return si.getHeight();
	}

	public String getName() {
		return si.getName();
	}

	public boolean isFlip() {
		return false;
	}

	public void paintLeftTop(Graphics g, int left, int top) {
		si.paintLeftTop(g, left, top, isFlip());
	}

	public void paintOrigin(Graphics g, int originX, int originY) {
		si.paintOrigin(g, originX, originY, isFlip());
	}

	public void paintOrigin(Graphics g, int originX, int originY, boolean flip) {
		si.paintOrigin(g, originX, originY, flip);
	}
}

class SISprite
	extends BasicSprite
	implements Layerable, Copyable, Flipable {
	private SingleImage si;
	private int layer;
	private boolean flip;

	public SISprite(SingleImage si, int id, int left, int top, boolean flip) {
		super(id, left, top, si.getName());
		init(si, flip);
	}

	public SISprite(SingleImage si, int id, int left, int top, boolean flip, String name) {
		super(id, left, top, name);
		init(si, flip);
	}

	private void init(SingleImage si, boolean flip) {
		this.si = si;
		this.flip = flip;
		this.layer = si.getDefLayer();
	}

	public Copyable copy() {
		return getCopy();
	}

	public SISprite getCopy() {
		SISprite result = new SISprite(this.si, this.getID(),
									   this.getX(), this.getY(), this.flip, this.getName());
		result.setLayer(this.layer);
		return result;
	}
	
	protected void copyFrom(SISprite sprite) {
	}

	public int compareTo(Object o) {
		if (o != null) {
			if (o instanceof Layerable) {
				Layerable dest = (Layerable) o;
				if (this.getLayer() != dest.getLayer()) {
					return this.getLayer() - dest.getLayer();
				}
			}
		}
		return super.compareTo(o);
	}

	public SingleImage getSI() {
		return si;
	}

	public void setSI(SingleImage si) {
		this.si = si;
	}

	public int getLayer() {
		return layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}

	public int getLeft() {
		return getX();
	}

	public int getTop() {
		return getY();
	}

	public int getWidth() {
		return si.getWidth();
	}

	public int getHeight() {
		return si.getHeight();
	}

	public boolean isFlip() {
		return flip;
	}

	public void flip() {
		flip = !flip;
	}

	public String getInfo() {
		String result = getName() +
						"  ID£∫" + getID() +
						"  øÌ£∫" + getWidth() +
						"  ∏ﬂ£∫" + getHeight() +
						"  ≤„£∫" + getLayer();
		return result;
	}

	public void paintIdle(Graphics g, int x, int y) {
		si.paintLeftTop(g, x, y, isFlip());
	}

	public void saveMobileData(DataOutputStream out) throws Exception {
		SL.writeInt(si.getID(), out);
		BasicSprite.saveMobileData(this, out);
//		SL.writeInt(layer, out);
		int tmp = 0;
		if (flip) {
			tmp |= 1;
		}
		SL.writeInt(tmp, out);
	}

	public void save(DataOutputStream out) throws Exception {
		out.writeInt(si.getID());
		BasicSprite.save(this, out);
		out.writeInt(layer);
		out.writeBoolean(flip);
	}

	public final static SISprite createViaFile(DataInputStream in, SIManager siManager) throws Exception {
		int siID = in.readInt();
		SingleImage si = siManager.getSI(siID);
		SISprite result = new SISprite(si, 0, 0, 0, false, "");
		result.load(in, siManager);
		return result;
	}

	private void load(DataInputStream in, SIManager siManager) throws Exception {
		BasicSprite.load(this, in);
		this.layer = in.readInt();
		this.flip = in.readBoolean();
	}
}

class SISpriteManager
	extends SpriteManager {
	public SISpriteManager(ScrollablePanel scrollablePanel, MouseInfo mouseInfo) {
		super(scrollablePanel, mouseInfo);
	}

	protected Sprite createSprite(int x, int y) {
		if (! (mouseInfo.getPainter()instanceof SIPainter)) {
			return null;
		}
		SIPainter painter = (SIPainter) mouseInfo.getPainter();
		return new SISprite(painter.getSI(), useMaxID(),
							painter.getSI().getLeft(x), painter.getSI().getTop(y),
							mouseInfo.isFlip());
	}

	public void saveMobileData(DataOutputStream out, Object[] resManagers) throws Exception {
		SL.writeInt(sprites.size(), out);
		for (int i = 0; i < sprites.size(); ++i) {
			SISprite siSprite = (SISprite) (sprites.get(i));
			siSprite.saveMobileData(out);
		}
	}

	public void save(DataOutputStream out, Object[] resManagers) throws Exception {
		out.writeInt(sprites.size());
		for (int i = 0; i < sprites.size(); ++i) {
			SISprite siSprite = (SISprite) (sprites.get(i));
			siSprite.save(out);
		}
	}

	public void load(DataInputStream in, Object[] resManagers) throws Exception {
		reset();
		SIManager siManager = (SIManager) (resManagers[MainFrame.RES_SI]);
		int length = in.readInt();
		for (int i = 0; i < length; ++i) {
			SISprite siSprite = SISprite.createViaFile(in, siManager);
			addSprite(siSprite);
		}
	}
}

class SISpritePanel
	extends SpriteManagerPanel {

	public SISpritePanel(JFrame owner, MouseInfo mouseInfo) {
		super(owner, mouseInfo);
	}

	public SISpritePanel(JDialog owner, MouseInfo mouseInfo) {
		super(owner, mouseInfo);
	}

	protected SpriteManager createManager() {
		return new SISpriteManager(this, mouseInfo);
	}
}

class FloorManager
	extends SISpriteManager {
	
	private Color color;
	
	public FloorManager(ScrollablePanel scrollablePanel, MouseInfo mouseInfo) {
		super(scrollablePanel, mouseInfo);
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
		scrollablePanel.repaint();
	}
	
	public void paintStatic(Graphics g) {
		if(color != null) {
			Color oldColor = g.getColor();
			g.setColor(color);
			g.fillRect(0, 0, scrollablePanel.getBasicWidth(), scrollablePanel.getBasicHeight());
			g.setColor(oldColor);
		}
		super.paintStatic(g);
	}
	
	public void paintSprites(Graphics g) {
		if(color != null) {
			Color oldColor = g.getColor();
			g.setColor(color);
			g.fillRect(0, 0, scrollablePanel.getBasicWidth(), scrollablePanel.getBasicHeight());
			g.setColor(oldColor);
		}
		super.paintSprites(g);
	}
}

class FloorPanel
	extends SISpritePanel {

	public FloorPanel(JFrame owner, MouseInfo mouseInfo) {
		super(owner, mouseInfo);
	}

	public FloorPanel(JDialog owner, MouseInfo mouseInfo) {
		super(owner, mouseInfo);
	}

	protected SpriteManager createManager() {
		return new FloorManager(this, mouseInfo);
	}
}

