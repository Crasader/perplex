package editor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class URDBSetter
	extends OKCancelDialog {
	private URManager urManager;
	private ARManager arManager;
	private JList urList;
	private DefaultListModel listModel;
	private URPropPanel[] propPanels;
	private int maxID;

	public URDBSetter(JFrame owner, URManager urManager, ARManager arManager) {
		super(owner);
		init(urManager, arManager);
	}

	public URDBSetter(JDialog owner, URManager urManager, ARManager arManager) {
		super(owner);
		init(urManager, arManager);
	}

	private void init(URManager urManager, ARManager arManager) {
		this.urManager = urManager;
		this.arManager = arManager;

		setTitle("管理作战单位资源");

		initPropPanels();

		maxID = 0;
		listModel = new DefaultListModel();
		UnitRes[] urs = urManager.getURs();
		if (urs != null) {
			for (int i = 0; i < urs.length; ++i) {
				listModel.addElement(urs[i].getCopy());
				if (maxID <= urs[i].getID()) {
					maxID = urs[i].getID() + 1;
				}
			}
		}

		urList = new JList(listModel);
		urList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent listSelectionEvent) {
				urChanged();
			}
		});
		SwingUtil.MakeListDeleteable(urList, null);
		SwingUtil.SetObjListRenderer(urList);

		JButton addButton = new JButton("添加一种作战单位");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addUR();
			}
		});

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setInputMap(JTabbedPane.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, new InputMap());

		if (propPanels != null) {
			for (int i = 0; i < propPanels.length; ++i) {
				if (propPanels[i] != null) {
					if (propPanels[i] instanceof ScrollablePanel) {
						tabbedPane.add(propPanels[i].getName(),
									   ( (ScrollablePanel) propPanels[i]).getBackPanel());
					}
					else if (propPanels[i] instanceof Component) {
						tabbedPane.add(propPanels[i].getName(),
									   (Component) propPanels[i]);
					}
				}
			}
		}

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(urList);
		SwingUtil.setDefScrollIncrement(scrollPane);
		leftPanel.add(scrollPane, BorderLayout.CENTER);
		leftPanel.add(addButton, BorderLayout.SOUTH);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, tabbedPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(200);

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(splitPane, BorderLayout.CENTER);
		cp.add(buttonPanel, BorderLayout.SOUTH);
	}

	private void initPropPanels() {
		propPanels = new URPropPanel[5];
		int i = 0;
		propPanels[i++] = new URBasicPanel(this);
		propPanels[i++] = new URBodyPanel(this);
		propPanels[i++] = new URMovePanel(this);
		propPanels[i++] = new URDeadPanel(this, arManager);
		propPanels[i++] = new URWeaponPosPanel(this);
	}

	public void setSelectedUR(UnitRes ur) {
		urList.setSelectedIndex( -1);
		if (ur != null) {
			for (int i = 0; i < listModel.size(); ++i) {
				UnitRes tmp = (UnitRes) (listModel.get(i));
				if (tmp.getID() == ur.getID()) {
					urList.setSelectedValue(tmp, true);
				}
			}
		}
	}

	private void urChanged() {
		//save
		if (propPanels != null) {
			for (int i = 0; i < propPanels.length; ++i) {
				if (propPanels[i] != null) {
					propPanels[i].updateURProp();
				}
			}
		}

		//load
		UnitRes ur;
		Object obj = urList.getSelectedValue();
		if (obj == null) {
			ur = new UnitRes();
		}
		else {
			ur = (UnitRes) obj;
		}
		if (propPanels != null) {
			for (int i = 0; i < propPanels.length; ++i) {
				if (propPanels[i] != null) {
					propPanels[i].setUR(ur);
				}
			}
		}
	}

	private void addUR() {
		AnimSelecter selecter = new AnimSelecter(this, -1, arManager);
		selecter.show();
		if (selecter.getCloseType() == OKCancelDialog.OK_PERFORMED) {
			UnitRes ur = new UnitRes(maxID++);
			ur.setAnim(selecter.getSelectedAnim());
			listModel.addElement(ur);
		}
	}

	protected void okPerformed() {
		//save
		if (propPanels != null) {
			for (int i = 0; i < propPanels.length; ++i) {
				if (propPanels[i] != null) {
					propPanels[i].updateURProp();
				}
			}
		}

		UnitRes[] urs = new UnitRes[listModel.size()];
		for (int i = 0; i < listModel.size(); ++i) {
			urs[i] = (UnitRes) (listModel.get(i));
		}
		urManager.setURs(urs);
		closeType = OK_PERFORMED;
		dispose();
	}

	protected void cancelPerformed() {
		dispose();
	}

	public void dispose() {
		if (propPanels != null) {
			for (int i = 0; i < propPanels.length; ++i) {
				if (propPanels[i] != null) {
					propPanels[i].dispose();
				}
			}
		}
		super.dispose();
	}
}