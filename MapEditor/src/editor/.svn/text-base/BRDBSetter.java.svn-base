package editor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

class BRCreator extends OKCancelDialog {
	private BuildingRes br;
	
	public BRCreator(JDialog owner) {
		super(owner);
		setTitle("新建一种建筑");
		
		br = null;
		
		ActionListener buttonListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int type = Integer.parseInt(e.getActionCommand());
				br = BuildingRes.createInstance(0, type);
				okPerformed();
			}
		};

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout(BuildingRes.TYPE_COUNT + 1, 1, 5, 5));
		centerPanel.add(new JLabel("选择建筑的类型："));

		JButton[] buttons = new JButton[BuildingRes.TYPE_COUNT];
		for(int i = 0; i < buttons.length; ++i) {
			buttons[i] = new JButton(BuildingRes.DESCS[i]);
			buttons[i].setActionCommand(i + "");
			buttons[i].addActionListener(buttonListener);
			centerPanel.add(buttons[i]);
		}
		
		buttonPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		buttonPanel.add(new JLabel(), c);
		c.gridx = 1;
		c.weightx = 0;
		buttonPanel.add(cancelButton, c);
		
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(centerPanel, BorderLayout.CENTER);
		cp.add(buttonPanel, BorderLayout.SOUTH);
	}
	
	public BuildingRes getBR() {
		return br;
	}
	
	public void okPerformed() {
		this.closeType = OK_PERFORMED;
		dispose();
	}
	
	public void cancelPerformed() {
		dispose();
	}
}

public class BRDBSetter
	extends OKCancelDialog {
	private BRManager brManager;
	private ARManager arManager;
	private SIManager siManager;
	private JList brList;
	private DefaultListModel listModel;
	private JTabbedPane tabbedPane;
	private BRPropPanel[] propPanels;
	private int maxID;

	public BRDBSetter(JFrame owner, BRManager brManager, ARManager arManager, SIManager siManager) {
		super(owner);
		init(brManager, arManager, siManager);
	}

	public BRDBSetter(JDialog owner, BRManager brManager, ARManager arManager, SIManager siManager) {
		super(owner);
		init(brManager, arManager, siManager);
	}

	private void init(BRManager brManager, ARManager arManager, SIManager siManager) {
		this.brManager = brManager;
		this.arManager = arManager;
		this.siManager = siManager;

		setTitle("管理建筑资源");

		initPropPanels();

		maxID = 0;
		listModel = new DefaultListModel();
		BuildingRes[] urs = brManager.getBRs();
		if (urs != null) {
			for (int i = 0; i < urs.length; ++i) {
				listModel.addElement(urs[i].getCopy());
				if (maxID <= urs[i].getID()) {
					maxID = urs[i].getID() + 1;
				}
			}
		}

		brList = new JList(listModel);
		brList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent listSelectionEvent) {
				brChanged();
			}
		});
		SwingUtil.MakeListDeleteable(brList, null);
		SwingUtil.SetObjListRenderer(brList);

		JButton addButton = new JButton("添加一种建筑");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addBR();
			}
		});

		tabbedPane = new JTabbedPane();
		tabbedPane.setInputMap(JTabbedPane.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, new InputMap());
		
		if(propPanels != null) {
			for(int i = 0; i < propPanels.length; ++i) {
				addPropPanel(propPanels[i]);
			}
		}

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(brList);
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
		propPanels = new BRPropPanel[3];
		int i = 0;
		propPanels[i++] = new BRBasicPanel(this);
		propPanels[i++] = new BRBodyPanel(this);
		propPanels[i++] = new BRExplorePanel(this, arManager);
	}

	private void addPropPanel(BRPropPanel panel) {
		if (panel != null) {
			if (panel instanceof ScrollablePanel) {
				tabbedPane.add(panel.getName(),
							   ( (ScrollablePanel) panel).getBackPanel());
			}
			else if (panel instanceof Component) {
				tabbedPane.add(panel.getName(),
							   (Component) panel);
			}
		}
	}

	public void setSelectedBR(BuildingRes br) {
		brList.setSelectedIndex( -1);
		if (br != null) {
			for (int i = 0; i < listModel.size(); ++i) {
				BuildingRes tmp = (BuildingRes) (listModel.get(i));
				if (tmp.getID() == br.getID()) {
					brList.setSelectedValue(tmp, true);
				}
			}
		}
	}

	private void brChanged() {
		//save
		if (propPanels != null) {
			for (int i = 0; i < propPanels.length; ++i) {
				if (propPanels[i] != null) {
					propPanels[i].updateBRProp();
				}
			}
		}

		//load
		BuildingRes br;
		Object obj = brList.getSelectedValue();
		if (obj == null) {
			return;
//			br = new BuildingRes();
		}
		else {
			br = (BuildingRes) obj;
		}
		if (propPanels != null) {
			for (int i = 0; i < propPanels.length; ++i) {
				if (propPanels[i] != null) {
					propPanels[i].setBR(br);
				}
			}
		}
	}

	private void addBR() {
		BRCreator creator = new BRCreator(this);
		creator.show();
		if(creator.getCloseType() == OKCancelDialog.OK_PERFORMED) {
			BuildingRes br = creator.getBR();
			br.setID(maxID++);
			br.setName("建筑" + br.getID());
			if(br instanceof ExploreBR) {
				ExploreBR ebr = (ExploreBR)br;
				BRImageSelecter imageSetter = new BRImageSelecter(this, br, siManager);
				imageSetter.show();
				if(imageSetter.getCloseType() == OKCancelDialog.OK_PERFORMED) {
					ebr.setImages(imageSetter.getImages());
				}
			}
			listModel.addElement(br);
		}
	}

	protected void okPerformed() {
		//save
		if (propPanels != null) {
			for (int i = 0; i < propPanels.length; ++i) {
				if (propPanels[i] != null) {
					propPanels[i].updateBRProp();
				}
			}
		}

		BuildingRes[] urs = new BuildingRes[listModel.size()];
		for (int i = 0; i < listModel.size(); ++i) {
			urs[i] = (BuildingRes) (listModel.get(i));
		}
		brManager.setBRs(urs);
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
