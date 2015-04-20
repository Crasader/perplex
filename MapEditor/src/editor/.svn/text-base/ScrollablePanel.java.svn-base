package editor;

import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

interface ScrollablePanelListener {
	public void sizeChanged();

	public void scaleChanged();
}

class ScrollablePanelAdapter
	implements ScrollablePanelListener {
	public void sizeChanged() {}

	public void scaleChanged() {}
}

/**
 可以缩放的控件的基类。
 */
public class ScrollablePanel
	extends JPanel {
	public final static int MOUSE_MOVING_VIEWPORT_OFFSET = 5;

	public final static double[] SCALES = {0.2, 0.4, 0.8, 1, 2, 4, 6, 8, 12, 16};

	public final static AlphaComposite
		DEF_ALPHA_COMPOSITE = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 0.5);

	private JPanel backPanel;
	private JScrollPane scrollPane;
	private boolean mouseMovingViewport;
	private int mouseMovingViewportX, mouseMovingViewPortY;
	private int basicWidth, basicHeight;

	protected double scale;
	private MouseMotionListener doScrollRectToVisible;
	protected RenderingHints renderingHints;
	private boolean controlPressed, available, mouseIn;
	private Cursor oldCursor;
	private ArrayList listeners;
	private boolean mustFocus;

	public ScrollablePanel(int basicWidth, int basicHeight) {
		super();
		try {
			init(basicWidth, basicHeight);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void init(int basicWidth, int basicHeight) throws Exception {
		mustFocus = true;
		listeners = new ArrayList();
		scrollPane = new JScrollPane(this);
		SwingUtil.setDefScrollIncrement(scrollPane);
		scrollPane.setInputMap(JScrollPane.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, new InputMap());
		backPanel = new JPanel();
		backPanel.setLayout(null);
		backPanel.add(scrollPane);
		backPanel.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				sizeChanged();
			}
		});

		oldCursor = null;
		renderingHints = new RenderingHints(null);
		renderingHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		renderingHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		renderingHints.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
		renderingHints.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
						   RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		renderingHints.put(RenderingHints.KEY_COLOR_RENDERING,
						   RenderingHints.VALUE_COLOR_RENDER_SPEED);

		initReset(basicWidth, basicHeight);

		addMouseListener(new MouseAdapter() {
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

		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				selfMouseDragged(e);
			}
		});

		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				selfKeyPressed(e);
			}

			public void keyReleased(KeyEvent e) {
				selfKeyReleased(e);
			}
		});

		//autoscroll
		doScrollRectToVisible = new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
				( (JPanel) e.getSource()).scrollRectToVisible(r);
			}
		};
		addMouseMotionListener(doScrollRectToVisible);
		setAutoscrolls(true);
		setAvailable(true);
	}

	private void initReset(int basicWidth, int basicHeight) {
		scale = 1;
		mouseMovingViewport = false;
		controlPressed = false;
		this.basicWidth = basicWidth;
		this.basicHeight = basicHeight;
		setPreferredSize(new Dimension(basicWidth, basicHeight));
	}

	public boolean isMustFocus() {
		return mustFocus;
	}

	public void setMustFocus(boolean mustFocus) {
		this.mustFocus = mustFocus;
	}

	public void addListener(ScrollablePanelListener listener) {
		if (listener != null) {
			listeners.add(listener);
		}
	}

	public void removeListener(ScrollablePanelListener listener) {
		if (listener != null) {
			listeners.remove(listener);
		}
	}

	public void reset(int basicWidth, int basicHeight) {
		initReset(basicWidth, basicHeight);
		sizeChanged();
	}

	public int getMouseX(MouseEvent e) {
		return (int) (e.getX() / scale);
	}

	public int getMouseY(MouseEvent e) {
		return (int) (e.getY() / scale);
	}

	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	public JPanel getBackPanel() {
		return backPanel;
	}

	public double getScale() {
		return scale;
	}

	public int getBasicWidth() {
		return basicWidth;
	}

	public int getBasicHeight() {
		return basicHeight;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
		setVisible(available);
//		setEnabled(available);
//		setFocusable(available);
//		if(scrollPane != null) scrollPane.setEnabled(available);
	}

	public boolean isMouseIn() {
		return mouseIn;
	}

	public boolean isMouseMovingViewport() {
		return mouseMovingViewport;
	}

	protected void sizeChanged() {
		resizeScroll();
		for (int i = 0; i < listeners.size(); ++i) {
			( (ScrollablePanelListener) (listeners.get(i))).sizeChanged();
		}
	}

	protected void scaleChanged() {
		resizeScroll();
		for (int i = 0; i < listeners.size(); ++i) {
			( (ScrollablePanelListener) (listeners.get(i))).scaleChanged();
		}
	}

	protected void resizeScroll() {
		int left = 0, top = 0, width = 0, height = 0;
		int scrollLeft = 0, scrollTop = 0;
		int scrollWidth = backPanel.getWidth()
						  - backPanel.getInsets().left - backPanel.getInsets().right;
		int scrollHeight = backPanel.getHeight()
						   - backPanel.getInsets().top - backPanel.getInsets().bottom;

		width = getPreferredSize().width +
				scrollPane.getInsets().left + scrollPane.getInsets().right;
		height = getPreferredSize().height +
				 scrollPane.getInsets().top + scrollPane.getInsets().bottom;

		if (width > scrollWidth) {
			width = scrollWidth;
			height += scrollPane.getHorizontalScrollBar().getHeight();
			if (height > scrollHeight) {
				height = scrollHeight;
			}
		}
		else if (height > scrollHeight) {
			height = scrollHeight;
			width += scrollPane.getVerticalScrollBar().getWidth();
			if (width > scrollWidth) {
				width = scrollWidth;
			}
		}

		left = scrollLeft + (scrollWidth - width) / 2;
		top = scrollTop + (scrollHeight - height) / 2;
//    	System.out.println(left + "  " + top + "  " + width + "  " + height);
		scrollPane.setBounds(left, top, width, height);
		Thread t = new Thread() {
			public void run() {
				try {
					synchronized (this) {
						this.wait(10);
						revalidate();
					}
				}
				catch (Exception e) {
				}
			}
		};
		t.start();
	}

	public void setScale(double newScale) {
		if (scale < SCALES[0]) {
			newScale = SCALES[0];
		}
		else if (scale > SCALES[SCALES.length - 1]) {
			newScale = SCALES[SCALES.length - 1];
		}
		if (newScale == scale) {
			return;
		}

		if (newScale < scale) {
			scrollForNewScale(newScale);
//			this.revalidate();
		}
		setPreferredSize(new Dimension( (int) (basicWidth * newScale),
									   (int) (basicHeight * newScale)));
		if (newScale > scale) {
			this.revalidate();
			scrollForNewScale(newScale);
		}
		scale = newScale;
		scaleChanged();
		repaint();
	}

	private void scrollForNewScale(double newScale) {
		JViewport viewPort = scrollPane.getViewport();
		Rectangle viewRect = viewPort.getViewRect();

		int viewLeft = viewRect.x;
		int viewTop = viewRect.y;
		int viewWidth = viewRect.width;
		int viewHeight = viewRect.height;

		viewLeft = (int) ( (viewLeft + viewWidth / 2) * newScale / scale - viewWidth / 2);
		viewTop = (int) ( (viewTop + viewHeight / 2) * newScale / scale - viewHeight / 2);
		this.scrollRectToVisible(new Rectangle(viewLeft, viewTop, viewWidth, viewHeight));
	}

	private void increaseScale() {
		if (scale < SCALES[0]) {
			setScale(SCALES[0]);
		}
		else if (scale >= SCALES[SCALES.length - 1]) {
			setScale(SCALES[SCALES.length - 1]);
		}
		else {
			for (int i = 0; i < SCALES.length - 1; ++i) {
				if (scale >= SCALES[i] && scale < SCALES[i + 1]) {
					setScale(SCALES[i + 1]);
					return;
				}
			}
		}
	}

	private void decreaseScale() {
		if (scale <= SCALES[0]) {
			setScale(SCALES[0]);
		}
		else {
			for (int i = SCALES.length - 1; i > 0; --i) {
				if (scale > SCALES[i - 1] && scale <= SCALES[i]) {
					setScale(SCALES[i - 1]);
					return;
				}
			}
		}
	}

	private void selfMouseEntered(MouseEvent e) {
		mouseIn = true;
		if (mustFocus) {
			requestFocus();
		}
	}

	private void selfMouseExited(MouseEvent e) {
		mouseIn = false;
	}

	private void selfMousePressed(MouseEvent e) {
		requestFocus();
		if (mouseMovingViewport) {
			mouseMovingViewportPressed(e);
		}
	}

	private void selfMouseReleased(MouseEvent e) {
		if (mouseMovingViewport) {
			mouseMovingViewportReleased(e);
		}
	}

	private void selfMouseDragged(MouseEvent e) {
		if (mouseMovingViewport) {
			mouseMovingViewportDragged(e);
		}
	}

	private void selfKeyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_CONTROL:
				controlPressed = true;
				break;
			case KeyEvent.VK_SPACE:
				if (!mouseMovingViewport) {
					removeMouseMotionListener(doScrollRectToVisible);
					mouseMovingViewport = true;
					oldCursor = this.getCursor();
					this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					repaint();
				}
				break;
			case KeyEvent.VK_ADD:
			case KeyEvent.VK_EQUALS:
				if (controlPressed) {
					increaseScale();
				}
				break;
			case KeyEvent.VK_MINUS:
			case KeyEvent.VK_SUBTRACT:
				if (controlPressed) {
					decreaseScale();
				}
				break;
			default:
				break;
		}
	}

	private void selfKeyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_CONTROL:
				controlPressed = false;
				break;
			case KeyEvent.VK_SPACE:
				if (mouseMovingViewport) {
					addMouseMotionListener(doScrollRectToVisible);
					mouseMovingViewport = false;
					if (oldCursor == null) {
						this.setCursor(Cursor.getDefaultCursor());
					}
					else {
						this.setCursor(oldCursor);
					}
					repaint();
				}
				break;
			default:
				break;
		}
	}

	private void mouseMovingViewportPressed(MouseEvent e) {
		switch (e.getButton()) {
			case XUtil.LEFT_BUTTON:
				mouseMovingViewportX = e.getX();
				mouseMovingViewPortY = e.getY();
				repaint();
				break;
			default:
				break;
		}
	}

	private void mouseMovingViewportDragged(MouseEvent e) {
		if (scrollPane != null
			&& (Math.abs(mouseMovingViewportX - e.getX()) >= MOUSE_MOVING_VIEWPORT_OFFSET
				|| Math.abs(mouseMovingViewPortY - e.getY()) >= MOUSE_MOVING_VIEWPORT_OFFSET)) {

			Rectangle viewRect = scrollPane.getViewport().getViewRect();

			int viewLeft = viewRect.x + mouseMovingViewportX - e.getX();
			int viewTop = viewRect.y + mouseMovingViewPortY - e.getY();
			int viewWidth = viewRect.width;
			int viewHeight = viewRect.height;

			this.scrollRectToVisible(new Rectangle(viewLeft, viewTop, viewWidth, viewHeight));
		}
	}

	private void mouseMovingViewportReleased(MouseEvent e) {
		repaint();
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (!isVisible()) {
			return;
		}
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHints(renderingHints);
		g2.scale(scale, scale);
	}
}