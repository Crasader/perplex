package editor;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

public class BuildingPropTrigger {
	public final static String getPropDescription(int type) {
		String result = "[未知：" + type + "]";
		switch(type) {
		case Trigger.UNIT_HP:
			result = "[HP]";
			break;
		}
		return result;
	}
}

class BuildingPropTriggerSetter extends TriggerSetter {	
	private BuildingChoosePanel buildingChooser;
	private RangeSetPanel rangeSetPanel;

	public BuildingPropTriggerSetter(JDialog owner, MainFrame mainFrame, Trigger trigger) {
		super(owner, mainFrame, trigger);
		setTitle("设置作战单位的" + BuildingPropTrigger.getPropDescription(trigger.getType()) +  "的范围");

		buildingChooser = new BuildingChoosePanel(this, mainFrame);
		buildingChooser.setSelectedBuildingID(trigger.getTargetID());

		rangeSetPanel = new RangeSetPanel(trigger.getData());

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);		

		Container cp = this.getContentPane();
		cp.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2, 2, 3, 3);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		cp.add(buildingChooser, c);

		c.gridy = 1;
		cp.add(rangeSetPanel, c);

		c.gridy = 2;
		c.weighty = 0;
		cp.add(buttonPanel, c);		

	}

	public void okPerformed() {
		int buildingID = buildingChooser.getSelectedBuildingID();
		int min = rangeSetPanel.getMinimum();
		int max = rangeSetPanel.getMaximum();
		trigger.setTargetID(buildingID);
		trigger.setData(new int[] {min, max});
		closeType = OK_PERFORMED;
		dispose();
	}

	public void cancelPerformed() {
		dispose();
	}
}
