///*
package editor;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

interface SpriteManagerListener {
	public void mouseChanged();

	public void keyChanged();

	public void selectionChanged();
}

class SpriteManagerAdapter
	implements SpriteManagerListener {
	public void mouseChanged() {}

	public void keyChanged() {}

	public void selectionChanged() {}
}

/**
 管理Sprite的选择、添加、移动和删除的管理器。
 */
abstract public class SpriteManager {
	//鼠标状态
	private final static int MOUSE_STATE_NORMAL = 0;
	private final static int MOUSE_STATE_DRAGGING_SPRITE = 1;
	private final static int MOUSE_STATE_RECTANGLING = 2; //鼠标拉框
	private final static int MOUSE_STATE_CTRL_RECTING = 3; //按住Ctrl键鼠标拉框
	private final static int MOUSE_STATE_SELECT_POINT_RIGHT_PRESSED = 4; //在SelectPoint状态按下鼠标右键的标志
	private final static int MOUSE_STATE_NEW_SPRITE_LEFT_PRESSED = 5; //在NewSprite状态按下鼠标左键
	//选择状态
	public final static int NONE_SELECTION = 0;
	public final static int SINGLE_SELECTION = 1;
	public final static int MULTI_SELECTION = 2;
	//新加节点状态
	public final static int NONE_NEWSPRITE = 0;
	public final static int SINGLE_NEWSPRITE = 1;
	public final static int MULTI_NEWSPRITE = 2;

	public final static int[][] KEY_CODES = { {KeyEvent.VK_UP, 0x00000001, KeyEvent.VK_DOWN}
											, {KeyEvent.VK_DOWN, 0x00000002, KeyEvent.VK_UP}
											, {KeyEvent.VK_LEFT, 0x00000004, KeyEvent.VK_RIGHT}
											, {KeyEvent.VK_RIGHT, 0x00000008, KeyEvent.VK_LEFT}
	};

	protected ScrollablePanel scrollablePanel;
	protected MouseInfo mouseInfo;
	protected ArrayList sprites;
	protected static ArrayList newspritePoints;
	protected static ArrayList copiedSprites;
	protected SpriteSelection selection;
	protected SpriteUndoManager undoManager;
	protected boolean undoable, moveable, deleteable, copyable, layerable, tooltipable;
	protected int
		maxID, mouseState,
		oldMouseX, oldMouseY, mouseX, mouseY,
		pressedKey, keyMoveSpriteX, keyMoveSpriteY,
		newSpriteX, newSpriteY,
		selectionMode, newspriteMode;

	private ArrayList listeners;

	public SpriteManager(ScrollablePanel scrollablePanel, MouseInfo mouseInfo) {
		try {
			init(scrollablePanel, mouseInfo);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void init(ScrollablePanel scrollablePanel, MouseInfo mouseInfo) throws Exception {
		this.scrollablePanel = scrollablePanel;
		this.mouseInfo = mouseInfo;

		sprites = new ArrayList();
		newspritePoints = new ArrayList();
		copiedSprites = new ArrayList();
		listeners = new ArrayList();

		this.selection = new SpriteSelection(this);
		this.undoManager = new SpriteUndoManager(this);

		selectionMode = MULTI_SELECTION;
		newspriteMode = MULTI_NEWSPRITE;
		undoable = true;
		moveable = true;
		deleteable = true;
		copyable = true;
		layerable = true;
		tooltipable = false;

		initReset();
		newspritePoints.clear();
		copiedSprites.clear();


		scrollablePanel.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				selfMouseClicked(e);
			}

			public void mousePressed(MouseEvent e) {
				selfMousePressed(e);
			}

			public void mouseReleased(MouseEvent e) {
				selfMouseReleased(e);
			}

			public void mouseEntered(MouseEvent e) {
				selfMouseEntered(e);
			}

			public void mouseExited(MouseEvent e) {
				selfMouseExited(e);
			}
		});

		scrollablePanel.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				selfMouseDragged(e);
			}

			public void mouseMoved(MouseEvent e) {
				selfMouseMoved(e);
			}
		});

		scrollablePanel.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
				selfKeyTyped(e);
			}

			public void keyPressed(KeyEvent e) {
				selfKeyPressed(e);
			}

			public void keyReleased(KeyEvent e) {
				selfKeyReleased(e);
			}
		});
	}

	private void initReset() {
		maxID = 0;
		mouseState = MOUSE_STATE_NORMAL;
		pressedKey = 0;
		keyMoveSpriteX = 0;
		keyMoveSpriteY = 0;

		sprites.clear();
//		newspritePoints.clear();
//		copiedSprites.clear();

		if (selection != null) {
			selection.clear();
		}
		if (undoManager != null) {
			undoManager.discardAllEdits();
		}
	}

	public void reset() {
		initReset();
	}

	public void resetOrigin(int offsetX, int offsetY) {
		for (int i = 0; i < sprites.size(); ++i) {
			Sprite sprite = (Sprite) (sprites.get(i));
			sprite.confirmMoving();
			sprite.setPosition(sprite.getX() - offsetX, sprite.getY() - offsetY);
		}
	}

	public void addListener(SpriteManagerListener listener) {
		if (listener != null) {
			listeners.add(listener);
		}
	}

	public void removeListener(SpriteManagerListener listener) {
		if (listener != null) {
			listeners.remove(listener);
		}
	}

	protected int useMaxID() {
		return maxID++;
	}

	public int getMouseX() {
		return mouseX;
	}

	public int getMouseY() {
		return mouseY;
	}

	public SpriteUndoManager getUndoManager() {
		return undoManager;
	}

	public Sprite getSprite(int spriteID) {
		Sprite result = null;
		for (int i = 0; i < sprites.size(); ++i) {
			if ( ( (Sprite) (sprites.get(i))).getID() == spriteID) {
				result = (Sprite) (sprites.get(i));
				break;
			}
		}
		return result;
	}

	public Sprite[] getSprites() {
		Sprite[] result = new Sprite[sprites.size()];
		for (int i = 0; i < sprites.size(); ++i) {
			result[i] = (Sprite) (sprites.get(i));
		}
		return result;
	}

	public SpriteSelection getSelection() {
		return selection;
	}

	public void setSelectionMode(int mode) {
		if (this.selectionMode == mode) {
			return;
		}
		selection.clear();
		selectionMode = mode;
	}

	public void setNewspriteMode(int newspriteMode) {
		if (this.newspriteMode == newspriteMode) {
			return;
		}
		newspritePoints.clear();
		this.newspriteMode = newspriteMode;
	}

	public void setUndoable(boolean undoable) {
		if (this.undoable == undoable) {
			return;
		}
		undoManager.discardAllEdits();
		this.undoable = undoable;
	}

	public void setMoveable(boolean moveable) {
		this.moveable = moveable;
	}

	public void setDeleteable(boolean deleteable) {
		this.deleteable = deleteable;
	}

	public void setCopyable(boolean copyable) {
		this.copyable = copyable;
		copiedSprites.clear();
	}

	public void setLayerable(boolean layerable) {
		this.layerable = layerable;
	}

	public void setToolTipable(boolean tooltipable) {
		this.tooltipable = tooltipable;
	}

	protected void setMouseState(int state) {
		switch (state) {
			case MOUSE_STATE_NORMAL: //鼠标状态还原
				this.mouseState = state;
				break;
			case MOUSE_STATE_DRAGGING_SPRITE: //拖动Sprite
				if (moveable) {
					this.mouseState = state;
				}
				break;
			case MOUSE_STATE_RECTANGLING: //框选Sprite
				if (selectionMode == MULTI_SELECTION) {
					this.mouseState = state;
				}
				break;
			case MOUSE_STATE_CTRL_RECTING: //Ctrl框选Sprite
				if (selectionMode == MULTI_SELECTION) {
					this.mouseState = state;
				}
				break;
			case MOUSE_STATE_SELECT_POINT_RIGHT_PRESSED: //右键选择Sprite
				if (selectionMode != NONE_SELECTION) {
					this.mouseState = state;
				}
				break;
			case MOUSE_STATE_NEW_SPRITE_LEFT_PRESSED: //拖动新增Sprite
				if (newspriteMode == MULTI_NEWSPRITE) {
					this.mouseState = state;
				}
				break;
			default:
				break;
		}
	}

	protected int getMouseX(MouseEvent e) {
		return scrollablePanel.getMouseX(e);
	}

	protected int getMouseY(MouseEvent e) {
		return scrollablePanel.getMouseY(e);
	}

	private void selfMouseClicked(MouseEvent e) {
		if (!scrollablePanel.isMouseMovingViewport()) {
			mouseClicked(e);
		}
		mouseChanged();
	}

	protected void mouseClicked(MouseEvent e) {
		if (e.getButton() == XUtil.LEFT_BUTTON && e.getClickCount() >= 2) {
			switch (mouseInfo.getInfo()) {
				case MouseInfo.SELECT_POINT:
					selectPointMouseDoubleClick(e);
					break;
				default:
					break;
			}
		}
	}

	private void selfMousePressed(MouseEvent e) {
		oldMouseX = mouseX = getMouseX(e);
		oldMouseY = mouseY = getMouseY(e);
		if (!scrollablePanel.isMouseMovingViewport()) {
			mousePressed(e);
		}
		mouseChanged();
	}

	protected void mousePressed(MouseEvent e) {
		switch (e.getButton()) {
			case XUtil.LEFT_BUTTON:
				switch (mouseInfo.getInfo()) {
					case MouseInfo.SELECT_POINT:
						selectPointMouseLeftPressed(e);
						break;
					case MouseInfo.NEW_SPRITE:
						newSpriteMouseLeftPressed(e);
						break;
					default:
						break;
				}
				break;
			case XUtil.RIGHT_BUTTON:
				switch (mouseInfo.getInfo()) {
					case MouseInfo.SELECT_POINT:
						selectPointMouseRightPressed(e);
						break;
					default:
						otherMouseRightPressed(e);
						break;
				}
			default:
				break;
		}
	}

	private void selfMouseReleased(MouseEvent e) {
		mouseX = getMouseX(e);
		mouseY = getMouseY(e);
		if (!scrollablePanel.isMouseMovingViewport()) {
			mouseReleased(e);
		}
		setMouseState(MOUSE_STATE_NORMAL);
		mouseChanged();
	}

	protected void mouseReleased(MouseEvent e) {
		switch (e.getButton()) {
			case XUtil.LEFT_BUTTON:
				switch (mouseInfo.getInfo()) {
					case MouseInfo.SELECT_POINT:
						selectPointMouseLeftReleased(e);
						break;
					case MouseInfo.NEW_SPRITE:
						newSpriteMouseLeftReleased(e);
					default:
						break;
				}
				break;
			case XUtil.RIGHT_BUTTON:
				switch (mouseInfo.getInfo()) {
					case MouseInfo.SELECT_POINT:
						selectPointMouseRightReleased(e.getX(), e.getY());
						break;
					default:
						break;
				}
				break;
			default:
				break;
		}
	}

	private void selfMouseDragged(MouseEvent e) {
		mouseX = getMouseX(e);
		mouseY = getMouseY(e);
//		checkMouseXY();
		if (!scrollablePanel.isMouseMovingViewport()) {
			mouseDragged(e);
		}
		mouseChanged();
	}

	protected void mouseDragged(MouseEvent e) {
		switch (mouseInfo.getInfo()) {
			case MouseInfo.SELECT_POINT:
				selectPointMouseDragged(e);
				break;
			case MouseInfo.NEW_SPRITE:
				newSpriteMouseDragged(e);
			default:
				break;
		}
	}

	private void selfMouseMoved(MouseEvent e) {
		mouseX = getMouseX(e);
		mouseY = getMouseY(e);
		if (!scrollablePanel.isMouseMovingViewport()) {
			mouseMoved(e);
		}
		mouseChanged();
	}

	protected void mouseMoved(MouseEvent e) {
		switch (e.getButton()) {
			case MouseEvent.NOBUTTON:
				switch (mouseInfo.getInfo()) {
					case MouseInfo.SELECT_POINT:
						selectPointMouseMoved(e);
						break;
					case MouseInfo.NEW_SPRITE:
						newSpriteMouseMoved(e);
						break;
					default:
						break;
				}
				break;
			default:
				break;
		}
	}

	private void selfMouseEntered(MouseEvent e) {
		selfMouseMoved(e);
		mouseEntered(e);
	}

	protected void mouseEntered(MouseEvent e) {
	}

	private void selfMouseExited(MouseEvent e) {
		selfMouseMoved(e);
		mouseExited(e);
	}

	protected void mouseExited(MouseEvent e) {
	}

	private void selfKeyTyped(KeyEvent e) {
	}

	private void selfKeyPressed(KeyEvent e) {
		int tmp, keyID = getKeyID(e.getKeyCode());
		if (keyID >= 0) {
			pressedKey = pressedKey | KEY_CODES[keyID][1];
			for (int i = 2; i < KEY_CODES[keyID].length; ++i) {
				tmp = getKeyID(KEY_CODES[keyID][i]);
				if (tmp >= 0) {
					pressedKey = pressedKey & (~KEY_CODES[tmp][1]);
				}
			}
		}
		keyPressed(e);
		keyChanged();
	}

	protected void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_UP:
				keyUpPressed(e);
				break;
			case KeyEvent.VK_DOWN:
				keyDownPressed(e);
				break;
			case KeyEvent.VK_LEFT:
				keyLeftPressed(e);
				break;
			case KeyEvent.VK_RIGHT:
				keyRightPressed(e);
				break;
			case KeyEvent.VK_Z:
				if (undoable && e.isControlDown()) {
					undoManager.undo();
				}
				break;
			case KeyEvent.VK_Y:
				if (undoable && e.isControlDown()) {
					undoManager.redo();
				}
				break;
			default:
				break;
		}
	}

	private void selfKeyReleased(KeyEvent e) {
		int keyID = getKeyID(e.getKeyCode());
		if (keyID >= 0) {
			pressedKey = pressedKey & (~KEY_CODES[keyID][1]);
		}
		keyReleased(e);
		keyChanged();
	}

	protected void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_UP:
				keyUpReleased(e);
				break;
			case KeyEvent.VK_DOWN:
				keyDownReleased(e);
				break;
			case KeyEvent.VK_LEFT:
				keyLeftReleased(e);
				break;
			case KeyEvent.VK_RIGHT:
				keyRightReleased(e);
				break;
			case KeyEvent.VK_DELETE:
				if (deleteable) {
					keyDeleteReleased();
				}
				break;
			case KeyEvent.VK_C:
				if (copyable && e.isControlDown() && !selection.isEmpty()) {
					copySprites(selection.getSprites());
				}
				break;
			case KeyEvent.VK_V:
				if (copyable && e.isControlDown()) {
					if (scrollablePanel.isMouseIn()) {
						pasteSprites(mouseX, mouseY);
					}
					else {
						pasteSprites(scrollablePanel.getBasicWidth() / 2,
									 scrollablePanel.getBasicHeight() / 2);
					}
				}
				break;
			case KeyEvent.VK_HOME:
				if (layerable && e.isControlDown() && !selection.isEmpty()) {
					setSpritesToTop(selection.getSprites());
				}
				break;
			case KeyEvent.VK_END:
				if (layerable && e.isControlDown() && !selection.isEmpty()) {
					setSpritesToBottom(selection.getSprites());
				}
				break;
			default:
				break;
		}
	}

	protected int getKeyID(int keyCode) {
		for (int i = 0; i < KEY_CODES.length; ++i) {
			if (keyCode == KEY_CODES[i][0]) {
				return i;
			}
		}
		return -1;
	}

	protected boolean isKeyPressed(int keyCode) {
		int keyID = getKeyID(keyCode);
		if (keyID >= 0) {
			return (pressedKey & KEY_CODES[keyID][1]) != 0;
		}
		return false;
	}

	protected int getSpriteIndex(Sprite sprite) {
		if (sprite != null) {
			Sprite tmp;
			for (int i = 0; i < sprites.size(); ++i) {
				tmp = (Sprite) (sprites.get(i));
				if (tmp.getID() == sprite.getID()) {
					return i;
				}
			}
		}
		return -1;
	}

	protected Sprite findSprite(int id) {
		Sprite tmp;
		for (int i = 0; i < sprites.size(); ++i) {
			tmp = (Sprite) (sprites.get(i));
			if (tmp.getID() == id) {
				return tmp;
			}
		}
		return null;
	}

	protected boolean addNewSprite(int x, int y) {
		Sprite sprite = createSprite(x, y);
		if (sprite == null) {
			return false;
		}
		return addUndoableSprite(sprite);
	}

	protected Sprite createSprite(int x, int y) {
		return null;
	}

	protected boolean addUndoableSprite(Sprite sprite) {
		Sprite[] tmp = new Sprite[1];
		tmp[0] = sprite;
		return addUndoableSprites(tmp);
	}

	protected boolean addUndoableSprites(Sprite[] sprites) {
		ArrayList tmp = new ArrayList();
		for (int i = 0; i < sprites.length; ++i) {
			if (addSprite(sprites[i])) {
				tmp.add(sprites[i]);
			}
		}
		if (tmp.size() > 0) {
			Sprite[] undoArray = new Sprite[tmp.size()];
			for (int i = 0; i < tmp.size(); ++i) {
				undoArray[i] = (Sprite) (tmp.get(i));
			}
			undoManager.addUndoSpriteAdd(undoArray);
			resortSprites();
			return true;
		}
		else {
			return false;
		}
	}

	protected boolean addSprite(Sprite sprite) {
		if (getSpriteIndex(sprite) < 0) {
			if (sprites.add(sprite)) {
				if (maxID <= sprite.getID()) {
					maxID = sprite.getID() + 1;
				}
				return true;
			}
		}
		return false;
	}

	protected boolean removeUndoableSprite(Sprite sprite) {
		Sprite[] tmp = new Sprite[1];
		tmp[0] = sprite;
		return removeUndoableSprites(tmp);
	}

	protected boolean removeUndoableSprites(Sprite[] sprites) {
		ArrayList tmp = new ArrayList();
		for (int i = 0; i < sprites.length; ++i) {
			if (removeSprite(sprites[i])) {
				tmp.add(sprites[i]);
			}
		}
		if (tmp.size() > 0) {
			Sprite[] undoArray = new Sprite[tmp.size()];
			for (int i = 0; i < tmp.size(); ++i) {
				undoArray[i] = (Sprite) (tmp.get(i));
			}
			undoManager.addUndoSpriteRemove(undoArray);
			return true;
		}
		else {
			return false;
		}
	}

	protected boolean removeSprite(Sprite sprite) {
		selection.removeSprite(sprite);
		int spriteIndex = getSpriteIndex(sprite);
		if (spriteIndex >= 0) {
			sprites.remove(spriteIndex);
			return true;
		}
		return false;
	}

	protected void resortSprites() {
		TreeSet tmp = new TreeSet();
		for (int i = 0; i < sprites.size(); ++i) {
			tmp.add(sprites.get(i));
		}
		sprites.clear();
		Iterator it = tmp.iterator();
		while (it.hasNext()) {
			sprites.add(it.next());
		}
	}

	protected Sprite[] getAllSpritesAtPoint(int x, int y) {
		Sprite sprite;
		ArrayList tmp = new ArrayList();

		for (int i = 0; i < sprites.size(); ++i) {
			sprite = (Sprite) (sprites.get(i));
			if (sprite.containPoint(x, y)) {
				tmp.add(sprite);
			}
		}

		Sprite[] result = new Sprite[tmp.size()];
		for (int i = 0; i < tmp.size(); ++i) {
			result[i] = (Sprite) (tmp.get(i));
		}
		return result;
	}

	protected Sprite getTopSpriteAtPoint(int x, int y) {
		Sprite sprite;
		for (int i = sprites.size() - 1; i >= 0; --i) {
			sprite = (Sprite) (sprites.get(i));
			if (sprite.containPoint(x, y)) {
				return sprite;
			}
		}
		return null;
	}

	protected Sprite[] getAllSpritesInRect(int x, int y, int w, int h) {
		Sprite sprite;
		ArrayList tmp = new ArrayList();

		for (int i = 0; i < sprites.size(); ++i) {
			sprite = (Sprite) (sprites.get(i));
			if (sprite.inRect(x, y, w, h)) {
				tmp.add(sprite);
			}
		}

		Sprite[] result = new Sprite[tmp.size()];
		for (int i = 0; i < tmp.size(); ++i) {
			result[i] = (Sprite) (tmp.get(i));
		}
		return result;
	}

	protected void selectPointMouseLeftPressed(MouseEvent e) {
		switch (selectionMode) {
			case NONE_SELECTION:
				break;
			case SINGLE_SELECTION:
				singleSelectionMouseLeftPressed(e);
				break;
			case MULTI_SELECTION:
				multiSelectionMouseLeftPressed(e);
				break;
			default:
				break;
		}
	}

	protected void singleSelectionMouseLeftPressed(MouseEvent e) {
		if (selection.pointInSprites(mouseX, mouseY)) { //按键点在已选择的物体内
			selection.click(mouseX, mouseY);
			setMouseState(MOUSE_STATE_DRAGGING_SPRITE); //可以拖动物体了
		}
		else {
			Sprite sprite = getTopSpriteAtPoint(mouseX, mouseY);
			if (sprite != null) {
				selection.selectSprite(sprite);
				selection.click(mouseX, mouseY);
				setMouseState(MOUSE_STATE_DRAGGING_SPRITE); //可以拖动物体了
			}
			else {
				selection.clear();
			}
		}
		scrollablePanel.repaint();
	}

	protected void multiSelectionMouseLeftPressed(MouseEvent e) {
		if (selection.pointInSprites(mouseX, mouseY)) { //按键点在已选择的物体内
			if (e.isControlDown()) { //按了Ctrl键
				selection.click(mouseX, mouseY, KeyEvent.VK_CONTROL); //取消该物体的选择
			}
			else { //没有按Ctrl键
				selection.click(mouseX, mouseY); //无反应
				setMouseState(MOUSE_STATE_DRAGGING_SPRITE); //可以拖动物体了
			}
		}
		else { //按键点不在已选的物体内
			Sprite sprite = getTopSpriteAtPoint(mouseX, mouseY); //获得最上方的物体
			if (sprite != null) { //选中物体
				if (e.isControlDown()) { //按了Ctrl键
					selection.addSprite(sprite); //将选中的物体加入到selection中
					selection.click(mouseX, mouseY); //无反应
				}
				else {
					selection.selectSprite(sprite); //取消之前选中的物体，然后将现在选中的物体加入到selection中
					selection.click(mouseX, mouseY); //无反应
					setMouseState(MOUSE_STATE_DRAGGING_SPRITE); //可以拖动物体了
				}
			}
			else { //没有选中任何物体
				if (e.isControlDown()) {
					setMouseState(MOUSE_STATE_CTRL_RECTING); //Ctrl键拖框
				}
				else {
					selection.clear(); //取消之前选中的物体
					setMouseState(MOUSE_STATE_RECTANGLING); //普通拖框
				}
			}
		}
		scrollablePanel.repaint();
	}

	protected void selectPointMouseRightPressed(MouseEvent e) {
		switch (mouseState) {
			case MOUSE_STATE_RECTANGLING:
				setMouseState(MOUSE_STATE_NORMAL);
				scrollablePanel.repaint();
				break;
			case MOUSE_STATE_DRAGGING_SPRITE:
				setMouseState(MOUSE_STATE_NORMAL);
				if (!selection.isEmpty()) {
					selection.cancelMoving();
//				selection.clear();
					scrollablePanel.repaint();
				}
				break;
			case MOUSE_STATE_NORMAL:
				setMouseState(MOUSE_STATE_SELECT_POINT_RIGHT_PRESSED);
				Sprite sprite = selection.getTopSpriteAtPoint(mouseX, mouseY);
				if (sprite == null) {
					selection.clear();
					scrollablePanel.repaint();
				}
				break;
			default:
				break;
		}
	}

	protected void otherMouseRightPressed(MouseEvent e) {
		boolean spriteChanged = false;
		if (!selection.isEmpty()) {
			selection.cancelMoving();
			selection.clear();
		}
		newspritePoints.clear();
		mouseInfo.resetAll();
		setMouseState(MOUSE_STATE_NORMAL);
		if (spriteChanged) {
			resortSprites();
		}
		scrollablePanel.repaint();
	}

	protected void selectPointMouseDragged(MouseEvent e) {
		switch (mouseState) {
			case MOUSE_STATE_DRAGGING_SPRITE:
				if (e.isShiftDown()) { //按住Shift键，水平或者垂直方向移动
					if (Math.abs(mouseX - oldMouseX) >= Math.abs(mouseY - oldMouseY)) { //X方向偏移量较大
						selection.moving(mouseX - oldMouseX, 0);
					}
					else { //Y方向偏移量较大
						selection.moving(0, mouseY - oldMouseY);
					}
				}
				else { //普通移动
					selection.moving(mouseX - oldMouseX, mouseY - oldMouseY);
				}
				scrollablePanel.repaint();
				break;
			case MOUSE_STATE_RECTANGLING:
				scrollablePanel.repaint();
				break;
			default:
				break;
		}
	}

	protected void selectPointMouseLeftReleased(MouseEvent e) {
		switch (mouseState) {
			case MOUSE_STATE_DRAGGING_SPRITE:
				if (mouseX == oldMouseX && mouseY == oldMouseY) {
					selection.cancelMoving();
				}
				else {
					selection.confirmMoving();
					resortSprites();
					scrollablePanel.repaint();
				}
				break;
			case MOUSE_STATE_RECTANGLING:
				selection.selectSprites(
					getAllSpritesInRect(Math.min(oldMouseX, mouseX), Math.min(oldMouseY, mouseY),
										Math.abs(mouseX - oldMouseX), Math.abs(mouseY - oldMouseY)));
				scrollablePanel.repaint();
				break;
			default:
				break;
		}
		setMouseState(MOUSE_STATE_NORMAL);
	}

	protected void selectPointMouseRightReleased(int x, int y) {
		switch (mouseState) {
			case MOUSE_STATE_SELECT_POINT_RIGHT_PRESSED: //can select
				if (selection.isEmpty()) {
					Sprite[] sprites = getAllSpritesAtPoint(mouseX, mouseY);
					if (sprites != null) {
						if (sprites.length == 1) {
							selection.selectSprite(sprites[0]);
							scrollablePanel.repaint();
							showPopupMenu(sprites[0], x, y);
						}
						else if (sprites.length > 1) {
							showPopupMenu(sprites, x, y);
						}
					}
				}
				else {
					Sprite sprite = selection.getTopSpriteAtPoint(mouseX, mouseY);
					if (sprite != null) {
						showPopupMenu(sprite, x, y);
					}
				}
				break;
			default:
				break;
		}
		setMouseState(MOUSE_STATE_NORMAL);
	}

	protected void selectPointMouseDoubleClick(MouseEvent e) {
		switch (mouseState) {
			case MOUSE_STATE_NORMAL:
				Sprite sprite = selection.getTopSpriteAtPoint(mouseX, mouseY);
				if (sprite != null) {
					showProperties(sprite);
				}
				break;
			default:
				break;
		}
	}

	protected void newSpriteMouseLeftPressed(MouseEvent e) {
		if (newspriteMode == NONE_NEWSPRITE) {
			return;
		}
		Painter painter = mouseInfo.getPainter();
		if (painter != null) {
			setMouseState(MOUSE_STATE_NEW_SPRITE_LEFT_PRESSED);
			newSpriteX = mouseX;
			newSpriteY = mouseY;
			if (addNewSprite(newSpriteX, newSpriteY)) {
				newspritePoints.clear();
				newspritePoints.add(new IntPair(newSpriteX, newSpriteY));
				scrollablePanel.repaint();
			}
		}
	}

	protected void selectPointMouseMoved(MouseEvent e) {
		if (tooltipable) {
			scrollablePanel.setToolTipText(getToolTipText(getTopSpriteAtPoint(mouseX, mouseY)));
		}
	}

	protected String getToolTipText(Sprite sprite) {
		if (sprite == null) {
			return null;
		}
		else {
			return sprite.getName();
		}
	}

	protected void newSpriteMouseMoved(MouseEvent e) {
		scrollablePanel.repaint();
	}

	protected void newSpriteMouseDragged(MouseEvent e) {
		if (newspriteMode != MULTI_NEWSPRITE) {
			return;
		}
		Painter painter = mouseInfo.getPainter();
		if (painter != null && mouseState == MOUSE_STATE_NEW_SPRITE_LEFT_PRESSED) {

			int width = painter.getImageWidth();
			int height = painter.getImageHeight();
			int x = newSpriteX;
			int y = newSpriteY;
			int offsetX = 0; //允许的偏差
			int offsetY = 0;

			if (mouseX <= newSpriteX - width + offsetX || mouseX >= newSpriteX + width - offsetX) {
				x = newSpriteX + (mouseX > newSpriteX ? width : -width);
				if (mouseY <= newSpriteY - height / 2 || mouseY >= newSpriteY + height / 2) {
					y = newSpriteY + (mouseY > newSpriteY ? height : -height);
				}
			}
			if (mouseY <= newSpriteY - height + offsetY || mouseY >= newSpriteY + height - offsetY) {
				y = newSpriteY + (mouseY > newSpriteY ? height : -height);
				if (mouseX <= newSpriteX - width / 2 || mouseX >= newSpriteX + width / 2) {
					x = newSpriteX + (mouseX > newSpriteX ? width : -width);
				}
			}

			if (x != newSpriteX || y != newSpriteY) {
				newSpriteX = x;
				newSpriteY = y;
				IntPair tmp;
				boolean hasSamePos = false;
				for (int i = 0; i < newspritePoints.size(); ++i) {
					tmp = (IntPair) (newspritePoints.get(i));
					if (tmp.x == newSpriteX && tmp.y == newSpriteY) {
						hasSamePos = true;
						break;
					}
				}
				if (!hasSamePos) {
					if (addNewSprite(newSpriteX, newSpriteY)) {
						newspritePoints.add(new IntPair(newSpriteX, newSpriteY));
					}
				}
			}
			scrollablePanel.repaint();
		}
	}

	protected void newSpriteMouseLeftReleased(MouseEvent e) {
		newspritePoints.clear();
		setMouseState(MOUSE_STATE_NORMAL);
	}

	protected void copySprites(Sprite[] sprites) {
		ArrayList tmp = new ArrayList();
		if (sprites != null) {
			for (int i = 0; i < sprites.length; ++i) {
				if (sprites[i] != null) {
					if (sprites[i] instanceof Copyable) {
						tmp.add( ( (Copyable) (sprites[i])).copy());
					}
				}
			}
		}
		if (tmp.size() > 0) {
			copiedSprites.clear();
			for (int i = 0; i < tmp.size(); ++i) {
				copiedSprites.add(tmp.get(i));
			}
		}
	}

	protected void pasteSprites(int x, int y) {
		if (copiedSprites.size() > 0) {
			ArrayList tmp = new ArrayList();
			int left = 0;
			int top = 0;
			int right = 0;
			int bottom = 0;

			boolean init = false;
			for (int i = 0; i < copiedSprites.size(); ++i) {
				Sprite sprite = (Sprite) (copiedSprites.get(i));
				if (sprite instanceof Copyable) {
					if (!init) {
						left = sprite.getX();
						top = sprite.getY();
						right = sprite.getX();
						bottom = sprite.getY();
						init = true;
					}
					else {
						if (left > sprite.getX()) {
							left = sprite.getX();
						}
						if (top > sprite.getY()) {
							top = sprite.getY();
						}
						if (right < sprite.getX()) {
							right = sprite.getX();
						}
						if (bottom < sprite.getY()) {
							bottom = sprite.getY();
						}
					}
					tmp.add( ( (Copyable) sprite).copy());
				}
			}

			if (tmp.size() > 0) {
				Sprite[] sprites = new Sprite[tmp.size()];
				for (int i = 0; i < tmp.size(); ++i) {
					Sprite sprite = (Sprite) (tmp.get(i));
					sprite.setID(useMaxID());
					sprite.setPosition(sprite.getX() + mouseX - (left + right) / 2,
									   sprite.getY() + mouseY - (top + bottom) / 2);
					sprites[i] = sprite;
				}
				addUndoableSprites(sprites);
				selection.selectSprites(sprites);
				scrollablePanel.repaint();
			}
		}
	}

	protected void keyUpPressed(KeyEvent e) {
		if (!e.isControlDown() &&
			!e.isShiftDown() &&
			!e.isAltDown()) {

			if (!selection.isEmpty() && moveable) {
				selection.moving(keyMoveSpriteX, --keyMoveSpriteY); //move up
				scrollablePanel.repaint();
			}
		}
		else if (layerable && e.isControlDown() && !selection.isEmpty()) {
			increaseLayer(selection.getSprites(), 1);
		}
	}

	protected void keyDownPressed(KeyEvent e) {
		if (!e.isControlDown() &&
			!e.isShiftDown() &&
			!e.isAltDown()) {

			if (!selection.isEmpty() && moveable) {
				selection.moving(keyMoveSpriteX, ++keyMoveSpriteY); //move down
				scrollablePanel.repaint();
			}
		}
		else if (layerable && e.isControlDown() && !selection.isEmpty()) {
			increaseLayer(selection.getSprites(), -1);
		}
	}

	protected void keyLeftPressed(KeyEvent e) {
		if (!e.isControlDown() &&
			!e.isShiftDown() &&
			!e.isAltDown()) {

			if (!selection.isEmpty() && moveable) {
				selection.moving(--keyMoveSpriteX, keyMoveSpriteY); //move left
				scrollablePanel.repaint();
			}
		}
	}

	protected void keyRightPressed(KeyEvent e) {
		if (!e.isControlDown() &&
			!e.isShiftDown() &&
			!e.isAltDown()) {

			if (!selection.isEmpty() && moveable) {
				selection.moving(++keyMoveSpriteX, keyMoveSpriteY); //move right
				scrollablePanel.repaint();
			}
		}
	}

	protected void keyUpReleased(KeyEvent e) {
		if (!e.isControlDown() &&
			!e.isShiftDown() &&
			!e.isAltDown()) {

			if (!selection.isEmpty() && moveable) {
				keyMoveSpriteConfirm();
			}
		}
	}

	protected void keyDownReleased(KeyEvent e) {
		if (!e.isControlDown() &&
			!e.isShiftDown() &&
			!e.isAltDown()) {

			if (!selection.isEmpty() && moveable) {
				keyMoveSpriteConfirm();
			}
		}
	}

	protected void keyLeftReleased(KeyEvent e) {
		if (!e.isControlDown() &&
			!e.isShiftDown() &&
			!e.isAltDown()) {

			if (!selection.isEmpty() && moveable) {
				keyMoveSpriteConfirm();
			}
		}
	}

	protected void keyRightReleased(KeyEvent e) {
		if (!e.isControlDown() &&
			!e.isShiftDown() &&
			!e.isAltDown()) {

			if (!selection.isEmpty() && moveable) {
				keyMoveSpriteConfirm();
			}
		}
	}

	protected void keyMoveSpriteConfirm() {
		if (!isKeyPressed(KeyEvent.VK_UP) &&
			!isKeyPressed(KeyEvent.VK_DOWN) &&
			!isKeyPressed(KeyEvent.VK_LEFT) &&
			!isKeyPressed(KeyEvent.VK_RIGHT)) {

			keyMoveSpriteX = keyMoveSpriteY = 0;
			if (!selection.isEmpty()) {
				selection.confirmMoving();
				resortSprites();
				scrollablePanel.repaint();
			}
		}
	}

	protected void setSpritesToTop(Sprite[] sprites) {
		int minLayer = getMinLayer(sprites);
		int maxLayer = getAllSpritesMaxLayer();

		increaseLayer(sprites, maxLayer - minLayer + 1);
	}

	protected void setSpritesToBottom(Sprite[] sprites) {
		int minLayer = getAllSpritesMinLayer();
		int maxLayer = getMaxLayer(sprites);

		increaseLayer(sprites, minLayer - maxLayer - 1);
	}

	protected int getAllSpritesMinLayer() {
		int minLayer = 0;
		boolean first = true;
		for (int i = 0; i < sprites.size(); ++i) {
			if (sprites.get(i)instanceof Layerable) {
				Layerable tmp = (Layerable) (sprites.get(i));
				if (first) {
					minLayer = tmp.getLayer();
					first = false;
				}
				else {
					if (minLayer > tmp.getLayer()) {
						minLayer = tmp.getLayer();
					}
				}
			}
		}
		return minLayer;
	}

	protected int getAllSpritesMaxLayer() {
		int maxLayer = 0;
		boolean first = true;
		for (int i = 0; i < sprites.size(); ++i) {
			if (sprites.get(i)instanceof Layerable) {
				Layerable tmp = (Layerable) (sprites.get(i));
				if (first) {
					maxLayer = tmp.getLayer();
					first = false;
				}
				else {
					if (maxLayer < tmp.getLayer()) {
						maxLayer = tmp.getLayer();
					}
				}
			}
		}
		return maxLayer;
	}

	protected int getMinLayer(Sprite[] sprites) {
		int minLayer = 0;
		boolean first = true;
		if (sprites != null) {
			for (int i = 0; i < sprites.length; ++i) {
				if (sprites[i] instanceof Layerable) {
					Layerable tmp = (Layerable) (sprites[i]);
					if (first) {
						minLayer = tmp.getLayer();
						first = false;
					}
					else {
						if (minLayer > tmp.getLayer()) {
							minLayer = tmp.getLayer();
						}
					}
				}
			}
		}
		return minLayer;
	}

	protected int getMaxLayer(Sprite[] sprites) {
		int maxLayer = 0;
		boolean first = true;
		if (sprites != null) {
			for (int i = 0; i < sprites.length; ++i) {
				if (sprites[i] instanceof Layerable) {
					Layerable tmp = (Layerable) (sprites[i]);
					if (first) {
						maxLayer = tmp.getLayer();
						first = false;
					}
					else {
						if (maxLayer < tmp.getLayer()) {
							maxLayer = tmp.getLayer();
						}
					}
				}
			}
		}
		return maxLayer;
	}

	protected void increaseLayer(Sprite[] sprites, int offsetLayer) {
		if (sprites == null || offsetLayer == 0) {
			return;
		}
		for (int i = 0; i < sprites.length; ++i) {
			if (sprites[i] instanceof Layerable) {
				Layerable tmp = (Layerable) (sprites[i]);
				tmp.setLayer(tmp.getLayer() + offsetLayer);
			}
		}
		resortSprites();
		scrollablePanel.repaint();
		selectionChanged();
	}

	protected void keyDeleteReleased() {
		if (!selection.isEmpty() &&
			mouseInfo.getInfo() == MouseInfo.SELECT_POINT &&
			mouseState == MOUSE_STATE_NORMAL) {

			selection.cancelMoving();
			removeUndoableSprites(selection.getSprites());
			selection.clear();
			scrollablePanel.repaint();
		}
	}

	public void undoAdd(Sprite[] sprites) {
		for (int i = 0; i < sprites.length; ++i) {
			removeSprite(sprites[i]);
		}
		selection.cancelMoving();
		selection.clear();
		scrollablePanel.repaint();
	}

	public void redoAdd(Sprite[] sprites) {
		for (int i = 0; i < sprites.length; ++i) {
			addSprite(sprites[i]);
		}
		resortSprites();
		scrollablePanel.repaint();
	}

	public void undoRemove(Sprite[] sprites) {
		for (int i = 0; i < sprites.length; ++i) {
			addSprite(sprites[i]);
		}
		resortSprites();
		scrollablePanel.repaint();
	}

	public void redoRemove(Sprite[] sprites) {
		for (int i = 0; i < sprites.length; ++i) {
			removeSprite(sprites[i]);
		}
		selection.cancelMoving();
		selection.clear();
		scrollablePanel.repaint();
	}

	public void undoMove(Sprite[] sprites, int offsetX, int offsetY) {
		for (int i = 0; i < sprites.length; ++i) {
			sprites[i].setPosition(sprites[i].getX() - offsetX, sprites[i].getY() - offsetY);
		}
		resortSprites();
		scrollablePanel.repaint();
	}

	public void redoMove(Sprite[] sprites, int offsetX, int offsetY) {
		for (int i = 0; i < sprites.length; ++i) {
			sprites[i].setPosition(sprites[i].getX() + offsetX, sprites[i].getY() + offsetY);
		}
		resortSprites();
		scrollablePanel.repaint();
	}

	public void flip() {
		if (!selection.isEmpty()) {
			flipUndoableSprites(selection.getSprites());
		}
	}

	protected void flipUndoableSprites(Sprite[] sprites) {
		flipSprites(sprites);
		undoManager.addUndoSpriteFlip(sprites);
	}

	public void flipSprites(Sprite[] sprites) {
		if (sprites != null) {
			for (int i = 0; i < sprites.length; ++i) {
				if (sprites[i] instanceof Flipable) {
					( (Flipable) sprites[i]).flip();
				}
			}
		}
		scrollablePanel.repaint();
	}

	protected void showPopupMenu(Sprite[] sprites, int x, int y) {
		JPopupMenu popup = new JPopupMenu();
		JMenuItem menu;
		for (int i = sprites.length - 1; i >= 0; --i) {
			menu = new JMenuItem(sprites[i].getName());
			menu.addActionListener(new SelectSpriteActionListener(sprites[i]));
			popup.add(menu);
		}
		popup.show(scrollablePanel, x, y);
	}

	private class SelectSpriteActionListener
		implements ActionListener {
		Sprite sprite;
		public SelectSpriteActionListener(Sprite sprite) {
			this.sprite = sprite;
		}

		public void actionPerformed(ActionEvent e) {
			selection.selectSprite(sprite);
			scrollablePanel.repaint();
		}
	}

	public void paintSprite(Sprite sprite, Graphics g) {
		if (sprite != null) {
			sprite.paint(g);
		}
	}

	public void paintSprites(Graphics g) {
		paintSprites(sprites, g);
	}

	public void paintSprites(ArrayList sprites, Graphics g) {
		if (sprites == null) {
			return;
		}
		//先画静止的
		for (int i = 0; i < sprites.size(); ++i) {
			Sprite sprite = (Sprite) (sprites.get(i));
			if (!sprite.isMoving()) {
				paintSprite(sprite, g);
			}
		}
		//再画运动的
		for (int i = 0; i < sprites.size(); ++i) {
			Sprite sprite = (Sprite) (sprites.get(i));
			if (sprite.isMoving()) {
				paintSprite(sprite, g);
			}
		}
	}

	public void paintStatic(Graphics g) {
		paintStatic(sprites, g);
	}

	public void paintStatic(ArrayList sprites, Graphics g) {
		if (sprites == null) {
			return;
		}
		for (int i = 0; i < sprites.size(); ++i) {
			Sprite sprite = (Sprite) (sprites.get(i));
			sprite.paintIdle(g);
		}
	}

	public void paintOthers(Graphics g) {
		if (scrollablePanel.isMouseMovingViewport()) {
			paintMouseMovingViewport(g);
		}
		else {
			mouseInfo.paint(g, mouseX, mouseY);
			switch (mouseState) {
				case MOUSE_STATE_RECTANGLING:
					paintRectangling(g);
					break;
				default:
					break;
			}
		}
	}

	protected void paintMouseMovingViewport(Graphics g) { //do sth like special mouse icon
	}

	protected void paintRectangling(Graphics g) {
		Color oldColor = g.getColor();
		g.setColor(Color.WHITE);
		g.drawRect(Math.min(oldMouseX, mouseX), Math.min(oldMouseY, mouseY),
				   Math.abs(mouseX - oldMouseX), Math.abs(mouseY - oldMouseY));
		g.setColor(oldColor);
	}

	protected void showPopupMenu(Sprite sprite, int x, int y) {}

	protected void showProperties(Sprite sprite) {}

	protected void selectionChanged() {
		for (int i = 0; i < listeners.size(); ++i) {
			( (SpriteManagerListener) (listeners.get(i))).selectionChanged();
		}
	}

	protected void mouseChanged() {
		for (int i = 0; i < listeners.size(); ++i) {
			( (SpriteManagerListener) (listeners.get(i))).mouseChanged();
		}
	}

	protected void keyChanged() {
		for (int i = 0; i < listeners.size(); ++i) {
			( (SpriteManagerListener) (listeners.get(i))).keyChanged();
		}
	}

	abstract public void saveMobileData(DataOutputStream out, Object[] resManagers) throws Exception;

	abstract public void save(DataOutputStream out, Object[] resManagers) throws Exception;

	abstract public void load(DataInputStream in, Object[] resManagers) throws Exception;

}

class SpriteInfoPanel
	extends JPanel {
	private JLabel mapLabel;
	private JLabel mouseLabel;
	private JLabel spriteLabel;
	private ScrollablePanel scrollablePanel;
	private SpriteManager spriteManager;
	private ScrollablePanelListener scrollablePanelListener;
	private SpriteManagerListener spriteManagerListener;

	public SpriteInfoPanel() {
		init(true, true, true);
	}

	public SpriteInfoPanel(boolean showMapInfo, boolean showMouseInfo, boolean showSpriteInfo) {
		init(showMapInfo, showMouseInfo, showSpriteInfo);
	}

	private void init(boolean showMapInfo, boolean showMouseInfo, boolean showSpriteInfo) {
		setPreferredSize(new Dimension(100, XUtil.getDefPropInt("InfoHeight"))); //100是随意值
		Border raisedetched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		mapLabel = new JLabel();
		mapLabel.setHorizontalAlignment(JLabel.CENTER);
		mapLabel.setBorder(raisedetched);

		mouseLabel = new JLabel();
		mouseLabel.setHorizontalAlignment(JLabel.CENTER);
		mouseLabel.setBorder(raisedetched);

		spriteLabel = new JLabel();
		spriteLabel.setHorizontalAlignment(JLabel.CENTER);
		spriteLabel.setBorder(raisedetched);

		scrollablePanel = null;
		spriteManager = null;

		scrollablePanelListener = new ScrollablePanelListener() {
			public void sizeChanged() {
				refreshMapInfo();
			}

			public void scaleChanged() {
				refreshMapInfo();
			}
		};

		spriteManagerListener = new SpriteManagerListener() {
			public void mouseChanged() {
				refreshMouseInfo();
			}

			public void keyChanged() {}

			public void selectionChanged() {
				refreshSpriteInfo();
			}
		};

		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 1;

		mapLabel.setPreferredSize(new Dimension(XUtil.getDefPropInt("MapInfoWidth"),
												XUtil.getDefPropInt("InfoHeight")));
		mouseLabel.setPreferredSize(new Dimension(XUtil.getDefPropInt("MouseInfoWidth"),
												  XUtil.getDefPropInt("InfoHeight")));

		if (showMapInfo) {
			this.add(mapLabel, c);
			c.gridx++;
		}
		if (showMouseInfo) {
			this.add(mouseLabel, c);
			c.gridx++;
		}
		c.weightx = 1;
		if (showSpriteInfo) {
			this.add(spriteLabel, c);
		}
		else {
			this.add(new JLabel(), c);
		}
	}

	public void setScrollablePanel(ScrollablePanel p) {
		if (this.scrollablePanel != null) {
			this.scrollablePanel.removeListener(this.scrollablePanelListener);
		}
		this.scrollablePanel = null;
		if (p != null) {
			this.scrollablePanel = p;
			this.scrollablePanel.addListener(this.scrollablePanelListener);
		}
		refresh();
	}

	public void setSpriteManager(SpriteManager m) {
		if (this.spriteManager != null) {
			this.spriteManager.removeListener(this.spriteManagerListener);
		}
		this.spriteManager = null;
		if (m != null) {
			this.spriteManager = m;
			this.spriteManager.addListener(this.spriteManagerListener);
		}
		refresh();
	}

	public void setMapInfo(String info) {
		mapLabel.setText(info);
		mapLabel.setToolTipText(info);
	}

	public void setMouseInfo(String info) {
		mouseLabel.setText(info);
		mouseLabel.setToolTipText(info);
	}

	public void setSpriteInfo(String info) {
		spriteLabel.setText(info);
		spriteLabel.setToolTipText(info);
	}

	public void refresh() {
		refreshMapInfo();
		refreshMouseInfo();
		refreshSpriteInfo();
	}

	private void refreshMapInfo() {
		if (scrollablePanel == null) {
			setMapInfo(null);
		}
		else {
			MapInfo info = MainFrame.self.getMapInfo();
			setMapInfo("宽：" + info.getRealWidth() +
					   "  高：" + info.getRealHeight() +
					   "  比例：" + (int) (scrollablePanel.getScale() * 100) + "%");
		}
	}

	private void refreshMouseInfo() {
		if (spriteManager == null) {
			setMouseInfo(null);
		}
		else {
			boolean setted = false;
			if (scrollablePanel != null) {
				if (!scrollablePanel.isMouseIn()) {
					setMouseInfo(null);
					setted = true;
				}
			}
			if (!setted) {
				MapInfo info = MainFrame.self.getMapInfo();
				setMouseInfo("X：" + info.changeToMobileX(spriteManager.getMouseX()) +
							 "  Y：" + info.changeToMobileY(spriteManager.getMouseY(), 0));
			}
		}
	}

	private void refreshSpriteInfo() {
		if (spriteManager == null) {
			setSpriteInfo(null);
		}
		else {
			Sprite[] sprites = spriteManager.getSelection().getSprites();
			if (sprites == null) {
				setSpriteInfo(null);
			}
			else if (sprites.length <= 0) {
				setSpriteInfo(null);
			}
			else if (sprites.length == 1) {
				setSpriteInfo(sprites[0].getInfo());
			}
			else {
				setSpriteInfo("一共选取了" + sprites.length + "个物体");
			}
		}
	}
}

abstract class SpriteManagerPanel
	extends ScrollablePanel {
	protected JDialog dlgOwner;
	protected JFrame frmOwner;
	protected SpriteManager manager;
	protected MouseInfo mouseInfo;
	protected ArrayList backManagers;
	protected ArrayList foreManagers;
	protected AlphaComposite backAlpha;
	protected AlphaComposite foreAlpha;
	protected boolean showAlpha;

	public SpriteManagerPanel(JFrame owner, MouseInfo mouseInfo) {
		super(XUtil.getDefPropInt("DefMapWidth"), XUtil.getDefPropInt("DefMapHeight"));
		init(owner, null, mouseInfo);
	}

	public SpriteManagerPanel(JDialog owner, MouseInfo mouseInfo) {
		super(XUtil.getDefPropInt("DefMapWidth"), XUtil.getDefPropInt("DefMapHeight"));
		init(null, owner, mouseInfo);
	}

	private void init(JFrame frmOwner, JDialog dlgOwner, MouseInfo mouseInfo) {
		this.frmOwner = frmOwner;
		this.dlgOwner = dlgOwner;
		this.mouseInfo = mouseInfo;
		this.manager = createManager();
		backManagers = new ArrayList();
		foreManagers = new ArrayList();
		backAlpha = AlphaComposite.DstOver;
		foreAlpha = AlphaComposite.DstOver;
		showAlpha = true;
	}

	abstract protected SpriteManager createManager();

	public void reset(int basicWidth, int basicHeight) {
		super.reset(basicWidth, basicHeight);
		manager.reset();
	}
	
	public void setMapSize(int basicWidth, int basicHeight) {
		super.reset(basicWidth, basicHeight);
	}
	
	public SpriteManager getManager() {
		return manager;
	}

	public void addBackManager(SpriteManager manager) {
		if (manager == null) {
			return;
		}
		for (int i = 0; i < backManagers.size(); ++i) {
			if (backManagers.get(i).equals(manager)) {
				return;
			}
		}
		backManagers.add(manager);
	}

	public void removeBackManager(SpriteManager manager) {
		if (manager == null) {
			return;
		}
		backManagers.remove(manager);
	}

	public void addForeManager(SpriteManager manager) {
		if (manager == null) {
			return;
		}
		for (int i = 0; i < foreManagers.size(); ++i) {
			if (foreManagers.get(i).equals(manager)) {
				return;
			}
		}
		foreManagers.add(manager);
	}

	public void removeForeManager(SpriteManager manager) {
		if (manager == null) {
			return;
		}
		foreManagers.remove(manager);
	}

	public void setShowAlpha(boolean showAlpha) {
		this.showAlpha = showAlpha;
		repaint();
	}

	public void setAlpha(double alpha) {
		backAlpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) alpha);
		foreAlpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) alpha);
		repaint();
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (!isVisible()) {
			return;
		}

		paintBack(g);
		paintSelf(g);
		paintFore(g);
		

		MapInfo mapInfo = MainFrame.self.getMapInfo();
		mapInfo.paintBorder(g);
	}

	protected void paintBack(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		Composite oldComposite = null;
		if (showAlpha) {
			oldComposite = g2.getComposite();
			if (backAlpha != null) {
				g2.setComposite(backAlpha);
			}
			paintBackManagers(g);
			g2.setComposite(oldComposite);
		}
	}

	protected void paintBackManagers(Graphics g) {
		for (int i = 0; i < backManagers.size(); ++i) {
			( (SpriteManager) (backManagers.get(i))).paintStatic(g);
		}
	}

	protected void paintSelf(Graphics g) {
		manager.paintSprites(g);
		manager.paintOthers(g);
	}

	protected void paintFore(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		Composite oldComposite = null;
		if (showAlpha) {
			oldComposite = g2.getComposite();
			if (foreAlpha != null) {
				g2.setComposite(foreAlpha);
			}
			paintForeManagers(g);
			g2.setComposite(oldComposite);
		}
	}

	protected void paintForeManagers(Graphics g) {
		for (int i = 0; i < foreManagers.size(); ++i) {
			( (SpriteManager) (foreManagers.get(i))).paintStatic(g);
		}
	}

	protected String getClassName() {
		String name = XUtil.getClassName(this.getClass());
		if (name.endsWith("SpritePanel")) {
			name = name.substring(0, name.length() - "SpritePanel".length());
		}
		else if (name.endsWith("ManagerPanel")) {
			name = name.substring(0, name.length() - "ManagerPanel".length());
		}
		else if (name.endsWith("Panel")) {
			name = name.substring(0, name.length() - "Panel".length());
		}
		return name;
	}

	protected String getFilePath() {
		return XUtil.getDefPropStr(getClassName() + "FilePath");
	}

	public void saveMobileData(String name, Object[] resManagers) throws Exception {
		String n = name.toLowerCase();
		String cn = getClassName().toLowerCase();
		String fp = XUtil.getDefPropStr("MobilePath");
		if (cn.equals("tree") || cn.equals("wa")) {
			fp = getFilePath();
		}
		File f = new File(fp  + "\\" + n + "_" + cn + "_mobile.dat");
		DataOutputStream out =
			new DataOutputStream(
			new BufferedOutputStream(
			new FileOutputStream(f)));
		manager.saveMobileData(out, resManagers);
		out.flush();
		out.close();
	}

	public void save(String name, Object[] resManagers) throws Exception {
		File f = new File(getFilePath() + "\\" + name + "_" + getClassName() + ".dat");
		DataOutputStream out =
			new DataOutputStream(
			new BufferedOutputStream(
			new FileOutputStream(f)));
		manager.save(out, resManagers);
		out.flush();
		out.close();
	}

	public void load(String name, Object[] resManagers) throws Exception {
		File f = new File(getFilePath() + "\\" + name + "_" + getClassName() + ".dat");
		if (!f.exists()) {
			return;
		}
		DataInputStream in =
			new DataInputStream(
			new BufferedInputStream(
			new FileInputStream(f)));
		manager.load(in, resManagers);
		in.close();
		repaint();
	}
}