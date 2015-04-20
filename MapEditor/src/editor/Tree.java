package editor;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;

class TreeSprite
	extends BasicSprite
	implements Layerable, Copyable, Flipable {
	private SingleImage si;
	private int layer;
	private boolean flip;

	public TreeSprite(SingleImage si, int id, int left, int top, boolean flip) {
		super(id, left, top, si.getName());
		init(si, flip);
	}

	public TreeSprite(SingleImage si, int id, int left, int top, boolean flip, String name) {
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

	public TreeSprite getCopy() {
		TreeSprite result = new TreeSprite(this.si, this.getID(),
										   this.getX(), this.getY(), this.flip, this.getName());
		result.setLayer(this.layer);
		return result;
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
						"  ID£º" + getID() +
						"  ¿í£º" + getWidth() +
						"  ¸ß£º" + getHeight() +
						"  ²ã£º" + getLayer();
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

	public final static TreeSprite createViaFile(DataInputStream in, SIManager siManager) throws Exception {
		int siID = in.readInt();
		SingleImage si = siManager.getSI(siID);
		TreeSprite result = new TreeSprite(si, 0, 0, 0, false, "");
		result.load(in, siManager);
		return result;
	}

	private void load(DataInputStream in, SIManager siManager) throws Exception {
		BasicSprite.load(this, in);
		this.layer = in.readInt();
		this.flip = in.readBoolean();
	}
}

class TreeSpriteManager
	extends SpriteManager {
	public TreeSpriteManager(ScrollablePanel scrollablePanel, MouseInfo mouseInfo) {
		super(scrollablePanel, mouseInfo);
	}

	protected Sprite createSprite(int x, int y) {
		if (! (mouseInfo.getPainter()instanceof SIPainter)) {
			return null;
		}
		SIPainter painter = (SIPainter) mouseInfo.getPainter();
		return new TreeSprite(painter.getSI(), useMaxID(),
							  painter.getSI().getLeft(x), painter.getSI().getTop(y),
							  mouseInfo.isFlip());
	}

	public void saveMobileData(DataOutputStream out, Object[] resManagers) throws Exception {
		SL.writeInt(sprites.size(), out);
		for (int i = 0; i < sprites.size(); ++i) {
			TreeSprite treeSprite = (TreeSprite) (sprites.get(i));
			treeSprite.saveMobileData(out);
		}
	}

	public void save(DataOutputStream out, Object[] resManagers) throws Exception {
		out.writeInt(sprites.size());
		for (int i = 0; i < sprites.size(); ++i) {
			TreeSprite treeSprite = (TreeSprite) (sprites.get(i));
			treeSprite.save(out);
		}
	}

	public void load(DataInputStream in, Object[] resManagers) throws Exception {
		reset();
		SIManager siManager = (SIManager) (resManagers[MainFrame.RES_SI]);
		int length = in.readInt();
		for (int i = 0; i < length; ++i) {
			TreeSprite treeSprite = TreeSprite.createViaFile(in, siManager);
			addSprite(treeSprite);
		}
	}
}

class TreeSpritePanel
	extends SpriteManagerPanel {

	public TreeSpritePanel(JFrame owner, MouseInfo mouseInfo) {
		super(owner, mouseInfo);
	}

	public TreeSpritePanel(JDialog owner, MouseInfo mouseInfo) {
		super(owner, mouseInfo);
	}

	protected SpriteManager createManager() {
		return new TreeSpriteManager(this, mouseInfo);
	}
}
