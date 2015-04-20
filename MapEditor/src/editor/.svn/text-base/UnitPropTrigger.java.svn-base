package editor;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

public class UnitPropTrigger {
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

class UnitPropTriggerSetter extends TriggerSetter {	
	private UnitChoosePanel unitChooser;
	private RangeSetPanel rangeSetPanel;
	
    public UnitPropTriggerSetter(JDialog owner, MainFrame mainFrame, Trigger trigger) {
		super(owner, mainFrame, trigger);
		setTitle("设置作战单位的" + UnitPropTrigger.getPropDescription(trigger.getType()) +  "的范围");
		
		unitChooser = new UnitChoosePanel(this, mainFrame);
		unitChooser.setSelectedUnitID(trigger.getTargetID());
		
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
		cp.add(unitChooser, c);
		
		c.gridy = 1;
		cp.add(rangeSetPanel, c);
		
		c.gridy = 2;
		c.weighty = 0;
		cp.add(buttonPanel, c);		
    }
	
	public void okPerformed() {
		int unitID = unitChooser.getSelectedUnitID();
		int min = rangeSetPanel.getMinimum();
		int max = rangeSetPanel.getMaximum();
		trigger.setTargetID(unitID);
		trigger.setData(new int[] {min, max});
		closeType = OK_PERFORMED;
		dispose();
	}
	
	public void cancelPerformed() {
		dispose();
	}
}