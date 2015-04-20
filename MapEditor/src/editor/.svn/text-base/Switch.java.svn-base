package editor;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

//public class Switch {
//    public Switch() {
//    }
//}

class SwitchManager {
	public final static int DEF_SWITCH_LENGTH = XUtil.getDefPropInt("SwitchsDefLength");

	String[] switchs;

	public SwitchManager() {
		switchs = createEmptySwitchs(DEF_SWITCH_LENGTH);
	}

	public void reset() {
		switchs = createEmptySwitchs(DEF_SWITCH_LENGTH);
	}

	public static String[] createEmptySwitchs(int length) {
		String[] result = new String[length];
		for (int i = 0; i < length; ++i) {
			result[i] = "";
		}
		return result;
	}

	public String[] getSwitchs() {
		return switchs;
	}

	public void setSwitchs(String[] switchs) {
		this.switchs = switchs;
	}

	public String getSwitch(int switchID) {
		if (switchID < 0 || switchID >= switchs.length) {
			return null;
		}
		else {
			return switchs[switchID];
		}
	}

	public void save(String name) throws Exception {
		File f = new File(XUtil.getDefPropStr("SwitchFilePath") + "\\" + name + "_Switch.dat");
		DataOutputStream out =
			new DataOutputStream(
			new BufferedOutputStream(
			new FileOutputStream(f)));
		out.writeInt(switchs.length);
		for (int i = 0; i < switchs.length; ++i) {
			SL.writeString(switchs[i], out);
		}
		out.flush();
		out.close();
	}

	public void load(String name) throws Exception {
		File f = new File(XUtil.getDefPropStr("SwitchFilePath") + "\\" + name + "_Switch.dat");
		if (!f.exists()) {
			return;
		}
		DataInputStream in =
			new DataInputStream(
			new BufferedInputStream(
			new FileInputStream(f)));
		int length = in.readInt();
		switchs = createEmptySwitchs(length);
		for (int i = 0; i < length; ++i) {
			switchs[i] = SL.readString(in);
		}
		in.close();
	}
}

class SwitchSelecter
	extends OKCancelDialog {
	private final static int GROUP_COUNT = XUtil.getDefPropInt("SwitchGroupLength");

	private String[] switchs;
	private JList groupList;
	private DefaultListModel groupModel;
	private JList switchList;
	private DefaultListModel switchModel;
	private JTextField switchText;
	private int selectedSwitch;
	private MainFrame mainFrame;

	public SwitchSelecter(JDialog owner, int switchID, MainFrame mainFrame) {
		super(owner);
		this.mainFrame = mainFrame;
		setTitle("选择开关量");

		iniSwitchs();

		groupModel = new DefaultListModel();
		groupList = new JList(groupModel);
		groupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		groupList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				groupSelectionChanged();
			}
		});
		JScrollPane groupScroll = new JScrollPane(groupList);

		JButton buttonAddGroup = new JButton("添加一组");
		buttonAddGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addAGroup();
			}
		});

		JPanel groupPanel = new JPanel();
		groupPanel.setLayout(new BorderLayout());
		groupPanel.add(groupScroll, BorderLayout.CENTER);
		groupPanel.add(buttonAddGroup, BorderLayout.SOUTH);

		switchModel = new DefaultListModel();
		switchList = new JList(switchModel);
		switchList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		switchList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				switchSelectionChanged();
			}
		});
		switchList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				switchListMouseClicked(e);
			}
		});
		JScrollPane switchScroll = new JScrollPane(switchList);

		switchText = new JTextField();
		switchText.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				switchTextChanged();
			}

			public void insertUpdate(DocumentEvent e) {
				switchTextChanged();
			}

			public void removeUpdate(DocumentEvent e) {
				switchTextChanged();
			}
		});
		switchText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okPerformed();
			}
		});

		JPanel switchPanel = new JPanel();
		switchPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;

		c.weightx = 1;
		c.weighty = 1;
		c.gridy = 0;
		switchPanel.add(switchScroll, c);

		switchText.setPreferredSize(new Dimension(25, 25));
		c.weighty = 0;
		c.gridy = 1;
		c.insets = new Insets(5, 2, 0, 0);
		switchPanel.add(switchText, c);

		iniGroupList();
		setSelectedSwitch(switchID);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, groupPanel, switchPanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(150);

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		Container cp = this.getContentPane();
		cp.add(splitPane, BorderLayout.CENTER);
		cp.add(buttonPanel, BorderLayout.SOUTH);
	}

	private void iniSwitchs() {
		String[] tmp = mainFrame.getEventManager().getSwitchManager().getSwitchs();
		int length;
		if (tmp == null) {
			length = GROUP_COUNT;
		}
		else {
			if (tmp.length % GROUP_COUNT != 0) {
				length = ( (int) (tmp.length / GROUP_COUNT) + 1) * GROUP_COUNT;
			}
			else {
				length = tmp.length;
			}
		}
		int oldLength;
		if (tmp == null) {
			oldLength = 0;
		}
		else {
			oldLength = tmp.length;
		}
		switchs = new String[length];
		for (int i = 0; i < oldLength; ++i) {
			switchs[i] = tmp[i];
		}
		for (int i = oldLength; i < length; ++i) {
			switchs[i] = "";
		}
	}

	private void iniGroupList() {
		int length = (int) (switchs.length / GROUP_COUNT);
		for (int i = 0; i < length; ++i) {
			groupModel.addElement(getGroupListItem(i * GROUP_COUNT, (i + 1) * GROUP_COUNT - 1));
		}
	}

	private String getGroupListItem(int start, int end) {
		int switchIDStringLength = XUtil.getDefPropInt("SwitchIDStringLength");
		return "[  "
			+ XUtil.getNumberString(start, switchIDStringLength)
			+ "  －  "
			+ XUtil.getNumberString(end, switchIDStringLength)
			+ "  ]";
	}

	private void addAGroup() {
		String[] tmp = switchs;
		switchs = new String[tmp.length + GROUP_COUNT];
		for (int i = 0; i < tmp.length; ++i) {
			switchs[i] = tmp[i];
		}
		for (int i = tmp.length; i < switchs.length; ++i) {
			switchs[i] = "";
		}
		groupModel.addElement(getGroupListItem(tmp.length, switchs.length - 1));
	}

	private void setSelectedSwitch(int switchID) {
		if (switchID < 0 || switchID >= switchs.length) {
			switchList.clearSelection();
			groupList.clearSelection();
		}
		else {
			groupList.setSelectedValue(groupModel.get( (int) (switchID / GROUP_COUNT)), true);
			switchList.setSelectedValue(switchModel.get( (int) (switchID % GROUP_COUNT)), true);
		}
	}

	private void groupSelectionChanged() {
		switchList.clearSelection();
		switchModel.clear();
		int groupID = groupList.getSelectedIndex();
		if (groupID >= 0 && groupID < (int) (switchs.length / GROUP_COUNT)) {
			for (int i = groupID * GROUP_COUNT; i < (groupID + 1) * GROUP_COUNT; ++i) {
				switchModel.addElement(getSwitchListItem(i));
			}
		}
	}

	private String getSwitchListItem(int switchIndex) {
		return Event.getSwitchDescription(switchIndex, switchs);
	}

	private void switchSelectionChanged() {
		selectedSwitch = -1;
		switchText.setText(null);
		int groupID = groupList.getSelectedIndex();
		int switchID = switchList.getSelectedIndex();
		if (groupID >= 0 && groupID < (int) (switchs.length / GROUP_COUNT)) {
			if (switchID >= 0 && switchID < GROUP_COUNT) {
				selectedSwitch = groupID * GROUP_COUNT + switchID;
				switchText.setText(switchs[selectedSwitch]);
			}
		}
	}

	private void switchTextChanged() {
		if (selectedSwitch >= 0 && selectedSwitch < switchs.length) {
			switchs[selectedSwitch] = switchText.getText();
			switchModel.set( (int) (selectedSwitch % GROUP_COUNT),
							getSwitchListItem(selectedSwitch));
		}
	}

	public int getSelectedSwitchID() {
		return selectedSwitch;
	}

	public void switchListMouseClicked(MouseEvent e) {
		if (e.getButton() == MouseInfo.LEFT_BUTTON && e.getClickCount() == 2) {
			okPerformed();
		}
	}

	public void okPerformed() {
		if (selectedSwitch >= 0 && selectedSwitch < switchs.length) {
			closeType = OK_PERFORMED;
			mainFrame.getEventManager().getSwitchManager().setSwitchs(switchs);
			dispose();
		}
		else {
			JOptionPane.showMessageDialog(this, "必须选择一个开关量",
										  "错误", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void cancelPerformed() {
		dispose();
	}
}

class RadioPanel extends JPanel {
	public final static int[] VALUES = {1, 0};
	public final static String[] DESCS_ON = {"On", "Off"};
	public final static String[] DESCS_YES = {"是", "否"};
	public final static String[] DESCS_CAN = {"能", "不能"};
	
	private int[] values;
	private JRadioButton[] radios;
	
	public RadioPanel() {
		init(VALUES, DESCS_ON);
	}
	
	public RadioPanel(int[] values) {
		init(values, DESCS_ON);
	}
	
	public RadioPanel(String[] descs) {
		init(VALUES, descs);
	}
	
	public RadioPanel(int[] values, String[] descs) {
		init(values, descs);
	}
	
	private void init(int[] values, String[] descs) {
		this.values = XUtil.copyArray(values);
		radios = new JRadioButton[values.length];
		this.setLayout(new GridLayout(1, radios.length));
		ButtonGroup radioGroup = new ButtonGroup();
		
		for(int i = 0; i < values.length; ++i) {
			String title = "未知";
			if(i < descs.length) {
				if(descs[i] != null) {
					title = descs[i];
				}
			}
			radios[i] = new JRadioButton(title);
		    radioGroup.add(radios[i]);
			this.add(radios[i]);
		}
	}
	
	public void setValue(boolean on) {
		int value = on ? 1 : 0;
		setValue(value);
	}
	
	public void setValue(int value) {
		for(int i = 0; i < values.length ;++i) {
			if(value == values[i]) {
				radios[i].setSelected(true);
				break;
			}
		}
	}
	
	public int getIntValue() {
		int result = 0;
		for(int i = 0; i < radios.length; ++i) {
			if(radios[i].isSelected()) {
				result = values[i];
				break;
			}
		}
		return result;
	}
	
	public boolean getBoolValue() {
		int value = getIntValue();
		boolean result = value == 0 ? false : true;
		return result;
	}
}

class SwitchSetPanel
	extends JPanel {

	private ButtonText switchText;
	private RadioPanel radioPanel;
	private int switchID;
	private int switchState;
	private JDialog owner;
	private MainFrame mainFrame;

	public SwitchSetPanel(JDialog owner, MainFrame mainFrame, int switchID, int switchState) {
		super();
		this.owner = owner;
		this.mainFrame = mainFrame;
		this.switchID = switchID;
		this.switchState = switchState;

		switchText = new ButtonText(Event.getSwitchDescription(switchID));
		switchText.setActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectSwitch();
			}
		});

		radioPanel = new RadioPanel();
		radioPanel.setValue(switchState);
		
		this.setLayout(new GridLayout(2, 1));
		this.add(switchText);
		this.add(radioPanel);
	}

	public int getSwitchID() {
		return switchID;
	}

	public void setSwitchID(int switchID) {
		this.switchID = switchID;
		switchText.setValue(Event.getSwitchDescription(switchID));
	}

	public int getSwitchState() {
		return radioPanel.getIntValue();
	}

	private void selectSwitch() {
		SwitchSelecter selecter = new SwitchSelecter(owner, switchID, mainFrame);
		selecter.show();
		if (selecter.getCloseType() == OKCancelDialog.OK_PERFORMED) {
			setSwitchID(selecter.getSelectedSwitchID());
		}
	}
}

class SwitchTriggerSetter
	extends TriggerSetter {
	private SwitchSetPanel switchSetPanel;

	public SwitchTriggerSetter(JDialog owner, MainFrame mainFrame, Trigger trigger) {
		super(owner, mainFrame, trigger);
		setTitle("选择开关量及状态");

		int switchState = 1;
		int[] data = trigger.getData();
		if (data != null) {
			if (data.length > 0) {
				switchState = data[0];
			}
		}
		switchSetPanel = new SwitchSetPanel(this, mainFrame, trigger.getTargetID(), switchState);

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(switchSetPanel, BorderLayout.CENTER);
		cp.add(buttonPanel, BorderLayout.SOUTH);
	}

	public void okPerformed() {
		try {
			int switchID = switchSetPanel.getSwitchID();
			String[] switchs = mainFrame.getEventManager().getSwitchManager().getSwitchs();
			if (switchID < 0 || switchID >= switchs.length) {
				throw new Exception("必须选择一个正确的开关量！");
			}
			trigger.setTargetID(switchID);
			trigger.setData(new int[] {switchSetPanel.getSwitchState()});
			closeType = OK_PERFORMED;
			dispose();
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, e, "逻辑错误", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void cancelPerformed() {
		dispose();
	}

}

class SwitchChange
	extends Operation {
	private int switchID;
	private int switchState;

	public SwitchChange(int id) {
		super(id, SWITCH_CHANGE);
		init();
	}

	public SwitchChange(int id, int type) {
		super(id, type);
		init();
	}

	private void init() {
		switchID = -1;
		switchState = 1;
	}

	public int getSwitchID() {
		return switchID;
	}

	public void setSwitchID(int switchID) {
		this.switchID = switchID;
	}

	public int getSwitchState() {
		return switchState;
	}

	public void setSwitchState(int switchState) {
		this.switchState = switchState;
	}

	public void saveMobileData(DataOutputStream out, StringManager stringManager) throws Exception {
		super.saveMobileData(out, stringManager);
		SL.writeInt(switchID, out);
		SL.writeInt(switchState, out);
	}

	public void save(DataOutputStream out, StringManager stringManager) throws Exception {
		super.save(out, stringManager);
		out.writeInt(switchID);
		out.writeInt(switchState);
	}

	protected void load(DataInputStream in, StringManager stringManager) throws Exception {
		super.load(in, stringManager);
		init();

		switchID = in.readInt();
		switchState = in.readInt();
	}

	public String getListItemDescription() {
		String result = "设置" + Event.getSwitchDescription(switchID) + "为" +
						(switchState == 0 ? "Off" : "On");
		return result;
	}

	public Operation getCopy() {
		SwitchChange result = (SwitchChange) (Operation.createInstance(this.id, this.type));
		result.switchID = this.switchID;
		result.switchState = this.switchState;
		return result;
	}
}

class SwitchChangeSetter
	extends OperationSetter {
	private SwitchChange switchChange;
	private SwitchSetPanel switchSetPanel;

	public SwitchChangeSetter(JDialog owner, MainFrame mainFrame, SwitchChange switchChange) {
		super(owner, mainFrame);
		init(switchChange);
	}

	private void init(SwitchChange switchChange) {
		this.switchChange = switchChange;
		this.setTitle("设置开关量");

		switchSetPanel = new SwitchSetPanel(this, mainFrame,
											switchChange.getSwitchID(), switchChange.getSwitchState());

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(switchSetPanel, BorderLayout.CENTER);
		cp.add(buttonPanel, BorderLayout.SOUTH);
	}

	public Operation getOperation() {
		return switchChange;
	}

	protected void okPerformed() {
		try {
			int switchID = switchSetPanel.getSwitchID();
			String[] switchs = mainFrame.getEventManager().getSwitchManager().getSwitchs();
			if (switchID < 0 || switchID >= switchs.length) {
				throw new Exception("必须选择一个正确的开关量！");
			}
			switchChange.setSwitchID(switchID);
			switchChange.setSwitchState(switchSetPanel.getSwitchState());
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
