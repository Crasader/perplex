package editor;

import java.io.*;
import java.awt.*;

import com.sun.org.apache.regexp.internal.recompile;

interface Sprite
	extends Comparable {

	public int getID();

	public void setID(int id);

	public int getX();

	public int getY();

	public void setPosition(int x, int y);

	public int getWidth();

	public int getHeight();

	public String getName();

	public void setName(String name);

	public boolean isSelected();

	public void setSelected(boolean selected);

	public boolean isMoving();

	public boolean containPoint(int x, int y);

	public boolean inRect(int x, int y, int w, int h);

	public void moving(int offsetX, int offsetY);

	public void cancelMoving();

	public void confirmMoving();

	public String getInfo();

	public void paint(Graphics g);

	public void paintIdle(Graphics g);

	public void paintSelected(Graphics g);

	public void paintMoving(Graphics g);
}

interface Layerable {
	public int getLayer();

	public void setLayer(int layer);
}

interface Copyable {
	public Copyable copy();
}

interface Flipable {
	public void flip();
}

abstract class BasicSprite
	implements Sprite {

	private int id, x, y, movingX, movingY;
	private boolean selected, moving;
	private String name;
	MapInfo info = MainFrame.self.getMapInfo();
	
	public BasicSprite(int id, int x, int y, String name) {
		init(id, x, y, name);
	}

	private void init(int id, int x, int y, String name) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.name = name;
	}

	public int compareTo(Object o) {
		if (o == null) {
			return 1;
		}
		else if (! (o instanceof BasicSprite)) {
			return this.hashCode() - o.hashCode();
		}

		BasicSprite dest = (BasicSprite) o;

		IntPair selfGrid = Grid.getSortXY(this.getX(), this.getY());
		IntPair destGrid = Grid.getSortXY(dest.getX(), dest.getY());

		if (selfGrid.y != destGrid.y) {
			return selfGrid.y - destGrid.y;
		}
		else if (selfGrid.x != selfGrid.x) {
			return selfGrid.x - destGrid.x;
		}
		else if (this.getY() != dest.getY()) {
			return this.getY() - dest.getY();
		}
		else if (this.getX() != dest.getX()) {
			return this.getX() - dest.getX();
		}
		else {
			return this.getID() - dest.getID();
		}
	}

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	abstract public int getLeft();

	abstract public int getTop();

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getMobileY()
	{
		int my = info.getRealHeight() - (y - info.getRealTop() + getHeight());
		return  my;
	}
	
	public int getMobileX()
	{
		return  (x - info.getRealLeft());
	}
	
	public int getMobileCenterX()
	{
		return getMobileX() + getWidth() / 2;
	}
	
	public int getMobileCenterY()
	{
		return getMobileY() + getHeight() / 2;
	}

	public int getMovingX() {
		return movingX;
	}

	public int getMovingY() {
		return movingY;
	}

	public void setPosition(int x, int y) {
		this.x = this.movingX = x;
		this.y = this.movingY = y;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isMoving() {
		return moving;
	}

	public boolean containPoint(int x, int y) {
		return (x >= getLeft() && x <= getLeft() + getWidth() &&
				y >= getTop() && y <= getTop() + getHeight());
	}

	public boolean inRect(int x, int y, int w, int h) {
		return (getLeft() >= x && getLeft() + getWidth() <= x + w &&
				getTop() > y && getTop() + getHeight() <= y + h);
	}

	public void moving(int offsetX, int offsetY) {
		moving = true;
		movingX = x + offsetX;
		movingY = y + offsetY;
	}

	public void cancelMoving() {
		if (moving) {
			moving = false;
			movingX = x;
			movingY = y;
		}
	}

	public void confirmMoving() {
		moving = false;
		moveTo(movingX, movingY);
	}

	protected void moveTo(int x, int y) {
		if (this.x == x && this.y == y) {
			return;
		}
		setPosition(x, y);
	}

	public String getInfo() {
		String result = getName() +
						"  ID:" + getID();
		return result;
	}

	public void paint(Graphics g) {
		if (selected) {
			if (!moving) {
				paintSelected(g);
			}
			else {
				paintMoving(g);
			}
		}
		else {
			paintIdle(g);
		}
	}

	public void paintIdle(Graphics g) {
		paintIdle(g, x, y);
	}

	abstract public void paintIdle(Graphics g, int x, int y);

	public void paintSelected(Graphics g) {
		paintIdle(g);
		paintBorder(g, getSelectedBorderColor());
		paintArchor(g, Color.CYAN);
	}

	public Color getSelectedBorderColor() {
		return new Color(0xFF00FF);
	}

	public void paintMoving(Graphics g) {
		paintIdle(g, movingX, movingY);
		paintBorder(g, getMovingBorderColor());
	}

	public Color getMovingBorderColor() {
		return Color.BLUE;
	}

	public void paintBorder(Graphics g) {
		paintBorder(g, isMoving() ? getMovingBorderColor() : getSelectedBorderColor());
	}

	public void paintBorder(Graphics g, Color color) {
		Color oldColor = g.getColor();
		g.setColor(color);
		int left = getLeft();
		int top = getTop();
		if (moving) {
			left += movingX - x;
			top += movingY - y;
		}
		g.fillRect(left, top, getWidth(), 1);
		g.fillRect(left, top, 1, getHeight());
		g.fillRect(left, top + getHeight() - 1, getWidth(), 1);
		g.fillRect(left + getWidth() - 1, top, 1, getHeight());
		g.setColor(oldColor);
	}

	public void paintArchor(Graphics g, Color color)
	{
		Color oldColor = g.getColor();
		g.setColor(color);
		g.fillOval(getX(), getY(), 5, 5);
		g.setColor(oldColor);
	}
	public final static void saveMobileData(BasicSprite sprite, DataOutputStream out) throws Exception {
		SL.writeInt(sprite.id, out);
		SL.writeInt(sprite.getMobileX(), out);
		SL.writeInt(sprite.getMobileY(), out);
	}

	public final static void save(BasicSprite sprite, DataOutputStream out) throws Exception {
		out.writeInt(sprite.id);
		out.writeInt(sprite.x);
		out.writeInt(sprite.y);
		SL.writeString(sprite.name, out);
	}

	public final static void load(BasicSprite sprite, DataInputStream in) throws Exception {
		sprite.id = in.readInt();
		int x = in.readInt();
		int y = in.readInt();
		sprite.setPosition(x, y);
		sprite.name = SL.readString(in);
	}

//	public void copyFrom(BasicSprite source) {
//		this.id = source.id;
//		this.x = this.movingX = source.x;
//		this.y = this.movingY = source.y;
//		this.selected = false;
//		this.moving = false;
//		this.name = source.name;
//	}
}