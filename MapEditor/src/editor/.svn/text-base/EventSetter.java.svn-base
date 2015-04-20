package editor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 设置Event的属性。
 @see Event
 */
public class EventSetter
	extends OKCancelDialog {
	private Event event;
	private MainFrame mainFrame;
	private TriggerList triggerList;
	private OperationList operationList;
	private JTextField nameText;
	private JButton unitOwnerButton, eventobjectOwnerButton, clearOwnerButton;
	private int ownerType, ownerID;
	private JLabel ownerInfoLabel;
	private int eventType;

	public EventSetter(JDialog owner, Event event, MainFrame mainFrame) {
		super(owner);
		init(event, mainFrame);
	}

	public EventSetter(JFrame owner, Event event, MainFrame mainFrame) {
		super(owner);
		init(event, mainFrame);
	}

	private void init(Event event, MainFrame mainFrame) {
		this.event = event;
		this.mainFrame = mainFrame;
		ownerType = event.getOwnerType();
		ownerID = event.getOwnerID();
		setTitle("设置事件");

		JPanel headPanel = new JPanel();
		headPanel.setLayout(new GridLayout(1, 5));
		nameText = new JTextField(event.getName());
		headPanel.add(nameText);
		ownerInfoLabel = new JLabel("", SwingConstants.CENTER);
		headPanel.add(ownerInfoLabel);
		refreshOwnerInfo();
		unitOwnerButton = new JButton("属于Unit");
		unitOwnerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setUnitOwner();
			};
		});
		headPanel.add(unitOwnerButton);
		eventobjectOwnerButton = new JButton("属于事件物体");
		eventobjectOwnerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setEventobjectOwner();
			};
		});
		headPanel.add(eventobjectOwnerButton);
		clearOwnerButton = new JButton("不属于任何事物");
		clearOwnerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearOwner();
			};
		});
		headPanel.add(clearOwnerButton);
		headPanel.setPreferredSize(new Dimension(100, 25));

		triggerList = new TriggerList(event.getTriggers(), this, mainFrame);
		operationList = new OperationList(event.getOperations(), this, mainFrame);
		
		JPanel triggerPanel = new JPanel();
		triggerPanel.setLayout(new BorderLayout());
		triggerPanel.add(new JLabel("触发条件", JLabel.CENTER), BorderLayout.NORTH);
		triggerPanel.add(new JScrollPane(triggerList));
		
		JPanel operationPanel = new JPanel();
		operationPanel.setLayout(new BorderLayout());
		operationPanel.add(new JLabel("执行的操作", JLabel.CENTER), BorderLayout.NORTH);
		operationPanel.add(new JScrollPane(operationList));
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
											  triggerPanel, operationPanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(300);

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		Container cp = this.getContentPane();
		cp.add(headPanel, BorderLayout.NORTH);
		cp.add(splitPane, BorderLayout.CENTER);
		cp.add(buttonPanel, BorderLayout.SOUTH);
	}

	public Event getEvent() {
		return event;
	}

	public int getEventType() {
		return eventType;
	}

	private void refreshOwnerInfo() {
//		String info = "";
//		switch (ownerType) {
//			case Event.OWNER_Unit:
//				info = "属于" + Event.getUnitDescription(ownerID);
//				break;
//			case Event.OWNER_EVENTOBJECT:
//				info = "属于" + XUtil.getEventobjectDescription(ownerID);
//				break;
//			case Event.OWNER_NONE:
//				info = "不属于任何事物";
//				break;
//		}
//		ownerInfoLabel.setText(info);
	}

	private void setUnitOwner() {
//		int unitID = (ownerType == Event.OWNER_Unit) ? ownerID : -1;
//		UnitChooser unitChooser = new UnitChooser(this, mainFrame, unitID, -1);
//		unitChooser.show();
//		if (unitChooser.getCloseType() == OKCancelDialog.OK_PERFORMED) {
//			ownerType = Event.OWNER_Unit;
//			ownerID = unitChooser.getUnitID();
//			refreshOwnerInfo();
//		}
	}

	private void setEventobjectOwner() {
//		int eventobjectID = (ownerType == Event.OWNER_EVENTOBJECT) ? ownerID : -1;
//		EventobjectChooser eventobjectChooser = new EventobjectChooser(this, mainFrame, eventobjectID, -1);
//		eventobjectChooser.show();
//		if (eventobjectChooser.getCloseType() == OKCancelDialog.OK_PERFORMED) {
//			ownerType = Event.OWNER_EVENTOBJECT;
//			ownerID = eventobjectChooser.getEventobjectID();
//			refreshOwnerInfo();
//		}
	}

	private void clearOwner() {
		ownerType = Event.OWNER_NONE;
		ownerID = -1;
		refreshOwnerInfo();
	}

	protected void okPerformed() {
		String name = nameText.getText().trim();
		if (name.equals("")) {
			JOptionPane.showMessageDialog(this, "名称不允许为空", "错误", JOptionPane.ERROR_MESSAGE);
		}
		event.setName(name);
		event.setOwnerType(ownerType);
		event.setOwnerID(ownerID);
		event.setTriggers(triggerList.getTriggers());
		event.setOperations(operationList.getOperations());
		closeType = OK_PERFORMED;
		dispose();
	}

	protected void cancelPerformed() {
		dispose();
	}
}