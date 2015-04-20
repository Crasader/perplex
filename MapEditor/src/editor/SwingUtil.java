package editor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

interface SwingUtilListener {
	public void listItemDeleted(int oldIndex, int newIndex);
}

class SwingUtilAdapter
	implements SwingUtilListener {
	public void listItemDeleted(int oldIndex, int newIndex) {}
}

public class SwingUtil {
	public final static void setDefScrollIncrement(JScrollPane scrollPane) {
		if (scrollPane == null) {
			return;
		}
		scrollPane.getHorizontalScrollBar().setUnitIncrement(XUtil.getDefPropInt("ScrollUnitIncrement"));
		scrollPane.getVerticalScrollBar().setUnitIncrement(XUtil.getDefPropInt("ScrollUnitIncrement"));
		scrollPane.getHorizontalScrollBar().setBlockIncrement(XUtil.getDefPropInt("ScrollBlockIncrement"));
		scrollPane.getVerticalScrollBar().setBlockIncrement(XUtil.getDefPropInt("ScrollBlockIncrement"));
	}

	public final static void MakeListDeleteable(final JList list, final SwingUtilListener listener) {
		if (list == null) {
			return;
		}
		list.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_DELETE:
						if (list.getModel() == null) {
							return;
						}
						if (! (list.getModel()instanceof DefaultListModel)) {
							return;
						}
						DefaultListModel model = (DefaultListModel) (list.getModel());
						int index = list.getSelectedIndex();
						if (index < 0 || index >= model.size()) {
							return;
						}
						int oldIndex = index;
						model.remove(index);
						if (index >= model.size()) {
							index = model.size() - 1;
						}
						if (index < 0) {
							index = -1;
						}
						list.setSelectedIndex(index);
						int newIndex = index;
						if (listener != null) {
							listener.listItemDeleted(oldIndex, newIndex);
						}
						break;
					default:
						break;
				}
			}
		});
	}

	public final static void SetObjListRenderer(final JList list) {
		if (list == null) {
			return;
		}

		list.setCellRenderer(new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(
				JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				String strValue = "";
				if (value != null) {
					if (value.toString() != null) {
						strValue = value.toString();
					}
				}
				if (strValue.equals("")) {
					strValue = " ";
				}
				return super.getListCellRendererComponent(
					list, strValue, index, isSelected, cellHasFocus);
			}
		});

	}
}

abstract class OKCancelDialog
	extends JDialog {
	public final static int CANCEL_PERFORMED = 0;
	public final static int OK_PERFORMED = 1;

	protected int closeType;

	protected JButton okButton, cancelButton;
	protected JPanel buttonPanel;

	public OKCancelDialog(JDialog owner) {
		super(owner, true);
		init();
		setLocationRelativeTo(owner);
	}

	public OKCancelDialog(JFrame owner) {
		super(owner, true);
		init();
		setLocationRelativeTo(owner);
	}

	private void init() {
		setSize(new Dimension(XUtil.getDefPropInt(XUtil.getClassName(getClass()) + "Width"),
							  XUtil.getDefPropInt(XUtil.getClassName(getClass()) + "Height")));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		closeType = CANCEL_PERFORMED;

		okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okPerformed();
			}
		});
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelPerformed();
			}
		});

		buttonPanel = new JPanel();
		GridLayout buttonLayout = new GridLayout();
		buttonLayout.setHgap(10);
		buttonPanel.setLayout(buttonLayout);
		buttonPanel.setPreferredSize(new Dimension(100, XUtil.getDefPropInt("ButtonPanelHeight")));
	}

	public int getCloseType() {
		return closeType;
	}

	protected abstract void okPerformed();

	protected abstract void cancelPerformed();
}

class ButtonText
	extends JPanel {
	private ActionListener actionListener = null;
	private Object value;
	private JButton button;
	private JTextField text;
	private boolean acting;

	public ButtonText(Object value) {
		super();

		text = new JTextField();
		button = new JButton("...");
		acting = false;

		this.value = value;
		if (value != null) {
			text.setText(value.toString());
			text.setCaretPosition(0);
		}
		text.setEditable(false);
		text.setBackground(Color.WHITE);
		text.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == XUtil.LEFT_BUTTON && e.getClickCount() == 2) {
					if (button != null) {
						button.doClick();
					}
				}
			}
		});

		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonActionPerformed(e);
			}
		});

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;

		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		add(text, c);

		button.setPreferredSize(new Dimension(20, 28));
		c.weightx = 0;
		c.gridx = 1;
		add(button, c);

		FocusListener focusListener = new FocusListener() {
			public void focusGained(FocusEvent e) {
				textFocusGained();
			}

			public void focusLost(FocusEvent e) {
				textFocusLost();
			}
		};
		text.addFocusListener(focusListener);
		button.addFocusListener(focusListener);
	}

	private void textFocusGained() {
		text.setBackground(text.getSelectionColor());
	}

	private void textFocusLost() {
		if (!acting) {
			text.setBackground(Color.WHITE);
		}
	}

	/**
	  获得该控件所显示的对象
	 */
	public Object getValue() {
		return value;
	}

	/**
	  设置然后刷新该控件所显示的对象
	 */
	public void setValue(Object value) {
		this.value = value;
		refresh();
	}

	/**
	  刷新该控件所显示的对象
	 */
	public void refresh() {
		if (value != null) {
			text.setText(value.toString());
		}
		else {
			text.setText(null);
		}
		text.setCaretPosition(0);
	}

	/**
	  设置该特定的ActionListener，在双击JText或者点击JButton时触发
	 */
	public void setActionListener(ActionListener actionListener) {
		this.actionListener = actionListener;
	}

	private void buttonActionPerformed(ActionEvent e) {
		if (actionListener != null) {
			acting = true;
			actionListener.actionPerformed(e);
			acting = false;
		}
	}
}

class ValueChooser
	extends JComboBox {
	private class ValueComboItem {
		public int value;
		public String description;
		public ValueComboItem(int value, String description) {
			this.value = value;
			this.description = description;
		}

		public String toString() {
			return description;
		}
	}

	public ValueChooser(int value, int[] defValues, String[] defDescriptions) {
		super();
		init(value, defValues, defDescriptions);
	}

	private void init(int value, int[] defValues, String[] defDescriptions) {
		resetDefValues(defValues, defDescriptions);
		setValue(value);
	}

	public void resetDefValues(int[] defValues, String[] defDescriptions) {
		removeAllItems();
		if (defValues != null) {
			int unknownCount = 0;
			for (int i = 0; i < defValues.length; ++i) {
				int value = defValues[i];
				String description = "";
				boolean hasDescription = false;
				if (defDescriptions != null) {
					if (value >= 0 && value < defDescriptions.length) {
						hasDescription = true;
						description = defDescriptions[value];
						if (description == null) {
							description = "";
						}
					}
				}
				if (!hasDescription) {
					description = "未知" + (++unknownCount);
				}
				addItem(new ValueComboItem(value, description));
			}
		}
	}

	public int getValue() {
		Object item = getSelectedItem();
		if (item != null) {
			if (item instanceof ValueComboItem) {
				return ( (ValueComboItem) item).value;
			}
		}
		return -1;
	}

	public void setValue(int value) {
		setSelectedIndex( -1);
		for (int i = 0; i < getItemCount(); ++i) {
			Object item = getItemAt(i);
			if (item == null) {
				continue;
			}
			if (item instanceof ValueComboItem) {
				ValueComboItem valueItem = (ValueComboItem) item;
				if (valueItem.value == value) {
					setSelectedIndex(i);
					break;
				}
			}
		}
	}
}

class ValueEditor
	extends JPanel {
	private JComboBox valueComboBox;
	private NumberSpinner valueSpinner;
	private boolean hasSpinner;
	private ActionListener comboListener;

	private class ValueComboItem {
		public int value;
		public String description;
		public ValueComboItem(int value, String description) {
			this.value = value;
			this.description = description;
		}

		public String toString() {
			return description;
		}
	}

	public ValueEditor(int value, int[] defValues, String[] defDescriptions) {
		super();
		init(value, defValues, defDescriptions, true);
	}

	public ValueEditor(int value, int[] defValues, String[] defDescriptions, boolean hasSpinner) {
		super();
		init(value, defValues, defDescriptions, hasSpinner);
	}

	private void init(int value, int[] defValues, String[] defDescriptions, boolean hasSpinner) {
		this.hasSpinner = hasSpinner;
		valueSpinner = new NumberSpinner();
		valueComboBox = new JComboBox();
		resetDefValues(defValues, defDescriptions);
		valueComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				valueComboBoxActionPerformed(e);
			}
		});
		setValue(value);

		if (hasSpinner) {
			setLayout(new GridLayout(1, 2, 10, 0));
			add(valueSpinner);
			add(valueComboBox);
		}
		else {
			setLayout(new BorderLayout());
			add(valueComboBox, BorderLayout.CENTER);
		}
	}

	public void resetDefValues(int[] defValues, String[] defDescriptions) {
		valueComboBox.removeAllItems();
		if (defValues != null) {
			int unknownCount = 0;
			for (int i = 0; i < defValues.length; ++i) {
				String description = "";
				boolean hasDescription = false;
				if (defDescriptions != null) {
					if (i >= 0 && i < defDescriptions.length) {
						hasDescription = true;
						description = defDescriptions[i];
						if (description == null) {
							description = "";
						}
					}
				}
				if (!hasDescription) {
					description = "未知" + (++unknownCount);
				}
				valueComboBox.addItem(new ValueComboItem(defValues[i], description));
			}
		}
	}

	public void setComboBoxListener(ActionListener comboListener) {
		this.comboListener = comboListener;
	}

	public JComboBox getComboBox() {
		return valueComboBox;
	}

	public JSpinner getSpinner() {
		return valueSpinner;
	}

	public int getValue() {
		return valueSpinner.getIntValue();
	}

	public void setValue(int value) {
		valueSpinner.setIntValue(value);
		valueComboBox.setSelectedIndex( -1);
		for (int i = 0; i < valueComboBox.getItemCount(); ++i) {
			Object item = valueComboBox.getItemAt(i);
			if (item == null) {
				continue;
			}
			if (item instanceof ValueComboItem) {
				ValueComboItem valueItem = (ValueComboItem) item;
				if (valueItem.value == value) {
					valueComboBox.setSelectedIndex(i);
					break;
				}
			}
		}
	}

	private void valueComboBoxActionPerformed(ActionEvent e) {
		Object item = valueComboBox.getSelectedItem();
		if (item == null) {
			return;
		}
		if (item instanceof ValueComboItem) {
			valueSpinner.setIntValue( ( (ValueComboItem) item).value);
		}
		if (comboListener != null) {
			comboListener.actionPerformed(e);
		}
	}
}

class NumberSpinner
	extends JSpinner {
	SpinnerNumberModel model;
	public NumberSpinner() {
		super(new SpinnerNumberModel());
		model = (SpinnerNumberModel) (getModel());
		JSpinner.NumberEditor editor = new JSpinner.NumberEditor(this);
		editor.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 3));
		setEditor(editor);
		setBorder(BorderFactory.createEmptyBorder());
	}

	public Object getValue() {
		try {
			commitEdit();
		}
		catch (Exception e) {
		}
		return super.getValue();
	}

	public int getIntValue() {
		return ( (Integer) (this.getValue())).intValue();
	}

	public void setIntValue(int value) {
		setValue(new Integer(value));
	}

	public void setMin(int min) {
		model.setMinimum(new Integer(min));
	}

	public void setMax(int max) {
		model.setMaximum(new Integer(max));
	}
}

class ComboTableCellRenderer
	implements TableCellRenderer {
	private ValueChooser chooser;

	public ComboTableCellRenderer(int[] values, String[] descriptions) {
		super();
		chooser = new ValueChooser(0, values, descriptions);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
		boolean hasFocus, int row, int column) {
		chooser.setValue( ( (Integer) value).intValue());
		return chooser;
	}
}

class ComboTableCellEditor
	extends AbstractCellEditor
	implements TableCellEditor {
	private ValueChooser chooser;

	public ComboTableCellEditor(int[] values, String[] descriptions) {
		super();
		chooser = new ValueChooser(0, values, descriptions);
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
												 boolean isSelected, int row, int column) {
		chooser.setValue( ( (Integer) value).intValue());
		return chooser;
	}

	public Object getCellEditorValue() {
		return new Integer(chooser.getValue());
	}
}

class SpinnerTableCellRenderer
	implements TableCellRenderer {
	private NumberSpinner spinner;

	public SpinnerTableCellRenderer() {
		super();
		spinner = new NumberSpinner();
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
		boolean hasFocus, int row, int column) {
		spinner.setValue( (Integer) value);
		return spinner;
	}
}

class SpinnerTableCellEditor
	extends AbstractCellEditor
	implements TableCellEditor {
	private NumberSpinner spinner;

	public SpinnerTableCellEditor() {
		super();
		spinner = new NumberSpinner();
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
												 boolean isSelected, int row, int column) {
		spinner.setValue( (Integer) value);
		return spinner;
	}

	public boolean stopCellEditing() {
		try {
			spinner.commitEdit();
		}
		catch (Exception e) {
			//				return super.cancelCellEditing();
		}
		return super.stopCellEditing();
	}

	public Object getCellEditorValue() {
		return (Integer) (spinner.getValue());
	}
}

class ProgressDialog extends JDialog {
	private JLabel titleLabel;
	private JLabel infoLabel;
	private JProgressBar progressBar;

	public ProgressDialog() {
		super();
		init();
		setLocationRelativeTo(null);
	}

	public ProgressDialog(JFrame owner) {
		super(owner);
		init();
		setLocationRelativeTo(owner);
	}

	private void init() {
		setBounds(0, 0, 200, 60);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setUndecorated(true);
		Container cp = getContentPane();

		titleLabel = new JLabel("标题",JLabel.CENTER);
		cp.add(titleLabel,BorderLayout.NORTH);

		progressBar = new JProgressBar();
		cp.add(progressBar,BorderLayout.CENTER);

		infoLabel = new JLabel("信息",JLabel.CENTER);
		cp.add(infoLabel,BorderLayout.SOUTH);

		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	public void setTitle(String s) {
		super.setTitle(s);
		titleLabel.setText(s);
	}

	public void setValue(int value) {
		progressBar.setValue(value);
	}

	public void setInfo(String s) {
		infoLabel.setText(s);
	}
}

class XTable extends JPanel {
	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		f.setSize(new Dimension(800, 600));
		f.setLocationRelativeTo(null);

		f.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
		
		XTable t = new XTable("测试", new String[] {"C1", "C2", "C3", "C4", "C5", "C6"});
		t.setNumberCol(0);
		t.setComboCol(1, new int[] {0, 1, 2, 3, 4}, new String[] {"A0", "A1", "A2", "A3", "A4"});
		t.setNumberCol(2);
		t.setNumberCol(3);
		t.setComboCol(4, new int[] {2, 3, 4}, new String[] {"B0", "B1", "B2", "B3"});
		t.setComboCol(5, new int[] {2, 3, 4, 5, 6}, new String[] {"C1", "C2", "C3", "C4"});
		
		Container cp = f.getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(t, BorderLayout.CENTER);
		f.show();
	}
	
	
	public final static int NUMBER = 0;
	public final static int COMBO = 1;
	public final static int STRING = 2;
	
	
	private DefaultTableModel model;
	private JTable table;
	private TableColumnModel columnModel;
	
	public DefaultTableModel getModel() {return model;}
	public JTable getTable() {return table;}
	public TableColumnModel getColumnModel() {return columnModel;}
	
	public XTable(String name, String[] colNames) {
		super();
		model = new DefaultTableModel();
		for(int i = 0; i < colNames.length; ++i) {
			model.addColumn(colNames[i]);
		}
		table = new JTable(model);
		table.setRowSelectionAllowed(false);
		table.setColumnSelectionAllowed(false);
		table.setRowHeight(getDefaultRowHeight());
		columnModel = table.getColumnModel();	
	
		//scrollPane
		JScrollPane scrollPane = new JScrollPane(table);
		SwingUtil.setDefScrollIncrement(scrollPane);
		
		//button:add/remove row
		JButton addButton = new JButton("添加");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addRow();
			}
		});

		JButton removeButton = new JButton("删除");
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeRow();
			}
		});
		
		//buttonPanel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 5, 5, 5);
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		buttonPanel.add(addButton, c);
		c.gridx = 1;
		buttonPanel.add(removeButton, c);
		
		this.setLayout(new BorderLayout());
		this.add(new JLabel(name), BorderLayout.NORTH);
		this.add(scrollPane, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.SOUTH);
	}
	
	public int getDefaultRowHeight() {
		int result = XUtil.getDefPropInt("DefaultTableRowHeight");
		if(result <= 0) result = 25;
		return result;
	}
	
	public void setNumberCol(int colIndex) {
		TableColumn column = columnModel.getColumn(colIndex);
		column.setCellRenderer(new SpinnerTableCellRenderer());
		column.setCellEditor(new SpinnerTableCellEditor());
	}
	
	public void setComboCol(int colIndex, int[] defValues, String[] defDescs) {
		TableColumn column = columnModel.getColumn(colIndex);
		column.setCellRenderer(new ComboTableCellRenderer(defValues, defDescs));
		column.setCellEditor(new ComboTableCellEditor(defValues, defDescs));
	}
	
	public Object getDefaultColValue(int col) {
		return new Integer(0);
	}
	
	public void addRow() {
		int colCount = model.getColumnCount();
		int row = table.getSelectedRow();
		Object[] data = new Object[colCount];
		for(int col = 0; col < colCount; ++col) {
			data[col] = getDefaultColValue(col);
		}
		if(row >= 0 && row < model.getRowCount() - 1) {
			model.insertRow(row + 1, data);
		}
		else {
			model.addRow(data);
		}
	}
	
	public void removeRow() {
		stopEditing();
		int row = table.getSelectedRow();
		if (row >= 0 && row < model.getRowCount()) {
			model.removeRow(row);
		}
		if (row > model.getRowCount() - 1) {
			row = model.getRowCount() - 1;
		}
		if (row >= 0 && row < model.getRowCount()) {
			table.setRowSelectionInterval(row, row);
			table.requestFocus();
		}
	}
	
	public void stopEditing() {
		TableCellEditor editor = table.getCellEditor();
		if (editor != null) {
			editor.stopCellEditing();
		}
	}
}