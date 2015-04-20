package editor;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.util.*;

class Head {
	private int id;
	private String name;

	public Head(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return name;
	}
}

class HeadResManager {
	private File iniFile;
	private Head[] heads;

	public HeadResManager() {
		iniFile = new File(XUtil.getDefPropStr("HeadIniFile"));
	}

	public Head[] getHeads() {
		return heads;
	}

	public Head getHead(int headID) {
		for (int i = 0; i < heads.length; ++i) {
			if (heads[i].getID() == headID) {
				return heads[i];
			}
		}
		return null;
	}

	private void readIniFile() throws Exception {
		BufferedReader in = new BufferedReader(
			new InputStreamReader(
			new FileInputStream(iniFile)));
		ArrayList data = new ArrayList();
		String sLine;

		sLine = in.readLine();
		while (sLine != null) {
			data.add(sLine);
			sLine = in.readLine();
		}
		in.close();

		//get max group id
		ArrayList tmp = new ArrayList();
		for (int i = 0; i < data.size(); ++i) {
			sLine = ( (String) (data.get(i))).trim();
			if (sLine == null) {
				continue;
			}
			if (sLine.length() < 2) {
				continue;
			}
			if (sLine.startsWith("@") && sLine.endsWith(";") && sLine.length() > 2) { //headline
				String infos[] = sLine.substring(1, sLine.length() - 1).split(",", 0);
				if (infos != null) {
					if (infos.length >= 2) {
						int headID = Integer.parseInt(infos[0]);
						String headName = infos[1];
						tmp.add(new Head(headID, headName));
					}
				}
			}
		}

		heads = new Head[tmp.size()];
		for (int i = 0; i < tmp.size(); ++i) {
			heads[i] = (Head) (tmp.get(i));
		}
	}

	public void load() throws Exception {
		readIniFile();
	}
}

class HeadSelecter
	extends OKCancelDialog {
	private DefaultListModel headModel;
	private JList headList;
	private MainFrame mainFrame;

	public HeadSelecter(JDialog owner, int headID, MainFrame mainFrame) {
		super(owner);
		this.mainFrame = mainFrame;
		setTitle("选择头像");
		Container cp = this.getContentPane();

		headModel = new DefaultListModel();
		headModel.addElement("无");
		Head[] heads = mainFrame.getEventManager().getHeadResManager().getHeads();
		for (int i = 0; i < heads.length; ++i) {
			headModel.addElement(heads[i]);
		}
		headList = new JList(headModel);
		headList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		headList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				headListMouseClicked(e);
			}
		});
		JScrollPane headScroll = new JScrollPane(headList);

		setSelectedHead(headID);
		cp.add(headScroll, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 2));
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		cp.add(buttonPanel, BorderLayout.SOUTH);
		setLocationRelativeTo(owner);
	}

	public int getHeadID() {
		Object obj = headList.getSelectedValue();
		if (obj != null) {
			if (obj instanceof Head) {
				return ( (Head) obj).getID();
			}
		}
		return -1;
	}

	private void setSelectedHead(int headID) {
		headList.clearSelection();
		for (int i = 0; i < headModel.getSize(); ++i) {
			Object obj = headModel.get(i);
			if (obj != null) {
				if (obj instanceof Head) {
					Head head = (Head) obj;
					if (head.getID() == headID) {
						headList.setSelectedValue(obj, true);
						break;
					}
				}
			}
		}
	}

	private void headListMouseClicked(MouseEvent e) {
		if (e.getButton() == MouseInfo.LEFT_BUTTON && e.getClickCount() == 2) {
			okPerformed();
		}
	}

	public void okPerformed() {
		closeType = OK_PERFORMED;
		dispose();
	}

	public void cancelPerformed() {
		dispose();
	}
}

public class CommonDialog
	extends Operation {
	public final static int HEAD_LEFT = 0;
	public final static int HEAD_RIGHT = 1;

	private int headID;
	private int headPos;
	private String text;

	public CommonDialog(int id) {
		super(id, COMMON_DIALOG);
		init();
	}

	public CommonDialog(int id, int type) {
		super(id, type);
		init();
	}

	private void init() {
		headID = -1;
		headPos = HEAD_LEFT;
		setText(null);
	}

	public int getHeadID() {
		return headID;
	}

	public void setHeadID(int headID) {
		this.headID = headID;
	}

	public int getHeadPos() {
		return headPos;
	}

	public void setHeadPos(int headPos) {
		this.headPos = headPos;
	}

	public String getText() {
		if (text == null) {
			return "";
		}
		else {
			return text;
		}
	}

	public void setText(String text) {
		if (text == null) {
			this.text = "";
		}
		else {
			this.text = text;
		}
	}

	public void saveMobileData(DataOutputStream out, StringManager stringManager) throws Exception {
		super.saveMobileData(out, stringManager);
		SL.writeInt(headID, out);
		SL.writeInt(headPos, out);
		SL.writeInt(stringManager.add(getText()), out);
	}

	public void save(DataOutputStream out, StringManager stringManager) throws Exception {
		super.save(out, stringManager);
		out.writeInt(headID);
		out.writeInt(headPos);
		out.writeInt(stringManager.add(getText()));
	}

	protected void load(DataInputStream in, StringManager stringManager) throws Exception {
		super.load(in, stringManager);
		headID = in.readInt();
		headPos = in.readInt();
		int textID = in.readInt();
		setText(stringManager.get(textID));
	}

	public String getListItemDescription() {
		Head head = MainFrame.self.getEventManager().getHeadResManager().getHead(headID);
		String result = head == null ? "无" : head.getName();
		return result + "：" + text;
	}

	public Operation getCopy() {
		CommonDialog result = (CommonDialog) (Operation.createInstance(this.id, this.type));
		result.headID = this.headID;
		result.headPos = this.headPos;
		result.text = this.text;
		return result;
	}
}

class CommonDialogSetter
	extends OperationSetter {
	
	private CommonDialog commonDialog;
	private int headID;
	private ButtonText headText;
	private JRadioButton radioLeft;
	private JRadioButton radioRight;
	private JTextArea dialogText;

	public CommonDialogSetter(JDialog owner, MainFrame mainFrame, CommonDialog commonDialog) {
		super(owner, mainFrame);
		this.commonDialog = commonDialog;
		headID = commonDialog.getHeadID();
		this.mainFrame = mainFrame;

		headText = new ButtonText(getHeadDescription());
		headText.setActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectHead();
			}
		});

		radioLeft = new JRadioButton("Left");
		radioRight = new JRadioButton("Right");
		ButtonGroup radioGroup = new ButtonGroup();
		radioGroup.add(radioLeft);
		radioGroup.add(radioRight);
		setHeadPos(commonDialog.getHeadPos());

		dialogText = new JTextArea(commonDialog.getText());
		JScrollPane dialogScroll = new JScrollPane(dialogText);
	
		Container cp = this.getContentPane();
		cp.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = 0;
		
		c.gridwidth = 2;
		cp.add(headText, c);
		
		radioLeft.setPreferredSize(new Dimension(28, 28));
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		cp.add(radioLeft, c);
		c.gridx = 1;
		cp.add(radioRight, c);

		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		c.weighty = 1;
		cp.add(dialogScroll, c);
		
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		c.gridx = 0;
		c.gridy = 3;
		c.weighty = 0;
		cp.add(buttonPanel, c);
	}

	private String getHeadDescription() {
		Head head = mainFrame.getEventManager().getHeadResManager().getHead(headID);
		return "头像[" + (head == null ? "无" : head.getName()) + "]";
	}

	public int getHeadID() {
		return headID;
	}

	private void setHeadID(int headID) {
		this.headID = headID;
		headText.setValue(getHeadDescription());
	}

	private void setHeadPos(int headPos) {
		switch (headPos) {
			case CommonDialog.HEAD_LEFT:
				radioLeft.setSelected(true);
				break;
			case CommonDialog.HEAD_RIGHT:
				radioRight.setSelected(true);
				break;
		}
	}

	public int getHeadPos() {
		if (radioLeft.isSelected()) {
			return CommonDialog.HEAD_LEFT;
		}
		else if (radioRight.isSelected()) {
			return CommonDialog.HEAD_RIGHT;
		}
		return -1;
	}

	private void setText(String text) {
		dialogText.setText(text);
	}

	public String getText() {
		String result = dialogText.getText();
		if (result == null) {
			result = "";
		}
		return result;
	}

	private void selectHead() {
		HeadSelecter headSelecter = new HeadSelecter(this, headID, mainFrame);
		headSelecter.show();
		if (headSelecter.getCloseType() == OKCancelDialog.OK_PERFORMED) {
			setHeadID(headSelecter.getHeadID());
		}
	}
	
	public Operation getOperation() {
		return commonDialog;
	}
	
	public void okPerformed() {
		commonDialog.setHeadID(getHeadID());
		commonDialog.setHeadPos(getHeadPos());
		commonDialog.setText(getText());
		this.closeType = OK_PERFORMED;
		dispose();
	}
	
	public void cancelPerformed() {
		dispose();
	}
}
