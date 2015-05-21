package editor;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.util.*;

public class UnitAppear
	extends Operation {
	
	private int unitID;
	private int x;
	private int y;

	public UnitAppear(int id) {
		super(id, UNIT_APPEAR);
		init();
	}

	public UnitAppear(int id, int type) {
		super(id, type);
		init();
	}

	private void init() {
		unitID = UnitManager.PLAYER_ID;
		x = 0;
		y = 0;
	}
	
	public int getUnitID() {
		return unitID;
	}
	
	public void setUnitID(int unitID) {
		this.unitID = unitID;
	}
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}

	public void saveMobileData(DataOutputStream out, StringManager stringManager) throws Exception {
		super.saveMobileData(out, stringManager);
		SL.writeInt(unitID, out);
		SL.writeInt(MainFrame.self.getMapInfo().changeToMobileX(x), out);
		SL.writeInt(MainFrame.self.getMapInfo().changeToMobileY(y, 0), out);
	}

	public void save(DataOutputStream out, StringManager stringManager) throws Exception {
		super.save(out, stringManager);
		out.writeInt(unitID);
		out.writeInt(x);
		out.writeInt(y);
	}

	protected void load(DataInputStream in, StringManager stringManager) throws Exception {
		super.load(in, stringManager);
		unitID = in.readInt();
		x = in.readInt();
		y = in.readInt();
	}

	public String getListItemDescription() {
		String result = Event.getUnitDescription(unitID) + "出现在" + Event.getPointDescription(new int[] {x, y});
		return result;
	}

	public Operation getCopy() {
		UnitAppear result = (UnitAppear) (Operation.createInstance(this.id, this.type));
		result.unitID = this.unitID;
		result.x = this.x;
		result.y = this.y;
		return result;
	}
}

class UnitAppearSetter
	extends OperationSetter {

	private UnitAppear unitAppear;
	private UnitChoosePanel unitChooser;
	private MapPointSetPanel pointPanel;

	public UnitAppearSetter(JDialog owner, MainFrame mainFrame, UnitAppear unitAppear) {
		super(owner, mainFrame);
		this.setTitle("设置Unit出现");
		this.unitAppear = unitAppear;
		this.mainFrame = mainFrame;

		unitChooser = new UnitChoosePanel(this, mainFrame);
		unitChooser.setSelectedUnitID(unitAppear.getUnitID());

		pointPanel = new MapPointSetPanel(this, mainFrame, unitAppear.getX(), unitAppear.getY());
		
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		Container cp = this.getContentPane();
		cp.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		cp.add(unitChooser, c);
		
		c.gridy = 1;
		cp.add(pointPanel, c);
		
		c.gridy = 2;
		c.weighty = 0;
		cp.add(buttonPanel, c);
	}

	public Operation getOperation() {
		return unitAppear;
	}

	public void okPerformed() {
		unitAppear.setUnitID(unitChooser.getSelectedUnitID());
		unitAppear.setX(pointPanel.getPointX());
		unitAppear.setY(pointPanel.getPointY());
		this.closeType = OK_PERFORMED;
		dispose();
	}

	public void cancelPerformed() {
		dispose();
	}
}


class UnitDisappear
	extends Operation {

	private int unitID;

	public UnitDisappear(int id) {
		super(id, UNIT_DISAPPEAR);
		init();
	}

	public UnitDisappear(int id, int type) {
		super(id, type);
		init();
	}

	private void init() {
		unitID = UnitManager.PLAYER_ID;
	}

	public int getUnitID() {
		return unitID;
	}

	public void setUnitID(int unitID) {
		this.unitID = unitID;
	}

	public void saveMobileData(DataOutputStream out, StringManager stringManager) throws Exception {
		super.saveMobileData(out, stringManager);
		SL.writeInt(unitID, out);
	}

	public void save(DataOutputStream out, StringManager stringManager) throws Exception {
		super.save(out, stringManager);
		out.writeInt(unitID);
	}

	protected void load(DataInputStream in, StringManager stringManager) throws Exception {
		super.load(in, stringManager);
		unitID = in.readInt();
	}

	public String getListItemDescription() {
		String result = Event.getUnitDescription(unitID) + "消失";
		return result;
	}

	public Operation getCopy() {
		UnitDisappear result = (UnitDisappear) (Operation.createInstance(this.id, this.type));
		result.unitID = this.unitID;
		return result;
	}
}

class UnitDisappearSetter
	extends OperationSetter {

	private UnitDisappear unitDisappear;
	private UnitChoosePanel unitChooser;

	public UnitDisappearSetter(JDialog owner, MainFrame mainFrame, UnitDisappear unitDisappear) {
		super(owner, mainFrame);
		this.setTitle("设置Unit消失");
		this.unitDisappear = unitDisappear;
		this.mainFrame = mainFrame;

		unitChooser = new UnitChoosePanel(this, mainFrame);
		unitChooser.setSelectedUnitID(unitDisappear.getUnitID());

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		Container cp = this.getContentPane();
		cp.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		cp.add(unitChooser, c);

		c.gridy = 1;
		c.weighty = 0;
		cp.add(buttonPanel, c);
	}

	public Operation getOperation() {
		return unitDisappear;
	}

	public void okPerformed() {
		unitDisappear.setUnitID(unitChooser.getSelectedUnitID());
		this.closeType = OK_PERFORMED;
		dispose();
	}

	public void cancelPerformed() {
		dispose();
	}
}
