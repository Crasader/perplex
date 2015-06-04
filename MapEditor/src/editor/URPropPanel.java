package editor;

import java.util.*;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

interface URPropPanel {
	public void setUR(UnitRes ur);

	public void updateURProp();

	public String getName();

	public void dispose();
}

class URBasicPanel
	extends JPanel
	implements URPropPanel {
	private JDialog owner;
	private UnitRes ur;
	private NumberSpinner idSpinner;
	private JTextField nameText;
	private NumberSpinner hpSpinner;
	private UnitMoveModePanel umPanel;
	private UnitFireModePanel ufPanel;
	private DropItemModeList dimList;

	public URBasicPanel(JDialog owner) {
		this.owner = owner;
		this.ur = null;

		idSpinner = new NumberSpinner();
		nameText = new JTextField();
		hpSpinner = new NumberSpinner();
		umPanel = new UnitMoveModePanel(owner, new UnitMoveMode(), 1);
		ufPanel = new UnitFireModePanel(owner, new UnitFireMode());
		dimList = new DropItemModeList(owner);

		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		c.insets = new Insets(0, 0, 10, 10);
		this.add(new JLabel("ID："), c);

		c.gridx = 1;
		c.weightx = 1;
		this.add(idSpinner, c);

		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0;
		this.add(new JLabel("名称："), c);

		c.gridx = 1;
		c.weightx = 1;
		this.add(nameText, c);
	
		c.gridy = 2;
		c.gridx = 0;
		c.weightx = 0;
		this.add(new JLabel("HP："), c);
		
		c.gridx = 1;
		c.weightx = 1;
		this.add(hpSpinner, c);
		
		c.gridy = 3;
		c.gridx = 0;
		c.gridwidth = 2;
		this.add(umPanel, c);
		
		c.gridy = 4;
		this.add(ufPanel, c);
		
		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 2;
		c.weightx = 1;
		c.weighty = 0;
		this.add(new JLabel("掉落道具："), c);
		
		c.gridy = 6;
		c.weighty = 1;
		this.add(new JScrollPane(dimList), c);
		
//		c.gridy = 5;
//		c.gridx = 0;
//		c.gridwidth = 2;
//		c.weightx = 1;
//		c.weighty = 1;
//		this.add(new JPanel(), c);

		copyURProp();
	}

	public void setUR(UnitRes ur) {
		this.ur = ur;
		copyURProp();
	}

	private void copyURProp() {
		if(ur == null) return;
		idSpinner.setIntValue(ur.getID());
		nameText.setText(ur.getName());
		hpSpinner.setIntValue(ur.getHP());
		umPanel.SetUM(ur.getUM());
		ufPanel.SetUF(ur.getUF());
		dimList.setDIM(ur.getDIM());
	}

	public void updateURProp() {
		if(ur == null) return;
		ur.setID(idSpinner.getIntValue());
		ur.setName(nameText.getText());
		ur.setHP(hpSpinner.getIntValue());
		umPanel.updateUM();
		ur.SetUM(umPanel.getUM());
		ufPanel.updataUF();
		ur.SetUF(ufPanel.getUF());
		ur.setDIM(dimList.getDIM());
	}

	public String getName() {
		return "设置基本属性";
	}

	public void dispose() {
	}
}

class BodyManager {
	public final static int MOUSE_NONE = 0;
	public final static int MOUSE_PAINT_RECT = 1;
	public final static int MOUSE_MODIFY_RECT = 0;
	public final static int MOUSE_SET_CENTER = 3;
	
	private ScrollablePanel panel;
	private int mouseState;
	private IntPair bodyCenter;
	private ArrayList bodyRects;
	
	private IntPair oldPoint;
	private boolean paintingRect;
	private Rect paintedRect;
	private Rect selectedRect;
	private boolean modifyingRect;
	private int modifyCorner;
	private JButton paintRectButton;
	private JButton modifyRectButton;
	private JButton setCenterButton;
	private Thread pointThread;
	private int pointCount;

	public BodyManager(ScrollablePanel panel) {
		this.panel = panel;
		init();
	}

	private void init() {
		bodyCenter = new IntPair();
		bodyRects = new ArrayList();
		oldPoint = new IntPair();
		paintedRect = new Rect(0, 0, 0, 0);
		reset();
		
		panel.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (panel.isMouseMovingViewport()) {
					return;
				}
				selfMousePressed(e);
			}

			public void mouseReleased(MouseEvent e) {
				if (panel.isMouseMovingViewport()) {
					return;
				}
				selfMouseReleased(e);
			}

			public void mouseClicked(MouseEvent e) {
				if (panel.isMouseMovingViewport()) {
					return;
				}
				selfMouseClicked(e);
			}
		});

		panel.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				if (panel.isMouseMovingViewport()) {
					return;
				}
				selfMouseMoved(e);
			}

			public void mouseDragged(MouseEvent e) {
				if (panel.isMouseMovingViewport()) {
					return;
				}
				selfMouseDragged(e);
			}
		});

		panel.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (panel.isMouseMovingViewport()) {
					return;
				}
				selfKeyReleased(e);
			}
		});

		pointThread = new Thread() {
			public void run() {
				pointCount = 0;
				while (!isInterrupted()) {
					++pointCount;
					if (pointCount > 1) {
						pointCount = 0;
					}
					try {
						sleep(300);
						panel.repaint();
					}
					catch (Exception e) {
					}
				}
			}
		};
		pointThread.start();
	}
	
	public void reset() {
		bodyCenter = new IntPair();
		bodyRects = new ArrayList();
		bodyCenter.x = bodyCenter.y = 0;
		bodyRects.clear();
		oldPoint.x = oldPoint.y = 0;
		paintedRect.x = paintedRect.y = paintedRect.width = paintedRect.height = 0;
		setMouseState(MOUSE_NONE);
		paintingRect = false;
		selectedRect = null;
		modifyingRect = false;
		modifyCorner = Rect.CORNER_NONE;
	}
	
	public IntPair getBodyCenter() {
		return bodyCenter.getCopy();
	}
	
	public void setBodyCenter(IntPair p) {
		bodyCenter.x = p.x;
		bodyCenter.y = p.y;
	}
	
	public ArrayList getBodyRects(int left, int top, int width, int height) {
		ArrayList result = null;
		if(bodyRects.size() > 0) {
			result = new ArrayList();
			for(int i = 0; i < bodyRects.size(); ++i) {
				Rect rect = (Rect)(bodyRects.get(i));				
				if(rect.x + rect.width <= left || rect.y + rect.height <= top ||
				   rect.x >= width || rect.y >= height) {

					System.out.println("rect out of bounds");
					continue;
				}

				result.add(rect.getCopy());
			}
		}
		return result;
	}
	
	public void setBodyRects(ArrayList rects, 
							 int left, int top, int width, int height) {
		bodyRects.clear();
		if(rects != null) {
			for(int i = 0; i < rects.size(); ++i) {
				Rect rect = (Rect)(rects.get(i));			
				if(rect.x + rect.width <= left || rect.y + rect.height <= top ||
				   rect.x >= width || rect.y >= height) {

					System.out.println("rect out of bounds");
					continue;
				}
				bodyRects.add(rect.getCopy());
			}
		}
	}

	public void stop() {
		try {
			pointThread.interrupt();
		}
		catch (Exception e) {
		}
	}

	public void setMouseState(int state) {
		this.mouseState = state;
		switch (mouseState) {
			case MOUSE_PAINT_RECT:
				panel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				break;
			default:
				panel.setCursor(Cursor.getDefaultCursor());
				break;
		}
	}

	private void selfMousePressed(MouseEvent e) {
		if (e.getButton() == XUtil.LEFT_BUTTON) {
			int x = oldPoint.x = panel.getMouseX(e);
			int y = oldPoint.y = panel.getMouseY(e);
			if (mouseState == MOUSE_PAINT_RECT) {
				paintingRect = true;
				paintedRect.x = x;
				paintedRect.y = y;
				paintedRect.width = 0;
				paintedRect.height = 0;
			}
			else if (mouseState == MOUSE_MODIFY_RECT) {
				if (selectedRect == null) {
					selectedRect = selectRect(e);
				}
				else {
					modifyCorner = getRectCorner(selectedRect, e);
					if (modifyCorner == Rect.CORNER_NONE) {
						if (!selectedRect.contains(x, y)) {
							selectedRect = selectRect(e);
						}
					}
					else {
						modifyingRect = true;
					}
				}
			}
			panel.repaint();
		}
	}

	private int getRectCorner(Rect rect, MouseEvent e) {
		int result = Rect.CORNER_NONE;
		if (rect != null) {
			result = rect.getSelectCorner(e.getX(), e.getY(), 
										  panel.getScale(), panel.getScale());
		}
		return result;
	}

	private void selfMouseReleased(MouseEvent e) {
		int x = panel.getMouseX(e);
		int y = panel.getMouseY(e);
		if (e.getButton() == XUtil.LEFT_BUTTON) {
			if (paintingRect) {
//				paintedRect.x = Math.min(x, oldPoint.x);
//				paintedRect.y = Math.min(y, oldPoint.y);
//				paintedRect.width = Math.abs(x - oldPoint.x);
//				paintedRect.height = Math.abs(y - oldPoint.y);
				if (paintedRect.width >= 2 || paintedRect.height >= 2) {
					addBodyRect(paintedRect);
				}
				paintingRect = false;
			}
			else if (modifyingRect) {
				modifyingRect = false;
			}
		}
		else if (e.getButton() == XUtil.RIGHT_BUTTON) {
			if (!modifyingRect && !paintingRect) {
				selectedRect = null;
				setMouseState(MOUSE_NONE);
			}
		}
		panel.repaint();
	}

	private void selfMouseMoved(MouseEvent e) {
		if (mouseState == MOUSE_MODIFY_RECT && selectedRect != null) {
			panel.setCursor(selectedRect.getCornerCursor(getRectCorner(selectedRect, e)));
		}
	}

	private void selfMouseDragged(MouseEvent e) {
		int x = panel.getMouseX(e);
		int y = panel.getMouseY(e);
		if (paintingRect) {
			paintedRect.x = Math.min(x, oldPoint.x);
			paintedRect.y = Math.min(y, oldPoint.y);
			paintedRect.width = Math.abs(x - oldPoint.x);
			paintedRect.height = Math.abs(y - oldPoint.y);
		}
		else if (modifyingRect) {
			modifyCorner = selectedRect.resizeByCorner(modifyCorner, x - oldPoint.x, y - oldPoint.y);
			oldPoint.x = x;
			oldPoint.y = y;
		}
		panel.repaint();
	}

	private void selfKeyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_DELETE && mouseState == MOUSE_MODIFY_RECT) {
			if (selectedRect != null) {
				removeBodyRect(selectedRect);
				selectedRect = null;
			}
		}
	}

	private void selfMouseClicked(MouseEvent e) {
		if (mouseState != MOUSE_SET_CENTER) {
			return;
		}
		bodyCenter.x = panel.getMouseX(e);
		bodyCenter.y = panel.getMouseY(e);
		panel.repaint();
	}

	private boolean mouseInRect(MouseEvent e, Rect rect) {
		return rect.contains(panel.getMouseX(e), panel.getMouseY(e));
	}

	private Rect selectRect(MouseEvent e) {
		Rect result = null;
		for(int i = 0; i < bodyRects.size(); ++i) {
			Rect rect = (Rect)(bodyRects.get(i));
			if(mouseInRect(e, rect)) {
				result = rect;
				break;
			}
		}
		return result;
	}

	private void addBodyRect(Rect bodyRect) {
		for (int i = 0; i < bodyRects.size(); ++i) {
			Rect rect = (Rect) (bodyRects.get(i));
			if (rect.x == bodyRect.x && rect.y == bodyRect.y &&
				rect.width == bodyRect.width && rect.height == bodyRect.height) {
				return;
			}
		}
		bodyRects.add(bodyRect.getCopy());
	}

	private void removeBodyRect(Rect bodyRect) {
		for (int i = 0; i < bodyRects.size(); ++i) {
			Rect rect = (Rect) (bodyRects.get(i));
			if (rect.x == bodyRect.x && rect.y == bodyRect.y &&
				rect.width == bodyRect.width && rect.height == bodyRect.height) {
				bodyRects.remove(i);
				panel.repaint();
				return;
			}
		}
	}

	public void paintRects(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		Color oldColor = g2.getColor();
		Composite oldComposite = g2.getComposite();
		
		g2.setComposite(panel.DEF_ALPHA_COMPOSITE);
		g2.setColor(Color.RED);

		for (int i = 0; i < bodyRects.size(); ++i) {
			Rect rect = (Rect) (bodyRects.get(i));
			g2.fillRect(rect.x, rect.y, rect.width, rect.height);
		}

		if (paintingRect) {
			g2.fillRect(paintedRect.x, paintedRect.y,
						paintedRect.width, paintedRect.height);
		}
		else if (selectedRect != null) {
			g2.setColor(Color.BLUE);
			selectedRect.paintCorner(g2);
		}
		
		g2.setComposite(oldComposite);
		g2.setColor(oldColor);
	}

	public void paintPoint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		Color oldColor = g.getColor();
		Composite oldComposite = g2.getComposite();
		
		g2.setComposite(panel.DEF_ALPHA_COMPOSITE);
		g.setColor(Color.GREEN);
		int range = 6 * (pointCount + 1);
		g.fillOval(bodyCenter.x - range / 2, bodyCenter.y - range / 2, range, range);
		
		g2.setComposite(oldComposite);
		g2.setColor(oldColor);
	}
}

class URBodyPanel
	extends AnimPlayPanel
	implements URPropPanel {
	
	private UnitRes ur;
	private IntPair[] standAnimRanges;
	private IntPair[] bodyCenters;
	private ArrayList[] bodyRects;
	private JPanel backPanel;
	private int dir;
	private ValueChooser dirChooser;
	private JDialog owner;
	private JButton paintRectButton;
	private JButton modifyRectButton;
	private JButton setCenterButton;
	
	private BodyManager manager;

	public URBodyPanel(JDialog owner) {
		super(true);
		this.owner = owner;
		init();
	}

	private void init() {
		this.ur = null;
		
		manager = new BodyManager(this);

		backPanel = new JPanel();

		dirChooser = new ValueChooser(Dir.D, Dir.FULL_STAND_DIRS, Dir.DESCRIPTIONS);
		dirChooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dirChanged();
			}
		});

		paintRectButton = new JButton("绘制攻击矩形");
		paintRectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manager.setMouseState(BodyManager.MOUSE_PAINT_RECT);
			}
		});

		modifyRectButton = new JButton("修改攻击矩形");
		modifyRectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manager.setMouseState(BodyManager.MOUSE_MODIFY_RECT);
			}
		});

		setCenterButton = new JButton("设置中心点");
		setCenterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manager.setMouseState(BodyManager.MOUSE_SET_CENTER);
			}
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(2, 2, 1, 3);
		c.weighty = 1;
		c.gridy = 0;

		c.gridx = 0;
		c.weightx = 0;
		buttonPanel.add(new JLabel("方向："), c);

		c.gridx = 1;
		buttonPanel.add(dirChooser, c);

		c.gridx = 2;
		buttonPanel.add(paintRectButton, c);

		c.gridx = 3;
		buttonPanel.add(modifyRectButton, c);

		c.gridx = 4;
		buttonPanel.add(setCenterButton, c);

		c.gridx = 5;
		c.weightx = 1;
		buttonPanel.add(new JPanel(), c);

		backPanel.setLayout(new BorderLayout());
		backPanel.add(super.getBackPanel(), BorderLayout.CENTER);
		backPanel.add(buttonPanel, BorderLayout.NORTH);

		copyURProp();
	}

	public String getName() {
		return "设置站立动画和可攻击范围";
	}

	public JPanel getBackPanel() {
		return backPanel;
	}

	public void setUR(UnitRes ur) {
		dir = -1;
		this.ur = ur;
		copyURProp();
	}

	private void copyURProp() {
	    checkUR();
		if(ur == null) {
			super.setAnim(null);
			return;
		}
		super.setAnim(ur.getAnim());
		IntPair animOrigin = getAnimOrigin();
		standAnimRanges = new IntPair[Dir.LENGTH];
		bodyCenters = new IntPair[Dir.LENGTH];
		bodyRects = new ArrayList[Dir.LENGTH];
		for (int i = 0; i < Dir.STAND_DIRS.length; ++i) {
			standAnimRanges[Dir.STAND_DIRS[i]] = ur.getStandAnimRange(Dir.STAND_DIRS[i]);
		}
		for (int i = 0; i < Dir.FULL_STAND_DIRS.length; ++i) {
			bodyCenters[Dir.FULL_STAND_DIRS[i]] = ur.getBodyCenter(Dir.FULL_STAND_DIRS[i]);
			bodyCenters[Dir.FULL_STAND_DIRS[i]].x += animOrigin.x;
			bodyCenters[Dir.FULL_STAND_DIRS[i]].y += animOrigin.y;
		}

		for (int i = 0; i < Dir.FULL_STAND_DIRS.length; ++i) {
			bodyRects[Dir.FULL_STAND_DIRS[i]] = ur.getBodyRect(Dir.FULL_STAND_DIRS[i]);
			if (bodyRects[Dir.FULL_STAND_DIRS[i]] != null) {
				for (int j = 0; j < bodyRects[Dir.FULL_STAND_DIRS[i]].size(); ++j) {
					Rect rect = (Rect) (bodyRects[Dir.FULL_STAND_DIRS[i]].get(j));
					rect.x += animOrigin.x;
					rect.y += animOrigin.y;
				}
			}
		}

		manager.setMouseState(BodyManager.MOUSE_NONE);
		dirChooser.setValue(Dir.D);

		stop();
	}

	public void updateURProp() {
		if(ur == null) {
			return;
		}
		IntPair animOrigin = getAnimOrigin();

		if (dir >= 0 && dir < standAnimRanges.length && !Dir.isFlip(dir)) {
			standAnimRanges[dir] = getRange();
		}

		if(dir >= 0 && dir < standAnimRanges.length) {
		    bodyCenters[dir] = manager.getBodyCenter();
			bodyRects[dir] = manager.getBodyRects(0, 0, 
												  this.getBasicWidth(), this.getBasicHeight());
		}

		for (int i = 0; i < Dir.STAND_DIRS.length; ++i) {
			ur.setStandAnimRange(Dir.STAND_DIRS[i], standAnimRanges[Dir.STAND_DIRS[i]]);
		}

		for (int i = 0; i < Dir.FULL_STAND_DIRS.length; ++i) {
			IntPair center = bodyCenters[Dir.FULL_STAND_DIRS[i]].getCopy();
			center.x -= animOrigin.x;
			center.y -= animOrigin.y;
			ur.setBodyCenter(Dir.FULL_STAND_DIRS[i], center);
		}

		for (int i = 0; i < Dir.FULL_STAND_DIRS.length; ++i) {
			ArrayList tmpBody = null;
			if (bodyRects[Dir.FULL_STAND_DIRS[i]] != null) {
				tmpBody = new ArrayList();
				for (int j = 0; j < bodyRects[Dir.FULL_STAND_DIRS[i]].size(); ++j) {
					Rect rect = ( (Rect) (bodyRects[Dir.FULL_STAND_DIRS[i]].get(j))).getCopy();
					if(rect.x + rect.width <= 0 || rect.y + rect.height <= 0 ||
					   rect.x >= this.getBasicWidth() || rect.y >= this.getBasicHeight()) {

						System.out.println("save unit body rect, out of bounds");
						continue;
					}
					rect.x -= animOrigin.x;
					rect.y -= animOrigin.y;
					tmpBody.add(rect);
				}
			}
			ur.setBodyRect(Dir.FULL_STAND_DIRS[i], tmpBody);
		}
	}

	public void dispose() {
		stopAll();
	}

	private void checkUR() {
		paintRectButton.setEnabled(ur != null);
		modifyRectButton.setEnabled(ur != null);
		setCenterButton.setEnabled(ur != null);
	}

	private void dirChanged() {
		//save
		if (dir >= 0 && dir < standAnimRanges.length && !Dir.isFlip(dir)) {
			standAnimRanges[dir] = getRange();
		}

		if(dir >= 0 && dir < standAnimRanges.length) {
			bodyCenters[dir] = manager.getBodyCenter();
			bodyRects[dir] = manager.getBodyRects(0, 0, 
												  this.getBasicWidth(), this.getBasicHeight());
		}

		//clear
		manager.reset();

		//load
		dir = dirChooser.getValue();
		if (dir >= 0 && dir < standAnimRanges.length) {
			setRange(standAnimRanges[Dir.flip(dir)]);
			manager.setBodyCenter(bodyCenters[dir]);
			manager.setBodyRects(bodyRects[dir], 
								 0, 0,  
								 this.getBasicWidth(), this.getBasicHeight());
		}
		stop();
		repaint();
	}

	public void stopAll() {
		stop();
		manager.stop();
	}

	protected boolean isFlip() {
		return Dir.isFlip(dir);
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (!isVisible() || ur == null) {
			return;
		}
		paintAnim(g);
		manager.paintRects(g);
		paintAnimOrigin(g, false);
		manager.paintPoint(g);
	}
}

class URDeadPanel
	extends ExplorePanel
	implements URPropPanel {
	private UnitRes ur;
	private JPanel backPanel;
	private IntPair[] deadAnimRanges;
	private ArrayList[] deadExplores;
	private int dir;
	private ValueChooser dirChooser;
	private PainterTree tree;

	public URDeadPanel(JDialog owner, ARManager arManager) {
		super(owner, new MouseInfo());
		init(arManager);
	}

	private void init(ARManager arManager) {
		this.ur = null;

		backPanel = new JPanel();

		dirChooser = new ValueChooser(Dir.D, Dir.FULL_STAND_DIRS, Dir.DESCRIPTIONS);
		dirChooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dirChanged();
			}
		});
		
		tree = new PainterTree("爆炸",
							   null, //AnimPainterGroup.getGroups(arManager.getGroups()),
							   AnimPainter.getPainters(arManager.getAnims(XUtil.getDefPropInt("exploreGroupID"))),
							   mouseInfo);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;

		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(2, 10, 2, 10);
		c.weightx = 0;
		c.weighty = 0;
		topPanel.add(new JLabel("选择方向："), c);

		c.gridx = 1;
		topPanel.add(dirChooser, c);

		c.gridx = 2;
		c.weightx = 1;
		topPanel.add(new JPanel(), c);

		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout());
		rightPanel.add(topPanel, BorderLayout.NORTH);
		rightPanel.add(super.getBackPanel(), BorderLayout.CENTER);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
											  tree.getScrollPane(),
											  rightPanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(200);
		backPanel.setLayout(new BorderLayout());
		backPanel.add(splitPane, BorderLayout.CENTER);

		copyURProp();
	}

	public String getName() {
		return "设置死亡动画和爆炸效果";
	}

	public JPanel getBackPanel() {
		return backPanel;
	}

	public void setUR(UnitRes ur) {
		dir = -1;
		this.ur = ur;
		copyURProp();
	}

	private void copyURProp() {
		if(ur == null) {
			super.setAnim(null);
			return;
		}
		super.setAnim(ur.getAnim());
		IntPair animOrigin = getAnimOrigin();
		//动画
		deadAnimRanges = new IntPair[Dir.LENGTH];
		for (int i = 0; i < Dir.STAND_DIRS.length; ++i) {
			deadAnimRanges[Dir.STAND_DIRS[i]] = ur.getDeadAnimRange(Dir.STAND_DIRS[i]);
		}
		//爆炸
		deadExplores = new ArrayList[Dir.LENGTH];
		for (int i = 0; i < Dir.FULL_STAND_DIRS.length; ++i) {
			deadExplores[Dir.FULL_STAND_DIRS[i]] = ur.getDeadExplores(Dir.FULL_STAND_DIRS[i]);
		}

		dirChooser.setValue(Dir.D);

		stop();
	}

	public void updateURProp() {
		if(ur == null) {
			return;
		}
		//save
		IntPair animOrigin = getAnimOrigin();
		if (dir >= 0 && dir < deadExplores.length) {
			ArrayList deadExplore = exploreManager.getExplores();
			for (int i = 0; i < deadExplore.size(); ++i) {
				Explore explore = (Explore) (deadExplore.get(i));
				explore.setPosition(explore.getX() - animOrigin.x, explore.getY() - animOrigin.y);
			}
			deadExplores[dir] = deadExplore;
		}

		if (dir >= 0 && dir < deadAnimRanges.length && !Dir.isFlip(dir)) {
			deadAnimRanges[dir] = getRange();
		}

		//update
		for (int i = 0; i < Dir.STAND_DIRS.length; ++i) {
			ur.setDeadAnimRange(Dir.STAND_DIRS[i], deadAnimRanges[Dir.STAND_DIRS[i]]);
		}

		for (int i = 0; i < Dir.FULL_STAND_DIRS.length; ++i) {
			ur.setDeadExplores(Dir.FULL_STAND_DIRS[i], deadExplores[Dir.FULL_STAND_DIRS[i]]);
		}
	}

	public void dispose() {
		stop();
	}

	private void dirChanged() {
		IntPair animOrigin = getAnimOrigin();
		//save		
		if (dir >= 0 && dir < deadExplores.length) {
			ArrayList deadExplore = exploreManager.getExplores();
			for (int i = 0; i < deadExplore.size(); ++i) {
				Explore explore = (Explore) (deadExplore.get(i));
				explore.setPosition(explore.getX() - animOrigin.x, explore.getY() - animOrigin.y);
			}
			deadExplores[dir] = deadExplore;
		}

		if (dir >= 0 && dir < deadAnimRanges.length && !Dir.isFlip(dir)) {
			deadAnimRanges[dir] = getRange();
		}

		dir = dirChooser.getValue();

		//load
		exploreManager.reset();
		if (dir >= 0 && dir < deadExplores.length) {
			ArrayList tmp = new ArrayList();
			for (int i = 0; i < deadExplores[dir].size(); ++i) {
				Explore explore = ( (Explore) (deadExplores[dir].get(i))).getCopy();
				explore.setPosition(explore.getX() + animOrigin.x,
									explore.getY() + animOrigin.y);
				tmp.add(explore);
			}
			exploreManager.addExplores(tmp);
		}

		if (dir >= 0 && dir < deadAnimRanges.length) {
			setRange(deadAnimRanges[Dir.flip(dir)]);
		}
		stop();
		repaint();
	}

	protected boolean isFlip() {
		return Dir.isFlip(dir);
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (!isVisible() || ur == null) {
			return;
		}
		paintAnim(g);
		paintAnimOrigin(g, false);
		exploreManager.paintSprites(g);
		exploreManager.paintOthers(g);
	}
}

class URMovePanel
	extends AnimPlayPanel
	implements URPropPanel {
	private final static int MOUSE_NONE = 0;
	private final static int MOUSE_PAINT_GRID = 1;
	private final static int MOUSE_ERASE_GRID = 2;

	private int mouseState;
	private UnitRes ur;
	private IntPair[] moveAnimRanges;
	private DoublePair[] moveSpeeds;
	private ArrayList moveGrids;
	private JPanel backPanel;
	private int dir;
	private JDialog owner;
	private ValueChooser dirChooser;
	private JButton paintGridButton;
	private JButton eraseGridButton;
	private JTextField speedXText;
	private JTextField speedYText;

	public URMovePanel(JDialog owner) {
		super(true);
		this.owner = owner;
		init();
	}

	private void init() {
		this.ur = null;

		backPanel = new JPanel();

		dirChooser = new ValueChooser(Dir.D, Dir.FULL_MOVE_DIRS, Dir.DESCRIPTIONS);
		dirChooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dirChanged();
			}
		});

		paintGridButton = new JButton("绘制占用格");
		paintGridButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setMouseState(MOUSE_PAINT_GRID);
			}
		});

		eraseGridButton = new JButton("删除占用格");
		eraseGridButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setMouseState(MOUSE_ERASE_GRID);
			}
		});

		speedXText = new JTextField();
		speedYText = new JTextField();

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (isMouseMovingViewport()) {
					return;
				}
				selfMousePressed(e);
			}

			public void mouseReleased(MouseEvent e) {
				if (isMouseMovingViewport()) {
					return;
				}
				selfMouseReleased(e);
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				if (isMouseMovingViewport()) {
					return;
				}
				selfMouseDragged(e);
			}
		});

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(2, 2, 1, 3);
		c.weighty = 1;
		c.gridy = 0;

		c.gridx = 0;
		c.weightx = 0;
		topPanel.add(new JLabel("方向："), c);

		c.gridx = 1;
		topPanel.add(dirChooser, c);

		c.gridx = 2;
		topPanel.add(paintGridButton, c);

		c.gridx = 3;
		topPanel.add(eraseGridButton, c);

		c.gridx = 4;
		topPanel.add(new JLabel("水平方向速度："), c);

		c.gridx = 5;
		c.weightx = 1;
		topPanel.add(speedXText, c);

		c.gridx = 6;
		c.weightx = 0;
		topPanel.add(new JLabel("垂直方向速度："), c);

		c.gridx = 7;
		c.weightx = 1;
		topPanel.add(speedYText, c);

		backPanel.setLayout(new BorderLayout());
		backPanel.add(super.getBackPanel(), BorderLayout.CENTER);
		backPanel.add(topPanel, BorderLayout.NORTH);

		copyURProp();
	}

	public String getName() {
		return "设置移动动画和占地范围";
	}

	public JPanel getBackPanel() {
		return backPanel;
	}

	public void setUR(UnitRes ur) {
		dir = -1;
		this.ur = ur;
		copyURProp();
	}

	private void copyURProp() {
		checkUR();
		if(ur == null) {
			super.setAnim(null);
			return;
		}
		super.setAnim(ur.getAnim());

		moveAnimRanges = new IntPair[Dir.LENGTH];
		for (int i = 0; i < Dir.MOVE_DIRS.length; ++i) {
			moveAnimRanges[Dir.MOVE_DIRS[i]] = ur.getMoveAnimRange(Dir.MOVE_DIRS[i]);
		}

		moveSpeeds = new DoublePair[Dir.LENGTH];
		for (int i = 0; i < Dir.FULL_MOVE_DIRS.length; ++i) {
			moveSpeeds[Dir.FULL_MOVE_DIRS[i]] = ur.getMoveSpeed(Dir.FULL_MOVE_DIRS[i]);
		}

		moveGrids = ur.getMoveGrids();

		setMouseState(MOUSE_NONE);
		dirChooser.setValue(Dir.D);

		stop();
	}

	public void updateURProp() {
		if(ur == null) {
			return;
		}
		if (dir >= 0 && dir < moveAnimRanges.length && !Dir.isFlip(dir)) {
			moveAnimRanges[dir] = getRange();
		}

		if (dir >= 0 && dir < moveSpeeds.length) {
			moveSpeeds[dir] = getSpeed();
		}

		for (int i = 0; i < Dir.MOVE_DIRS.length; ++i) {
			ur.setMoveAnimRange(Dir.MOVE_DIRS[i], moveAnimRanges[Dir.MOVE_DIRS[i]]);
		}

		for (int i = 0; i < Dir.FULL_MOVE_DIRS.length; ++i) {
			ur.setMoveSpeed(Dir.FULL_MOVE_DIRS[i], moveSpeeds[Dir.FULL_MOVE_DIRS[i]]);
		}

		ur.setMoveGrids(moveGrids);
	}

	public void dispose() {
		stop();
	}

	private void checkUR() {
		paintGridButton.setEnabled(ur != null);
		eraseGridButton.setEnabled(ur != null);
	}

	public DoublePair getSpeed() {
		DoublePair result = new DoublePair();
		try {
			result.x = Double.parseDouble(speedXText.getText());
		}
		catch (Exception e) {
			result.x = 0;
		}

		try {
			result.y = Double.parseDouble(speedYText.getText());
		}
		catch (Exception e) {
			result.y = 0;
		}
		return result;
	}

	public void setSpeed(DoublePair speed) {
		speedXText.setText(speed.x + "");
		speedYText.setText(speed.y + "");
	}

	private void dirChanged() {
		//save
		if (dir >= 0 && dir < moveAnimRanges.length && !Dir.isFlip(dir)) {
			moveAnimRanges[dir] = getRange();
		}
		if (dir >= 0 && dir < moveSpeeds.length) {
			moveSpeeds[dir] = getSpeed();
		}

		dir = dirChooser.getValue();

		//load
		if (dir >= 0 && dir < moveAnimRanges.length) {
			setRange(moveAnimRanges[Dir.flip(dir)]);
		}
		if (dir >= 0 && dir < moveSpeeds.length) {
			setSpeed(moveSpeeds[dir]);
		}
		stop();
		repaint();
	}

	private void setMouseState(int state) {
		this.mouseState = state;
		switch (state) {
			case MOUSE_PAINT_GRID:
			case MOUSE_ERASE_GRID:
				setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				break;
			default:
				setCursor(Cursor.getDefaultCursor());
				break;
		}
	}

	private void selfMousePressed(MouseEvent e) {
		if (e.getButton() == XUtil.LEFT_BUTTON) {
			if (mouseState == MOUSE_PAINT_GRID) {
				addMoveGrid(getMoveGrid(getMouseGrid(e)));
			}
			else if (mouseState == MOUSE_ERASE_GRID) {
				removeMoveGrid(getMoveGrid(getMouseGrid(e)));
			}
		}
	}

	private void selfMouseReleased(MouseEvent e) {
		if (e.getButton() == XUtil.RIGHT_BUTTON) {
			setMouseState(MOUSE_NONE);
		}
	}

	private void selfMouseDragged(MouseEvent e) {
		if (mouseState == MOUSE_PAINT_GRID) {
			addMoveGrid(getMoveGrid(getMouseGrid(e)));
		}
		else if (mouseState == MOUSE_ERASE_GRID) {
			removeMoveGrid(getMoveGrid(getMouseGrid(e)));
		}
	}

	private IntPair getMouseGrid(MouseEvent e) {
		return Grid.getGridXY(getMouseX(e), getMouseY(e));
	}

	private IntPair getMoveGrid(IntPair mouseGrid) {
		IntPair animOriginGrid = getAnimOriginGrid();
		return new IntPair(mouseGrid.x - animOriginGrid.x,
						   mouseGrid.y - animOriginGrid.y);
	}

	private void addMoveGrid(IntPair moveGrid) {
		boolean hasGrid = false;
		for (int i = 0; i < moveGrids.size(); ++i) {
			IntPair grid = (IntPair) (moveGrids.get(i));
			if (moveGrid.equals(grid)) {
				hasGrid = true;
			}
		}

		if (!hasGrid) {
			moveGrids.add(moveGrid);
			repaint();
		}
	}

	private void removeMoveGrid(IntPair moveGrid) {
		for (int i = 0; i < moveGrids.size(); ++i) {
			IntPair grid = (IntPair) (moveGrids.get(i));
			if (moveGrid.equals(grid)) {
				moveGrids.remove(i);
				repaint();
				return;
			}
		}
	}

	protected boolean isFlip() {
		return Dir.isFlip(dir);
	}

	private void paintGridLines(Graphics g) {

	}

	private void paintMoveGrids(Graphics g) {
		IntPair animOriginGrid = getAnimOriginGrid();

		Graphics2D g2 = (Graphics2D) g;
		Composite oldComposite = g2.getComposite();
		g2.setComposite(DEF_ALPHA_COMPOSITE);

		Color oldColor = g2.getColor();
		g2.setColor(Color.GREEN);
		IntPair tmp = new IntPair();
		for (int i = 0; i < moveGrids.size(); ++i) {
			IntPair grid = (IntPair) (moveGrids.get(i));
			tmp.x = grid.x + animOriginGrid.x;
			tmp.y = grid.y + animOriginGrid.y;
			paintGrid(tmp, g2);
		}
		g2.setColor(oldColor);
		g2.setComposite(oldComposite);
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (!isVisible() || ur == null) {
			return;
		}
		paintGridLines(g);
		paintAnim(g);
		paintMoveGrids(g);
		paintAnimOrigin(g, false);
	}
}

class URWeaponPosPanel
	extends AnimPlayPanel
	implements URPropPanel {
	private JDialog owner;
	private UnitRes ur;
	private IntPair[] standAnimRanges;
	private JPanel backPanel;
	private int currentPos;
	private int maxID;
	private int dir;
	private JList posList;
	private DefaultListModel listModel;
	private JButton btAddPos;
	private JTextField nameText;
	private ValueChooser dirChooser;
	private Thread pointThread;
	private int pointCount;
	private ValueChooser coverChooser;
	private ValueChooser weaponChooser;

	public URWeaponPosPanel(JDialog owner) {
		super(true);
		this.owner = owner;
		init();
	}

	private void init() {
		this.ur = null;

		dirChooser = new ValueChooser(Dir.D, Dir.FULL_STAND_DIRS, Dir.DESCRIPTIONS);
		dirChooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dirChanged();
			}
		});

		listModel = new DefaultListModel();
		posList = new JList(listModel);
		posList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		posList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent listSelectionEvent) {
				posChanged();
			}
		});
		SwingUtil.MakeListDeleteable(posList, null);
		SwingUtil.SetObjListRenderer(posList);

		nameText = new JTextField();
		nameText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				namePerformed();
			}
		});
		
		weaponChooser = new ValueChooser(Weapon.MACHINE_GUN, Weapon.TYPES, 
										 Weapon.DESCRIPTIONS);

		coverChooser = new ValueChooser(WeaponPos.COVER_Y, WeaponPos.COVERS,
										WeaponPos.COVER_DESCRIPTIONS);

		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (!isMouseMovingViewport()) {
					changePoint(e);
				}
			}
		});

		btAddPos = new JButton("添加一个挂接点");
		btAddPos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addPos();
			}
		});

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());
		leftPanel.add(posList, BorderLayout.CENTER);
		leftPanel.add(btAddPos, BorderLayout.SOUTH);

		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
//		c.insets = new Insets(0, 4, 0, 6);
		rightPanel.add(new JLabel("方向："), c);

		c.gridx = 1;
		rightPanel.add(dirChooser, c);

		c.gridx = 2;
		rightPanel.add(new JLabel("名称："), c);

		c.gridx = 3;
		c.weightx = 1;
		rightPanel.add(nameText, c);

		c.gridx = 4;
		c.weightx = 0;
		rightPanel.add(new JLabel("遮挡："), c);

		c.gridx = 5;
//		c.insets = new Insets(0, 0, 0, 0);
		rightPanel.add(coverChooser, c);
		
		c.gridx = 6;
		rightPanel.add(new JLabel("类型："), c);
		
		c.gridx = 7;
		rightPanel.add(weaponChooser, c);

		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 1;
		c.gridwidth = 8;
		rightPanel.add(super.getBackPanel(), c);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
											  leftPanel, rightPanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(200);
		backPanel = new JPanel();
		backPanel.setLayout(new BorderLayout());
		backPanel.add(splitPane, BorderLayout.CENTER);

		copyURProp();

		pointThread = new Thread() {
			public void run() {
				pointCount = 0;
				while (!isInterrupted()) {
					++pointCount;
					if (pointCount > 1) {
						pointCount = 0;
					}
					try {
						sleep(300);
						repaint();
					}
					catch (Exception e) {
					}
				}
			}
		};
		pointThread.start();
	}

	public String getName() {
		return "设置武器挂接点";
	}

	public JPanel getBackPanel() {
		return backPanel;
	}

	public void setUR(UnitRes ur) {
		currentPos = -1;
		this.ur = ur;
		copyURProp();
	}

	private void copyURProp() {
		checkUR();
		if(ur == null) {
			super.setAnim(null);
			return;
		}
		super.setAnim(ur.getAnim());

		standAnimRanges = new IntPair[Dir.LENGTH];
		for (int i = 0; i < Dir.STAND_DIRS.length; ++i) {
			standAnimRanges[Dir.STAND_DIRS[i]] = ur.getStandAnimRange(Dir.STAND_DIRS[i]);
		}

		listModel.clear();
		ArrayList weaponPos = ur.getWeaponPos();
		if (weaponPos != null) {
			for (int i = 0; i < weaponPos.size(); ++i) {
				listModel.addElement(weaponPos.get(i));
			}
		}
		maxID = 0;
		if (weaponPos != null) {
			for (int i = 0; i < weaponPos.size(); ++i) {
				WeaponPos p = (WeaponPos) weaponPos.get(i);
				if (maxID <= p.getID()) {
					maxID = p.getID() + 1;
				}
			}
		}

		currentPos = -1;
		posList.setSelectedIndex(currentPos);
		dirChooser.setValue(Dir.D);

		stop();
	}

	public void updateURProp() {
		if(ur == null) {
			return;
		}
		//save
		if (currentPos >= 0 && currentPos < listModel.size()) {
			WeaponPos p = (WeaponPos) (listModel.get(currentPos));
			p.setName(nameText.getText());
			p.setWeapon(weaponChooser.getValue());
			p.setCover(dir, coverChooser.getValue());
		}

		//update
		ArrayList weaponPos = new ArrayList();
		for (int i = 0; i < listModel.size(); ++i) {
			weaponPos.add(listModel.get(i));
		}
		ur.setWeaponPos(weaponPos);
	}

	public void dispose() {
		stopAll();
	}
	
	private void checkUR() {
		btAddPos.setEnabled(ur != null);
	}

	private void dirChanged() {
		if (currentPos >= 0 && currentPos < listModel.size()) {
			//save
			WeaponPos p = (WeaponPos) (listModel.get(currentPos));
			p.setCover(dir, coverChooser.getValue());
		}

		//load
		dir = dirChooser.getValue();
		if (currentPos >= 0 && currentPos < listModel.size()) {
			WeaponPos p = (WeaponPos) (listModel.get(currentPos));
			coverChooser.setValue(p.getCover(dir));
		}

		if (dir >= 0 && dir < standAnimRanges.length) {
			setRange(standAnimRanges[Dir.flip(dir)]);
		}
		stop();
		repaint();
	}

	private void namePerformed() {
		if (currentPos < 0 || currentPos >= listModel.size()) {
			return;
		}
		WeaponPos p = (WeaponPos) (listModel.get(currentPos));
		p.setName(nameText.getText());
		p.setWeapon(weaponChooser.getValue());
		posList.repaint();
	}

	private void posChanged() {
		if (currentPos == posList.getSelectedIndex()) {
			return;
		}
		//save
		if (currentPos >= 0 && currentPos < listModel.size()) {
			WeaponPos p = (WeaponPos) (listModel.get(currentPos));
			p.setName(nameText.getText());
			p.setWeapon(weaponChooser.getValue());
			p.setCover(dir, coverChooser.getValue());
		}
		dirChooser.setValue( -1);
		//load
		currentPos = posList.getSelectedIndex();
		if (currentPos >= 0 && currentPos < listModel.size()) {
			WeaponPos p = (WeaponPos) (listModel.get(currentPos));
			nameText.setText(p.getName());
			weaponChooser.setValue(p.getWeapon());
			dirChooser.setValue(Dir.D);
		}
		repaint();
	}

	private void addPos() {
		int id = maxID++;
		WeaponPos newPos = new WeaponPos(id);
		listModel.addElement(newPos);
	}

	private void changePoint(MouseEvent e) {
		if (currentPos < 0 || currentPos >= listModel.size()) {
			return;
		}
		WeaponPos p = (WeaponPos) (listModel.get(currentPos));
		IntPair point = new IntPair();
		IntPair animOrigin = getAnimOrigin();
		point.x = getMouseX(e) - animOrigin.x;
		point.y = getMouseY(e) - animOrigin.y;
		p.setPos(dir, point);
		repaint();
	}

	public void stopAll() {
		stop();
		try {
			pointThread.interrupt();
		}
		catch (Exception e) {
		}
	}

	protected boolean isFlip() {
		return Dir.isFlip(dir);
	}

	private void paintPoint(Graphics g) {
		if (currentPos < 0 || currentPos >= listModel.size()) {
			return;
		}
		WeaponPos p = (WeaponPos) (listModel.get(currentPos));
		IntPair point = p.getPos(dir);
		if (point == null) {
			return;
		}
		IntPair animOrigin = getAnimOrigin();
		Graphics2D g2 = (Graphics2D) g;
		Composite oldComposite = g2.getComposite();
		g2.setComposite(DEF_ALPHA_COMPOSITE);
		Color oldColor = g.getColor();
		g.setColor(Color.RED);
		int range = 6 * (pointCount + 1);
		g.fillOval(point.x + animOrigin.x - range / 2, point.y + animOrigin.y - range / 2, range, range);

		g2.setColor(oldColor);
		g2.setComposite(oldComposite);
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (!isVisible() || ur == null) {
			return;
		}
		paintAnim(g);
		paintAnimOrigin(g, false);
		paintPoint(g);
	}
}
