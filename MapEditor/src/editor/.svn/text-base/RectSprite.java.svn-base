package editor;

import java.io.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class RectPainter
	extends PainterPanel {
	public final static int W = 50;
	public final static int H = 50;
	public final static int TYPE_BODY = 0;
	public final static int[] TYPES = {TYPE_BODY};
	public final static String[] NAMES = {"¿É¹¥»÷·¶Î§"};

	public final static RectPainter[] getPainters() {
		RectPainter[] result = new RectPainter[TYPES.length];
		for (int i = 0; i < TYPES.length; ++i) {
			result[i] = new RectPainter(TYPES[i]);
			result[i].computeSize();
		}
		return result;
	}

	public static void paint(Graphics g, int type, int x, int y, int width, int height) {
		switch (type) {
			case TYPE_BODY:
				Color oldColor = g.getColor();
				g.setColor(Color.RED);
				g.fillRect(x, y, width, height);
				g.setColor(oldColor);
				break;
		}
	}

	int type;

	public RectPainter(int type) {
		this.type = type;
	}

	public int getGroupID() {
		return -1;
	}

	public int getID() {
		return type;
	}

	public int getType() {
		return type;
	}

	public int getImageWidth() {
		return W;
	}

	public int getImageHeight() {
		return H;
	}

	public String getName() {
		return NAMES[type];
	}

	public void paintLeftTop(Graphics g, int left, int top) {
		paint(g, type, left, top, W, H);
	}

	public void paintOrigin(Graphics g, int originX, int originY) {
//		paintLeftTop(g, originX - W / 2, originY - H / 2);
	}
}

public class RectSprite
	extends Rect
	implements Sprite {
	private int id;
	private int type;
	private String name;

	public RectSprite(int id, int type, int x, int y, int width, int height) {
		super(x, y, width, height);
		this.id = id;
		this.type = type;
		this.name = "";
	}

	public int compareTo(Object o) {
		if (o instanceof RectSprite) {
			return this.id - ( (RectSprite) o).id;
		}
		return this.hashCode() - o.hashCode();
	}

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSelected() {
		return false;
	}

	public void setSelected(boolean selected) {}

	public boolean isMoving() {
		return false;
	}

	public boolean containPoint(int x, int y) {
		return contains(x, y);
	}

	public boolean inRect(int x, int y, int w, int h) {
		return (this.x >= x && this.x + this.width <= x + w &&
				this.y > y && this.y + this.height <= y + h);
	}

	public void moving(int offsetX, int offsetY) {}

	public void cancelMoving() {}

	public void confirmMoving() {}

	public String getInfo() {
		return "ID£º" + id +
			"  X£º" + x +
			"  Y£º" + y +
			"  ¿í£º" + width +
			"  ¸ß£º" + height;
	}

	public void paint(Graphics g) {
		paintIdle(g);
	}

	public void paintIdle(Graphics g) {
		RectPainter.paint(g, type, x, y, width, height);
	}

	public void paintSelected(Graphics g) {
		paintIdle(g);
		Graphics2D g2 = (Graphics2D) g;
		Composite oldComposite = g2.getComposite();
		Color oldColor = g2.getColor();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 1));
		g2.setColor(Color.BLUE);
		this.paintCorner(g2);
		g2.setColor(oldColor);
		g2.setComposite(oldComposite);
	}

	public void paintMoving(Graphics g) {
		paintIdle(g);
	}
	
	public void saveMobileData(DataOutputStream out) throws Exception {
		SL.writeInt(x, out);
		SL.writeInt(y, out);
		SL.writeInt(width, out);
		SL.writeInt(height, out);
	}
	
	public void save(DataOutputStream out) throws Exception {
		out.writeInt(id);
		out.writeInt(type);
		out.writeInt(x);
		out.writeInt(y);
		out.writeInt(width);
		out.writeInt(height);
	}
	
	public static RectSprite createViaFile(DataInputStream in) throws Exception {
		RectSprite result = new RectSprite(0, 0, 0, 0, 0, 0);
		result.load(in);
		return result;
	}
	
	private void load(DataInputStream in) throws Exception {
		id = in.readInt();
		type = in.readInt();
		x = in.readInt();
		y = in.readInt();
		width = in.readInt();
		height = in.readInt();
	}
}

class RectManager
	extends SpriteManager {
	private RectSprite modifyRect;
	private int modifyCorner;
	private int modifyX;
	private int modifyY;

	public RectManager(ScrollablePanel scrollablePanel, MouseInfo mouseInfo) {
		super(scrollablePanel, mouseInfo);
		setCopyable(false);
		setLayerable(false);
		setMoveable(false);
		setDeleteable(false);
		setUndoable(false);
		setSelectionMode(NONE_SELECTION);
		setNewspriteMode(NONE_NEWSPRITE);

		modifyRect = null;
		modifyCorner = Rect.CORNER_NONE;
		modifyX = 0;
		modifyY = 0;
	}

	public void reset() {
		super.reset();
		setModifyRect(null);
		setModifyCorner(Rect.CORNER_NONE);
	}

	private void setModifyRect(Sprite rect) {
		if (modifyRect != null) {
			if (modifyRect.width < 5 && modifyRect.height < 5) {
				modifyRect.width = modifyRect.height = 5;
			}
		}
		modifyRect = (RectSprite) rect;
		selection.clear();
		if (modifyRect != null) {
			selection.selectSprite(modifyRect);
		}
	}

	private void setModifyCorner(int corner) {
		modifyX = mouseX;
		modifyY = mouseY;
		modifyCorner = corner;
	}

	protected void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		if (e.getButton() == XUtil.LEFT_BUTTON) {
			if (mouseInfo.getInfo() == MouseInfo.NEW_SPRITE) {
				createRect(e);
			}
			else {
				selectRect(e);
			}
		}
		scrollablePanel.repaint();
	}

	private void createRect(MouseEvent e) {
		if (! (mouseInfo.getPainter()instanceof RectPainter)) {
			return;
		}
		int type = ( (RectPainter) (mouseInfo.getPainter())).getType();
		RectSprite rect = new RectSprite(useMaxID(), type, mouseX, mouseY, 1, 1);
		addSprite(rect);
		setModifyRect(rect);
		setModifyCorner(Rect.CORNER_RIGHT_BOTTOM);
	}

	private void selectRect(MouseEvent e) {
		boolean reselect = true;
		if (modifyRect != null) {
			setModifyCorner(modifyRect.getSelectCorner(e.getX(), e.getY(),
				scrollablePanel.getScale(), scrollablePanel.getScale()));
			if (modifyCorner != Rect.CORNER_NONE) {
				reselect = false;
			}
			else if (modifyRect.containPoint(mouseX, mouseY)) {
				reselect = false;
			}
		}
		if (reselect) {
			setModifyRect(getTopSpriteAtPoint(mouseX, mouseY));
		}
	}

	protected void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
		if (e.getButton() == XUtil.LEFT_BUTTON) {
			if (mouseInfo.getInfo() == MouseInfo.NEW_SPRITE) {
				setModifyRect(null);
				setModifyCorner(Rect.CORNER_NONE);
			}
			else {
				setModifyCorner(Rect.CORNER_NONE);
			}
		}
		else if (e.getButton() == XUtil.RIGHT_BUTTON) {
			setModifyRect(null);
			setModifyCorner(Rect.CORNER_NONE);
		}
		scrollablePanel.repaint();
	}

	protected void mouseMoved(MouseEvent e) {
		super.mouseMoved(e);
		if (mouseInfo.getInfo() == MouseInfo.NEW_SPRITE) {
			scrollablePanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		}
		else {
			if (modifyRect != null) {
				scrollablePanel.setCursor(
					modifyRect.getCornerCursor(
					modifyRect.getSelectCorner(e.getX(), e.getY(),
											   scrollablePanel.getScale(), scrollablePanel.getScale())));
			}
			else {
				scrollablePanel.setCursor(Cursor.getDefaultCursor());
			}
		}
		scrollablePanel.repaint();
	}

	protected void mouseDragged(MouseEvent e) {
		super.mouseDragged(e);
		if (modifyRect != null) {
			setModifyCorner(modifyRect.resizeByCorner(modifyCorner, mouseX - modifyX, mouseY - modifyY));
		}
		scrollablePanel.repaint();
		selectionChanged();
	}

	protected void keyUpPressed(KeyEvent e) {
		super.keyUpPressed(e);
		int offset = getOffset(e);
		if (modifyRect != null) {
			modifyRect.y -= offset;
			scrollablePanel.repaint();
		}
	}

	protected void keyDownPressed(KeyEvent e) {
		super.keyDownPressed(e);
		int offset = getOffset(e);
		if (modifyRect != null) {
			modifyRect.y += offset;
			scrollablePanel.repaint();
		}
	}

	protected void keyLeftPressed(KeyEvent e) {
		super.keyLeftPressed(e);
		int offset = getOffset(e);
		if (modifyRect != null) {
			modifyRect.x -= offset;
			scrollablePanel.repaint();
		}
	}

	protected void keyRightPressed(KeyEvent e) {
		super.keyRightPressed(e);
		int offset = getOffset(e);
		if (modifyRect != null) {
			modifyRect.x += offset;
			scrollablePanel.repaint();
		}
	}

	private int getOffset(KeyEvent e) {
		int result = XUtil.getDefPropInt("DefRectOffset");
		if (e.isControlDown()) {
			result = XUtil.getDefPropInt("CtrlRectOffset");
		}
		if (e.isShiftDown()) {
			result = XUtil.getDefPropInt("ShiftRectOffset");
		}
		return result;
	}

	protected void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_DELETE && modifyRect != null) {
			removeSprite(modifyRect);
			setModifyRect(null);
			setModifyCorner(Rect.CORNER_NONE);
		}
		scrollablePanel.repaint();
	}

	public void paintStatic(Graphics g) {
		for (int i = 0; i < sprites.size(); ++i) {
			RectSprite rect = (RectSprite) (sprites.get(i));
			rect.paintIdle(g);
		}
	}

	public void paintSprites(Graphics g) {
		boolean found = false;
		for (int i = 0; i < sprites.size(); ++i) {
			RectSprite rect = (RectSprite) (sprites.get(i));
			if (modifyRect != null) {
				if (rect.getID() == modifyRect.getID()) {
					found = true;
					continue;
				}
			}
			rect.paintIdle(g);
		}
		if (found) {
			modifyRect.paintSelected(g);
		}
	}

	public void saveMobileData(DataOutputStream out, Object[] resManagers) throws Exception {
		SL.writeInt(sprites.size(), out);
		for(int i = 0; i < sprites.size(); ++i) {
			RectSprite rect = (RectSprite)(sprites.get(i));
			rect.saveMobileData(out);
		}
	}

	public void save(DataOutputStream out, Object[] resManagers) throws Exception {
		out.writeInt(sprites.size());
		for(int i = 0; i < sprites.size(); ++i) {
			RectSprite rect = (RectSprite)(sprites.get(i));
			rect.save(out);
		}
	}

	public void load(DataInputStream in, Object[] resManagers) throws Exception {
		reset();
		int count = in.readInt();
		for(int i = 0; i < count; ++i) {
			RectSprite rect = RectSprite.createViaFile(in);
			addSprite(rect);
		}
	}
}

class RectPanel
	extends SpriteManagerPanel {

	public RectPanel(JFrame owner, MouseInfo mouseInfo) {
		super(owner, mouseInfo);
	}

	public RectPanel(JDialog owner, MouseInfo mouseInfo) {
		super(owner, mouseInfo);
	}

	protected SpriteManager createManager() {
		return new RectManager(this, mouseInfo);
	}

	protected void paintBack(Graphics g) {
		paintBackManagers(g);
	}

	protected void paintSelf(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		Composite oldComposite = null;
		if (showAlpha) {
			oldComposite = g2.getComposite();
			if (backAlpha != null) {
				g2.setComposite(backAlpha);
			}
			super.paintSelf(g);
			g2.setComposite(oldComposite);
		}
	}

	protected void paintFore(Graphics g) {
		paintForeManagers(g);
	}
}
