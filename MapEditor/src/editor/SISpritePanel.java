/*******************************************************************************
 * Copyright (C)   Inc.All Rights Reserved.
 * FileName: SISpritePanel.java 
 * Description:
 * History:  
 * 版本号 作者 日期 简要介绍相关操作 
 * 1.0 cole  2015年4月21日 上午10:00:43
 *******************************************************************************/
package editor;

import java.awt.Graphics;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import javax.swing.JDialog;
import javax.swing.JFrame;


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
					"  ID：" + getID() +
					"  宽：" + getWidth() +
					"  高：" + getHeight() +
					"  层：" + getLayer() +
					"  位置：" + getMobileX() + "," + getMobileY();
	return result;
}

public void paintIdle(Graphics g, int x, int y) {
	si.paintLeftTop(g, x, y, isFlip());
}

public void saveMobileData(DataOutputStream out) throws Exception {
	SL.writeInt(si.getID(), out);
	BasicSprite.saveMobileData(this, out);
//	SL.writeInt(layer, out);
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
