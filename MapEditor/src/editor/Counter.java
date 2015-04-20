package editor;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

class CounterManager {
	public final static int DEF_COUNTER_LENGTH = XUtil.getDefPropInt("CountersDefLength");

	String[] counters;

	public CounterManager() {
		counters = createEmptyCounters(DEF_COUNTER_LENGTH);
	}

	public void reset() {
		counters = createEmptyCounters(DEF_COUNTER_LENGTH);
	}

	public static String[] createEmptyCounters(int length) {
		String[] result = new String[length];
		for (int i = 0; i < length; ++i) {
			result[i] = "";
		}
		return result;
	}

	public String[] getCounters() {
		return counters;
	}

	public void setCounters(String[] counters) {
		this.counters = counters;
	}

	public String getCounter(int counterID) {
		if (counterID < 0 || counterID >= counters.length) {
			return null;
		}
		else {
			return counters[counterID];
		}
	}

	public void save(String name) throws Exception {
		File f = new File(XUtil.getDefPropStr("CounterFilePath") + "\\" + name + "_Counter.dat");
		DataOutputStream out =
			new DataOutputStream(
			new BufferedOutputStream(
			new FileOutputStream(f)));
		out.writeInt(counters.length);
		for (int i = 0; i < counters.length; ++i) {
			SL.writeString(counters[i], out);
		}
		out.flush();
		out.close();
	}

	public void load(String name) throws Exception {
		File f = new File(XUtil.getDefPropStr("CounterFilePath") + "\\" + name + "_Counter.dat");
		if (!f.exists()) {
			return;
		}
		DataInputStream in =
			new DataInputStream(
			new BufferedInputStream(
			new FileInputStream(f)));
		int length = in.readInt();
		counters = createEmptyCounters(length);
		for (int i = 0; i < length; ++i) {
			counters[i] = SL.readString(in);
		}
		in.close();
	}
}

class CounterSelecter
	extends OKCancelDialog {
	private final static int GROUP_LENGTH = XUtil.getDefPropInt("CounterGroupLength");

	private String[] counters;
	private JList groupList;
	private DefaultListModel groupModel;
	private JList counterList;
	private DefaultListModel counterModel;
	private JTextField counterText;
	private int selectedCounter;
	private MainFrame mainFrame;

	public CounterSelecter(JDialog owner, int counterID, MainFrame mainFrame) {
		super(owner);
		this.mainFrame = mainFrame;
		setTitle("选择计数器");

		iniCounters();

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

		counterModel = new DefaultListModel();
		counterList = new JList(counterModel);
		counterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		counterList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				counterSelectionChanged();
			}
		});
		counterList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				counterListMouseClicked(e);
			}
		});
		JScrollPane counterScroll = new JScrollPane(counterList);

		counterText = new JTextField();
		counterText.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				counterTextChanged();
			}

			public void insertUpdate(DocumentEvent e) {
				counterTextChanged();
			}

			public void removeUpdate(DocumentEvent e) {
				counterTextChanged();
			}
		});
		counterText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okPerformed();
			}
		});

		JPanel counterPanel = new JPanel();
		counterPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;

		c.weightx = 1;
		c.weighty = 1;
		c.gridy = 0;
		counterPanel.add(counterScroll, c);

		counterText.setPreferredSize(new Dimension(25, 25));
		c.weighty = 0;
		c.gridy = 1;
		c.insets = new Insets(5, 2, 0, 0);
		counterPanel.add(counterText, c);

		iniGroupList();
		setSelectedCounter(counterID);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, groupPanel, counterPanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(150);

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		Container cp = this.getContentPane();
		cp.add(splitPane, BorderLayout.CENTER);
		cp.add(buttonPanel, BorderLayout.SOUTH);
	}

	private void iniCounters() {
		String[] tmp = mainFrame.getEventManager().getCounterManager().getCounters();
		int length;
		if (tmp == null) {
			length = GROUP_LENGTH;
		}
		else {
			if (tmp.length % GROUP_LENGTH != 0) {
				length = ( (int) (tmp.length / GROUP_LENGTH) + 1) * GROUP_LENGTH;
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
		counters = new String[length];
		for (int i = 0; i < oldLength; ++i) {
			counters[i] = tmp[i];
		}
		for (int i = oldLength; i < length; ++i) {
			counters[i] = "";
		}
	}

	private void iniGroupList() {
		int length = (int) (counters.length / GROUP_LENGTH);
		for (int i = 0; i < length; ++i) {
			groupModel.addElement(getGroupListItem(i * GROUP_LENGTH, (i + 1) * GROUP_LENGTH - 1));
		}
	}

	private String getGroupListItem(int start, int end) {
		int counterIDStringLength = XUtil.getDefPropInt("CounterIDStringLength");
		return "[  "
			+ XUtil.getNumberString(start, counterIDStringLength)
			+ "  －  "
			+ XUtil.getNumberString(end, counterIDStringLength)
			+ "  ]";
	}

	private void addAGroup() {
		String[] tmp = counters;
		counters = new String[tmp.length + GROUP_LENGTH];
		for (int i = 0; i < tmp.length; ++i) {
			counters[i] = tmp[i];
		}
		for (int i = tmp.length; i < counters.length; ++i) {
			counters[i] = "";
		}
		groupModel.addElement(getGroupListItem(tmp.length, counters.length - 1));
	}

	private void setSelectedCounter(int counterID) {
		if (counterID < 0 || counterID >= counters.length) {
			counterList.clearSelection();
			groupList.clearSelection();
		}
		else {
			groupList.setSelectedValue(groupModel.get( (int) (counterID / GROUP_LENGTH)), true);
			counterList.setSelectedValue(counterModel.get( (int) (counterID % GROUP_LENGTH)), true);
		}
	}

	private void groupSelectionChanged() {
		counterList.clearSelection();
		counterModel.clear();
		int groupID = groupList.getSelectedIndex();
		if (groupID >= 0 && groupID < (int) (counters.length / GROUP_LENGTH)) {
			for (int i = groupID * GROUP_LENGTH; i < (groupID + 1) * GROUP_LENGTH; ++i) {
				counterModel.addElement(getCounterListItem(i));
			}
		}
	}

	private String getCounterListItem(int counterIndex) {
		return Event.getCounterDescription(counterIndex, counters);
	}

	private void counterSelectionChanged() {
		selectedCounter = -1;
		counterText.setText(null);
		int groupID = groupList.getSelectedIndex();
		int counterID = counterList.getSelectedIndex();
		if (groupID >= 0 && groupID < (int) (counters.length / GROUP_LENGTH)) {
			if (counterID >= 0 && counterID < GROUP_LENGTH) {
				selectedCounter = groupID * GROUP_LENGTH + counterID;
				counterText.setText(counters[selectedCounter]);
			}
		}
	}

	private void counterTextChanged() {
		if (selectedCounter >= 0 && selectedCounter < counters.length) {
			counters[selectedCounter] = counterText.getText();
			counterModel.set( (int) (selectedCounter % GROUP_LENGTH),
							 getCounterListItem(selectedCounter));
		}
	}

	public int getSelectedCounterID() {
		return selectedCounter;
	}

	public void counterListMouseClicked(MouseEvent e) {
		if (e.getButton() == MouseInfo.LEFT_BUTTON && e.getClickCount() == 2) {
			okPerformed();
		}
	}

	public void okPerformed() {
		if (selectedCounter >= 0 && selectedCounter < counters.length) {
			closeType = OK_PERFORMED;
			mainFrame.getEventManager().getCounterManager().setCounters(counters);
			dispose();
		}
		else {
			JOptionPane.showMessageDialog(this, "必须选择一个计数器",
										  "错误", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void cancelPerformed() {
		dispose();
	}
}

class CounterSetPanel
	extends JPanel {

	private ButtonText counterText;
	private ValueChooser relationChooser;
	private NumberSpinner valueSpinner;
	private int counterID;
	private int counterRelation;
	private int counterValue;
	private JDialog owner;
	private MainFrame mainFrame;

	public CounterSetPanel(JDialog owner, MainFrame mainFrame, int type,
						   int counterID, int counterRelation, int counterValue) {
		super();
		this.owner = owner;
		this.mainFrame = mainFrame;
		this.counterID = counterID;
		this.counterRelation = counterRelation;
		this.counterValue = counterValue;

		counterText = new ButtonText(Event.getCounterDescription(counterID));
		counterText.setActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectCounter();
			}
		});

		int[] values = Relation.RELATIONS[type];
		String[] descriptions = Relation.DESCRIPTIONS;
		relationChooser = new ValueChooser(counterRelation, values, descriptions);
		
		valueSpinner = new NumberSpinner();
		valueSpinner.setIntValue(counterValue);

		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(0, 0, 0, 5); 
		
		c.gridx = 0;
		c.gridy = 0;
		this.add(counterText, c);
		
		c.gridx = 2;
		this.add(valueSpinner, c);
		
		c.gridx = 1;
		c.weightx = 0;
		this.add(relationChooser, c);
	}

	public int getCounterID() {
		return counterID;
	}

	public void setCounterID(int counterID) {
		this.counterID = counterID;
		counterText.setValue(Event.getCounterDescription(counterID));
	}

	public int getCounterRelation() {
		return relationChooser.getValue();
	}
	
	public int getCounterValue() {
		return valueSpinner.getIntValue();
	}

	private void selectCounter() {
		CounterSelecter selecter = new CounterSelecter(owner, counterID, mainFrame);
		selecter.show();
		if (selecter.getCloseType() == OKCancelDialog.OK_PERFORMED) {
			setCounterID(selecter.getSelectedCounterID());
		}
	}
}

class CounterTriggerSetter
	extends TriggerSetter {
	private CounterSetPanel counterSetPanel;

	public CounterTriggerSetter(JDialog owner, MainFrame mainFrame, Trigger trigger) {
		super(owner, mainFrame, trigger);
		setTitle("设置计数器");

		int counterRelation = Relation.EQUAL;
		int counterValue = 0;
		int[] data = trigger.getData();
		if (data != null) {
			if (data.length > 0) {
				counterRelation = data[0];
			}
			if(data.length > 1) {
				counterValue = data[1];
			}
		}
		counterSetPanel = new CounterSetPanel(this, mainFrame, Relation.TYPE_COMPARE, 
											  trigger.getTargetID(), counterRelation, counterValue);

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(counterSetPanel, BorderLayout.CENTER);
		cp.add(buttonPanel, BorderLayout.SOUTH);
	}

	public void okPerformed() {
		try {
			int counterID = counterSetPanel.getCounterID();
			String[] counters = mainFrame.getEventManager().getCounterManager().getCounters();
			if (counterID < 0 || counterID >= counters.length) {
				throw new Exception("必须选择一个正确的计数器！");
			}
			
			int counterRelation = counterSetPanel.getCounterRelation();
			if(counterRelation < 0) {
				throw new Exception("必须选择一个正确的比较关系");
			}
			
			int counterValue = counterSetPanel.getCounterValue();
			
			trigger.setTargetID(counterID);
			trigger.setData(new int[] {counterRelation, counterValue});
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

class CounterChange
	extends Operation {
	private int counterID;
	private int counterRelation;
	private int counterValue;

	public CounterChange(int id) {
		super(id, COUNTER_CHANGE);
		init();
	}

	public CounterChange(int id, int type) {
		super(id, type);
		init();
	}

	private void init() {
		counterID = -1;
		counterRelation = Relation.SET;
		counterValue = 0;
	}

	public int getCounterID() {
		return counterID;
	}

	public void setCounterID(int counterID) {
		this.counterID = counterID;
	}

	public int getCounterRelation() {
		return counterRelation;
	}

	public void setCounterRelation(int counterRelation) {
		this.counterRelation = counterRelation;
	}
	
	public int getCounterValue() {
		return counterValue;
	}
	
	public void setCounterValue(int counterValue) {
		this.counterValue = counterValue;
	}

	public void saveMobileData(DataOutputStream out, StringManager stringManager) throws Exception {
		super.saveMobileData(out, stringManager);
		SL.writeInt(counterID, out);
		SL.writeInt(counterRelation, out);
		SL.writeInt(counterValue, out);
	}

	public void save(DataOutputStream out, StringManager stringManager) throws Exception {
		super.save(out, stringManager);
		out.writeInt(counterID);
		out.writeInt(counterRelation);
		out.writeInt(counterValue);
	}

	protected void load(DataInputStream in, StringManager stringManager) throws Exception {
		super.load(in, stringManager);
		init();

		counterID = in.readInt();
		counterRelation = in.readInt();
		counterValue = in.readInt();
	}

	public String getListItemDescription() {
		String result = Event.getCounterDescription(counterID) + 
						Relation.DESCRIPTIONS[counterRelation] + counterValue;
		return result;
	}

	public Operation getCopy() {
		CounterChange result = (CounterChange) (Operation.createInstance(this.id, this.type));
		result.counterID = this.counterID;
		result.counterRelation = this.counterRelation;
		result.counterValue = this.counterValue;
		return result;
	}
}

class CounterChangeSetter
	extends OperationSetter {
	private CounterChange counterChange;
	private CounterSetPanel counterSetPanel;

	public CounterChangeSetter(JDialog owner, MainFrame mainFrame, CounterChange counterChange) {
		super(owner, mainFrame);
		init(counterChange);
	}

	private void init(CounterChange counterChange) {
		this.counterChange = counterChange;
		this.setTitle("设置计数器");

		counterSetPanel = new CounterSetPanel(this, mainFrame, Relation.TYPE_MODIFY, 
											  counterChange.getCounterID(), 
											  counterChange.getCounterRelation(), 
											  counterChange.getCounterValue());

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(counterSetPanel, BorderLayout.CENTER);
		cp.add(buttonPanel, BorderLayout.SOUTH);
	}

	public Operation getOperation() {
		return counterChange;
	}

	protected void okPerformed() {
		try {
			int counterID = counterSetPanel.getCounterID();
			String[] counters = mainFrame.getEventManager().getCounterManager().getCounters();
			if (counterID < 0 || counterID >= counters.length) {
				throw new Exception("必须选择一个正确的计数器！");
			}
			
			int counterRelation = counterSetPanel.getCounterRelation();
			if(counterRelation < 0) {
				throw new Exception("必须选择一个正确的动作");
			}
			
			counterChange.setCounterID(counterID);
			counterChange.setCounterRelation(counterRelation);
			counterChange.setCounterValue(counterSetPanel.getCounterValue());
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
