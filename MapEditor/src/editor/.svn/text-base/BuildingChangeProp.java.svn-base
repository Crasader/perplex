package editor;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.util.*;


class BuildingExplore
	extends Operation {

	private int buildingID;

	public BuildingExplore(int id) {
		super(id, BUILDING_EXPLORE);
		init();
	}

	public BuildingExplore(int id, int type) {
		super(id, type);
		init();
	}

	private void init() {
		buildingID = -1;
	}

	public int getBuildingID() {
		return buildingID;
	}

	public void setBuildingID(int buildingID) {
		this.buildingID = buildingID;
	}

	public void saveMobileData(DataOutputStream out, StringManager stringManager) throws Exception {
		super.saveMobileData(out, stringManager);
		SL.writeInt(buildingID, out);
	}

	public void save(DataOutputStream out, StringManager stringManager) throws Exception {
		super.save(out, stringManager);
		out.writeInt(buildingID);
	}

	protected void load(DataInputStream in, StringManager stringManager) throws Exception {
		super.load(in, stringManager);
		buildingID = in.readInt();
	}

	public String getListItemDescription() {
		String result = Event.getBuildingDescription(buildingID) + "±¨’®";
		return result;
	}

	public Operation getCopy() {
		BuildingExplore result = (BuildingExplore) (Operation.createInstance(this.id, this.type));
		result.buildingID = this.buildingID;
		return result;
	}
}

class BuildingExploreSetter
	extends OperationSetter {

	private BuildingExplore buildingExplore;
	private BuildingChoosePanel buildingChooser;

	public BuildingExploreSetter(JDialog owner, MainFrame mainFrame, BuildingExplore buildingExplore) {
		super(owner, mainFrame);
		this.setTitle("…Ë÷√Building±¨’®");
		this.buildingExplore = buildingExplore;
		this.mainFrame = mainFrame;

		buildingChooser = new BuildingChoosePanel(this, mainFrame);
		buildingChooser.setSelectedBuildingID(buildingExplore.getBuildingID());

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
		cp.add(buildingChooser, c);

		c.gridy = 1;
		c.weighty = 0;
		cp.add(buttonPanel, c);
	}

	public Operation getOperation() {
		return buildingExplore;
	}

	public void okPerformed() {
		buildingExplore.setBuildingID(buildingChooser.getSelectedBuildingID());
		this.closeType = OK_PERFORMED;
		dispose();
	}

	public void cancelPerformed() {
		dispose();
	}
}