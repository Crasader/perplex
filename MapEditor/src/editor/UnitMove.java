package editor;

import java.io.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import com.sun.javafx.scene.layout.region.SliceSequenceConverter;

class UnitPathMove
	extends Operation {

	private int unitID;
	private IntPair startPoint;
	private IntPair[] path;
	private int[] animID;
	private int animCount;
	
	public int getAnimCount()
	{
		return animCount;
	}
	
	public UnitPathMove(int id) {
		super(id, UNIT_PATH_MOVE);
		init();
	}

	public UnitPathMove(int id, int type) {
		super(id, type);
		init();
	}

	private void init() {
		unitID = -1;
		this.startPoint = new IntPair();
		path = null;
		animID = null;
		animCount = 1;
	}

	public int getUnitID() {
		return unitID;
	}

	public void setUnitID(int unitID) {
		this.unitID = unitID;
	}

	public IntPair getStartPoint() {
		return startPoint.getCopy();
	}

	public void setStartPoint(IntPair startPoint) {
		this.startPoint = startPoint.getCopy();
	}

	public IntPair[] getPath() {
		return XUtil.copyArray(path);
	}

	public int[] getAnimaID() {
		return XUtil.copyArray(animID);
	}
	
	public void setAnimaID(int [] anima)
	{
		this.animID = XUtil.copyArray(anima);
	}
	
	public void setPath(IntPair[] path) {
		this.path = XUtil.copyArray(path);
	}

	public String getListItemDescription() {
		String result = Event.getUnitDescription(unitID) + "按照" +
						UnitPath.getPathDescription(path) + "移动";
		return result;
	}

	public void saveMobileData(DataOutputStream out, StringManager stringManager) throws Exception {
		super.saveMobileData(out, stringManager);
		SL.writeInt(unitID, out);
		SL.writeIntPairArrayMobile(path, out);
		SL.writeIntArray(animID, out);
	}

	public void save(DataOutputStream out, StringManager stringManager) throws Exception {
		super.save(out, stringManager);
		out.writeInt(unitID);
		SL.writeIntPair(startPoint, out);
		SL.writeIntPairArray(path, out);
		SL.writeIntArray(animID, out);
	}

	protected void load(DataInputStream in, StringManager stringManager) throws Exception {
		super.load(in, stringManager);
		unitID = in.readInt();
		startPoint = SL.readIntPair(in);
		path = SL.readIntPairArray(in);
		animID = SL.readIntArray(in);
	}

	public Operation getCopy() {
		UnitPathMove result = (UnitPathMove) (Operation.createInstance(this.id, this.type));
		result.unitID = this.unitID;
		result.startPoint = this.startPoint.getCopy();
		result.path = XUtil.copyArray(this.path);
		return result;
	}
}

class UnitPathMoveSetter
	extends OperationSetter {
	private UnitPathMove um;
	private UnitChoosePanel unitChoosePanel;
	private IntPair startPoint;
	private IntPair[] path;
	private int[] animaID;
	private int animCount;
	
	private ButtonText pathText;

	public UnitPathMoveSetter(JDialog owner, MainFrame mainFrame, UnitPathMove um) {
		super(owner, mainFrame);
		init(um);
	}

	private void init(UnitPathMove um) {
		setTitle("Unit按照固定路径移动");
		this.um = um;
		
		unitChoosePanel = new UnitChoosePanel(this, mainFrame);
		unitChoosePanel.setSelectedUnitID(um.getUnitID());
		unitChoosePanel.getCombo().addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				unitChanged();
			}
		});

		this.startPoint = um.getStartPoint();
		this.path = um.getPath();
		pathText = new ButtonText(new UnitPath(path,null));
		pathText.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
			"设置移动路径"));
		pathText.setActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectPath();
			}
		});

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
		mainPanel.add(pathText, c);

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(mainPanel, BorderLayout.CENTER);
		cp.add(buttonPanel, BorderLayout.SOUTH);
	}

	public Operation getOperation() {
		return um;
	}

	private void unitChanged() {
//		Sprite sprite = mainFrame.getPanels()[MainFrame.LAYER_UNIT].getManager().
//						getSprite(unitChoosePanel.getSelectedUnitID());
//		if(sprite == null) {
//			startPoint.x = startPoint.y = 0;
//		}
//		else {
//			startPoint.x = sprite.getX();
//			startPoint.y = sprite.getY();
//		}
//		setPath(null);
	}

	private void selectPath() {
		UnitPath up = new UnitPath(path, animaID);
		UnitPathSetter setter = new UnitPathSetter(this, mainFrame, startPoint, up, um.getAnimCount());
		setter.show();
		if (setter.getCloseType() == OKCancelDialog.OK_PERFORMED) {
			this.startPoint = setter.getStartPoint();
			setPath(setter.getUnitPath().getPath(), setter.getUnitPath().getAnimaID());
		}

	}

	private void setPath(IntPair[] path, int[] animaID) {
		this.path = path;
		this.animaID = animaID;
		pathText.setValue(new UnitPath(path, animaID));
	}

	public void okPerformed() {
		try {
			int unitID = unitChoosePanel.getSelectedUnitID();
			Sprite sprite = mainFrame.getPanels()[MainFrame.LAYER_UNIT].getManager().
							getSprite(unitID);
			if (unitID != UnitManager.PLAYER_ID && sprite == null) {
				throw new Exception("必须选择一个Unit");
			}
			if (path == null) {
				throw new Exception("必须设置路径");
			}
			um.setUnitID(unitID);
			um.setStartPoint(startPoint);
			um.setPath(path);
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
