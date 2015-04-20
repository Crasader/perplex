package editor;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;

class URPainter
	extends AnimPainter {
	public final static URPainter[] getPainters(UnitRes[] urs) {
		URPainter[] result = null;
		if (urs != null) {
			result = new URPainter[urs.length];
			for (int i = 0; i < urs.length; ++i) {
				result[i] = new URPainter(urs[i]);
				result[i].computeSize();
			}
		}
		return result;
	}

	private UnitRes ur;

	public URPainter(UnitRes ur) {
		super(ur.getAnim());
		this.ur = ur;
	}

	public UnitRes getUR() {
		return ur;
	}

	public int getGroupID() {
		return -1;
	}

	public int getID() {
		return ur.getID();
	}

	public String getName() {
		return ur.getName();
	}

	public int getDefFrameIndex() {
		return ur.getStandAnimRange(Dir.D).x;
	}
}

public class Unit
	extends AnimSprite
	implements Layerable, Copyable {
	private UnitRes ur;
	private int dir;
	private int layer;
	private int ai;
	private int alignment;
	private UnitMoveMode um;
	private UnitFireMode uf;
	private DropItemMode dim;
	private AutoMoveMode amm;
	private AutoMoveMode amm2;

	public Unit(UnitRes ur, int id, int originX, int originY) {
		super(ur.getAnim(), id, originX, originY, ur.getName());
		init(ur);
	}

	private void init(UnitRes ur) {
		this.ur = ur;
		dir = Dir.D;
		layer = 50;
		ai = AI.NORMAL;
		alignment = Alignment.ENEMY;
		um = ur.getUM();
		uf = ur.getUF();
		dim = ur.getDIM();
		amm = new AutoMoveMode();
		amm2 = new AutoMoveMode();
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

	public UnitRes getUR() {
		return ur;
	}

	public void setUR(UnitRes ur) {
		this.ur = ur;
		setAnim(ur.getAnim());
	}

	public int getDir() {
		return dir;
	}

	public void setDir(int dir) {
		this.dir = dir;
	}

	public int getLayer() {
		return layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}
	
	public int getAI() {
		return ai;
	}
	
	public void setAI(int ai) {
		this.ai = ai;
	}
		
	public int getAlignment() {
		return alignment;
	}
	
	public void setalignment(int alignment) {
		this.alignment = alignment;
	}
	
	public UnitMoveMode getUM() {
		return um.getCopy();
	}
	
	public void setUM(UnitMoveMode um) {
		this.um = um.getCopy();
	}
	
	public UnitFireMode getUF() {
		return uf.getCopy();
	}
	
	public void setUF(UnitFireMode uf) {
		this.uf = uf.getCopy();
	}

	public DropItemMode getDIM() {
		return dim.getCopy();
	}

	public void setDIM(DropItemMode dim) {
		this.dim = dim.getCopy();
	}
	
	public AutoMoveMode getAMM() {
		return amm.getCopy();
	}
	
	public void setAMM(AutoMoveMode amm) {
		this.amm = amm.getCopy();
	}
	
	public AutoMoveMode getAMM2() {
		return amm2.getCopy();
	}
	
	public void setAMM2(AutoMoveMode amm2) {
		this.amm2 = amm2.getCopy();
	}

	public Copyable copy() {
		return getCopy();
	}

	public Unit getCopy() {
		Unit result = new Unit(this.getUR(), this.getID(), this.getX(), this.getY());
		result.dir = this.dir;
		result.layer = this.layer;
		result.ai = this.ai;
		result.alignment = this.alignment;
		result.um = this.um.getCopy();
		result.uf = this.uf.getCopy();
		result.dim = this.dim.getCopy();
		result.amm = this.amm.getCopy();
		result.amm2 = this.amm2.getCopy();
		return result;
	}

	public int getDefFrameIndex() {
		return ur.getStandAnimRange(Dir.flip(dir)).x;
	}

	public boolean isFlip() {
		return Dir.isFlip(dir);
	}

	public String getInfo() {
		String result = getName() +
						"  ID：" + getID() +
						"  方向：" + Dir.DESCRIPTIONS[getDir()] +
						"  层：" + layer + 
						"  AI：" + AI.getDescription(ai) + 
						"  阵营：" + Alignment.getDescription(alignment);

		return result;
	}

	public void saveMobileData(DataOutputStream out) throws Exception {
		SL.writeInt(ur.getID(), out);
		BasicSprite.saveMobileData(this, out);
		SL.writeInt(dir, out);
		SL.writeInt(ai, out);
		SL.writeInt(alignment, out);
		um.saveMobileData(out);
		uf.saveMobileData(out);
		dim.saveMobileData(out);
		amm.saveMobileData(out);
		amm2.saveMobileData(out);
	}

	public void save(DataOutputStream out) throws Exception {
		out.writeInt(ur.getID());
		BasicSprite.save(this, out);
		out.writeInt(dir);
		out.writeInt(layer);
		out.writeInt(ai);
		out.writeInt(alignment);
		um.save(out);
		uf.save(out);
		dim.save(out);
		amm.save(out);
		amm2.save(out);
	}

	public final static Unit createViaFile(DataInputStream in, URManager urManager) throws Exception {
		int urID = in.readInt();
		UnitRes ur = urManager.getUR(urID);
		Unit result = new Unit(ur, 0, 0, 0);
		result.load(in, urManager);
		return result;
	}

	private void load(DataInputStream in, URManager urManager) throws Exception {
		BasicSprite.load(this, in);
		dir = in.readInt();
		layer = in.readInt();
		ai = in.readInt();
		alignment = in.readInt();
		um = UnitMoveMode.createViaFile(in);
		uf = UnitFireMode.createViaFile(in);
		dim.load(in);
		amm.load(in);
		amm2.load(in);
	}
}

class AI {
	public final static int NORMAL = 0;
	public final static int AI1 = 1;
	public final static int AI2 = 2;
	public final static int AI3 = 3;
	public final static int AI4 = 4;
	public final static int AI5 = 5;
	
	public final static int[] AIS = {
									NORMAL,
									AI1, 
									AI2,
									AI3,
									AI4, 
									AI5
	};
	
	public final static String[] DESCRIPTIONS = {
		"屏幕顶线", 
		"屏幕上方线",
		"屏幕中线",
		"屏幕下方线",
		"屏幕底线",
		"屏幕外线(上方)"
	};
	
	public final static String getDescription(int ai) {
		String result = "未知AI";
		if(ai >= 0 && ai < DESCRIPTIONS.length) {
			result = DESCRIPTIONS[ai];
		}
		return result;
	}
}

class Alignment {
	public final static int ENEMY = 0;
	public final static int ALLY = 1;
	public final static int PLAYER= 2;

	public final static int[] ALIGNMENTS = {
									ENEMY,
									ALLY, 
									PLAYER
	};

	public final static String[] DESCRIPTIONS = {
		"敌军", 
		"友军", 
		"主角"
	};

	public final static String getDescription(int alignment) {
		String result = "未知阵营";
		if(alignment >= 0 && alignment < DESCRIPTIONS.length) {
			result = DESCRIPTIONS[alignment];
		}
		return result;
	}
}

class UnitPropSetter extends OKCancelDialog {
	private Unit unit;
	private JTextField nameText;
	private ValueChooser dirChooser;
	private ValueChooser aiChooser;
	private ValueChooser alignmentChooser;
	private UnitMoveModePanel umPanel;
	private UnitFireModePanel ufPanel;
	private DropItemModeList dimList;
	private AutoMoveMode amm;
	private AutoMoveMode amm2;
	private ButtonText ammText;
	
	public UnitPropSetter(JFrame owner, Unit unit) {
		super(owner);
		init(unit);
	}
	
	public UnitPropSetter(JDialog owner, Unit unit) {
		super(owner);
		init(unit);
	}
	
	private void init(Unit unit) {
		this.unit = unit;
		nameText = new JTextField(unit.getName());
		dirChooser = new ValueChooser(unit.getDir(), Dir.FULL_STAND_DIRS, Dir.DESCRIPTIONS);
		aiChooser = new ValueChooser(unit.getAI(), AI.AIS, AI.DESCRIPTIONS);
		alignmentChooser = new ValueChooser(unit.getAlignment(), 
											Alignment.ALIGNMENTS, 
											Alignment.DESCRIPTIONS);
		umPanel = new UnitMoveModePanel(this, unit.getUM());
		ufPanel = new UnitFireModePanel(this, unit.getUF());
		dimList = new DropItemModeList(this, unit.getDIM());
		amm = unit.getAMM();
		amm2 = unit.getAMM2();
		ammText = new ButtonText("双击此处设置控制逻辑");
		ammText.setActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setAMM();
			}
		});
		
		JPanel p = new JPanel();
		p.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		c.insets = new Insets(2, 2, 3, 3);
		p.add(new JLabel("名称："), c);
		
		c.weightx = 1;
		c.gridx = 1;
		p.add(nameText, c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0;
		p.add(new JLabel("方向："), c);
		
		c.gridx = 1;
		c.weightx = 1;
		p.add(dirChooser, c);
		
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 0;
		p.add(new JLabel("AI："), c);

		c.gridx = 1;
		c.weightx = 1;
		p.add(aiChooser, c);
		
		c.gridx = 0;
		c.gridy = 3;
		c.weightx = 0;
		p.add(new JLabel("阵营："), c);

		c.gridx = 1;
		c.weightx = 1;
		p.add(alignmentChooser, c);
		
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 2;
		p.add(umPanel, c);
		
		c.gridy = 5;
		p.add(ufPanel, c);
		
		c.gridy = 6;
		c.gridx = 0;
		c.gridwidth = 1;
		c.weightx = 0;
		p.add(new JLabel("控制逻辑："), c);
		
		c.gridx = 1;
		c.weightx = 1;
		p.add(ammText, c);
		
		c.gridy = 7;
		c.gridx = 0;
		c.gridwidth = 2;
		p.add(new JLabel("掉落道具："), c);
		
		c.gridy = 8;
		c.weighty = 1;
		p.add(new JScrollPane(dimList), c);
		
		
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(p, BorderLayout.CENTER);
		cp.add(buttonPanel, BorderLayout.SOUTH);
	}
	
	private void setAMM() {
		AMMSetter setter = new AMMSetter(this, amm, amm2);
		setter.show();
		if(setter.getCloseType() == OKCancelDialog.OK_PERFORMED) {
			amm = setter.getAMM();
			amm2 = setter.getAMM2();
		}
	}
	
	public void okPerformed() {
		this.closeType = OK_PERFORMED;
		String name = nameText.getText();
		if(name == null) {
			name = "";
		}
		umPanel.updateUM();
		ufPanel.updataUF();
		unit.setName(name);
		unit.setDir(dirChooser.getValue());
		unit.setAI(aiChooser.getValue());
		unit.setalignment(alignmentChooser.getValue());
		unit.setUM(umPanel.getUM());
		unit.setUF(ufPanel.getUF());
		unit.setDIM(dimList.getDIM());
		unit.setAMM(amm);
		unit.setAMM2(amm2);
		dispose();
	}
	
	public void cancelPerformed() {
		dispose();
	}
}

class UnitManager
	extends SpriteManager {
	public final static int PLAYER_ID = 0;
	public final static String PLAYER_NAME = "主角";

	public UnitManager(ScrollablePanel scrollablePanel, MouseInfo mouseInfo) {
		super(scrollablePanel, mouseInfo);
		if (maxID <= 0) {
			maxID = 1; //因为0已经被Player使用了
		}
	}
	
	public void reset() {
		super.reset();
		if (maxID <= 0) {
			maxID = 1; //因为0已经被Player使用了
		}
	}

	protected Sprite createSprite(int x, int y) {
		if (! (mouseInfo.getPainter()instanceof URPainter)) {
			return null;
		}
		URPainter painter = (URPainter) mouseInfo.getPainter();
		return new Unit(painter.getUR(), useMaxID(), x, y);
	}

	public void refresh(URManager urManager) {
		UnitRes[] urs = urManager.getURs();
		int i = 0;
		while (sprites.size() > 0 && i < sprites.size()) {
			Unit unit = (Unit) (sprites.get(i));
			boolean remove = true;
			if (urs != null) {
				for (int j = 0; j < urs.length; ++j) {
					if (unit.getUR().getID() == urs[j].getID()) {
						remove = false;
						unit.setUR(urs[j]);
						break;
					}
				}
			}
			if (remove) {
				removeSprite(unit);
			}
			else {
				++i;
			}
		}
		scrollablePanel.repaint();
	}
	
	public void showProperties(Sprite sprite) {
		if(sprite != null) {
			if(sprite instanceof Unit) {
				Unit unit = (Unit)sprite;
				UnitPropSetter setter = new UnitPropSetter(MainFrame.self, unit);
				setter.show();
				scrollablePanel.repaint();
				super.selectionChanged();
			}
		}
	}

	public void saveMobileData(DataOutputStream out, Object[] resManagers) throws Exception {
		SL.writeInt(sprites.size(), out);
		for (int i = 0; i < sprites.size(); ++i) {
			Unit unit = (Unit) (sprites.get(i));
			unit.saveMobileData(out);
		}
	}

	public void save(DataOutputStream out, Object[] resManagers) throws Exception {
		out.writeInt(sprites.size());
		for (int i = 0; i < sprites.size(); ++i) {
			Unit unit = (Unit) (sprites.get(i));
			unit.save(out);
		}
	}

	public void load(DataInputStream in, Object[] resManagers) throws Exception {
		reset();
		URManager urManager = (URManager) (resManagers[MainFrame.RES_UR]);
		int length = in.readInt();
		for (int i = 0; i < length; ++i) {
			Unit unit = Unit.createViaFile(in, urManager);
			addSprite(unit);
		}
	}
}

class UnitPanel
	extends SpriteManagerPanel {

	public UnitPanel(JFrame owner, MouseInfo mouseInfo) {
		super(owner, mouseInfo);
	}

	public UnitPanel(JDialog owner, MouseInfo mouseInfo) {
		super(owner, mouseInfo);
	}

	protected SpriteManager createManager() {
		return new UnitManager(this, mouseInfo);
	}
}