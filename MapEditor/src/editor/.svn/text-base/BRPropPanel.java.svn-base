package editor;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

interface BRPropPanel {
	public void setBR(BuildingRes br);

	public void updateBRProp();

	public String getName();

	public void dispose();
}

class BRBasicPanel
	extends JPanel
	implements BRPropPanel {
	private JDialog owner;
	private BuildingRes br;
	private NumberSpinner idSpinner;
	private JTextField nameText;
	private NumberSpinner hpSpinner;
	private JRadioButton shakeRadioYes;
	private JRadioButton shakeRadioNo;
	private ButtonGroup shakeRadioGroup;
	private DropItemModeList dimList;

	public BRBasicPanel(JDialog owner) {
		this.owner = owner;
		this.br = null;

		idSpinner = new NumberSpinner();
		nameText = new JTextField();
		hpSpinner = new NumberSpinner();

		shakeRadioYes = new JRadioButton("是");
		shakeRadioNo = new JRadioButton("否");
		shakeRadioGroup = new ButtonGroup();
		shakeRadioGroup.add(shakeRadioYes);
		shakeRadioGroup.add(shakeRadioNo);
		JPanel shakeRadioPanel = new JPanel();
		shakeRadioPanel.setLayout(new GridLayout());
		shakeRadioPanel.add(shakeRadioYes);
		//shakeRadioPanel.add(new JPanel());
		shakeRadioPanel.add(shakeRadioNo);

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

		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 0;
		this.add(new JLabel("HP："), c);

		c.gridx = 1;
		c.weightx = 1;
		this.add(hpSpinner, c);

		c.gridx = 0;
		c.gridy = 3;
		c.weightx = 0;
		this.add(new JLabel("震屏："), c);

		c.gridx = 1;
		c.weightx = 1;
		this.add(shakeRadioPanel, c);

		c.gridx = 0;
		c.gridy = 4;
		c.weightx = 1;
		c.gridwidth = 2;
		this.add(new JLabel("掉落道具："), c);

		c.gridy = 5;
		c.weighty = 1;
		this.add(new JScrollPane(dimList), c);

//		c.gridy = 4;
//		c.gridx = 0;
//		c.gridwidth = 2;
//		c.weightx = 1;
//		c.weighty = 1;
//		this.add(new JPanel(), c);

		copyBRProp();
	}

	public void setBR(BuildingRes br) {
		this.br = br;
		copyBRProp();
	}

	private void copyBRProp() {
		if (br == null) {
			return;
		}
		idSpinner.setIntValue(br.getID());
		nameText.setText(br.getName());
		if (br instanceof ExploreBR) {
			hpSpinner.setIntValue( ( (ExploreBR) br).getHP());
		}
		else {
			hpSpinner.setEnabled(false);
		}
		if (br.isShake()) {
			shakeRadioYes.setSelected(true);
		}
		else {
			shakeRadioNo.setSelected(true);
		}
		dimList.setDIM(br.getDIM());
	}

	public void updateBRProp() {
		if (br == null) {
			return;
		}
		br.setID(idSpinner.getIntValue());
		br.setName(nameText.getText());
		if (br instanceof ExploreBR) {
			( (ExploreBR) br).setHP(hpSpinner.getIntValue());
		}
		if (shakeRadioYes.isSelected()) {
			br.setShake(true);
		}
		else {
			br.setShake(false);
		}
		br.setDIM(dimList.getDIM());
	}

	public String getName() {
		return "设置基本属性";
	}

	public void dispose() {
	}
}

abstract class ScrollableBRPropPanel
	extends ScrollablePanel
	implements BRPropPanel {
	protected JDialog owner;

	public ScrollableBRPropPanel(JDialog owner) {
		super(XUtil.getDefPropInt("BRPanelWidth"), XUtil.getDefPropInt("BRPanelHeight"));
		this.owner = owner;
	}

	public IntPair getImageOrigin() {
		return new IntPair(getBasicWidth() / 2, getBasicHeight() / 2);
	}
}

class BRImageSelecter
	extends OKCancelDialog {
	private SIManager siManager;
	private JButton[] buttons;
	private SingleImage[] images;

	public BRImageSelecter(JDialog owner, BuildingRes br, SIManager siManager) {
		super(owner);
		init(br, siManager);
	}

	private void init(BuildingRes br, SIManager siManager) {
		setTitle("设置建筑不同状态下的图片");
		this.siManager = siManager;
		images = new SingleImage[ExploreBR.STATE_COUNT];
		buttons = new JButton[ExploreBR.STATE_COUNT];
		ActionListener buttonListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int type = Integer.parseInt(e.getActionCommand().trim());
				selectSI(type);
			}
		};

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout(buttons.length, 1, 5, 5));

		for (int i = 0; i < buttons.length; ++i) {
			images[i] = null;
			buttons[i] = new JButton("设置" + ExploreBR.DESCS[i] + "的图片");
			buttons[i].setActionCommand(i + "");
			buttons[i].addActionListener(buttonListener);
			centerPanel.add(buttons[i]);
		}
		if (! (br instanceof HouseBR)) {
			buttons[BuildingRes.HOUSE].setEnabled(false);
		}

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(centerPanel, BorderLayout.CENTER);
		cp.add(buttonPanel, BorderLayout.SOUTH);
	}

	private void selectSI(int type) {
		if (type >= 0 && type < images.length) {
			int id = -1;
			if (images[type] != null) {
				id = images[type].getID();
			}
			SISelecter s = new SISelecter(this, id, siManager);
			s.show();
			if (s.getCloseType() == OKCancelDialog.OK_PERFORMED) {
				SingleImage image = s.getSelectedSI();
				images[type] = image;
			}
		}
	}

	public SingleImage[] getImages() {
		return images;
	}

	public void okPerformed() {
		closeType = OK_PERFORMED;
		dispose();
	}

	public void cancelPerformed() {
		dispose();
	}
}

class BRBodyPanel
	extends ScrollableBRPropPanel {
	private BuildingRes br;
	private BodyManager manager;
	private JButton paintRectButton;
	private JButton modifyRectButton;
	private JButton setOriginButton;
	private JPanel backPanel;

	public BRBodyPanel(JDialog owner) {
		super(owner);
		init();
	}

	private void init() {
		this.br = null;
		manager = new BodyManager(this);

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

		setOriginButton = new JButton("设置基准点");
		setOriginButton.addActionListener(new ActionListener() {
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
		buttonPanel.add(paintRectButton, c);

		c.gridx = 1;
		buttonPanel.add(modifyRectButton, c);

		c.gridx = 2;
		buttonPanel.add(setOriginButton, c);

		c.gridx = 3;
		c.weightx = 1;
		buttonPanel.add(new JPanel(), c);

		backPanel = new JPanel();
		backPanel.setLayout(new BorderLayout());
		backPanel.add(super.getBackPanel(), BorderLayout.CENTER);
		backPanel.add(buttonPanel, BorderLayout.NORTH);
	}

	public void setBR(BuildingRes br) {
		this.br = br;
		copyBRProp();
	}

	private void copyBRProp() {
		checkBR();
		if (br == null) {
			return;
		}

		IntPair origin = getImageOrigin();
		IntPair brOrigin = br.getOrigin();
		brOrigin.x += origin.x;
		brOrigin.y += origin.y;

		ArrayList rects = br.getBodyRects();
		if (rects != null) {
			for (int i = 0; i < rects.size(); ++i) {
				Rect rect = (Rect) (rects.get(i));
				rect.x += origin.x;
				rect.y += origin.y;
			}
		}

		manager.setBodyCenter(brOrigin);
		manager.setBodyRects(rects, 
							 0, 0, 
							 this.getBasicWidth(), this.getBasicHeight());
	}

	public void updateBRProp() {
		if (br == null) {
			return;
		}

		IntPair origin = getImageOrigin();
		IntPair brOrigin = manager.getBodyCenter();
		brOrigin.x -= origin.x;
		brOrigin.y -= origin.y;

		ArrayList rects = manager.getBodyRects(0, 0, 
											   this.getBasicWidth(), this.getBasicHeight());
		if (rects != null) {
			for (int i = 0; i < rects.size(); ++i) {
				Rect rect = (Rect) (rects.get(i));
				rect.x -= origin.x;
				rect.y -= origin.y;
			}
		}

		br.setOrigin(brOrigin);
		br.setBodyRects(rects);
	}

	public void dispose() {
		manager.stop();
	}

	public JPanel getBackPanel() {
		return backPanel;
	}

	public String getName() {
		return "设置攻击范围和中心点";
	}

	private void checkBR() {
		paintRectButton.setEnabled(br != null);
		modifyRectButton.setEnabled(br != null);
		setOriginButton.setEnabled(br != null);
	}

	public boolean isFlip() {
		return false;
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (!isVisible() || br == null) {
			return;
		}

		IntPair origin = getImageOrigin();
		br.paintLeftTop(g, origin.x - br.getWidth() / 2, origin.y - br.getHeight() / 2, isFlip());

		manager.paintRects(g);
		manager.paintPoint(g);
	}
}

class BRExplorePanel
	extends ScrollableBRPropPanel {
	private final static int NORMAL_DEAD = 0;
	private final static int NORMAL_HALF = 1;
	private final static int HALF_DEAD = 2;
	private final static String[] DESCS = {
										  "完好到全毁",
										  "完好到半毁",
										  "半毁到全毁"
	};

	private ExploreBR br;
	private JPanel backPanel;
	private ArrayList explores;
	private PainterTree tree;
	private ExploreManager manager;
	private ValueChooser stateChooser;
	private int interval;
	private NumberSpinner intervalSpinner;
	private PlayThread playThread;
	private JButton playButton;
	private JButton stopButton;

	private class PlayThread
		extends Thread {
		public void run() {
			intervalSpinner.setEnabled(false);
			playButton.setEnabled(false);
			stopButton.setEnabled(true);
			int deadInterval = intervalSpinner.getIntValue();
			interval = 0;
			try {
				while (!interrupted()) {
					Sprite[] sprites = manager.getSprites();
					boolean exploreEnd = true;
					for (int i = 0; i < sprites.length; ++i) {
						Sprite sprite = sprites[i];
						if (sprite instanceof Explore) {
							Explore explore = (Explore) sprite;
							int frameIndex = -1;
							int exploreBegin = explore.getBeginIndex() * 1000 / 64;
							if (exploreBegin <= interval) {
								frameIndex = explore.getAnim().getFrameIndex(
									explore.getStartFrame(), interval - exploreBegin);
								if (frameIndex >= 0 &&
									frameIndex >= explore.getStartFrame() &&
									frameIndex <= explore.getEndFrame()) {
									
									exploreEnd = false;
									break;
								}
							}
						}
					}
					if(!exploreEnd) {
						deadInterval = Math.max(deadInterval, interval);
					}
					if (exploreEnd) {
						if (interval > deadInterval + 1000) {
							repaint();
							break;
						}
					}
					sleep(20);
					interval += 20;
					repaint();
				}
			}
			catch (Exception e) {
			}
			interval = 0;
			intervalSpinner.setEnabled(true);
			playButton.setEnabled(true);
			stopButton.setEnabled(false);
			repaint();
		}

//		public void xstop() {
//			try {
//				this.interrupt();
//				interval = 0;
//				intervalSpinner.setEnabled(true);
//			}
//			catch(Exception e) {
//				
//			}
//		}
	}

	public BRExplorePanel(JDialog owner, ARManager arManager) {
		super(owner);
		init(arManager);
	}

	private void init(ARManager arManager) {
		this.br = null;

		stateChooser = new ValueChooser(NORMAL_DEAD, new int[] {NORMAL_DEAD}
										, DESCS);
		stateChooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stateChanged();
			}
		});

		MouseInfo mouseInfo = new MouseInfo();

		tree = new PainterTree("爆炸",
							   null, //AnimPainterGroup.getGroups(arManager.getGroups()),
							   AnimPainter.getPainters(arManager.getAnims(XUtil.getDefPropInt("exploreGroupID"))),
							   mouseInfo);

		manager = new ExploreManager(this, mouseInfo) {
			protected void showProperties(Sprite sprite) {
				if (sprite instanceof Explore) {
					Explore explore = (Explore) sprite;
					ExplorePropSetter setter = new ExplorePropSetter(owner, explore);
					setter.show();
					if (setter.getCloseType() == OKCancelDialog.OK_PERFORMED) {
						explore.setStartFrame(setter.getStartFrame());
						explore.setEndFrame(setter.getEndFrame());
						explore.setBeginIndex(setter.getBeginIndex());
					}
				}
			}

			public void paintSprite(Sprite sprite, Graphics g) {
				if (! (sprite instanceof Explore)) {
					sprite.paint(g);
				}
				else {
					Explore explore = (Explore) sprite;
					int frameIndex = -1;
					int exploreBegin = explore.getBeginIndex() * 1000 / 64;
					if (exploreBegin <= interval) {
						frameIndex = explore.getAnim().getFrameIndex(
							explore.getStartFrame(), interval - exploreBegin);
					}
					if (frameIndex >= 0 &&
						frameIndex >= explore.getStartFrame() &&
						frameIndex <= explore.getEndFrame()) {
						explore.paint(frameIndex, g);
					}
					else {
						if (explore.isSelected()) {
							explore.paintBorder(g);
						}
					}
				}
			}
		};

		interval = 0;
		intervalSpinner = new NumberSpinner();
		intervalSpinner.setPreferredSize(new Dimension(100, 28));

		playButton = new JButton("播放");
		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				play();
			}
		});

		stopButton = new JButton("停止");
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stop();
			}
		});

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;

		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(2, 10, 2, 10);
		c.weightx = 0;
		c.weighty = 0;
		topPanel.add(new JLabel("选择状态："), c);

		c.gridx = 1;
		topPanel.add(stateChooser, c);

		c.gridx = 2;
		c.weightx = 1;
		topPanel.add(new JPanel(), c);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		buttonPanel.add(new JLabel("设置爆炸时间(毫秒):"), c);

		c.gridx = 1;
		buttonPanel.add(intervalSpinner, c);

		c.gridx = 2;
		c.insets = new Insets(2, 30, 2, 10);
		buttonPanel.add(playButton, c);

		c.gridx = 3;
		c.insets = new Insets(2, 10, 2, 10);
		buttonPanel.add(stopButton, c);

		c.gridx = 4;
		c.weightx = 1;
		buttonPanel.add(new JPanel(), c);

		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout());
		rightPanel.add(topPanel, BorderLayout.NORTH);
		rightPanel.add(super.getBackPanel(), BorderLayout.CENTER);
		rightPanel.add(buttonPanel, BorderLayout.SOUTH);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
											  tree.getScrollPane(),
											  rightPanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(200);

		backPanel = new JPanel();
		backPanel.setLayout(new BorderLayout());
		backPanel.add(splitPane, BorderLayout.CENTER);
	}

	public String getName() {
		return "设置爆炸效果";
	}

	public JPanel getBackPanel() {
		return backPanel;
	}

	public void setBR(BuildingRes br) {
		if (! (br instanceof ExploreBR)) {
			return;
		}
		this.br = (ExploreBR) br;
		copyBRProp();
	}

	private void copyBRProp() {
		checkBR();
		if (br == null) {
			return;
		}

		if (br instanceof HouseBR) {
			stateChooser.resetDefValues(new int[] {NORMAL_HALF, HALF_DEAD}
										, DESCS);
			stateChooser.setValue(NORMAL_HALF);
		}
		else {
			stateChooser.resetDefValues(new int[] {NORMAL_DEAD}
										, DESCS);
			stateChooser.setValue(NORMAL_DEAD);
		}

		intervalSpinner.setIntValue(br.getInterval());

		IntPair origin = getImageOrigin();
		manager.reset();
		ArrayList tmp = new ArrayList();
		ArrayList explores = br.getExplores();
		for (int i = 0; i < explores.size(); ++i) {
			Explore explore = (Explore) (explores.get(i));
			explore.setPosition(explore.getX() + origin.x,
								explore.getY() + origin.y);
			tmp.add(explore);
		}
		manager.addExplores(tmp);

		stop();
	}

	public void updateBRProp() {
		if (br == null) {
			return;
		}

		br.setInterval(intervalSpinner.getIntValue());

		IntPair origin = getImageOrigin();
		ArrayList explores = manager.getExplores();
		for (int i = 0; i < explores.size(); ++i) {
			Explore explore = (Explore) (explores.get(i));
			explore.setPosition(explore.getX() - origin.x, explore.getY() - origin.y);
		}
		br.setExplores(explores);
	}

	private void checkBR() {
		intervalSpinner.setEnabled(br != null);
		playButton.setEnabled(br != null);
		stopButton.setEnabled(false);
	}

	public void dispose() {
		stop();
	}

	private void stateChanged() {
		stop();
	}

	public void play() {
		if (playThread != null) {
			if (playThread.isAlive()) {
				return;
			}
		}
		playThread = new PlayThread();
		playThread.start();
		repaint();
	}

	public void stop() {
		if (playThread != null) {
			if (playThread.isAlive()) {
				playThread.interrupt();
			}
		}
		repaint();
	}

	protected boolean isFlip() {
		return false;
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (!isVisible() || br == null) {
			return;
		}
		super.paintComponent(g);
		if (!isVisible() || br == null) {
			return;
		}

		IntPair origin = getImageOrigin();
		int exploreState = ExploreBR.NORMAL;
		int deadInterval = intervalSpinner.getIntValue();
		int state = stateChooser.getValue();
		if (interval < deadInterval) {
			if (state == HALF_DEAD) {
				exploreState = ExploreBR.HALF_DEAD;
			}
			else {
				exploreState = ExploreBR.NORMAL;
			}
		}
		else {
			if (state == NORMAL_HALF) {
				exploreState = ExploreBR.HALF_DEAD;
			}
			else {
				exploreState = ExploreBR.DEAD;
			}
		}
		br.paintLeftTop(exploreState, g, origin.x - br.getWidth() / 2, origin.y - br.getHeight() / 2, isFlip());

		manager.paintSprites(g);
		manager.paintOthers(g);
	}
}
