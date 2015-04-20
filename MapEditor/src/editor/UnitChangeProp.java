package editor;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.util.*;

public class UnitChangeProp
	extends Operation {
	
	public final static String getPropDescription(int type) {
		String result = "[未知：" + type + "]";
		switch(type) {
		case Operation.UNIT_CHANGE_HP:
			result = "[HP]";
			break;
		}
		return result;
	}

	private int unitID;
	private int relation;
	private int value;

	public UnitChangeProp(int id, int type) {
		super(id, type);
		init();
	}

	private void init() {
		unitID = UnitManager.PLAYER_ID;
		relation = Relation.SET;
		value = 0;
	}

	public int getUnitID() {
		return unitID;
	}

	public void setUnitID(int unitID) {
		this.unitID = unitID;
	}
	
	public int getRelation() {
		return relation;
	}
	
	public void setRelation(int relation) {
		this.relation = relation;
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
		SL.writeInt(relation, out);
		SL.writeInt(value, out);
	}

	public void save(DataOutputStream out, StringManager stringManager) throws Exception {
		super.save(out, stringManager);
		out.writeInt(unitID);
		out.writeInt(relation);
		out.writeInt(value);
	}

	protected void load(DataInputStream in, StringManager stringManager) throws Exception {
		super.load(in, stringManager);
		unitID = in.readInt();
		relation = in.readInt();
		value = in.readInt();
	}

	public String getListItemDescription() {
		String result = Event.getUnitDescription(unitID) + "的" + getPropDescription(type) + 
						Relation.DESCRIPTIONS[relation] + value;						
		switch(type) {
		case Operation.UNIT_CHANGE_HP:
			break;
		}
		return result;
	}

	public Operation getCopy() {
		UnitChangeProp result = (UnitChangeProp) (Operation.createInstance(this.id, this.type));
		result.unitID = this.unitID;
		result.relation = this.relation;
		result.value = this.value;
		return result;
	}
}

class UnitChangePropSetter
	extends OperationSetter {

	private UnitChangeProp unitChangeProp;
	private UnitChoosePanel unitChooser;
	private ValueChooser relationChooser;
	private NumberSpinner valueSpinner;


	public UnitChangePropSetter(JDialog owner, MainFrame mainFrame, UnitChangeProp unitChangeProp) {
		super(owner, mainFrame);
		this.unitChangeProp = unitChangeProp;
		this.mainFrame = mainFrame;

		unitChooser = new UnitChoosePanel(this, mainFrame);
		unitChooser.setSelectedUnitID(unitChangeProp.getUnitID());
		
		relationChooser = new ValueChooser(unitChangeProp.getRelation(), 
										   Relation.RELATIONS[1], 
										   Relation.DESCRIPTIONS);

		valueSpinner = new NumberSpinner();
		valueSpinner.setIntValue(unitChangeProp.getValue());

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
		cp.add(new JLabel("的" + UnitChangeProp.getPropDescription(unitChangeProp.getType())), c);

		c.gridx = 2;
		cp.add(relationChooser, c);

		c.gridx = 3;
		c.weightx = 1;
		cp.add(valueSpinner, c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 4;
		c.weighty = 0;
		cp.add(buttonPanel, c);
	}

	public Operation getOperation() {
		return unitChangeProp;
	}

	public void okPerformed() {
		unitChangeProp.setUnitID(unitChooser.getSelectedUnitID());
		unitChangeProp.setRelation(relationChooser.getValue());
		unitChangeProp.setValue(valueSpinner.getIntValue());
		this.closeType = OK_PERFORMED;
		dispose();
	}

	public void cancelPerformed() {
		dispose();
	}
}

class UnitChangeAI
	extends Operation {

	public final static String getPropDescription(int type) {
		String result = "[未知：" + type + "]";
		switch(type) {
		case Operation.UNIT_CHANGE_HP:
			result = "[HP]";
			break;
		}
		return result;
	}

	private int unitID;
	private int ai;
	
	public UnitChangeAI(int id) {
		super(id, Operation.UNIT_CHANGE_AI);
		init();
	}

	public UnitChangeAI(int id, int type) {
		super(id, type);
		init();
	}

	private void init() {
		unitID = UnitManager.PLAYER_ID;
		ai = AI.NORMAL;
	}

	public int getUnitID() {
		return unitID;
	}

	public void setUnitID(int unitID) {
		this.unitID = unitID;
	}

	public int getAI() {
		return ai;
	}

	public void setAI(int ai) {
		this.ai = ai;
	}

	public void saveMobileData(DataOutputStream out, StringManager stringManager) throws Exception {
		super.saveMobileData(out, stringManager);
		SL.writeInt(unitID, out);
		SL.writeInt(ai, out);
	}

	public void save(DataOutputStream out, StringManager stringManager) throws Exception {
		super.save(out, stringManager);
		out.writeInt(unitID);
		out.writeInt(ai);
	}

	protected void load(DataInputStream in, StringManager stringManager) throws Exception {
		super.load(in, stringManager);
		unitID = in.readInt();
		ai = in.readInt();
	}

	public String getListItemDescription() {
		String result = Event.getUnitDescription(unitID) + "的AI改变为[" + 
						AI.getDescription(ai) + "]";
		return result;
	}

	public Operation getCopy() {
		UnitChangeAI result = (UnitChangeAI) (Operation.createInstance(this.id, this.type));
		result.unitID = this.unitID;
		result.ai = this.ai;
		return result;
	}
}

class UnitChangeAISetter
	extends OperationSetter {

	private UnitChangeAI unitChangeAI;
	private UnitChoosePanel unitChooser;
	private ValueChooser aiChooser;

	public UnitChangeAISetter(JDialog owner, MainFrame mainFrame, UnitChangeAI unitChangeAI) {
		super(owner, mainFrame);
		this.unitChangeAI = unitChangeAI;
		this.mainFrame = mainFrame;

		unitChooser = new UnitChoosePanel(this, mainFrame);
		unitChooser.setSelectedUnitID(unitChangeAI.getUnitID());

		aiChooser = new ValueChooser(unitChangeAI.getAI(), 
									 AI.AIS, AI.DESCRIPTIONS);

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
		cp.add(new JLabel("的AI改变为"), c);

		c.gridx = 2;
		c.weightx = 1;
		cp.add(aiChooser, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 3;
		c.weighty = 0;
		cp.add(buttonPanel, c);
	}

	public Operation getOperation() {
		return unitChangeAI;
	}

	public void okPerformed() {
		unitChangeAI.setUnitID(unitChooser.getSelectedUnitID());
		unitChangeAI.setAI(aiChooser.getValue());
		this.closeType = OK_PERFORMED;
		dispose();
	}

	public void cancelPerformed() {
		dispose();
	}
}

class UnitExplore
	extends Operation {

	private int unitID;

	public UnitExplore(int id) {
		super(id, UNIT_EXPLORE);
		init();
	}

	public UnitExplore(int id, int type) {
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
		String result = Event.getUnitDescription(unitID) + "爆炸";
		return result;
	}

	public Operation getCopy() {
		UnitExplore result = (UnitExplore) (Operation.createInstance(this.id, this.type));
		result.unitID = this.unitID;
		return result;
	}
}

class UnitExploreSetter
	extends OperationSetter {

	private UnitExplore unitExplore;
	private UnitChoosePanel unitChooser;

	public UnitExploreSetter(JDialog owner, MainFrame mainFrame, UnitExplore unitExplore) {
		super(owner, mainFrame);
		this.setTitle("设置Unit爆炸");
		this.unitExplore = unitExplore;
		this.mainFrame = mainFrame;

		unitChooser = new UnitChoosePanel(this, mainFrame);
		unitChooser.setSelectedUnitID(unitExplore.getUnitID());

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
		return unitExplore;
	}

	public void okPerformed() {
		unitExplore.setUnitID(unitChooser.getSelectedUnitID());
		this.closeType = OK_PERFORMED;
		dispose();
	}

	public void cancelPerformed() {
		dispose();
	}
}