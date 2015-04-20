package editor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 创建触发器的窗口。
 */
public class TriggerCreator
	extends OKCancelDialog {
	private Trigger trigger;
	private MainFrame mainFrame;
	private int[] types;

	public TriggerCreator(JDialog owner, MainFrame mainFrame) {
		super(owner);
		init(mainFrame);
	}

	private void init(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		trigger = null;
		setTitle("创建触发条件");

		types = Trigger.TYPES;

		JPanel triggerPanel = new JPanel();
		triggerPanel.setLayout(new GridLayout(types.length, 1));
		ActionListener createListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createTrigger(e);
			}
		};
		for (int i = 0; i < types.length; ++i) {
			int type = types[i];
			JButton bt = new JButton(Trigger.TYPE_DESCRIPTIONS[type]);
			bt.setActionCommand(type + "");
			bt.addActionListener(createListener);
			triggerPanel.add(bt);
		}

		buttonPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		buttonPanel.add(new JLabel(), c);
		c.gridx = 1;
		c.weightx = 0;
		buttonPanel.add(cancelButton, c);

		Container cp = this.getContentPane();
		cp.add(triggerPanel, BorderLayout.CENTER);
		cp.add(buttonPanel, BorderLayout.SOUTH);
	}

	public Trigger getTrigger() {
		return trigger;
	}

	private void createTrigger(ActionEvent e) {
		try {
			int type = Integer.parseInt(e.getActionCommand());
			boolean hasType = false;
			for (int i = 0; i < types.length; ++i) {
				if (type == types[i]) {
					hasType = true;
					break;
				}
			}
			if (!hasType) {
				throw new Exception("无法找到对应于 " + type + " 的触发器类型。");
			}

			TriggerSetter setter =
				TriggerSetter.createSetter(
				this, mainFrame, Trigger.createInstance(type));
			if (setter == null) {
				throw new Exception("无法创建触发器设置窗口。");
			}
			setter.show();
			if (setter.getCloseType() == OKCancelDialog.OK_PERFORMED) {
				trigger = setter.getTrigger();
				okPerformed();
			}
		}
		catch (Exception err) {
			JOptionPane.showMessageDialog(this, err, "逻辑错误", JOptionPane.ERROR_MESSAGE);
		}
	}

	protected void okPerformed() {
		try {
			if (trigger == null) {
				throw new Exception("空触发器。");
			}
			closeType = OK_PERFORMED;
			dispose();
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, e, "逻辑错误", JOptionPane.ERROR_MESSAGE);
		}
	}

	protected void cancelPerformed() {
		dispose();
	}
}