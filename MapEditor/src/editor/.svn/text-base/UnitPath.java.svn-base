package editor;

import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class UnitPath {
	public final static String getPathDescription(UnitPath up) {
		IntPair[] path = null;
		if(up != null) {
			path = up.getPath();
		}
		return getPathDescription(path);
	}
	
	public final static String getPathDescription(IntPair[] path) {
		String result = "路径[";
		if (path != null) {
			for (int i = 0; i < path.length; ++i) {
//				result = result + Dir.DESCRIPTIONS[path[i].x] + "：" + path[i].y;
				result = result + "(" + path[i].x + ", " + path[i].y + ")";
				result = result + "；";
			}
		}
		result = result + "]";
		return result;
	}

	public final static IntPair getEndPoint(IntPair startPoint, int dir, int distance) {
		int offsetX = startPoint.x;
		int offsetY = startPoint.y;
		switch (dir) {
			case Dir.U:
				offsetY -= distance;
				break;

			case Dir.UR:
				offsetX = (int) (offsetX + distance);
				offsetY = (int) (offsetY - distance);
				break;

			case Dir.R:
				offsetX += distance;
				break;

			case Dir.RD:
				offsetX = (int) (offsetX + distance);
				offsetY = (int) (offsetY + distance);
				break;

			case Dir.D:
				offsetY += distance;
				break;

			case Dir.DL:
				offsetX = (int) (offsetX - distance);
				offsetY = (int) (offsetY + distance);
				break;

			case Dir.L:
				offsetX -= distance;
				break;

			case Dir.LU:
				offsetX = (int) (offsetX - distance);
				offsetY = (int) (offsetY - distance);
				break;
		}

		return new IntPair(offsetX, offsetY);
	}

	private IntPair[] path;

	public UnitPath() {
		init(null);
	}

	public UnitPath(IntPair[] path) {
		init(path);
	}

	public UnitPath(UnitPath unitPath) {
		if (unitPath != null) {
			init(unitPath.path);
		}
		else {
			init(null);
		}
	}

	private void init(IntPair[] path) {
		if (path != null) {
			this.path = new IntPair[path.length];
			for (int i = 0; i < path.length; ++i) {
				this.path[i] = path[i].getCopy();
			}
		}
	}

	public IntPair[] getPath() {
		IntPair[] result = null;
		if (path != null) {
			result = new IntPair[path.length];
			for (int i = 0; i < path.length; ++i) {
				result[i] = path[i].getCopy();
			}
		}
		return result;
	}

	public IntPair getEndPoint(IntPair startPoint) {
		IntPair result = startPoint.getCopy();
		if (path != null) {
			if(path.length > 0) {
				result = path[path.length - 1].getCopy();
			}
//			for (int i = 0; i < path.length; ++i) {
//				result = UnitPath.getEndPoint(result, path[i].x, path[i].y);
//			}
		}
		return result;
	}

	public String toString() {
		return getPathDescription(path);
	}
}

class UnitPathPanel
	extends MapPanel {
	private IntPair startPoint;
	private UnitPath unitPath;

	public UnitPathPanel(JDialog owner, MainFrame mainFrame, IntPair startPoint, UnitPath unitPath) {
		super(owner, mainFrame);
		init(startPoint, unitPath);
	}

	private void init(IntPair startPoint, UnitPath unitPath) {
		setMustFocus(false);
		setStartPoint(startPoint);
		setUnitPath(unitPath);
	}

	public void setUnit(int unitID) {
		manager.reset();
		if (units != null) {
			for (int i = 0; i < units.length; ++i) {
				if (units[i].getID() == unitID) {
					units[i].setSelected(true);
					//manager.addSprite(units[i]);
					//manager.getSelection().selectSprite(units[i]);
				}
			}
		}
	}

	public void setStartPoint(IntPair startPoint) {
		if (startPoint == null) {
			this.startPoint = new IntPair();
		}
		else {
			this.startPoint = startPoint.getCopy();
		}
		repaint();
	}

	public void setUnitPath(UnitPath unitPath) {
		this.unitPath = new UnitPath(unitPath);
		repaint();
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		paintFloors(g);
		paintBuildings(g);
		paintUnits(g);
//		manager.paintSprites(g);
		IntPair p = startPoint.getCopy();

		paintPoint(p, g, 4, Color.green);

		IntPair[] path = unitPath.getPath();
		if (path != null) {
			for (int i = 0; i < path.length; ++i) {
				IntPair oldP = p;
//				p = UnitPath.getEndPoint(p, path[i].x, path[i].y);
				p = path[i].getCopy();
				g.drawLine(oldP.x, oldP.y, p.x, p.y);
			}
		}

		paintPoint(p, g, 2, Color.BLUE);
	}

	private void paintPoint(IntPair p, Graphics g, int radius, Color color) {
		Color oldColor = g.getColor();
		g.setColor(color);
		g.fillOval(p.x - radius, p.y - radius, radius * 2, radius * 2);
		g.setColor(oldColor);
	}
}

class UnitPathSetter
	extends OKCancelDialog {
	private final static int MOUSE_NONE = 0;
	private final static int MOUSE_START_POINT = 1;
	private final static int MOUSE_PATH = 2;

	private DefaultTableModel tableModel;
	private JTable pathTable;
	private UnitPathPanel pathPanel;
	private IntPair startPoint;
	private UnitPath unitPath;
	private int mouseState;

	public UnitPathSetter(JFrame owner, MainFrame mainFrame, IntPair startPoint, UnitPath unitPath) {
		super(owner);
		init(mainFrame, startPoint, unitPath);
	}

	public UnitPathSetter(JDialog owner, MainFrame mainFrame, IntPair startPoint, UnitPath unitPath) {
		super(owner);
		init(mainFrame, startPoint, unitPath);
	}

	private void init(MainFrame mainFrame, IntPair startPoint, UnitPath unitPath) {
		this.setTitle("设置行走路径");
		mouseState = MOUSE_NONE;
		pathPanel = new UnitPathPanel(this, mainFrame, startPoint, unitPath);

		tableModel = new DefaultTableModel();
		tableModel.addColumn("X");
		tableModel.addColumn("Y");
		pathTable = new JTable(tableModel);
		pathTable.setRowSelectionAllowed(false);
		pathTable.setRowHeight(XUtil.getDefPropInt("UPTableRowHeight"));
		TableColumnModel columnModel = pathTable.getColumnModel();
//		pathTable.addFocusListener(new FocusAdapter() {
//			public void focusLost(FocusEvent e) {
//				pathTableStopEditing();
//        		pathTableChanged();
//			}
//		});
		tableModel.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				pathTableChanged();
			}
		});

		//dir column
		TableColumn dirColumn = columnModel.getColumn(0);
		dirColumn.setCellRenderer(new SpinnerTableCellRenderer());
		dirColumn.setCellEditor(new SpinnerTableCellEditor());

		//dist column
		TableColumn distColumn = columnModel.getColumn(1);
		distColumn.setCellRenderer(new SpinnerTableCellRenderer());
		distColumn.setCellEditor(new SpinnerTableCellEditor());

		JScrollPane tableScroll = new JScrollPane(pathTable);
		SwingUtil.setDefScrollIncrement(tableScroll);

		JButton startPointButton = new JButton("设置起始点");
		startPointButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setMouseState(MOUSE_START_POINT);
			}
		});

		JButton addRowButton = new JButton("添加");
		addRowButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addRow();
			}
		});

		JButton removeRowButton = new JButton("删除");
		removeRowButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeRow();
			}
		});

		JButton mousePathButton = new JButton("鼠标点击");
		mousePathButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setMouseState(MOUSE_PATH);
			}
		});
		pathPanel.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				pathPanelMouseReleased(e);
			}
		});

		JPanel tableButtonPanel = new JPanel();
		tableButtonPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 0, 0);
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		tableButtonPanel.add(addRowButton, c);
		c.gridx = 1;
		tableButtonPanel.add(removeRowButton, c);
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		tableButtonPanel.add(mousePathButton, c);

		JPanel tablePanel = new JPanel();
		tablePanel.setLayout(new BorderLayout());
		tablePanel.add(startPointButton, BorderLayout.NORTH);
		tablePanel.add(tableScroll, BorderLayout.CENTER);
		tablePanel.add(tableButtonPanel, BorderLayout.SOUTH);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
											  tablePanel, pathPanel.getBackPanel());
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(200);

		setStartPoint(startPoint);
		setUnitPath(unitPath);
		scrollToVisiable();

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(splitPane, BorderLayout.CENTER);
		cp.add(buttonPanel, BorderLayout.SOUTH);
	}

	private void setMouseState(int mouseState) {
		this.mouseState = mouseState;
		switch (mouseState) {
			case MOUSE_NONE:
				pathPanel.setCursor(Cursor.getDefaultCursor());
				break;
			default:
				pathPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				break;
		}
	}

	private void pathTableStopEditing() {
		TableCellEditor editor = pathTable.getCellEditor();
		if (editor != null) {
			editor.stopCellEditing();
		}
	}

	private void pathTableChanged() {
		readPathTable();
		pathPanel.setUnitPath(unitPath);
	}

	private void addRow() {
		tableModel.addRow(new Object[] {
						  new Integer(0),
						  new Integer(0)
		});
	}

	private void addRow(IntPair row) {
		tableModel.addRow(new Object[] {
						  new Integer(row.x),
						  new Integer(row.y)
		});
	}

	private void removeRow() {
		pathTableStopEditing();
		int row = pathTable.getSelectedRow();
		if (row >= 0 && row < tableModel.getRowCount()) {
			tableModel.removeRow(row);
		}
		if (row > tableModel.getRowCount() - 1) {
			row = tableModel.getRowCount() - 1;
		}
		if (row >= 0 && row < tableModel.getRowCount()) {
			pathTable.setRowSelectionInterval(row, row);
			pathTable.requestFocus();
		}
	}

	private void pathPanelMouseReleased(MouseEvent e) {
		if (pathPanel.isMouseMovingViewport()) {
			return;
		}
		if (e.getButton() == XUtil.RIGHT_BUTTON) {
			setMouseState(MOUSE_NONE);
			return;
		}
		else if (e.getButton() == XUtil.LEFT_BUTTON) {
			if (mouseState == MOUSE_START_POINT) {
				mouseSetStartPoint(e);
			}
			if (mouseState == MOUSE_PATH) {
				mouseSetPath(e);
			}
		}
	}

	private void mouseSetStartPoint(MouseEvent e) {
		startPoint.x = pathPanel.getMouseX(e);
		startPoint.y = pathPanel.getMouseY(e);
		pathPanel.setStartPoint(startPoint);
	}

	private void mouseSetPath(MouseEvent e) {
		IntPair endPoint = unitPath.getEndPoint(startPoint);
		addRow(new IntPair(pathPanel.getMouseX(e), pathPanel.getMouseY(e)));
//		int x = pathPanel.getMouseX(e) - endPoint.x;
//		int y = pathPanel.getMouseY(e) - endPoint.y;
//		if (x == 0 && y == 0) { //都为0
//			return;
//		}
//
//		int ax = Math.abs(x);
//		int ay = Math.abs(y);
//		int dist = ax;
//		if (ax >= ay * 2) {
//			y = 0;
//		}
//		else if (ax > ay / 2) { // ay/2 < ax < ay * 2
//			if (y >= 0) {
//				y = ax;
//			}
//			else {
//				y = -ax;
//			}
//		}
//		else { // ax <= ay/2
//			x = 0;
//			dist = ay;
//		}
//
//		int dir = -1;
//		if (x > 0) {
//			if (y > 0) {
//				dir = Dir.RD;
//			}
//			else if (y == 0) {
//				dir = Dir.R;
//			}
//			else { // y < 0
//				dir = Dir.RU;
//			}
//		}
//		else if (x == 0) {
//			if (y > 0) {
//				dir = Dir.D;
//			}
//			else { // y < 0
//				dir = Dir.U;
//			}
//		}
//		else { // x < 0
//			if (y > 0) {
//				dir = Dir.LD;
//			}
//			else if (y == 0) {
//				dir = Dir.L;
//			}
//			else {
//				dir = Dir.LU;
//			}
//		}
//		if (dir >= 0 && dist > 0) {
//			addRow(new IntPair(dir, dist));
//		}
	}

	private void scrollToVisiable() {
		int left = startPoint.x;
		int top = startPoint.y;
		int right = left;
		int bottom = top;
		IntPair p = startPoint.getCopy();

		IntPair[] path = unitPath.getPath();

		if (path != null) {
			for (int i = 0; i < path.length; ++i) {
//				p = UnitPath.getEndPoint(p, path[i].x, path[i].y);
				p = new IntPair(path[i].x, path[i].y);
				if (p.x < left) {
					left = p.x;
				}
				else if (p.x > right) {
					right = p.x;
				}

				if (p.y < top) {
					top = p.y;
				}
				else if (p.y > bottom) {
					bottom = p.y;
				}
			}
		}
		pathPanel.scrollRectToVisible(new Rectangle(left - 25, top - 25,
			right - left + 50, bottom - top + 50));
	}

	private void updataStartPoint() {
		pathPanel.setStartPoint(startPoint);
	}

	private void updatePathTable() {
		while (tableModel.getRowCount() > 0) {
			tableModel.removeRow(0);
		}

		IntPair[] path = unitPath.getPath();
		if (path != null) {
			for (int i = 0; i < path.length; ++i) {
				if (path[i] != null) {
					tableModel.addRow(new Object[] {
									  new Integer(path[i].x),
									  new Integer(path[i].y)});
				}
			}
		}
	}

	private void readStartPoint() {
	}

	private void readPathTable() {
		ArrayList tmp = new ArrayList();
		for (int i = 0; i < tableModel.getRowCount(); ++i) {
			int x = 0; //((Integer)(tableModel.getValueAt(i, 0))).intValue();
			int y = 0; //((Integer)(tableModel.getValueAt(i, 1))).intValue();

			//read x
			Object xObj = tableModel.getValueAt(i, 0);
			if (xObj == null) {
				continue;
			}
			if (! (xObj instanceof Integer)) {
				continue;
			}
			x = ( (Integer) xObj).intValue();

			//read y
			Object yObj = tableModel.getValueAt(i, 1);
			if (yObj == null) {
				continue;
			}
			if (! (yObj instanceof Integer)) {
				continue;
			}
			y = ( (Integer) yObj).intValue();

			tmp.add(new IntPair(x, y));
		}

		IntPair[] path = null;
		if (tmp != null) {
			path = new IntPair[tmp.size()];
			for (int i = 0; i < tmp.size(); ++i) {
				path[i] = (IntPair) (tmp.get(i));
			}
		}
		unitPath = new UnitPath(path);
	}

	public void setStartPoint(IntPair startPoint) {
		if (startPoint == null) {
			this.startPoint = new IntPair();
		}
		else {
			this.startPoint = startPoint.getCopy();
		}
		updataStartPoint();
	}

	public void setUnitPath(UnitPath unitPath) {
		this.unitPath = new UnitPath(unitPath);
		updatePathTable();
	}

	public IntPair getStartPoint() {
		readStartPoint();
		return startPoint;
	}

	public UnitPath getUnitPath() {
		pathTableStopEditing();
		readPathTable();
		return unitPath;
	}

	public void okPerformed() {
		closeType = OK_PERFORMED;
		dispose();
	}

	public void cancelPerformed() {
		dispose();
	}
}
