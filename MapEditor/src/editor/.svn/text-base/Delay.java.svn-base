package editor;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.util.*;


public class Delay
	extends Operation {

	private int second;

	public Delay(int id) {
		super(id, DELAY);
		init();
	}

	public Delay(int id, int type) {
		super(id, type);
		init();
	}

	private void init() {
		second = 0;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	public void saveMobileData(DataOutputStream out, StringManager stringManager) throws Exception {
		super.saveMobileData(out, stringManager);
		SL.writeInt(second, out);
	}

	public void save(DataOutputStream out, StringManager stringManager) throws Exception {
		super.save(out, stringManager);
		out.writeInt(second);
	}

	protected void load(DataInputStream in, StringManager stringManager) throws Exception {
		super.load(in, stringManager);
		second = in.readInt();
	}

	public String getListItemDescription() {
		String result = "—”≥Ÿ" + second + "√Î";
		return result;
	}

	public Operation getCopy() {
		Delay result = (Delay) (Operation.createInstance(this.id, this.type));
		result.second = this.second;
		return result;
	}
}

class DelaySetter
	extends OperationSetter {

	private Delay delay;
	private NumberSpinner secondSpinner;

	public DelaySetter(JDialog owner, MainFrame mainFrame, Delay delay) {
		super(owner, mainFrame);
		this.setTitle("…Ë÷√—”≥Ÿ");
		this.delay = delay;
		this.mainFrame = mainFrame;

		secondSpinner = new NumberSpinner();
		secondSpinner.setIntValue(delay.getSecond());

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		Container cp = this.getContentPane();
		cp.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(2, 2, 3, 3);
		cp.add(new JLabel("—”≥Ÿ"), c);
		
		c.gridx = 1;
		c.weightx = 1;
		cp.add(secondSpinner, c);
		
		c.gridx = 2;
		c.weightx = 0;
		cp.add(new JLabel("√Î"), c);

		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 0;
		c.gridwidth = 3;
		cp.add(buttonPanel, c);
	}

	public Operation getOperation() {
		return delay;
	}

	public void okPerformed() {
		delay.setSecond(secondSpinner.getIntValue());
		this.closeType = OK_PERFORMED;
		dispose();
	}

	public void cancelPerformed() {
		dispose();
	}
}
