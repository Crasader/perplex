package editor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 操作的创建窗口。
 */
public class OperationCreator
	extends OKCancelDialog {
	private Operation operation;
	private MainFrame mainFrame;
	private int[][] types;

	public OperationCreator(JDialog owner, MainFrame mainFrame) {
		super(owner);
		init(mainFrame);
	}

	private void init(MainFrame mainFrame) {
		operation = null;
		this.mainFrame = mainFrame;
		setTitle("创建操作");

		types = Operation.TYPES;

		ActionListener createListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createOperation(e);
			}
		};

		int gridRows = 1, gridCols = 1, maxOperationCount = 1;
		for (int i = 0; i < types.length; ++i) {
			if (maxOperationCount < types[i].length) {
				maxOperationCount = types[i].length;
			}
		}

		if (maxOperationCount > XUtil.getDefPropInt("operationCreatorSingleRowMaxCount")) {
			gridCols = 2;
			gridRows = (maxOperationCount + 1) / 2;
		}
		else {
			gridCols = 1;
			gridRows = maxOperationCount;
		}

		JTabbedPane operationTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		for (int i = 0; i < types.length; ++i) {
			JPanel operationPanel = new JPanel();
			operationPanel.setLayout(new GridLayout(gridRows, gridCols));
			operationPanel.setName(Operation.KIND_DESCRIPTIONS[i]);
			for (int j = 0; j < types[i].length; ++j) {
				int type = types[i][j];
				JButton bt = new JButton(Operation.TYPE_DESCRIPTIONS[type]);
				bt.setActionCommand(type + "");
				bt.addActionListener(createListener);
				operationPanel.add(bt);
			}
			operationTabbedPane.add(operationPanel);
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
		cp.add(operationTabbedPane, BorderLayout.CENTER);
		cp.add(buttonPanel, BorderLayout.SOUTH);
	}

	public Operation getOperation() {
		return operation;
	}

	private void createOperation(ActionEvent e) {
		try {
			int type = Integer.parseInt(e.getActionCommand());
			boolean hasType = false;
			for (int i = 0; i < types.length; ++i) {
				for (int j = 0; j < types[i].length; ++j) {
					if (type == types[i][j]) {
						hasType = true;
						break;
					}
				}
			}
			if (!hasType) {
				throw new Exception("无法找到对应于 " + type + " 的操作类型。");
			}

			OperationSetter setter =
				OperationSetter.createSetter(
				this, mainFrame, Operation.createInstance(type));
			if (setter == null) {
				throw new Exception("无法创建操作设置窗口。");
			}
			setter.show();
			if (setter.getCloseType() == OKCancelDialog.OK_PERFORMED) {
				operation = setter.getOperation();
				okPerformed();
			}
		}
		catch (Exception err) {
			JOptionPane.showMessageDialog(this, err, "逻辑错误", JOptionPane.ERROR_MESSAGE);
		}
	}

	protected void okPerformed() {
		try {
			if (operation == null) {
				throw new Exception("空操作。");
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