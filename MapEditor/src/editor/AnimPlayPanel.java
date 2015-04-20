package editor;

import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class AnimPlayPanel
	extends ScrollablePanel {
	public static void main(String[] args) {
		JFrame test = new JFrame();
		final AnimPlayPanel p = new AnimPlayPanel();
		test.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				p.stop();
				System.exit(0);
			}
		});
		test.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		test.setSize(new Dimension(600, 500));
		test.setLocationRelativeTo(null);
		Container cp = test.getContentPane();
		cp.add(p.getBackPanel(), BorderLayout.CENTER);
		try {
			Animation a = new Animation(0, "test");
			p.setAnim(a);
			p.play();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		test.show();
	}

	private PlayThread playThread;
	private JPanel backPanel;
	private JButton playButton, stopButton;
	private JSlider slider;
	private NumberSpinner startSpinner, endSpinner, currentSpinner;
	private Animation anim;
	private int startFrame, endFrame, currentFrame;
	private IntPair animOrigin, animOriginGrid;
	private int interval;

	private class PlayThread
		extends Thread {
		private final static int INTERVAL = 20;

		private class Suspender {
			private boolean suspend = true;

			public boolean isSuspend() {
				return suspend;
			}

			public synchronized void setSuspend(boolean suspend) {
				this.suspend = suspend;
				this.notifyAll();
			}

			public synchronized void waitForResume() throws InterruptedException {
				while (suspend) {
					this.wait();
				}
			}
		}

		private Suspender suspender;

		public PlayThread() {
			suspender = new Suspender();
		}

		public boolean isPlaying() {
			return!suspender.isSuspend();
		}

		public void pauseThread() {
			suspender.setSuspend(true);
		}

		public void resumeThread() {
			suspender.setSuspend(false);
		}

		public void startThread() {
			setCurrentFrame(startFrame);
			suspender.setSuspend(false);
			if (!this.isAlive()) {
				this.start();
			}
		}

		public void stopThread() {
			setCurrentFrame(startFrame);
			this.interrupt();
		}

		public void run() {
			try {
				while (!interrupted()) {
					suspender.waitForResume();
					if (anim == null) {
						break;
					}
					else {
						int newFrame = anim.getFrameIndex(currentFrame, interval);
						boolean changeFrame = (currentFrame != newFrame);
						if (newFrame < startFrame) {
							newFrame = startFrame;
						} 
						else if(newFrame > endFrame) {
							if(shouldReturn()) {
							    newFrame = startFrame;
							}
							else {
							    changeFrame = false;
							}
						}
						if (!changeFrame) {
							repaint();
						}
						else {
							setCurrentFrame(newFrame);
						}
						sleep(INTERVAL);
						interval += INTERVAL;
					}
				}
			}
			catch (Exception e) {
			}
		}
	}
	
	public boolean shouldReturn() {
		return true;
	}

	public AnimPlayPanel() {
		super(XUtil.SCREEN_W, XUtil.SCREEN_H);
		init(false);
	}

	public AnimPlayPanel(boolean showRange) {
		super(XUtil.SCREEN_W, XUtil.SCREEN_H);
		init(showRange);
	}

	private void init(boolean showRange) {
		anim = null;
		playThread = null;
		startFrame = endFrame = currentFrame = 0;
		interval = 0;
		computeAnimOrigin();

		backPanel = new JPanel();
		JPanel buttonPanel = new JPanel();
		JPanel bp1 = new JPanel();
		JPanel bp2 = new JPanel();

		playButton = new JButton("²¥·Å");
		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (playThread != null && playThread.isPlaying()) {
					pause();
				}
				else {
					play();
				}
			}
		});

		stopButton = new JButton("Í£Ö¹");
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stop();
			}
		});

		slider = new JSlider();
		slider.setMajorTickSpacing(1);
		slider.setPaintLabels(true);
		slider.setPaintTicks(true);
		slider.setSnapToTicks(true);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				sliderChanged();
			}
		});

		startSpinner = new NumberSpinner();
		startSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				startSpinnerChanged();
			}
		});

		endSpinner = new NumberSpinner();
		endSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				endSpinnerChanged();
			}
		});

		currentSpinner = new NumberSpinner();
		currentSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				currentSpinnerChanged();
			}
		});

		bp1.setLayout(new GridBagLayout());
		bp2.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;

		c.gridy = 0;
		c.gridx = 0;
		c.weightx = 0;
		c.insets = new Insets(2, 2, 1, 3);
		bp1.add(playButton, c);

		c.gridx = 1;
		bp1.add(stopButton, c);

		c.gridx = 2;
		c.weightx = 1;
		bp1.add(slider, c);

		c.gridy = 0;
		c.gridx = 0;
		c.weightx = 0;
		bp2.add(new JLabel("²¥·Å·¶Î§£º"));

		c.gridx = 1;
		c.weightx = 1;
		bp2.add(startSpinner, c);

		c.gridx = 2;
		c.weightx = 0;
		bp2.add(new JLabel("  µ½  "));

		c.gridx = 3;
		c.weightx = 1;
		bp2.add(endSpinner, c);

		c.gridx = 4;
		c.weightx = 0;
		bp2.add(new JLabel("    µ±Ç°Ö¡£º"));

		c.gridx = 5;
		c.weightx = 1;
		bp2.add(currentSpinner, c);

		buttonPanel.setLayout(new GridBagLayout());
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 0, 0);
		c.weighty = 1;
		c.weightx = 1;
		buttonPanel.add(bp1, c);

		c.gridy = 1;
		c.weighty = 0;
		if (showRange) {
			buttonPanel.add(bp2, c);
		}
		backPanel.setLayout(new BorderLayout());
		backPanel.add(super.getBackPanel(), BorderLayout.CENTER);
		backPanel.add(buttonPanel, BorderLayout.SOUTH);

		resetButton();
	}

	private void resetButton() {
		playButton.setText("²¥·Å");
		playButton.setEnabled(false);
		stopButton.setText("Í£Ö¹");
		stopButton.setEnabled(false);
		slider.setValue(0);
		setSliderEnabled(false);
		setSliderRange(0, 0);
	}

	private void setSliderEnabled(boolean b) {
		slider.setEnabled(b);
		startSpinner.setEnabled(b);
		endSpinner.setEnabled(b);
		currentSpinner.setEnabled(b);
	}

	private void setSliderRange(int min, int max) {
		slider.setMinimum(min);
		slider.setMaximum(max);
		startSpinner.setMin(min);
		startSpinner.setMax(max);
		endSpinner.setMin(min);
		endSpinner.setMax(max);
		currentSpinner.setMin(min);
		currentSpinner.setMax(max);
	}

	public JPanel getBackPanel() {
		return backPanel;
	}

	public Animation getAnim() {
		return anim;
	}

	public void setAnim(Animation anim) {
		if (anim != null) {
			try {
				anim.load();
			}
			catch (Exception e) {
				anim = null;
			}
		}
		stop();
		resetButton();
		this.anim = anim;
		resetRange();
		Rectangle r;
		if (anim != null) {
			playButton.setEnabled(true);
			setSliderEnabled(true);
			setSliderRange(0, anim.getFrameCount() - 1);
			int labelLength = Math.max(1, (anim.getFrameCount() / 10));
			Dictionary sliderLabelTable = slider.getLabelTable();
			for (int i = slider.getMinimum(); i < slider.getMaximum(); ++i) {
				if (i == slider.getMaximum() || (i % labelLength) == 0) {
				}
				else {
					sliderLabelTable.put(new Integer(i), new JLabel());
				}
			}
			slider.setMajorTickSpacing(1);
			reset(anim.getFullWidth() + 200, anim.getHeight() + 200);
			computeAnimOrigin();
			r = new Rectangle(anim.getLeft(animOrigin.x),
							  anim.getTop(animOrigin.y),
							  anim.getFullWidth(),
							  anim.getHeight());
		}
		else {
			reset(XUtil.SCREEN_W, XUtil.SCREEN_H);
			r = new Rectangle(0, 0, XUtil.SCREEN_W, XUtil.SCREEN_H);
		}
		scrollRectToVisible(r);
		resizeScroll();
	}

	public IntPair getRange() {
		return new IntPair(startFrame, endFrame);
	}

	public int getStartFrame() {
		return startFrame;
	}

	public int getEndFrame() {
		return endFrame;
	}

	protected int getInterval() {
		return interval;
	}

	protected boolean isFlip() {
		return false;
	}

	public void resetRange() {
		if (anim == null) {
			setRange(0, 0);
		}
		else {
			setRange(0, anim.getFrameCount() - 1);
		}
	}

	public void setRange(IntPair range) {
		setRange(range.x, range.y);
	}

	public void setRange(int start, int end) {
		setStartFrame(start);
		setEndFrame(end);
	}

	private void setStartFrame(int start) {
		if (anim == null) {
			startFrame = 0;
		}
		else {
			startFrame = Math.min(anim.getFrameCount() - 1, Math.max(0, start));
		}
		startSpinner.setIntValue(startFrame);
	}

	private void setEndFrame(int end) {
		if (anim == null) {
			endFrame = 0;
		}
		else {
			endFrame = Math.min(anim.getFrameCount() - 1, Math.max(0, end));
		}
		endSpinner.setIntValue(endFrame);
	}

	private void startSpinnerChanged() {
		int value = startSpinner.getIntValue();
		if (value != startFrame) {
			setStartFrame(value);
		}
	}

	private void endSpinnerChanged() {
		int value = endSpinner.getIntValue();
		if (value != endFrame) {
			setEndFrame(value);
		}
	}

	public int getCurrentFrame() {
		if (anim == null) {
			return 0;
		}
		return currentFrame;
	}

	public void setCurrentFrame(int currentFrame) {
		if (anim == null) {
			this.currentFrame = 0;
		}
		else {
			interval = 0;
			this.currentFrame = currentFrame;
		}
		slider.setValue(currentFrame);
		currentSpinner.setIntValue(currentFrame);
		this.repaint();
	}

	private void sliderChanged() {
		if (slider.getValue() != currentFrame) {
			setCurrentFrame(slider.getValue());
		}
	}

	private void currentSpinnerChanged() {
		int value = currentSpinner.getIntValue();
		if (value != currentFrame) {
			setCurrentFrame(value);
		}
	}

	protected void sizeChanged() {
		super.sizeChanged();
		computeAnimOrigin();
	}

	private void computeAnimOrigin() {
		int originX = getBasicWidth() / 2;
		int originY = getBasicHeight() / 2;
		animOriginGrid = Grid.getGridXY(originX, originY);
		animOrigin = Grid.getScreenXY(animOriginGrid.x, animOriginGrid.y);
	}

	public IntPair getAnimOrigin() {
		return animOrigin;
	}

	public IntPair getAnimOriginGrid() {
		return animOriginGrid;
	}

	public IntPair getAnimOrigin(int frameIndex) {
		return getAnimOrigin();
	}

	protected void paintAnim(Graphics g) {
		paintAnim(currentFrame, g);
	}

	protected void paintAnim(int frameIndex, Graphics g) {
		if (anim != null) {
			IntPair animOrigin = getAnimOrigin(frameIndex);
			anim.paintOrigin(frameIndex, g, animOrigin.x, animOrigin.y, isFlip());
		}
	}

	protected void paintAnimOrigin(Graphics g) {
		paintAnimOrigin(g, false);
	}

	protected void paintAnimOrigin(Graphics g, boolean paintGrid) {
		if (anim == null) {
			return;
		}
		IntPair animOrigin = getAnimOrigin();

		if (paintGrid) {
			Graphics2D g2 = (Graphics2D) g;
			Composite oldComposite = g2.getComposite();
			g2.setComposite(DEF_ALPHA_COMPOSITE);
			paintAnimOriginGrid(g2);
			g2.setComposite(oldComposite);
		}
		Color oldColor = g.getColor();
		g.setColor(new Color(152, 49, 222));
		g.fillOval(animOrigin.x - 2, animOrigin.y - 2, 4, 4);
		g.setColor(oldColor);
	}

	private void paintAnimOriginGrid(Graphics g) {
		Color oldColor = g.getColor();
		g.setColor(Color.BLUE);
		paintGrid(getAnimOriginGrid(), g);
		g.setColor(oldColor);
	}

	protected void paintGrid(IntPair grid, Graphics g) {
		IntPair screenXY = Grid.getScreenXY(grid.x, grid.y);
//		g.fillPolygon(new int[] {
//					  screenXY.x,
//					  screenXY.x - (Grid.W >> 1),
//					  screenXY.x,
//					  screenXY.x + (Grid.W >> 1)}
//					  ,
//					  new int[] {
//					  screenXY.y - (Grid.H >> 1),
//					  screenXY.y,
//					  screenXY.y + (Grid.H >> 1),
//					  screenXY.y}
//					  ,
//					  4);
		g.fillRect(screenXY.x - (Grid.W >> 1),
				   screenXY.y - (Grid.H >> 1),
				   Grid.W, Grid.H);
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (!isVisible()) {
			return;
		}
	}

	public void play() {
		if (playThread != null) {
			if (playThread.isAlive()) {
				playThread.resumeThread();
			}
			else if (!playThread.isInterrupted()) {
				playThread.startThread();
			}
			else {
				playThread = new PlayThread();
				playThread.startThread();
			}
		}
		else {
			playThread = new PlayThread();
			playThread.startThread();
		}
		playButton.setText("ÔÝÍ£");
		stopButton.setEnabled(true);
		setSliderEnabled(false);
	}

	public void stop() {
		if (playThread != null) {
			playThread.stopThread();
			playThread = null;
		}
		else {
			setCurrentFrame(startFrame);
		}
		playButton.setText("²¥·Å");
		stopButton.setEnabled(false);
		setSliderEnabled(true);
	}

	public void pause() {
		if (playThread != null) {
			playThread.pauseThread();
		}
		playButton.setText("²¥·Å");
		setSliderEnabled(true);
	}
}