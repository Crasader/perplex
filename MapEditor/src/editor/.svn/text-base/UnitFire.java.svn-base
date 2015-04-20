package editor;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.util.*;

public class UnitFire
	extends Operation {
	
	private int unitID;
	private int weapon;
	private int value;

	public UnitFire(int id) {
		super(id, Operation.UNIT_FIRE);
		init();
	}

	public UnitFire(int id, int type) {
		super(id, type);
		init();
	}

	private void init() {
		unitID = UnitManager.PLAYER_ID;
		weapon = Weapon.MACHINE_GUN;
		value = 0;
	}

	public int getUnitID() {
		return unitID;
	}

	public void setUnitID(int unitID) {
		this.unitID = unitID;
	}

	public int getWeapon() {
		return weapon;
	}

	public void setWeapon(int weapon) {
		this.weapon = weapon;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public void saveMobileData(DataOutputStream out, StringManager stringManager) throws Exception {
		super.saveMobileData(out, stringManager);
		SL.writeInt(unitID, out);
		SL.writeInt(weapon, out);
		SL.writeInt(value, out);
	}

	public void save(DataOutputStream out, StringManager stringManager) throws Exception {
		super.save(out, stringManager);
		out.writeInt(unitID);
		out.writeInt(weapon);
		out.writeInt(value);
	}

	protected void load(DataInputStream in, StringManager stringManager) throws Exception {
		super.load(in, stringManager);
		unitID = in.readInt();
		weapon = in.readInt();
		value = in.readInt();
	}

	public String getListItemDescription() {
		String result = Event.getUnitDescription(unitID) + "使用武器[" + 
						Weapon.DESCRIPTIONS[weapon] + "]攻击" + value + "次";
		return result;
	}

	public Operation getCopy() {
		UnitFire result = (UnitFire) (Operation.createInstance(this.id, this.type));
		result.unitID = this.unitID;
		result.weapon = this.weapon;
		result.value = this.value;
		return result;
	}
}

class UnitFireSetter
	extends OperationSetter {

	private UnitFire unitFire;
	private UnitChoosePanel unitChooser;
	private ValueChooser weaponChooser;
	private NumberSpinner valueSpinner;


	public UnitFireSetter(JDialog owner, MainFrame mainFrame, UnitFire unitFire) {
		super(owner, mainFrame);
		this.unitFire = unitFire;
		this.mainFrame = mainFrame;

		unitChooser = new UnitChoosePanel(this, mainFrame);
		unitChooser.setSelectedUnitID(unitFire.getUnitID());

		weaponChooser = new ValueChooser(unitFire.getWeapon(), 
										 Weapon.TYPES, Weapon.DESCRIPTIONS);

		valueSpinner = new NumberSpinner();
		valueSpinner.setIntValue(unitFire.getValue());

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		Container cp = this.getContentPane();
		cp.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(2, 2, 3, 3);
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		cp.add(unitChooser, c);

		c.gridx = 1;
		c.weightx =0;
		cp.add(new JLabel("使用武器"), c);

		c.gridx = 2;
		c.weightx = 1;
		cp.add(weaponChooser, c);
		
		c.gridx = 3;
		c.weightx = 0;
		cp.add(new JLabel("攻击"), c);
		
		c.gridx = 4;
		c.weightx = 1;
		cp.add(valueSpinner, c);
		
		c.gridx = 5;
		c.weightx = 0;
		cp.add(new JLabel("次"), c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 6;
		c.weighty = 0;
		cp.add(buttonPanel, c);
	}

	public Operation getOperation() {
		return unitFire;
	}

	public void okPerformed() {
		unitFire.setUnitID(unitChooser.getSelectedUnitID());
		unitFire.setWeapon(weaponChooser.getValue());
		unitFire.setValue(valueSpinner.getIntValue());
		this.closeType = OK_PERFORMED;
		dispose();
	}

	public void cancelPerformed() {
		dispose();
	}
}
