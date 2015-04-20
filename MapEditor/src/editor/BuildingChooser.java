package editor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

class BuildingComboBox
	extends JComboBox {
	private MainFrame mainFrame;
	private int exceptedBuildingID;

	private class BuildingComboItem {
		public int id;
		public String name;

		public BuildingComboItem(int id, String name) {
			this.id = id;
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	public BuildingComboBox(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		refresh();
	}

	public BuildingComboBox(MainFrame mainFrame, int exceptedBuildingID) {
		this.mainFrame = mainFrame;
		refresh(exceptedBuildingID);
	}

	public void refresh() {
		refresh( -1);
	}

	public void refresh(int exceptedBuildingID) {
		this.exceptedBuildingID = exceptedBuildingID;
		int buildingID = getSelectedBuildingID();
		Sprite[] sprites = mainFrame.getPanels()[MainFrame.LAYER_BUILDING].getManager().getSprites();
		removeAllItems();
		for (int i = 0; i < sprites.length; ++i) {
			if (sprites[i].getID() != exceptedBuildingID) {
				addItem(new BuildingComboItem(sprites[i].getID(), sprites[i].getName()));
			}
		}
		setSelectedBuildingID(buildingID);
	}

	public void setSelectedBuildingID(int buildingID) {
		setSelectedIndex(getItemIndex(buildingID));
	}

	private int getItemIndex(int buildingID) {
		for (int i = 0; i < getItemCount(); ++i) {
			Object tmp = getItemAt(i);
			if (tmp != null) {
				if (tmp instanceof BuildingComboItem) {
					if ( ( (BuildingComboItem) tmp).id == buildingID) {
						return i;
					}
				}
			}
		}
		return -1;
	}

	public int getSelectedBuildingID() {
		Object tmp = getSelectedItem();
		if (tmp != null) {
			if (tmp instanceof BuildingComboItem) {
				return ( (BuildingComboItem) tmp).id;
			}
		}
		return -1;
	}

	public int getExceptedBuildingID() {
		return exceptedBuildingID;
	}
}

class BuildingSelecter
	extends OKCancelDialog {
	private MainFrame mainFrame;
	private MapPanel buildingPanel;

	public BuildingSelecter(JDialog owner, MainFrame mainFrame, int buildingID, int exceptedBuildingID) {
		super(owner);
		init(mainFrame, buildingID, exceptedBuildingID);
	}

	private void init(MainFrame mainFrame, int buildingID, int exceptedBuildingID) {
		this.mainFrame = mainFrame;
		setTitle("选择Building");
		buildingPanel = new MapPanel(this, mainFrame) {
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				this.paintFloors(g);
				this.paintUnits(g);
				this.manager.paintSprites(g);
			}
		};
		buildingPanel.manager.setSelectionMode(SpriteManager.SINGLE_SELECTION);
		for (int i = 0; i < buildingPanel.buildings.length; ++i) {
			Building building = buildingPanel.buildings[i];
			if (building.getID() == exceptedBuildingID) {
				continue;
			}
			buildingPanel.manager.addSprite(buildingPanel.buildings[i]);
			if (building.getID() == buildingID) {
				buildingPanel.manager.getSelection().selectSprite(building);
				buildingPanel.scrollRectToVisible(
					new Rectangle(building.getLeft() - 25,
								  building.getTop() - 25,
								  building.getWidth() + 50,
								  building.getHeight() + 50));
			}
		}

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(buildingPanel.getBackPanel(), BorderLayout.CENTER);
		cp.add(buttonPanel, BorderLayout.SOUTH);
	}

	public int getSelectedBuildingID() {
		if (buildingPanel.manager.getSelection().isEmpty()) {
			return -1;
		}
		return buildingPanel.manager.getSelection().getSprites()[0].getID();
	}

	public void okPerformed() {
		int selectedBuildingID = getSelectedBuildingID();
		if (selectedBuildingID >= 0) {
			closeType = OK_PERFORMED;
			dispose();
		}
		else {
			JOptionPane.showMessageDialog(this, "必须选择一个Building",
										  "错误", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void cancelPerformed() {
		dispose();
	}
}

class BuildingChoosePanel
	extends JPanel {
	private JDialog owner;
	private MainFrame mainFrame;
	private BuildingComboBox buildingCombo;

	public BuildingChoosePanel(JDialog owner, MainFrame mainFrame) {
		init(owner, mainFrame, -1);
	}

	public BuildingChoosePanel(JDialog owner, MainFrame mainFrame, int exceptedBuildingID) {
		init(owner, mainFrame, exceptedBuildingID);
	}

	private void init(JDialog owner, MainFrame mainFrame, int exceptedBuildingID) {
		this.owner = owner;
		this.mainFrame = mainFrame;

		TitledBorder border = BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
			"选择一个Building");
		setBorder(border);
		buildingCombo = new BuildingComboBox(mainFrame, exceptedBuildingID);
		JButton selectButton = new JButton("...");
		selectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectBuilding();
			}
		});

		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0;
		c.weighty = 1;
		c.gridx = 0;
		c.insets = new Insets(0, 5, 0, 5);
//		c.gridy = 0;
//		this.add(new JLabel("选择一个Building："), c);

		c.gridx = 0;
		c.weightx = 1;
		this.add(buildingCombo, c);

		c.gridx = 1;
		c.weightx = 0;
		this.add(selectButton, c);
	}

	public BuildingComboBox getCombo() {
		return buildingCombo;
	}

	public int getSelectedBuildingID() {
		return buildingCombo.getSelectedBuildingID();
	}

	public void setSelectedBuildingID(int buildingID) {
		buildingCombo.setSelectedBuildingID(buildingID);
	}

	private void selectBuilding() {
		BuildingSelecter selecter = new BuildingSelecter(owner, mainFrame,
												 buildingCombo.getSelectedBuildingID(),
												 buildingCombo.getExceptedBuildingID());
		selecter.show();
		if (selecter.getCloseType() == OKCancelDialog.OK_PERFORMED) {
			buildingCombo.setSelectedBuildingID(selecter.getSelectedBuildingID());
		}
	}
}
