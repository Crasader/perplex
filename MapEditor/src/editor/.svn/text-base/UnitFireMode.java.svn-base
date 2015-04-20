package editor;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

class Weapon {
	public final static int MACHINE_GUN = 0;
	public final static int E_MISSILE = 1;
	public final static int E_GUN = 2;
	public final static int A_LASER = 3;
	public final static int A_MISSILE = 4;
	public final static int E_EDDY_GUN = 5;
	public final static int A_CANNON = 6;
	public final static int E_LITTLE_MISSILE = 7;
	public final static int E_LARGE_MISSILE = 8;
	public final static int E_LASER = 9;
	public final static int E_LARGE_LASER = 10;
	
	public final static int[] TYPES = {
									  0, 
									  1, 
									  2, 
									  3,
									  4,
									  5,
									  6,
									  7,
									  8,
									  9,
									  10,
									  11,
									  12,
									  13,
									  14,
									  15
	};
	
	public final static String[] DESCRIPTIONS = {
												"A",
												"B",
												"C",
												"D",
												"E",
												"F",
												"旋转弹", 
												"紫色子弹", 
												"小紫色圆子弹",
												"G",
												"H",
												"I",
												"J",
												"K",
												"L",
												"敌人小导弹"
	};
}

public class UnitFireMode {
	public final static String getDescription(UnitFireMode uf) {
		IntPair[] data = null;
		if(uf != null) {
			data = uf.getData();
		}
		return getDescription(data);
	}
	
	public final static String getDescription(IntPair[] data) {
		String result = "开火几率[";
		if(data != null) {
			for(int i = 0; i < data.length; ++i) {
				result += Weapon.DESCRIPTIONS[data[i].x] + "：" + data[i].y + "；";
			}
		}
		result += "]";
		return result;
	}
	
	private IntPair[] data;

	public UnitFireMode() {
		data = null;
	}
	
	public UnitFireMode getCopy() {
		UnitFireMode result = new UnitFireMode();
		result.copyFrom(this);
		return result;
	}
	
	private void copyFrom(UnitFireMode dest) {
		this.data = XUtil.copyArray(dest.data);
	}
	
	public IntPair[] getData() {
		return XUtil.copyArray(data);
	}
	
	public void setData(IntPair[] data) {
		this.data = XUtil.copyArray(data);
	}

	public void saveMobileData(DataOutputStream out) throws Exception {
		SL.writeIntPairArrayMobile(data, out);
	}

	public void save(DataOutputStream out) throws Exception {
		SL.writeIntPairArray(data, out);
	}

	public final static UnitFireMode createViaFile(DataInputStream in) throws Exception {
		UnitFireMode uf = new UnitFireMode();
		uf.load(in);
		return uf;
	}

	private void load(DataInputStream in) throws Exception {
		data = SL.readIntPairArray(in);
	}
}

class UnitFireModePanel extends JPanel {
	private JDialog owner;
	private UnitFireMode uf;
	private IntPair[] data;
	private ButtonText dataText;
	
	public UnitFireModePanel (JDialog owner, UnitFireMode uf) {
		super();
		this.owner = owner;
		init(uf);
	}
	
	private void init(UnitFireMode uf) {
		this.uf = uf;
		this.data = uf.getData();
		dataText = new ButtonText(UnitFireMode.getDescription(uf));
		dataText.setActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setData();
			}
		});
		
		this.setLayout(new BorderLayout());
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = c.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 1;
		c.insets = new Insets(2, 2, 3, 3);
		this.add(new JLabel("设置开火几率："), c);
		
		c.gridx = 1;
		c.weightx = 1;
		this.add(dataText, c);
	}
	
	public void SetUF(UnitFireMode uf) {
		this.uf = uf;
		this.data = uf.getData();
		dataText.setValue(UnitFireMode.getDescription(uf));
	}
	
	public void updataUF() {
		uf.setData(data);
	}
	
	public UnitFireMode getUF() {
		return uf;
	}
	
	private void setData() {
		UnitFireDataSetter setter = new UnitFireDataSetter(owner, data);
		setter.show();
		if(setter.getCloseType() == OKCancelDialog.OK_PERFORMED) {
			data = setter.getData();
		}
		dataText.setValue(UnitFireMode.getDescription(data));
	}
}

class UnitFireDataSetter extends OKCancelDialog {
	private IntPair[] weaponProbs;
	private DefaultTableModel weaponModel;
	private JTable weaponTable;
	
	public UnitFireDataSetter(JDialog owner, IntPair[] data) {
		super(owner);
		init(data);
	}
	
	private void init(IntPair[] data) {
		setTitle("设置开火几率");
		this.weaponProbs = XUtil.copyArray(data);
		//Table
		weaponModel = new DefaultTableModel();
		weaponModel.addColumn("武器");
		weaponModel.addColumn("概率");
		weaponTable = new JTable(weaponModel);
		weaponTable.setRowSelectionAllowed(false);
		weaponTable.setRowHeight(XUtil.getDefPropInt("UPTableRowHeight"));
		TableColumnModel columnModel = weaponTable.getColumnModel();

		//weapon column
		TableColumn weaponColumn = columnModel.getColumn(0);
		weaponColumn.setCellRenderer(new ComboTableCellRenderer(Weapon.TYPES, Weapon.DESCRIPTIONS));
		weaponColumn.setCellEditor(new ComboTableCellEditor(Weapon.TYPES, Weapon.DESCRIPTIONS));

		//prob column
		TableColumn probColumn = columnModel.getColumn(1);
		probColumn.setCellRenderer(new SpinnerTableCellRenderer());
		probColumn.setCellEditor(new SpinnerTableCellEditor());

		JScrollPane weaponProbsScroll = new JScrollPane(weaponTable);
		SwingUtil.setDefScrollIncrement(weaponProbsScroll);

		if(weaponProbs != null) {
			for(int i = 0 ; i < weaponProbs.length; ++i) {
				weaponModel.addRow(new Object[]{
								 new Integer(weaponProbs[i].x), 
								 new Integer(weaponProbs[i].y)
				});
			}
		}


		JButton addWeaponButton = new JButton("添加");
		addWeaponButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addWeapon();
			}
		});

		JButton removeWeaonButton = new JButton("删除");
		removeWeaonButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeWeapon();
			}
		});

		JPanel weaponButtonPanel = new JPanel();
		weaponButtonPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 5, 5, 5);
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		weaponButtonPanel.add(addWeaponButton, c);
		c.gridx = 1;
		weaponButtonPanel.add(removeWeaonButton, c);

		JPanel weaponPanel = new JPanel();
		weaponPanel.setLayout(new BorderLayout());
		weaponPanel.add(new JLabel("设置武器种类以及其概率："), BorderLayout.NORTH);
		weaponPanel.add(weaponProbsScroll, BorderLayout.CENTER);
		weaponPanel.add(weaponButtonPanel, BorderLayout.SOUTH);
		
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(weaponPanel, BorderLayout.CENTER);
		cp.add(buttonPanel, BorderLayout.SOUTH);
	}
	
	private void addWeapon() {
		weaponModel.addRow(new Object[] {
						  new Integer(Weapon.MACHINE_GUN),
						  new Integer(0)
		});
	}

	private void removeWeapon() {
		weaponTableStopEditing();
		int row = weaponTable.getSelectedRow();
		if (row >= 0 && row < weaponModel.getRowCount()) {
			weaponModel.removeRow(row);
		}
		if (row > weaponModel.getRowCount() - 1) {
			row = weaponModel.getRowCount() - 1;
		}
		if (row >= 0 && row < weaponModel.getRowCount()) {
			weaponTable.setRowSelectionInterval(row, row);
			weaponTable.requestFocus();
		}
	}

	private void weaponTableStopEditing() {
		TableCellEditor editor = weaponTable.getCellEditor();
		if (editor != null) {
			editor.stopCellEditing();
		}
	}
	
	public IntPair[] getData() {
		return XUtil.copyArray(weaponProbs);
	}
	
	protected void okPerformed() {
		weaponTableStopEditing();
		ArrayList weapons = new ArrayList();
		for(int i = 0; i < weaponModel.getRowCount(); ++i) {
			int weapon = ((Integer)(weaponModel.getValueAt(i, 0))).intValue();
			int prob = ((Integer)(weaponModel.getValueAt(i, 1))).intValue();
			if(prob > 0) {
				weapons.add(new IntPair(weapon, prob));
			}
		}
		
		weaponProbs = null;
		if(weapons.size() > 0) {
			weaponProbs = new IntPair[weapons.size()];
			for(int i = 0; i < weapons.size(); ++i) {
				weaponProbs[i] = (IntPair)(weapons.get(i));
			}
		}

		this.closeType = OK_PERFORMED;
		dispose();
	}
	
	protected void cancelPerformed() {
		dispose();
	}
}















