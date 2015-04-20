package editor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

interface MapAreaChangeListener {
	public void areaChanged();
}

class MapAreaPanel
	extends MapPanel {
	private int areaLeft;
	private int areaTop;
	private int areaWidth;
	private int areaHeight;
	private int oldX;
	private int oldY;
	private boolean rectangling;
	private MapAreaChangeListener areaChangeListener;

	public MapAreaPanel(JDialog owner, MainFrame mainFrame,
						int areaLeft, int areaTop, int areaHeight, int areaWidth) {
		super(owner, mainFrame);
		init(areaLeft, areaTop, areaWidth, areaHeight);
	}

	private void init(int areaLeft, int areaTop, int areaHeight, int areaWidth) {
		setMustFocus(false);
		this.areaLeft = areaLeft;
		this.areaTop = areaTop;
		this.areaWidth = areaWidth;
		this.areaHeight = areaHeight;
		this.rectangling = false;
		areaChangeListener = null;

		setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				selfMousePressed(e);
			}

			public void mouseReleased(MouseEvent e) {
				selfMouseReleased(e);
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				selfMouseDragged(e);
			}
		});
	}

	public int getAreaLeft() {
		return areaLeft;
	}

	public int getAreaTop() {
		return areaTop;
	}

	public int getAreaWidth() {
		return areaWidth;
	}

	public int getAreaHeight() {
		return areaHeight;
	}

	public void setAreaLeft(int areaLeft) {
		this.areaLeft = areaLeft;
//		areaChanged();
		repaint();
	}

	public void setAreaTop(int areaTop) {
		this.areaTop = areaTop;
//		areaChanged();
		repaint();
	}

	public void setAreaWidth(int areaWidth) {
		this.areaWidth = areaWidth;
//		areaChanged();
		repaint();
	}

	public void setAreaHeight(int areaHeight) {
		this.areaHeight = areaHeight;
//		areaChanged();
		repaint();
	}

	public void setAreaChangeListener(MapAreaChangeListener areaChangeListener) {
		this.areaChangeListener = areaChangeListener;
	}

	private void areaChanged() {
		if (areaChangeListener != null) {
			areaChangeListener.areaChanged();
		}
	}

	private void selfMousePressed(MouseEvent e) {
		if(isMouseMovingViewport()) return;
		if (e.getButton() == XUtil.LEFT_BUTTON) {
			rectangling = true;
			oldX = this.getMouseX(e);
			oldY = this.getMouseY(e);
			areaLeft = oldX;
			areaTop = oldY;
			areaWidth = 0;
			areaHeight = 0;
			areaChanged();
			repaint();
		}
	}

	private void selfMouseReleased(MouseEvent e) {
		if(isMouseMovingViewport()) return;
		areaChanged();
		rectangling = false;
		repaint();
	}

	private void selfMouseDragged(MouseEvent e) {
		if(isMouseMovingViewport()) return;
		if (rectangling) {
			int mouseX = this.getMouseX(e);
			int mouseY = this.getMouseY(e);
			areaLeft = Math.min(mouseX, oldX);
			areaTop = Math.min(mouseY, oldY);
			areaWidth = Math.abs(mouseX - oldX);
			areaHeight = Math.abs(mouseY - oldY);
			areaChanged();
			repaint();
		}
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		paintFloors(g);
		paintBuildings(g);
		paintUnits(g);
		Color oldColor = g.getColor();
		g.setColor(Color.BLACK);
		g.drawRect(areaLeft, areaTop, areaWidth, areaHeight);
		g.setColor(oldColor);
	}
}

class MapAreaSetter
	extends OKCancelDialog {
	private MainFrame mainFrame;
	private MapAreaPanel areaPanel;
	private NumberSpinner areaLeftSpinner;
	private NumberSpinner areaTopSpinner;
	private NumberSpinner areaWidthSpinner;
	private NumberSpinner areaHeightSpinner;

	public MapAreaSetter(JDialog owner, MainFrame mainFrame,
						 int areaLeft, int areaTop, int areaWidth, int areaHeight) {
		super(owner);
		this.mainFrame = mainFrame;
		setTitle("选择一个矩形区域");

		areaPanel = new MapAreaPanel(this, mainFrame, areaLeft, areaTop, areaWidth, areaHeight);
		areaPanel.scrollRectToVisible(
			new Rectangle(areaLeft - 25, areaTop - 25,
						  areaWidth + 50, areaHeight + 50));
		areaPanel.setAreaChangeListener(new MapAreaChangeListener() {
			public void areaChanged() {
				selfAreaChanged();
			}
		});

		areaLeftSpinner = new NumberSpinner();
		areaLeftSpinner.setIntValue(areaLeft);
		areaLeftSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				areaLeftSpinnerChanged();
			}
		});

		areaTopSpinner = new NumberSpinner();
		areaTopSpinner.setIntValue(areaTop);
		areaTopSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				areaTopSpinnerChanged();
			}
		});

		areaWidthSpinner = new NumberSpinner();
		areaWidthSpinner.setIntValue(areaWidth);
		areaWidthSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				areaWidthSpinnerChanged();
			}
		});

		areaHeightSpinner = new NumberSpinner();
		areaHeightSpinner.setIntValue(areaHeight);
		areaHeightSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				areaHeightSpinnerChanged();
			}
		});

		JPanel spinnerPanel = new JPanel();
		spinnerPanel.setLayout(new GridLayout(2, 4, 5, 0));
		spinnerPanel.add(new JLabel("Left"));
		spinnerPanel.add(new JLabel("Top"));
		spinnerPanel.add(new JLabel("Width"));
		spinnerPanel.add(new JLabel("Height"));
		spinnerPanel.add(areaLeftSpinner);
		spinnerPanel.add(areaTopSpinner);
		spinnerPanel.add(areaWidthSpinner);
		spinnerPanel.add(areaHeightSpinner);

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		Container cp = this.getContentPane();
		cp.add(spinnerPanel, BorderLayout.NORTH);
		cp.add(areaPanel.getBackPanel(), BorderLayout.CENTER);
		cp.add(buttonPanel, BorderLayout.SOUTH);
	}

	public int getAreaLeft() {
		return areaPanel.getAreaLeft();
	}

	public int getAreaTop() {
		return areaPanel.getAreaTop();
	}

	public int getAreaWidth() {
		return areaPanel.getAreaWidth();
	}

	public int getAreaHeight() {
		return areaPanel.getAreaHeight();
	}

	private void areaLeftSpinnerChanged() {
		try {
			int value = areaLeftSpinner.getIntValue();
			if (value != areaPanel.getAreaLeft()) {
				areaPanel.setAreaLeft(value);
			}
		}
		catch (Exception e) {
		}
	}

	private void areaTopSpinnerChanged() {
		try {
			int value = areaTopSpinner.getIntValue();
			if (value != areaPanel.getAreaTop()) {
				areaPanel.setAreaTop(value);
			}
		}
		catch (Exception e) {
		}
	}

	private void areaWidthSpinnerChanged() {
		try {
			int value = areaWidthSpinner.getIntValue();
			if (value != areaPanel.getAreaWidth()) {
				areaPanel.setAreaWidth(value);
			}
		}
		catch (Exception e) {
		}
	}

	private void areaHeightSpinnerChanged() {
		try {
			int value = areaHeightSpinner.getIntValue();
			if (value != areaPanel.getAreaHeight()) {
				areaPanel.setAreaHeight(value);
			}
		}
		catch (Exception e) {
		}
	}

	private void selfAreaChanged() {
		int left = areaPanel.getAreaLeft();
		int top = areaPanel.getAreaTop();
		int width = areaPanel.getAreaWidth();
		int height = areaPanel.getAreaHeight();
		if (left != areaLeftSpinner.getIntValue()) {
			areaLeftSpinner.setIntValue(left);
		}
		if (top != areaTopSpinner.getIntValue()) {
			areaTopSpinner.setIntValue(top);
		}
		if (width != areaWidthSpinner.getIntValue()) {
			areaWidthSpinner.setIntValue(width);
		}
		if (height != areaHeightSpinner.getIntValue()) {
			areaHeightSpinner.setIntValue(height);
		}
	}

	public void okPerformed() {
		closeType = OK_PERFORMED;
		dispose();
	}

	public void cancelPerformed() {
		dispose();
	}
}

class MapAreaSetPanel
	extends JPanel {
	private JDialog owner;
	private MainFrame mainFrame;
	private NumberSpinner areaLeftSpinner;
	private NumberSpinner areaTopSpinner;
	private NumberSpinner areaWidthSpinner;
	private NumberSpinner areaHeightSpinner;
	private JButton setButton;

	public MapAreaSetPanel(JDialog owner, MainFrame mainFrame,
						   int areaLeft, int areaTop, int areaWidth, int areaHeight) {
		super();
		this.owner = owner;
		init(mainFrame, areaLeft, areaTop, areaWidth, areaHeight);
	}

	private void init(MainFrame mainFrame,
					  int areaLeft, int areaTop, int areaWidth, int areaHeight) {

		this.mainFrame = mainFrame;
		TitledBorder border = BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
			"设置地图范围");
		setBorder(border);

		areaLeftSpinner = new NumberSpinner();
		areaLeftSpinner.setIntValue(areaLeft);

		areaTopSpinner = new NumberSpinner();
		areaTopSpinner.setIntValue(areaTop);

		areaWidthSpinner = new NumberSpinner();
		areaWidthSpinner.setIntValue(areaWidth);

		areaHeightSpinner = new NumberSpinner();
		areaHeightSpinner.setIntValue(areaHeight);

		setButton = new JButton("...");
		add(setButton);
		setButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setMapArea();
			}
		});

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(2, 2, 3, 3);

		c.gridx = 0;
		c.gridy = 0;
		add(new JLabel("Left"), c);
		c.gridy = 1;
		add(areaLeftSpinner, c);

		c.gridx = 1;
		c.gridy = 0;
		add(new JLabel("Top"), c);
		c.gridy = 1;
		add(areaTopSpinner, c);

		c.gridx = 2;
		c.gridy = 0;
		add(new JLabel("Width"), c);
		c.gridy = 1;
		add(areaWidthSpinner, c);

		c.gridx = 3;
		c.gridy = 0;
		add(new JLabel("Height"), c);
		c.gridy = 1;
		add(areaHeightSpinner, c);

		c.weightx = 0;
		c.gridx = 4;
		c.gridy = 0;
		add(new JLabel("设置"), c);
		c.gridy = 1;
		add(setButton, c);
	}

	private void setMapArea() {
		MapAreaSetter setter = new MapAreaSetter(owner, mainFrame,
												 areaLeftSpinner.getIntValue(),
												 areaTopSpinner.getIntValue(),
												 areaWidthSpinner.getIntValue(),
												 areaHeightSpinner.getIntValue());
		setter.show();
		if (setter.getCloseType() == OKCancelDialog.OK_PERFORMED) {
			areaLeftSpinner.setIntValue(setter.getAreaLeft());
			areaTopSpinner.setIntValue(setter.getAreaTop());
			areaWidthSpinner.setIntValue(setter.getAreaWidth());
			areaHeightSpinner.setIntValue(setter.getAreaHeight());
		}
	}

	public int getAreaLeft() {
		return areaLeftSpinner.getIntValue();
	}

	public int getAreaTop() {
		return areaTopSpinner.getIntValue();
	}

	public int getAreaWidth() {
		return areaWidthSpinner.getIntValue();
	}

	public int getAreaHeight() {
		return areaHeightSpinner.getIntValue();
	}
}

class UnitInMapAreaSetter
	extends TriggerSetter {
	private UnitChoosePanel unitChoosePanel;
	private MapAreaSetPanel mapAreaSetPanel;

	public UnitInMapAreaSetter(JDialog owner, MainFrame mainFrame, Trigger trigger) {
		super(owner, mainFrame, trigger);
		setTitle("Unit位于某个固定地图范围内");

		unitChoosePanel = new UnitChoosePanel(this, mainFrame);
		unitChoosePanel.setSelectedUnitID(trigger.getTargetID());

		int left = 0;
		int top = 0;
		int width = 0;
		int height = 0;
		int[] data = trigger.getData();
		if (data != null) {
			if (data.length > 0) {
				left = data[0];
			}
			if (data.length > 1) {
				top = data[1];
			}
			if (data.length > 2) {
				width = data[2];
			}
			if (data.length > 3) {
				height = data[3];
			}
		}
		mapAreaSetPanel = new MapAreaSetPanel(this, mainFrame,
											  left, top, width, height);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 10, 0);
		mainPanel.add(unitChoosePanel, c);
		c.gridy = 1;
		mainPanel.add(mapAreaSetPanel, c);

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(mainPanel, BorderLayout.CENTER);
		cp.add(buttonPanel, BorderLayout.SOUTH);
	}

	public void okPerformed() {
		try {
			int unitID = unitChoosePanel.getSelectedUnitID();
			Sprite sprite = mainFrame.getPanels()[MainFrame.LAYER_UNIT].getManager().
							getSprite(unitID);
			if (unitID != UnitManager.PLAYER_ID && sprite == null) {
				throw new Exception("必须选择一个Unit");
			}
			int[] data = new int[] {
						 mapAreaSetPanel.getAreaLeft(),
						 mapAreaSetPanel.getAreaTop(),
						 mapAreaSetPanel.getAreaWidth(),
						 mapAreaSetPanel.getAreaHeight()
			};
			trigger.setTargetID(unitID);
			trigger.setData(data);
			this.closeType = OK_PERFORMED;
			dispose();
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, e, "错误", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void cancelPerformed() {
		dispose();
	}
}
