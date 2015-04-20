package editor;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

class RangeSetPanel
	extends JPanel {
	
	private NumberSpinner minSpinner;
	private NumberSpinner maxSpinner;

	public RangeSetPanel() {
		init(0, 0);
	}

	public RangeSetPanel(int[] data) {
		int min = 0;
		int max = 0;
		if (data != null) {
			if (data.length >= 1) {
				min = data[0];
			}
			if (data.length >= 2) {
				max = data[1];
			}
		}
		init(min, max);
	}

	public RangeSetPanel(int min, int max) {
		init(min, max);
	}

	private void init(int min, int max) {
		minSpinner = new NumberSpinner();
		minSpinner.setIntValue(min);
		
		maxSpinner = new NumberSpinner();
		maxSpinner.setIntValue(max);

		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0;
		c.weighty = 1;
		
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 0, 5);
		this.add(new JLabel("��Сֵ��"), c);
		
		c.gridx = 1;
		c.weightx = 1;
		this.add(minSpinner, c);

		c.gridx = 2;
		c.weightx = 0;
		c.insets = new Insets(0, 5, 0, 0);
		this.add(new JLabel("���ֵ��"), c);
		
		c.gridx = 3;
		c.weightx = 1;
		this.add(maxSpinner, c);
	}

	public int getMinimum() {
		return minSpinner.getIntValue();
	}

	public void setMinimum(int min) {
		minSpinner.setIntValue(min);
	}

	public int getMaximum() {
		return maxSpinner.getIntValue();
	}

	public void setMaximum(int max) {
		maxSpinner.setIntValue(max);
	}
}

class RandomSetter
	extends TriggerSetter {
	NumberSpinner targetSpinner;
	RangeSetPanel rangeSetPanel;

	public RandomSetter(JDialog owner, MainFrame mainFrame, Trigger trigger) {
		super(owner, mainFrame, trigger);
		setTitle("ĳ���������һ����Χ��");

		targetSpinner = new NumberSpinner();
		targetSpinner.setIntValue(trigger.getTargetID());

		rangeSetPanel = new RangeSetPanel(trigger.getData());

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		Container cp = this.getContentPane();
		cp.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;

		c.weightx = 0;
		c.weighty = 0;
		c.gridy = 0;
		c.gridx = 0;
		cp.add(new JLabel("����������Ļ�����"), c);
		c.weightx = 1;
		c.gridx = 1;
		cp.add(targetSpinner, c);

		c.gridwidth = 2;
		c.weighty = 1;
		c.gridy = 1;
		c.gridx = 0;
		cp.add(rangeSetPanel, c);

		buttonPanel.setPreferredSize(new Dimension(25, 30));
		c.weighty = 0;
		c.gridy = 2;
		c.gridx = 0;
		cp.add(buttonPanel, c);
	}

	protected void okPerformed() {
		try {
			int targetID = targetSpinner.getIntValue();
			int min = rangeSetPanel.getMinimum();
			int max = rangeSetPanel.getMaximum();
			if (min >= max) {
				throw new Exception("���뱣֤ ��Сֵ < ���ֵ ��");
			}
			trigger.setTargetID(targetID);
			trigger.setData(new int[] {min, max});
			closeType = OK_PERFORMED;
			dispose();
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, e, "�߼�����", JOptionPane.ERROR_MESSAGE);
		}
	}

	protected void cancelPerformed() {
		dispose();
	}
}
