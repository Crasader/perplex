package editor;

import java.io.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class WalkArea {
	public final static int TYPE_PAINT = 0;
	public final static int TYPE_ERASE = 1;
	public final static int DEF_TYPE = TYPE_PAINT;
	public final static int[] TYPES = {TYPE_PAINT, TYPE_ERASE};
	public final static Color[] COLORS = {Color.BLACK, Color.WHITE};

	public static void paint(Graphics g, int type, int left, int top) {
		Color oldColor = g.getColor();
		g.setColor(WalkArea.COLORS[type]);
		g.fillRect(left, top, Grid.W, Grid.H);
		g.setColor(oldColor);
	}
}

class WAPainter
	extends PainterPanel {
	public final static String[] NAMES = {"绘制不可行走区域", "绘制可行走区域"};

	public final static WAPainter[] getPainters() {
		WAPainter[] result = new WAPainter[WalkArea.TYPES.length];
		for (int i = 0; i < WalkArea.TYPES.length; ++i) {
			result[i] = new WAPainter(WalkArea.TYPES[i]);
			result[i].computeSize();
		}
		return result;
	}

	int type;

	public WAPainter(int type) {
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
		return Grid.W;
	}

	public int getImageHeight() {
		return Grid.H;
	}

	public String getName() {
		return NAMES[type];
	}

	public void paintLeftTop(Graphics g, int left, int top) {
		WalkArea.paint(g, type, left, top);
	}

	public void paintOrigin(Graphics g, int originX, int originY) {
		paintLeftTop(g, originX - Grid.W / 2, originY - Grid.H / 2);
	}

	public void paintOrigin(Graphics g, int originX, int originY, int range) {
		for (int h = originY - (range - 1) * Grid.H; h < originY + range * Grid.H; h += Grid.H) {
			for (int w = originX - (range - 1) * Grid.W; w < originX + range * Grid.W; w += Grid.W) {
				paintOrigin(g, w, h);
			}
		}
	}
}

class WAManager
	extends SpriteManager {
	int[][] grids;

	public WAManager(ScrollablePanel scrollablePanel, MouseInfo mouseInfo) {
		super(scrollablePanel, mouseInfo);
		setCopyable(false);
		setLayerable(false);
		setMoveable(false);
		setDeleteable(false);
		setUndoable(false);
		setSelectionMode(NONE_SELECTION);
		grids = createArray(scrollablePanel.getBasicWidth(), scrollablePanel.getBasicHeight());
		scrollablePanel.addListener(new ScrollablePanelAdapter() {
			public void sizeChanged() {
				panelSizeChanged();
			}
		});
	}

	private int[][] createArray(int width, int height) {
		int wLength = (int) ( (width + Grid.W - 1) / Grid.W);
		int hLength = (int) ( (height + Grid.H - 1) / Grid.H);
		int[][] result = new int[hLength][];
		for (int h = 0; h < hLength; ++h) {
			result[h] = new int[wLength];
			for (int w = 0; w < wLength; ++w) {
				result[h][w] = WalkArea.DEF_TYPE;
			}
		}
		return result;
	}

	private void copyArray(int[][] source, int[][] dest, int offsetW, int offsetH) {
		if (source == null || dest == null) {
			return;
		}
		for (int h = 0; h < dest.length; ++h) {
			for (int w = 0; w < dest[h].length; ++w) {
				dest[h][w] = WalkArea.DEF_TYPE;
				if (h + offsetH >= 0 && h + offsetH < source.length) {
					if (w + offsetW >= 0 && w + offsetW < source[h + offsetH].length) {
						dest[h][w] = source[h + offsetH][w + offsetW];
					}
				}
			}
		}
	}

	private void panelSizeChanged() {
		int[][] newGrids = createArray(scrollablePanel.getBasicWidth(),
									   scrollablePanel.getBasicHeight());
		copyArray(grids, newGrids, 0, 0);
		grids = newGrids;
	}
	
	public void reset() {
		super.reset();
		for (int h = 0; h < grids.length; ++h) {
			for (int w = 0; w < grids[h].length; ++w) {
				grids[h][w] = WalkArea.DEF_TYPE;
			}
		}
	}

	public void resetOrigin(int offsetX, int offsetY) {
		super.resetOrigin(offsetX, offsetY);
		int offsetW = (int) (offsetX / Grid.W);
		int offsetH = (int) (offsetY / Grid.H);
		int[][] newGrids = createArray(scrollablePanel.getBasicWidth(),
									   scrollablePanel.getBasicHeight());
		copyArray(grids, newGrids, offsetW, offsetH);
		grids = newGrids;
	}

	protected Sprite createSprite(int x, int y) {
		if (! (mouseInfo.getPainter()instanceof WAPainter)) {
			return null;
		}
		int type = ( (WAPainter) (mouseInfo.getPainter())).getType();

		int originW = (int) ( (x - Grid.W / 2) / Grid.W);
		int originH = (int) ( (y - Grid.H / 2) / Grid.H);
		int range = mouseInfo.getRange();
		for (int h = Math.max(0, originH - range + 1);
					 h < Math.min(grids.length, originH + range); ++h) {
			for (int w = Math.max(0, originW - range + 1);
						 w < Math.min(grids[h].length, originW + range); ++w) {
				grids[h][w] = type;
			}
		}
		return null;
	}

	public void paintStatic(Graphics g) {
		for (int h = 0; h < grids.length; ++h) {
			for (int w = 0; w < grids[h].length; ++w) {
				if (grids[h][w] != WalkArea.TYPE_ERASE) {
					WalkArea.paint(g, grids[h][w], w * Grid.W, h * Grid.H);
				}
			}
		}
	}

	public void paintSprites(Graphics g) {
		paintStatic(g);
	}

	public void saveMobileData(DataOutputStream out, Object[] resManagers) throws Exception {
		SL.writeInt(grids.length, out);
		SL.writeInt(grids[0].length, out);
		for (int h = 0; h < grids.length; ++h) {
			for (int w = 0; w < grids[h].length; ++w) {
				SL.writeByte(grids[h][w], out);
			}
		}
	}

	public void save(DataOutputStream out, Object[] resManagers) throws Exception {
		out.writeInt(grids.length);
		out.writeInt(grids[0].length);
		for (int h = 0; h < grids.length; ++h) {
			for (int w = 0; w < grids[h].length; ++w) {
				out.writeInt(grids[h][w]);
			}
		}
	}

	public void load(DataInputStream in, Object[] resManagers) throws Exception {
		int height = in.readInt();
		int width = in.readInt();
		grids = new int[height][width];
		
		for (int h = 0; h < grids.length; ++h) {
			for (int w = 0; w < grids[h].length; ++w) {
				grids[h][w] = in.readInt();
			}
		}
	}
}

class WAPanel
	extends SpriteManagerPanel {

	public WAPanel(JFrame owner, MouseInfo mouseInfo) {
		super(owner, mouseInfo);
	}

	public WAPanel(JDialog owner, MouseInfo mouseInfo) {
		super(owner, mouseInfo);
	}

	public int getMouseX(MouseEvent e) {
		int result = super.getMouseX(e);
		result = ( (int) (result / Grid.W)) * Grid.W + Grid.W / 2;
		return result;
	}

	public int getMouseY(MouseEvent e) {
		int result = super.getMouseY(e);
		result = ( (int) (result / Grid.H)) * Grid.H + Grid.H / 2;
		return result;
	}

	protected SpriteManager createManager() {
		return new WAManager(this, mouseInfo);
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
