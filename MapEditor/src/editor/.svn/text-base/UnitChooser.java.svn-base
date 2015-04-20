package editor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

class UnitComboBox
	extends JComboBox {
	private MainFrame mainFrame;
	private int exceptedUnitID;

	private class UnitComboItem {
		public int id;
		public String name;

		public UnitComboItem(int id, String name) {
			this.id = id;
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	public UnitComboBox(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		refresh();
	}

	public UnitComboBox(MainFrame mainFrame, int exceptedUnitID) {
		this.mainFrame = mainFrame;
		refresh(exceptedUnitID);
	}

	public void refresh() {
		refresh( -1);
	}

	public void refresh(int exceptedUnitID) {
		this.exceptedUnitID = exceptedUnitID;
		int unitID = getSelectedUnitID();
		Sprite[] sprites = mainFrame.getPanels()[MainFrame.LAYER_UNIT].getManager().getSprites();
		removeAllItems();
		if (exceptedUnitID != UnitManager.PLAYER_ID) {
			addItem(new UnitComboItem(UnitManager.PLAYER_ID, UnitManager.PLAYER_NAME));
		}
		for (int i = 0; i < sprites.length; ++i) {
			if (sprites[i].getID() != exceptedUnitID) {
				addItem(new UnitComboItem(sprites[i].getID(), sprites[i].getName()));
			}
		}
		setSelectedUnitID(unitID);
	}

	public void setSelectedUnitID(int unitID) {
		setSelectedIndex(getItemIndex(unitID));
	}

	private int getItemIndex(int unitID) {
		for (int i = 0; i < getItemCount(); ++i) {
			Object tmp = getItemAt(i);
			if (tmp != null) {
				if (tmp instanceof UnitComboItem) {
					if ( ( (UnitComboItem) tmp).id == unitID) {
						return i;
					}
				}
			}
		}
		return -1;
	}

	public int getSelectedUnitID() {
		Object tmp = getSelectedItem();
		if (tmp != null) {
			if (tmp instanceof UnitComboItem) {
				return ( (UnitComboItem) tmp).id;
			}
		}
		return -1;
	}

	public int getExceptedUnitID() {
		return exceptedUnitID;
	}
}

class UnitSelecter
	extends OKCancelDialog {
	private MainFrame mainFrame;
	private MapPanel unitPanel;

	public UnitSelecter(JDialog owner, MainFrame mainFrame, int unitID, int exceptedUnitID) {
		super(owner);
		init(mainFrame, unitID, exceptedUnitID);
	}

	private void init(MainFrame mainFrame, int unitID, int exceptedUnitID) {
		this.mainFrame = mainFrame;
		setTitle("选择Unit");
		unitPanel = new MapPanel(this, mainFrame) {
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				this.paintFloors(g);
				this.paintBuildings(g);
				this.manager.paintSprites(g);
			}
		};
		unitPanel.manager.setSelectionMode(SpriteManager.SINGLE_SELECTION);
		for (int i = 0; i < unitPanel.units.length; ++i) {
			Unit unit = unitPanel.units[i];
			if (unit.getID() == exceptedUnitID) {
				continue;
			}
			unitPanel.manager.addSprite(unitPanel.units[i]);
			if (unit.getID() == unitID) {
				unitPanel.manager.getSelection().selectSprite(unit);
				unitPanel.scrollRectToVisible(
					new Rectangle(unit.getLeft() - 25,
								  unit.getTop() - 25,
								  unit.getWidth() + 50,
								  unit.getHeight() + 50));
			}
		}

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(unitPanel.getBackPanel(), BorderLayout.CENTER);
		cp.add(buttonPanel, BorderLayout.SOUTH);
	}

	public int getSelectedUnitID() {
		if (unitPanel.manager.getSelection().isEmpty()) {
			return -1;
		}
		return unitPanel.manager.getSelection().getSprites()[0].getID();
	}

	public void okPerformed() {
		int selectedUnitID = getSelectedUnitID();
		if (selectedUnitID >= 0) {
			closeType = OK_PERFORMED;
			dispose();
		}
		else {
			JOptionPane.showMessageDialog(this, "必须选择一个Unit",
										  "错误", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void cancelPerformed() {
		dispose();
	}
}

class UnitChoosePanel
	extends JPanel {
	private JDialog owner;
	private MainFrame mainFrame;
	private UnitComboBox unitCombo;

	public UnitChoosePanel(JDialog owner, MainFrame mainFrame) {
		init(owner, mainFrame, -1);
	}

	public UnitChoosePanel(JDialog owner, MainFrame mainFrame, int exceptedUnitID) {
		init(owner, mainFrame, exceptedUnitID);
	}

	private void init(JDialog owner, MainFrame mainFrame, int exceptedUnitID) {
		this.owner = owner;
		this.mainFrame = mainFrame;

		TitledBorder border = BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
			"选择一个Unit");
		setBorder(border);
		unitCombo = new UnitComboBox(mainFrame, exceptedUnitID);
		JButton selectButton = new JButton("...");
		selectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectUnit();
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
//		this.add(new JLabel("选择一个Unit："), c);

		c.gridx = 0;
		c.weightx = 1;
		this.add(unitCombo, c);

		c.gridx = 1;
		c.weightx = 0;
		this.add(selectButton, c);
	}

	public UnitComboBox getCombo() {
		return unitCombo;
	}

	public int getSelectedUnitID() {
		return unitCombo.getSelectedUnitID();
	}

	public void setSelectedUnitID(int unitID) {
		unitCombo.setSelectedUnitID(unitID);
	}

	private void selectUnit() {
		UnitSelecter selecter = new UnitSelecter(owner, mainFrame,
												 unitCombo.getSelectedUnitID(),
												 unitCombo.getExceptedUnitID());
		selecter.show();
		if (selecter.getCloseType() == OKCancelDialog.OK_PERFORMED) {
			unitCombo.setSelectedUnitID(selecter.getSelectedUnitID());
		}
	}
}
