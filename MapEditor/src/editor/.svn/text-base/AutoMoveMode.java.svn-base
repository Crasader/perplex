package editor;
import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
import javax.swing.event.*;

class AutoMoveModeCell {
	public final static int[] DIRS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18};
	public final static String[] DIR_DESCRIPTIONS = {
		"下",
		"左下", 
		"左",
		"左上", 
		"上",
		"右上",
		"右",
		"右下",
		"脸朝Player",
		"发射点1",
		"发射点2",
		"发射点3",
		"发射点4",
		"发射点5",
		"发射点6",
		"发射点7",
		"发射点8",
		"发射点9",
		"发射点10"
	};
	
	public final static int[] BULLETS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
	public final static String[] BULLET_DESCRIPTIONS = {
		"无", 
		"旋转子弹",
		"紫色子弹(8方向)",
		"小圆紫子弹",
		"大圆紫子弹",
		"小圆蓝子弹",
		"大圆蓝子弹",
		"坦克紫炮弹",
		"坦克大炮弹",
		"敌人小导弹",
		"轰天炮弹"
	};
	
	public final static int[] FIRE_LOGICS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22};
	public final static String[] FIRE_LOGIC_DESCRIPTIONS = {
		"不发射", 
		"朝下直射(单发)",
		"对准玩家(单发)",
		"对准玩家(双发)",
		"对准玩家(狂发)",
		"双叉弹(对准玩家)",
		"三叉弹(对准玩家)",
		"五叉弹(对准玩家)",
		"8方向",
		"16方向",
		"旋转32方向",
		"单发(指定方向)",
		"双发(指定方向)",
		"双叉弹(指定方向)",
		"三叉弹(指定方向)",
		"五叉弹(指定方向)",
		"轰天炮(连续3发)",
		"左边小兵",
		"右边小兵",
		"坦克BOSS左炮管",
		"坦克BOSS右炮管",
		"坦克BOSS左轰天炮",
		"坦克BOSS右轰天炮"
	};
	
	public int dir;
	public int speed;
	public int tick;
	public int bullet;
	public int fireLogic;
	
	public AutoMoveModeCell() {
		dir = 0;
		speed = 0;
		tick = 0;
		bullet = 0;
		fireLogic = 0;
	}
	
	public AutoMoveModeCell getCopy() {
		AutoMoveModeCell result = new AutoMoveModeCell();
		result.copyFrom(this);
		return result;
	}
	
	public void copyFrom(AutoMoveModeCell enemyLogic) {
		this.dir = enemyLogic.dir;
		this.speed = enemyLogic.speed;
		this.tick = enemyLogic.tick;
		this.bullet = enemyLogic.bullet;
		this.fireLogic = enemyLogic.fireLogic;
	}
	
	public void save(DataOutputStream out) throws Exception {
		out.writeInt(dir);
		out.writeInt(speed);
		out.writeInt(tick);
		out.writeInt(bullet);
		out.writeInt(fireLogic);
	}
	
	public void saveMobileData(DataOutputStream out) throws Exception {
		SL.writeInt(dir, out);
		SL.writeInt(speed, out);
		SL.writeInt(tick, out);
		SL.writeInt(bullet, out);
		SL.writeInt(fireLogic, out);
	}
	
	public void load(DataInputStream in) throws Exception {
		dir = in.readInt();
		speed = in.readInt();
		tick = in.readInt();
		bullet = in.readInt();
		fireLogic = in.readInt();
	}
}

class AutoMoveMode {
	public AutoMoveModeCell[] ammcs;
	
	public void AutoMoveMode() {
		ammcs = null;
	}
	
	public AutoMoveMode getCopy() {
		AutoMoveMode result = new AutoMoveMode();
		result.copyFrom(this);
		return result;
	}
	
	public void copyFrom(AutoMoveMode amm) {
		if(amm.ammcs == null) {
			this.ammcs = null;
		}
		else {
			this.ammcs = new AutoMoveModeCell[amm.ammcs.length];
			for(int i = 0; i < amm.ammcs.length; ++i) {
				this.ammcs[i] = amm.ammcs[i].getCopy();
			}
		}
	}
	
	public void save(DataOutputStream out) throws Exception {
		if(ammcs == null) {
			out.writeInt(0);
		}
		else {
			out.writeInt(ammcs.length);
			for(int i = 0; i < ammcs.length; ++i) {
				ammcs[i].save(out);
			}
		}
	}
	
	public void saveMobileData(DataOutputStream out) throws Exception {
		if(ammcs == null) {
			SL.writeInt(0, out);
		}
		else {
			SL.writeInt(ammcs.length, out);
			for(int i = 0; i < ammcs.length; ++i) {
				ammcs[i].saveMobileData(out);
			}
		}
	}
	
	public void load(DataInputStream in) throws Exception {
		ammcs = null;
		int length = in.readInt();
		if(length > 0) {
			ammcs = new AutoMoveModeCell[length];
			for(int i = 0; i < length; ++i) {
				ammcs[i] = new AutoMoveModeCell();
				ammcs[i].load(in);
			}
		}
	}
}

class AMMPanel extends XTable {
	private AutoMoveMode amm;
	
	public AMMPanel(String name, AutoMoveMode amm) {
		super(name, new String[] {"方向", "速度", "时间", "子弹类型", "射击逻辑"});
		init(amm);
	}
	
	private void init(AutoMoveMode amm) {
		setComboCol(0, AutoMoveModeCell.DIRS, AutoMoveModeCell.DIR_DESCRIPTIONS);
		setNumberCol(1);
		setNumberCol(2);
		setComboCol(3, AutoMoveModeCell.BULLETS, AutoMoveModeCell.BULLET_DESCRIPTIONS);
		setComboCol(4, AutoMoveModeCell.FIRE_LOGICS, AutoMoveModeCell.FIRE_LOGIC_DESCRIPTIONS);
		
		this.amm = amm.getCopy();
		
		DefaultTableModel model = getModel();
		if(amm.ammcs != null) {
			for(int i = 0; i < amm.ammcs.length; ++i) {
				model.addRow(new Object[] {
					new Integer(amm.ammcs[i].dir), 
					new Integer(amm.ammcs[i].speed), 
					new Integer(amm.ammcs[i].tick), 
					new Integer(amm.ammcs[i].bullet), 
					new Integer(amm.ammcs[i].fireLogic)
				});
			}
		}
	}
	
	public AutoMoveMode getAMM() {
		return amm.getCopy();
	}
	
	public void okPerformed() {
		stopEditing();
		DefaultTableModel model = getModel();
		ArrayList tmp = new ArrayList();
		for(int i = 0; i < model.getRowCount(); ++i) {
			int dir = ((Integer)(model.getValueAt(i, 0))).intValue();
			int speed = ((Integer)(model.getValueAt(i, 1))).intValue();
			int tick = ((Integer)(model.getValueAt(i, 2))).intValue();
			int bullet = ((Integer)(model.getValueAt(i, 3))).intValue();
			int fireLogic = ((Integer)(model.getValueAt(i, 4))).intValue();
//			if(tick > 0) {
				AutoMoveModeCell ammc = new AutoMoveModeCell();
				ammc.dir = dir; 
				ammc.speed = speed;
				ammc.tick = tick;
				ammc.bullet = bullet;
				ammc.fireLogic = fireLogic;
				tmp.add(ammc);
//			}
		}
		
		amm.ammcs = null;
		if(tmp.size() > 0) {
			amm.ammcs = new AutoMoveModeCell[tmp.size()];
			for(int i = 0; i < tmp.size(); ++i) {
				amm.ammcs[i] = (AutoMoveModeCell)(tmp.get(i));
			}
		}
	}
}

class AMMSetter extends OKCancelDialog {
	private AutoMoveMode amm;
	private AutoMoveMode amm2;
	private AMMPanel table;
	private AMMPanel table2;
	
	public AMMSetter(JFrame owner, AutoMoveMode amm, AutoMoveMode amm2) {
		super(owner);
		init(amm, amm2);
	}
	
	public AMMSetter(JDialog owner, AutoMoveMode amm, AutoMoveMode amm2) {
		super(owner);
		init(amm, amm2);
	}
	
	private void init(AutoMoveMode amm, AutoMoveMode amm2) {
		table = new AMMPanel("设置初始逻辑：", amm);
		table2 = new AMMPanel("设置循环逻辑：", amm2);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, table, table2);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(250);
		
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(splitPane, BorderLayout.CENTER);
		cp.add(buttonPanel, BorderLayout.SOUTH);
	}
	
	public AutoMoveMode getAMM() {
		return table.getAMM();
	}
	
	public AutoMoveMode getAMM2() {
		return table2.getAMM();
	}
	
	public void okPerformed() {
		this.closeType = OK_PERFORMED;
		
		table.okPerformed();
		table2.okPerformed();
		
		dispose();
	}
	
	public void cancelPerformed() {
		dispose();
	}
}