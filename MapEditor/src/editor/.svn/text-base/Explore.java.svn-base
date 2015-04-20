package editor;

import java.io.*;
import java.util.*;

import java.awt.*;
import javax.swing.*;

public class Explore
	extends AnimSprite
	implements Layerable, Copyable {
	private int startFrame;
	private int endFrame;
	private int beginIndex; //该爆炸开始的时候位于死亡动画的（相对死亡动画开始的那一帧的）第几帧
	private int layer;

	public Explore(Animation anim, int id, int x, int y) {
		super(anim, id, x, y);
		init();
	}

	private void init() {
		startFrame = endFrame = 0;
		beginIndex = 0;
		layer = 50;
	}

	public Copyable copy() {
		return getCopy();
	}

	public Explore getCopy() {
		Explore result = new Explore(this.getAnim(), this.getID(), this.getX(), this.getY());
		result.startFrame = this.startFrame;
		result.endFrame = this.endFrame;
		result.beginIndex = this.beginIndex;
		result.layer = this.layer;
		return result;
	}

	public int compareTo(Object o) {
		if (o != null) {
			if (o instanceof Layerable) {
				Layerable dest = (Layerable) o;
				if (this.getLayer() != dest.getLayer()) {
					return this.getLayer() - dest.getLayer();
				}
			}
		}
		return super.compareTo(o);
	}

	public int getStartFrame() {
		return startFrame;
	}

	public void setStartFrame(int startFrame) {
		this.startFrame = startFrame;
	}

	public int getEndFrame() {
		return endFrame;
	}

	public void setEndFrame(int endFrame) {
		this.endFrame = endFrame;
	}

	public int getBeginIndex() {
		return beginIndex;
	}

	public void setBeginIndex(int beginIndex) {
		this.beginIndex = beginIndex;
	}

	public int getLayer() {
		return layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}

	public String getInfo() {
		String result = getName() +
						"  ID：" + getID() +
						"  层：" + getLayer() +
						"  从" + getBeginIndex() + "开始，播放" + getStartFrame() + "~" + getEndFrame() + "帧";
		return result;
	}

	public void saveMobileData(DataOutputStream out) throws Exception {
		SL.writeInt(getAnim().getID(), out);
		BasicSprite.saveMobileData(this, out);
		SL.writeInt(startFrame, out);
		SL.writeInt(endFrame, out);
		SL.writeInt(beginIndex, out);
	}

	public void save(DataOutputStream out) throws Exception {
		out.writeInt(getAnim().getID());
		BasicSprite.save(this, out);
		out.writeInt(startFrame);
		out.writeInt(endFrame);
		out.writeInt(beginIndex);
		out.writeInt(layer);
	}

	public final static Explore createViaFile(DataInputStream in, ARManager arManager) throws Exception {
		int animID = in.readInt();
		Animation anim = arManager.getAnim(animID);
		Explore result = new Explore(anim, 0, 0, 0);
		result.load(in);
		return result;
	}

	private void load(DataInputStream in) throws Exception {
		BasicSprite.load(this, in);
		startFrame = in.readInt();
		endFrame = in.readInt();
		beginIndex = in.readInt();
		layer = in.readInt();
	}
}

abstract class ExploreManager
	extends SpriteManager {
	public ExploreManager(ScrollablePanel scrollablePanel, MouseInfo mouseInfo) {
		super(scrollablePanel, mouseInfo);
	}

	public void addExplores(ArrayList explores) {
		if (explores != null) {
			for (int i = 0; i < explores.size(); ++i) {
				if (explores.get(i)instanceof Explore) {
					Explore explore = (Explore) (explores.get(i));
					addSprite(explore.getCopy());
				}
			}
		}
	}

	public ArrayList getExplores() {
		resortSprites();
		ArrayList result = new ArrayList();
		for (int i = 0; i < sprites.size(); ++i) {
			if (sprites.get(i)instanceof Explore) {
				Explore explore = (Explore) (sprites.get(i));
				result.add(explore.getCopy());
			}
		}
		return result;
	}

	protected Sprite createSprite(int x, int y) {
		if (! (mouseInfo.getPainter()instanceof AnimPainter)) {
			return null;
		}
		AnimPainter painter = (AnimPainter) mouseInfo.getPainter();
		return new Explore(painter.getAnim(), useMaxID(), x, y);
	}

	public void saveMobileData(DataOutputStream out, Object[] resManagers) {}

	public void save(DataOutputStream out, Object[] resManagers) {}

	public void load(DataInputStream in, Object[] resManagers) {}
}

class ExplorePanel
	extends AnimPlayPanel {
	protected JDialog owner;
	protected MouseInfo mouseInfo;
	protected ExploreManager exploreManager;

	public ExplorePanel(JDialog owner, MouseInfo mouseInfo) {
		super(true);
		this.owner = owner;
		this.mouseInfo = mouseInfo;
		init();
	}

	private void init() {
		this.exploreManager = new ExploreManager(this, mouseInfo) {
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
					int currentIndex = getCurrentFrame() - getStartFrame();
					Explore explore = (Explore) sprite;
					int interval = -1;
					if (currentIndex >= explore.getBeginIndex()) {
						interval = getAnim().getInterval(
							getStartFrame() + explore.getBeginIndex(), getCurrentFrame()) +
								   getInterval();
					}
					int frameIndex = explore.getAnim().getFrameIndex(
						explore.getStartFrame(), interval);
					if (frameIndex >= explore.getStartFrame() &&
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
	}

	public boolean shouldReturn() {
		Sprite[] sprites = exploreManager.getSprites();
		boolean result = true;
		if (sprites != null) {
			int currentIndex = getCurrentFrame() - getStartFrame();
			for (int i = 0; i < sprites.length; ++i) {
				Explore explore = (Explore) (sprites[i]);
				int interval = -1;
				if (currentIndex >= explore.getBeginIndex()) {
					interval = getAnim().getInterval(
						getStartFrame() + explore.getBeginIndex(),
						getCurrentFrame()) + getInterval();
				}
				int frameIndex = explore.getAnim().getFrameIndex(
					explore.getStartFrame(), interval);
				if (frameIndex >= explore.getStartFrame() &&
					frameIndex <= explore.getEndFrame()) {
					result = false;
					break;
				}
			}
		}
		return result;
	}
}

class ExplorePropSetter
	extends OKCancelDialog {
	private Explore explore;
	private AnimPlayPanel rangePanel;
	private NumberSpinner beginIndexSpinner;

	public ExplorePropSetter(JDialog owner, Explore explore) {
		super(owner);
		this.explore = explore;
		init(owner);
	}

	private void init(JDialog owner) {
		setTitle("设置爆炸动画的属性");
		rangePanel = new AnimPlayPanel(true) {
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				paintAnim(g);
			}
		};
		rangePanel.setAnim(explore.getAnim());
		rangePanel.setRange(explore.getStartFrame(), explore.getEndFrame());

		beginIndexSpinner = new NumberSpinner();
		beginIndexSpinner.setIntValue(explore.getBeginIndex());

		Container cp = this.getContentPane();
		cp.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		cp.add(new JLabel("设置该爆炸效果的起始帧（从死亡动画起始帧开始计算的帧数）："), c);

		c.gridx = 1;
		c.weightx = 1;
		c.insets = new Insets(0, 10, 0, 0);
		cp.add(beginIndexSpinner, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.insets = new Insets(2, 0, 2, 0);
		cp.add(new JLabel("设置该动画的播放范围："), c);

		c.gridy = 2;
		c.weighty = 1;
		c.insets = new Insets(0, 0, 2, 0);
		cp.add(rangePanel.getBackPanel(), c);

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		c.gridy = 3;
		c.weighty = 0;
		c.insets = new Insets(0, 10, 2, 10);
		cp.add(buttonPanel, c);
	}

	public int getStartFrame() {
		return rangePanel.getStartFrame();
	}

	public int getEndFrame() {
		return rangePanel.getEndFrame();
	}

	public int getBeginIndex() {
		return beginIndexSpinner.getIntValue();
	}

	public void okPerformed() {
		this.closeType = OK_PERFORMED;
		dispose();
	}

	public void cancelPerformed() {
		dispose();
	}

	public void dispose() {
		super.dispose();
		rangePanel.stop();
	}
}
