package editor;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import sun.applet.Main;

class SelectPoint {
	public IntPair point;
	public int selectIndex;
	/**
	 * construct
	 * @param point
	 * @param selectIndex
	 */
	public SelectPoint(IntPair point, int selectIndex) {
		this.point = point;
		this.selectIndex = selectIndex;
	}

	public boolean isSelect(IntPair pos)
	{
		Rect rect = new Rect(point.x - 10, point.y - 10, 20, 20);
		return rect.contains(pos.x, pos.y);
	}
}

public class UnitPath {
	public final static String getPathDescription(UnitPath up) {
		IntPair[] path = null;
		if(up != null) {
			path = up.getPath();
		}
		return getPathDescription(path);
	}
	
	public final static String getPathDescription(IntPair[] path) {
		MapInfo info = MainFrame.self.getMapInfo();
		String result = "路径[";
		if (path != null) {
			for (int i = 0; i < path.length; ++i) {
//				result = result + Dir.DESCRIPTIONS[path[i].x] + "：" + path[i].y;
				result = result + "(" + info.changeToMobileX(path[i].x) + ", " + info.changeToMobileY(path[i].y, 0) + ")";
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
	private IntPair selectPoint;
	private int selectIndex = -1;
	
	public UnitPathPanel(JDialog owner, MainFrame mainFrame, IntPair startPoint, UnitPath unitPath) {
		super(owner, mainFrame);
		init(startPoint, unitPath);
	}

	private void init(IntPair startPoint, UnitPath unitPath) {
		setMustFocus(false);
		setStartPoint(startPoint);
		setUnitPath(unitPath, 0);
	}

	public IntPair setSelectIndex(int index)
	{
		selectIndex = index;
		if (selectIndex < unitPath.getPath().length && selectIndex >= 0) {
			selectPoint = unitPath.getPath()[selectIndex].getCopy();
		}
		repaint();
		return selectPoint;
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

	public void setUnitPath(UnitPath unitPath, int selectRow) {
		this.unitPath = new UnitPath(unitPath);
		setSelectIndex(selectRow);
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
				IntPair center = new IntPair((p.x + oldP.x)/2,(p.y + oldP.y) /2 );
				g.drawLine(oldP.x, oldP.y, p.x, p.y);
				int y = (int) (10 * Math.sin(Math.toRadians(45)));
				int x = (int) (10 * Math.cos(Math.toRadians(45)));
				g.drawLine(p.x -x,  p.y + y, p.x, p.y);
				g.drawLine(p.x -x,  p.y - y, p.x, p.y);
				paintPoint(oldP, g, 4, Color.YELLOW);
			}
		}
		
		paintPoint(p, g, 2, Color.BLUE);
		if(selectPoint != null)paintPoint(selectPoint, g, 4, Color.YELLOW);
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
	private final static int MOUSE_MOVE = 3;

	private DefaultTableModel tableModel;
	private JTable pathTable;
	private UnitPathPanel pathPanel;
	private IntPair startPoint;
	private UnitPath unitPath;
	private int mouseState;
	private TableColumnModel columnModel;
	private SelectPoint selectPoint;
	
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
		tableModel.addColumn("动画id");
		pathTable = new JTable(tableModel);
		pathTable.setRowSelectionAllowed(false);
		pathTable.setRowHeight(XUtil.getDefPropInt("UPTableRowHeight"));
		columnModel = pathTable.getColumnModel();
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

		pathTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				int select = arg0.getFirstIndex();
				IntPair pos = pathPanel.setSelectIndex(select);
				if(pos != null)selectPoint = new SelectPoint(pos, select);
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

		TableColumn column = columnModel.getColumn(2);
		column.setCellRenderer(new ComboTableCellRenderer(new int[]{0, 1, 2}, new String[]{"0", "1", "2"}));
		column.setCellEditor(new ComboTableCellEditor(new int[]{0, 1, 2}, new String[]{"0", "1", "2"}));
		
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

		JButton adjustButton = new JButton("调整");
		removeRowButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
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
			
			public void mousePressed(MouseEvent e)
			{
				if (pathPanel.isMouseMovingViewport()) {
					return;
				}
				if (e.getButton() == XUtil.RIGHT_BUTTON) {
					setMouseState(MOUSE_NONE);
					return;
				}
				else if (e.getButton() == XUtil.LEFT_BUTTON) {
					if (selectPoint != null && selectPoint.isSelect(new IntPair(pathPanel.getMouseX(e),
							pathPanel.getMouseY(e))))
					{
						mouseState = MOUSE_MOVE;
					}
				}
			}
			
			public void mouseDragged(MouseEvent e)
			{
				System.out.println("mouseDragged....");
				if (pathPanel.isMouseMovingViewport()) {
					return;
				}
				if (e.getButton() == XUtil.RIGHT_BUTTON) {
					setMouseState(MOUSE_NONE);
					return;
				}
				else if (e.getButton() == XUtil.LEFT_BUTTON) {
					if (mouseState == MOUSE_MOVE)
					{
						updateSelect(e);
					}
				}
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
		c.gridx = 2;
		tableButtonPanel.add(adjustButton, c);
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
		this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		this.setLocation(0,0);
	}

	private void setMouseState(int mouseState) {
		this.mouseState = mouseState;
		switch (mouseState) {
			case MOUSE_MOVE:
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
		pathPanel.setUnitPath(unitPath, pathTable.getSelectedRow());
	}

	private void addRow() {
		MapInfo info = MainFrame.self.getMapInfo();
		tableModel.addRow(new Object[] {
				info.changeToMobileX(new Integer(0)),
				info.changeToMobileY(new Integer(0),0),
				new Integer(0)
		});
	}

	private void addRow(IntPair row) {
		MapInfo info = MainFrame.self.getMapInfo();
		tableModel.addRow(new Object[] {
				info.changeToMobileX(new Integer(row.x)),
				info.changeToMobileY(new Integer(row.y),0),
				new Integer(0)
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
			if(mouseState == MOUSE_MOVE)
			{
				updateSelect(e);
			}
		}
	}

	/**
	 * @param e
	 */
	private void updateSelect(MouseEvent e) {
		MapInfo info = MainFrame.self.getMapInfo();
		selectPoint.point = new IntPair(pathPanel.getMouseX(e),
				pathPanel.getMouseY(e));
		tableModel.setValueAt(new Integer(info.changeToMobileX(selectPoint.point.x)), selectPoint.selectIndex, 0);
		tableModel.setValueAt(new Integer(info.changeToMobileY(selectPoint.point.y, 0)), selectPoint.selectIndex, 1);
		pathPanel.setUnitPath(unitPath, selectPoint.selectIndex);
	}

	private void mouseSetStartPoint(MouseEvent e) {
		startPoint.x = pathPanel.getMouseX(e);
		startPoint.y = pathPanel.getMouseY(e);
		pathPanel.setStartPoint(startPoint);
	}

	private void mouseSetPath(MouseEvent e) {
		if (selectPoint != null && selectPoint.isSelect(new IntPair(pathPanel.getMouseX(e),
				pathPanel.getMouseY(e)))) {
			System.out.println("select......");
		}
		else {
			IntPair endPoint = unitPath.getEndPoint(startPoint);
			addRow(new IntPair(pathPanel.getMouseX(e),
					pathPanel.getMouseY(e)));
		}
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
		MapInfo info = MainFrame.self.getMapInfo();
		IntPair[] path = unitPath.getPath();
		if (path != null) {
			for (int i = 0; i < path.length; ++i) {
				if (path[i] != null) {
					tableModel.addRow(new Object[] {
									  info.changeToMobileX(new Integer(path[i].x)),
									  info.changeToMobileY(new Integer(path[i].y), 0),
									  new Integer(1)});
				}
			}
		}
	}

	private void readStartPoint() {
	}

	private void readPathTable() {
		MapInfo info = MainFrame.self.getMapInfo();
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

			tmp.add(new IntPair(info.changeToMapEditorX(x), info.changeToMapEditorY(y, 0)));
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
