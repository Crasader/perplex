package editor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

interface MapPointChangeListener {
	public void pointChanged();
}

class MapPointPanel
	extends MapPanel {
	private int radius;
	private int pointX;
	private int pointY;
	private MapPointChangeListener pointChangeListener;
	private Thread thread;

	public MapPointPanel(JDialog owner, MainFrame mainFrame,
						 int pointX, int pointY) {
		super(owner, mainFrame);
		init(pointX, pointY);
	}

	private void init(int pointX, int pointY) {
		setMustFocus(false);
		this.pointX = pointX;
		this.pointY = pointY;

		setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				selfMousePressed(e);
			}
		});
		
		thread = new Thread() {
			public void run() {
				while(!interrupted()) {
					if(radius == 4) {
						radius = 6;
					}
					else {
						radius = 4;
					}
					repaint();
					try {
					    sleep(500);
					}
					catch(Exception e) {
						break;
					}
				}
			}
		};
		thread.start();
	}
	
	public void stop() {
		if(thread.isAlive()) {
			try {
		        thread.interrupt();
			}
			catch(Exception e) {
			}
		}
	}

	public int getPointX() {
		return pointX;
	}

	public int getPointY() {
		return pointY;
	}

	public void setPointX(int pointX) {
		this.pointX = pointX;
//		pointChanged();
		repaint();
	}

	public void setPointY(int pointY) {
		this.pointY = pointY;
//		pointChanged();
		repaint();
	}

	public void setPointChangeListener(MapPointChangeListener pointChangeListener) {
		this.pointChangeListener = pointChangeListener;
	}

	private void pointChanged() {
		if (pointChangeListener != null) {
			pointChangeListener.pointChanged();
		}
	}

	private void selfMousePressed(MouseEvent e) {
		if(isMouseMovingViewport()) return;
		if (e.getButton() == XUtil.LEFT_BUTTON) {
			pointX = this.getMouseX(e);
			pointY = this.getMouseY(e);
			pointChanged();
			repaint();
		}
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		paintFloors(g);
		paintBuildings(g);
		paintUnits(g);
		Color oldColor = g.getColor();
		g.setColor(Color.RED);
		g.fillOval(pointX - radius, pointY - radius, radius * 2, radius * 2);
		g.setColor(oldColor);
	}
}

class MapPointSetter
	extends OKCancelDialog {
	private MainFrame mainFrame;
	private MapPointPanel pointPanel;
	private NumberSpinner pointXSpinner;
	private NumberSpinner pointYSpinner;

	public MapPointSetter(JDialog owner, MainFrame mainFrame,
						 int pointX, int pointY) {
		super(owner);
		this.mainFrame = mainFrame;
		setTitle("设置地图点");

		pointPanel = new MapPointPanel(this, mainFrame, pointX, pointY);
		pointPanel.scrollRectToVisible(
			new Rectangle(pointX - 100, pointY - 100, 200, 200));
		pointPanel.setPointChangeListener(new MapPointChangeListener() {
			public void pointChanged() {
				selfPointChanged();
			}
		});

		pointXSpinner = new NumberSpinner();
		pointXSpinner.setIntValue(pointX);
		pointXSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				pointXSpinnerChanged();
			}
		});

		pointYSpinner = new NumberSpinner();
		pointYSpinner.setIntValue(pointY);
		pointYSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				pointYSpinnerChanged();
			}
		});

		JPanel spinnerPanel = new JPanel();
		spinnerPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 1;
		c.insets = new Insets(2, 2, 3, 3);
		spinnerPanel.add(new JLabel("X："), c);
		
		c.gridx = 1;
		c.weightx = 1;
		spinnerPanel.add(pointXSpinner, c);
		
		c.gridx = 2;
		c.weightx = 0;
		spinnerPanel.add(new JLabel("Y："), c);
		
		c.gridx = 3;
		c.weightx = 1;
		spinnerPanel.add(pointYSpinner, c);

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		Container cp = this.getContentPane();
		cp.add(spinnerPanel, BorderLayout.NORTH);
		cp.add(pointPanel.getBackPanel(), BorderLayout.CENTER);
		cp.add(buttonPanel, BorderLayout.SOUTH);
	}

	public int getPointX() {
		return pointPanel.getPointX();
	}

	public int getPointY() {
		return pointPanel.getPointY();
	}

	private void pointXSpinnerChanged() {
		try {
			int value = pointXSpinner.getIntValue();
			if (value != pointPanel.getPointX()) {
				pointPanel.setPointX(value);
			}
		}
		catch (Exception e) {
		}
	}

	private void pointYSpinnerChanged() {
		try {
			int value = pointYSpinner.getIntValue();
			if (value != pointPanel.getPointY()) {
				pointPanel.setPointY(value);
			}
		}
		catch (Exception e) {
		}
	}

	private void selfPointChanged() {
		int x = pointPanel.getPointX();
		int y = pointPanel.getPointY();
		if (x != pointXSpinner.getIntValue()) {
			pointXSpinner.setIntValue(x);
		}
		if (y != pointYSpinner.getIntValue()) {
			pointYSpinner.setIntValue(y);
		}
	}

	public void okPerformed() {
		closeType = OK_PERFORMED;
		dispose();
	}

	public void cancelPerformed() {
		dispose();
	}
	
	public void dispose() {
		pointPanel.stop();
		super.dispose();
	}
}

class MapPointSetPanel
	extends JPanel {
	private JDialog owner;
	private MainFrame mainFrame;
	private NumberSpinner pointXSpinner;
	private NumberSpinner pointYSpinner;
	private JButton setButton;

	public MapPointSetPanel(JDialog owner, MainFrame mainFrame,
						   int pointX, int pointY) {
		super();
		this.owner = owner;
		init(mainFrame, pointX, pointY);
	}

	private void init(MainFrame mainFrame,
					  int pointX, int pointY) {

		this.mainFrame = mainFrame;
		TitledBorder border = BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
			"设置地图点");
		setBorder(border);

		pointXSpinner = new NumberSpinner();
		pointXSpinner.setIntValue(pointX);

		pointYSpinner = new NumberSpinner();
		pointYSpinner.setIntValue(pointY);

		setButton = new JButton("...");
		add(setButton);
		setButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setMapPoint();
			}
		});

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.insets = new Insets(2, 2, 3, 3);

		c.weightx = 0;
		c.gridx = 0;
		c.gridy = 0;
		add(new JLabel("X："), c);
		
		c.weightx = 1;
		c.gridx = 1;
		add(pointXSpinner, c);

		c.weightx = 0;
		c.gridx = 2;
		add(new JLabel("Y："), c);
		
		c.weightx = 1;
		c.gridx = 3;
		add(pointYSpinner, c);

		c.weightx = 0;
		c.gridx = 4;
		add(setButton, c);
	}

	private void setMapPoint() {
		MapPointSetter setter = new MapPointSetter(owner, mainFrame,
													pointXSpinner.getIntValue(),
												    pointYSpinner.getIntValue());
		setter.show();
		if (setter.getCloseType() == OKCancelDialog.OK_PERFORMED) {
			pointXSpinner.setIntValue(setter.getPointX());
			pointYSpinner.setIntValue(setter.getPointY());
		}
	}

	public int getPointX() {
		return pointXSpinner.getIntValue();
	}

	public int getPointY() {
		return pointYSpinner.getIntValue();
	}
}
