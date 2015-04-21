package editor;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;

class BRPainter
	extends SIPainter {
	public final static BRPainter[] getPainters(BuildingRes[] brs) {
		BRPainter[] result = null;
		if (brs != null) {
			result = new BRPainter[brs.length];
			for (int i = 0; i < brs.length; ++i) {
				result[i] = new BRPainter(brs[i]);
				result[i].computeSize();
			}
		}
		return result;
	}

	private BuildingRes br;

	public BRPainter(BuildingRes br) {
		super(br.getImages()[ExploreBR.NORMAL]);
		this.br = br;
	}

	public BuildingRes getBR() {
		return br;
	}
	
	public int getGroupID() {
		return -1;
	}

	public int getID() {
		return br.getID();
	}

	public String getName() {
		return br.getName();
	}
	
	public void paintLeftTop(Graphics g, int left, int top) {
		br.paintLeftTop(g, left, top, isFlip());
	}

	public void paintOrigin(Graphics g, int originX, int originY) {
		br.paintOrigin(g, originX, originY, isFlip());
	}

	public void paintOrigin(Graphics g, int originX, int originY, boolean flip) {
		br.paintOrigin(g, originX, originY, flip);
	}
}

class Building
	extends BasicSprite
	implements Copyable, Flipable {
	private BuildingRes br;
	private int state;
	private boolean flip;
	private BarrackMode bm;
	private DropItemMode dim;

	public Building(BuildingRes br, int id, int originX, int originY, boolean flip) {
		super(id, originX, originY, br.getName());
		init(br, flip);
	}

	public Building(BuildingRes br, int id, int originX, int originY, boolean flip, String name) {
		super(id, originX, originY, name);
		init(br, flip);
	}

	private void init(BuildingRes br, boolean flip) {
		this.br = br;
		this.flip = flip;
		this.state = ExploreBR.NORMAL;
		bm = new BarrackMode(this);
		dim = br.getDIM().getCopy();
	}

	public Copyable copy() {
		return getCopy();
	}

	public Building getCopy() {
		Building result = new Building(this.br, this.getID(),
									   this.getX(), this.getY(), this.flip, this.getName());
		result.state = this.state;
		result.bm = this.bm.getCopy();
		result.bm.setOwner(result);
		result.dim = this.dim.getCopy();
		return result;
	}

	public int compareTo(Object o) {
		return super.compareTo(o);
	}

	public BuildingRes getBR() {
		return br;
	}

	public void setBR(BuildingRes br) {
		this.br = br;
	}

	public int getLeft() {
		return br.getLeft(getX());
	}

	public int getTop() {
		return br.getTop(getY());
	}

	public int getWidth() {
		return br.getWidth();
	}

	public int getHeight() {
		return br.getHeight();
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public boolean isFlip() {
		return flip;
	}

	public void flip() {
		flip = !flip;
	}

	public BarrackMode getBM() {
		return bm.getCopy();
	}

	public void setBM(BarrackMode bm) {
		this.bm = bm.getCopy();
	}
	
	public DropItemMode getDIM() {
		return dim.getCopy();
	}
	
	public void setDIM(DropItemMode dim) {
		this.dim = dim.getCopy();
	}

	public String getInfo() {
		String result = getName() +
						"  ID£º" + getID() +
						"  ¿í£º" + getWidth() +
						"  ¸ß£º" + getHeight() + 
						"  " + ExploreBR.DESCS[state]+
						" Î»ÖÃ£º" + getMobileX() + ","+getMovingY();
		return result;
	}

	public void paintIdle(Graphics g, int x, int y) {
		br.paintOrigin(g, x, y, isFlip());
	}

	public void saveMobileData(DataOutputStream out) throws Exception {
		SL.writeInt(br.getID(), out);
		BasicSprite.saveMobileData(this, out);
		SL.writeInt(state, out);
		int tmp = 0;
		if (flip) {
			tmp |= 1;
		}
		SL.writeInt(tmp, out);
		bm.saveMobileData(out);
		dim.saveMobileData(out);
	}

	public void save(DataOutputStream out) throws Exception {
		out.writeInt(br.getID());
		BasicSprite.save(this, out);
		out.writeInt(state);
		out.writeBoolean(flip);
		bm.save(out);
		dim.save(out);
	}

	public final static Building createViaFile(DataInputStream in, BRManager brManager) throws Exception {
		int siID = in.readInt();
		BuildingRes br = brManager.getBR(siID);
		Building result = new Building(br, 0, 0, 0, false, "");
		result.load(in, brManager);
		return result;
	}

	private void load(DataInputStream in, BRManager siManager) throws Exception {
		BasicSprite.load(this, in);
		this.state = in.readInt();
		this.flip = in.readBoolean();
		this.bm.load(in);
		this.dim.load(in);
	}
}

class BuildingPropSetter extends OKCancelDialog {
	private MainFrame mainFrame;
	private Building building;
	private BarrackModeList bmList;
	private DropItemModeList dimList;
	
	public BuildingPropSetter(MainFrame mainFrame, Building building) {
		super(mainFrame);
		this.mainFrame = mainFrame;
		this.building = building;
		bmList = new BarrackModeList(this, mainFrame, building.getBM());
		dimList = new DropItemModeList(this, building.getDIM());
		
		JSplitPane split0 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(bmList), new JScrollPane(dimList));
		split0.setOneTouchExpandable(true);
		split0.setDividerLocation(400);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		mainPanel.add(split0, c);		
		
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(mainPanel, BorderLayout.CENTER);
		cp.add(buttonPanel, BorderLayout.SOUTH);
	}
	
	public void okPerformed() {
		this.closeType = OK_PERFORMED;
		building.setBM(bmList.getBM());
		building.setDIM(dimList.getDIM());
		dispose();
	}
	
	public void cancelPerformed() {
		dispose();
	}
}

class BuildingManager
	extends SpriteManager {
	public BuildingManager(ScrollablePanel scrollablePanel, MouseInfo mouseInfo) {
		super(scrollablePanel, mouseInfo);
	}

	protected Sprite createSprite(int x, int y) {
		if (! (mouseInfo.getPainter()instanceof BRPainter)) {
			return null;
		}
		BRPainter painter = (BRPainter) mouseInfo.getPainter();
		return new Building(painter.getBR(), useMaxID(), x, y, mouseInfo.isFlip());
	}

	public void refresh(BRManager brManager) {
		BuildingRes[] brs = brManager.getBRs();
		int i = 0;
		while (sprites.size() > 0 && i < sprites.size()) {
			Building building = (Building) (sprites.get(i));
			boolean remove = true;
			if (brs != null) {
				for (int j = 0; j < brs.length; ++j) {
					if (building.getBR().getID() == brs[j].getID()) {
						remove = false;
						building.setBR(brs[j]);
						break;
					}
				}
			}
			if (remove) {
				removeSprite(building);
			}
			else {
				++i;
			}
		}
		scrollablePanel.repaint();
	}
	
	public void showProperties(Sprite sprite) {
		if(sprite != null) {
			if(sprite instanceof Building) {
				Building building = (Building)sprite;
				BuildingPropSetter setter = new BuildingPropSetter(MainFrame.self, building);
				setter.show();
				scrollablePanel.repaint();
				super.selectionChanged();
			}
		}
	}


	public void saveMobileData(DataOutputStream out, Object[] resManagers) throws Exception {
		SL.writeInt(sprites.size(), out);
		for (int i = 0; i < sprites.size(); ++i) {
			Building building = (Building) (sprites.get(i));
			building.saveMobileData(out);
		}
	}

	public void save(DataOutputStream out, Object[] resManagers) throws Exception {
		out.writeInt(sprites.size());
		for (int i = 0; i < sprites.size(); ++i) {
			Building building = (Building) (sprites.get(i));
			building.save(out);
		}
	}

	public void load(DataInputStream in, Object[] resManagers) throws Exception {
		reset();
		BRManager brManager = (BRManager) (resManagers[MainFrame.RES_BR]);
		int length = in.readInt();
		for (int i = 0; i < length; ++i) {
			Building building = Building.createViaFile(in, brManager);
			addSprite(building);
		}
	}
}

class BuildingPanel
	extends SpriteManagerPanel {

	public BuildingPanel(JFrame owner, MouseInfo mouseInfo) {
		super(owner, mouseInfo);
	}

	public BuildingPanel(JDialog owner, MouseInfo mouseInfo) {
		super(owner, mouseInfo);
	}

	protected SpriteManager createManager() {
		return new BuildingManager(this, mouseInfo);
	}
}
