package editor;

import java.util.*;

import java.awt.*;
import java.awt.event.*;

interface MouseInfoListener {
	public void infoChanged();

	public void painterChanged();

	public void resetAll();
}

class MouseInfoAdapter
	implements MouseInfoListener {
	public void infoChanged() {}

	public void painterChanged() {}

	public void resetAll() {}
}

/**
 鼠标上附带的一些信息。例如图片或数据等。
 */
public class MouseInfo {
	//鼠标按键
	public final static int LEFT_BUTTON = MouseEvent.BUTTON1;
	public final static int RIGHT_BUTTON = MouseEvent.BUTTON3;
	//鼠标状态
	public final static int SELECT_POINT = 0;
	public final static int NEW_SPRITE = 1;
	public final static int DELETE_SPRITE = 2;

	private int info;
	private Painter painter;
	private int range;
	private boolean flip;
	private ArrayList listeners;

	public MouseInfo() {
		listeners = new ArrayList();
		info = SELECT_POINT;
		painter = null;
		range = 1;
		flip = false;
	}

	public void resetSelf() {
		setInfo(SELECT_POINT);
		setPainter(painter = null);
	}

	public int getInfo() {
		return info;
	}

	public void setInfo(int info) {
		this.info = info;
		for (int i = 0; i < listeners.size(); ++i) {
			( (MouseInfoListener) (listeners.get(i))).infoChanged();
		}
	}

	public Painter getPainter() {
		return painter;
	}

	public void setPainter(Painter painter) {
		this.painter = painter;
		for (int i = 0; i < listeners.size(); ++i) {
			( (MouseInfoListener) (listeners.get(i))).painterChanged();
		}
	}

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public boolean isFlip() {
		return flip;
	}

	public void setFlip(boolean flip) {
		this.flip = flip;
	}

	public void addListener(MouseInfoListener listener) {
		if (listener != null) {
			listeners.add(listener);
		}
	}

	public void removeListener(MouseInfoListener listener) {
		if (listener != null) {
			listeners.remove(listener);
		}
	}

	public void resetAll() {
		resetSelf();
		for (int i = 0; i < listeners.size(); ++i) {
			( (MouseInfoListener) (listeners.get(i))).resetAll();
		}
	}

	public void paint(Graphics g, int x, int y) {
		if (info == NEW_SPRITE && painter != null) {
			if (painter instanceof WAPainter) {
				( (WAPainter) (painter)).paintOrigin(g, x, y, range);
			}
			else if (painter instanceof SIPainter) {
				( (SIPainter) (painter)).paintOrigin(g, x, y, flip);
			}
			else {
				painter.paintOrigin(g, x, y);
			}
		}
	}
}