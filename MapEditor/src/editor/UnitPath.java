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

	/* 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SelectPoint [point=" + point + ", selectIndex=" + selectIndex
				+ "]";
	}
	

}

public class UnitPath {
	public final static String getPathDescription(UnitPath up) {
		IntPair[] path = null;
		if(up != null) {
			path = up.getData().getPath();
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

	UnitPathData data;
	
	public UnitPath() {
		init(null);
	}

	public UnitPath(UnitPathData data) {
		init(data);
	}

	private void init(UnitPathData data) {
		if(data == null) return;
		this.data = new UnitPathData(data);
	}

	public UnitPathData getData() {
		UnitPathData result = null;
		if (data != null) {
			result = new UnitPathData(data);
		}
		return result;
	}
	
	public IntPair getEndPoint(IntPair startPoint) {
		IntPair result = startPoint.getCopy();
		if (data.getPath() != null) {
			if(data.getPath().length > 0) {
				result = data.getPath()[data.getPath().length - 1].getCopy();
			}
//			for (int i = 0; i < path.length; ++i) {
//				result = UnitPath.getEndPoint(result, path[i].x, path[i].y);
//			}
		}
		return result;
	}

	public String toString() {
		return getPathDescription(data.getPath());
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
		if (unitPath.getData().getPath() != null)
		{
			if(selectIndex < unitPath.getData().getPath().length && selectIndex >= 0) {
				selectPoint = unitPath.getData().getPath()[selectIndex].getCopy();
			}
		}
		else {
			selectPoint = null;
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
		this.unitPath = new UnitPath(unitPath.data);
		setSelectIndex(selectRow);
		repaint();
	}
	
	public void drawAL(int sx, int sy, int ex, int ey,Graphics g2)
	{
		
		double  H  =   6 ;  // 箭头高度    
		double  L  =   6 ; // 底边的一半   
		int  x3  =   0 ;
		int  y3  =   0 ;
		int  x4  =   0 ;
		int  y4  =   0 ;
		double  awrad  =  Math.atan(L  /  H);  // 箭头角度    
		double  arraow_len  =  Math.sqrt(L  *  L  +  H  *  H); // 箭头的长度    
		double [] arrXY_1  =  rotateVec(ex  -  sx, ey  -  sy, awrad,  true , arraow_len);
		double [] arrXY_2  =  rotateVec(ex  -  sx, ey  -  sy,  - awrad,  true , arraow_len);
		double  x_3  =  ex  -  arrXY_1[ 0 ];  // (x3,y3)是第一端点    
		double  y_3  =  ey  -  arrXY_1[ 1 ];
		double  x_4  =  ex  -  arrXY_2[ 0 ]; // (x4,y4)是第二端点    
		double  y_4  =  ey  -  arrXY_2[ 1 ];
		
		Double X3  =   new  Double(x_3);
		x3  =  X3.intValue();
		Double Y3  =   new  Double(y_3);
		y3  =  Y3.intValue();
		Double X4  =   new  Double(x_4);
		x4  =  X4.intValue();
		Double Y4  =   new  Double(y_4);
		y4  =  Y4.intValue();
		// g.setColor(SWT.COLOR_WHITE);
		// 画线 
//		g2.drawLine(sx, sy, ex, ey);
		// 画箭头的一半 
		g2.drawLine(ex, ey, x3, y3);
		// 画箭头的另一半 
		g2.drawLine(ex, ey, x4, y4);
		
	}
	
	public  double [] rotateVec( int  px,  int  py,  double  ang,  boolean  isChLen,
			double  newLen)   {
		
		double  mathstr[]  =   new   double [ 2 ];
		// 矢量旋转函数，参数含义分别是x分量、y分量、旋转角、是否改变长度、新长度    
		double  vx  =  px  *  Math.cos(ang)  -  py  *  Math.sin(ang);
		double  vy  =  px  *  Math.sin(ang)  +  py  *  Math.cos(ang);
		if  (isChLen)   {
			double  d  =  Math.sqrt(vx  *  vx  +  vy  *  vy);
			vx  =  vx  /  d  *  newLen;
			vy  =  vy  /  d  *  newLen;
			mathstr[ 0 ]  =  vx;
			mathstr[ 1 ]  =  vy;
		} 
		return  mathstr;
	}
	public void drawArrow(int sx, int sy, int ex, int ey, double thata, double lenght, Graphics g)
	{
		double theta = Math.toRadians(45);
		double px, py, p1x, p1y, p2x, p2y;
		px = sx - ex;
		py = sy - ey;
		
		p1x = px * Math.cos(theta) - py*Math.sin(theta);
		p1y = px * Math.sin(theta) + py*Math.cos(theta);
		
		p2x = px * Math.cos(-theta) - py*Math.sin(-theta);
		p2y = px * Math.sin(-theta) + py*Math.cos(-theta);
		
		double x1, x2;
		x1 = Math.sqrt(p1x * p1x + p1y * p1y);
		p1x = p1x * lenght / x1;
		p1y = p1y * lenght / x1;
		x2 = Math.sqrt(p2x * p2x + p2y * p2y);
		p2x = p2x * lenght / x2;
		p2y = p2y * lenght / x2;
		
		int cx = (sx + ex)/2;
		int cy = (sy + ey)/2;
		p1x = p1x + cx;
		p1y = p1y + cy;
		p2x = p2x + cx;
		p2y = p2y + cy;
		Color color = g.getColor();
		g.setColor(color.GREEN);
		g.drawLine((int)p1x, (int)p1y, cx, cy);
		g.drawLine((int)p2x, (int)p2y, cx, cy);
		g.setColor(color);
	}
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		paintFloors(g);
		paintBuildings(g);
		paintUnits(g);
//		manager.paintSprites(g);
//		IntPair p = startPoint.getCopy();

		IntPair[] path = unitPath.getData().getPath();
		if (path != null && path.length > 0) {
			IntPair p = path[0].getCopy();;
			if (path.length == 1) {
				paintPoint(p, g, 4, Color.green);
			}
			else {
				if(path.length == 1) selectPoint = null;
				for (int i = 0; i < path.length; ++i) {
					if(i == 0) paintPoint(p, g, 4, Color.green);
					IntPair oldP = p;
					p = path[i].getCopy();
					g.drawLine(oldP.x, oldP.y, p.x, p.y);
					if(i > 0)drawArrow(oldP.x, oldP.y, p.x, p.y, 5, 5, g);
					paintRect(p, g, 4, Color.GRAY);
				}
			}
			paintPoint(p, g, 2, Color.BLUE);
			if(selectPoint != null)paintRect(selectPoint, g, 4, Color.YELLOW);
		}
	}

	private void paintPoint(IntPair p, Graphics g, int radius, Color color) {
		Color oldColor = g.getColor();
		g.setColor(color);
		g.fillOval(p.x - radius, p.y - radius, radius * 2, radius * 2);
		g.setColor(oldColor);
	}
	
	private void paintRect(IntPair p, Graphics g, int radius, Color color) {
		Color oldColor = g.getColor();
		g.setColor(color);
		g.fillRect(p.x - 5, p.y - 5, 10, 10);
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
	private Vector<SelectPoint> sps;
	private int count;
	
	public UnitPathSetter(JFrame owner, MainFrame mainFrame, IntPair startPoint, UnitPath unitPath, int animaCount) {
		super(owner);
		init(mainFrame, startPoint, unitPath, animaCount);
	}

	public UnitPathSetter(JDialog owner, MainFrame mainFrame, IntPair startPoint, UnitPath unitPath, int animaCount) {
		super(owner);
		init(mainFrame, startPoint, unitPath, animaCount);
	}

	private void init(MainFrame mainFrame, IntPair startPoint, UnitPath unitPath, int animaCount) {
		this.setTitle("设置行走路径");
		mouseState = MOUSE_NONE;
		count = animaCount;
		pathPanel = new UnitPathPanel(this, mainFrame, startPoint, unitPath);
		sps = new Vector<SelectPoint>();
		reflesh(unitPath);
		String[] name = new String[animaCount];
		int[] value = new int[animaCount];
		for (int i = 0; i < animaCount; i++) {
			String key = "00" + i;
			if (i > 9) {
				key = "0" + i;
			}
			name[i] = key;
			value[i] = i;
		}
		tableModel = new DefaultTableModel();
		tableModel.addColumn("X");
		tableModel.addColumn("Y");
		tableModel.addColumn("动画id");
		tableModel.addColumn("speed");
		tableModel.addColumn("改变方向");
		tableModel.addColumn("延迟");
		
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
//		pathTable.setCellSelectionEnabled(false);
//		pathTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		pathTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				int select = arg0.getLastIndex();
				System.out.println(select+" : " + arg0.getFirstIndex());
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
		column.setCellRenderer(new ComboTableCellRenderer(value, name));
		column.setCellEditor(new ComboTableCellEditor(value, name));
		
		TableColumn speedColumn = columnModel.getColumn(3);
		speedColumn.setCellRenderer(new SpinnerTableCellRenderer());
		speedColumn.setCellEditor(new SpinnerTableCellEditor());

		TableColumn orientation = columnModel.getColumn(4);
		orientation.setCellRenderer(new ComboTableCellRenderer(new int[]{0,1}, new String[] {"否","是"}));
		orientation.setCellEditor(new ComboTableCellEditor(new int[]{0,1}, new String[] {"否","是"}));
		
		TableColumn delay = columnModel.getColumn(5);
		delay.setCellRenderer(new SpinnerTableCellRenderer());
		delay.setCellEditor(new SpinnerTableCellEditor());
		
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
					if (mouseState == MOUSE_MOVE && selectPoint != null) {
						return;
					}
					System.out.println("pressed...." + sps.size());
					for (int i = 0; i < sps.size(); i++) {
						if (sps.get(i).isSelect(new IntPair(pathPanel.getMouseX(e),
							pathPanel.getMouseY(e)))) {
							selectPoint = sps.get(i);
							mouseState = MOUSE_MOVE;
							System.out.println(sps.get(i).toString());
							break;
						}
					}
				}
			}
//			
//			public void mouseDragged(MouseEvent e)
//			{
//				System.out.println("mouseDragged....");
//				if (pathPanel.isMouseMovingViewport()) {
//					return;
//				}
//				if (e.getButton() == XUtil.RIGHT_BUTTON) {
//					setMouseState(MOUSE_NONE);
//					return;
//				}
//				else if (e.getButton() == XUtil.LEFT_BUTTON) {
//					if (mouseState == MOUSE_MOVE)
//					{
//						updateSelect(e);
//					}
//				}
//			}
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
//		c.gridx = 2;
//		tableButtonPanel.add(adjustButton, c);
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

	/**
	 * @param unitPath
	 */
	private void reflesh(UnitPath unitPath) {
		if(unitPath != null && unitPath.getData().getPath() != null)
		{
			sps.clear();
			for (int i = 0; i < unitPath.getData().getPath().length; i++) {
				sps.add(new SelectPoint(unitPath.getData().getPath()[i], i));
			}
		}
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
		reflesh(unitPath);
	}

	private void addRow() {
		MapInfo info = MainFrame.self.getMapInfo();
		tableModel.addRow(new Object[] {
				info.changeToMobileX(new Integer(0)),
				info.changeToMobileY(new Integer(0),0),
				new Integer(0),
				new Integer(100),
				new Integer(1),
				new Integer(0)
		});
	}

	private void addRow(IntPair row) {
		MapInfo info = MainFrame.self.getMapInfo();
		tableModel.addRow(new Object[] {
				info.changeToMobileX(new Integer(row.x)),
				info.changeToMobileY(new Integer(row.y),0),
				new Integer(0),
				new Integer(100),
				new Integer(1),
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
				selectPoint = null;
				mouseState = MOUSE_NONE;
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
			
			sps.add(new SelectPoint(new IntPair(pathPanel.getMouseX(e),
					pathPanel.getMouseY(e)), sps.size()));
		}
	}

	private void scrollToVisiable() {
		int left = startPoint.x;
		int top = startPoint.y;
		int right = left;
		int bottom = top;
		IntPair p = startPoint.getCopy();

		IntPair[] path = unitPath.getData().getPath();

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
		IntPair[] path = unitPath.getData().getPath();
		int[] id = unitPath.getData().getAnimaID();
		int[] s = unitPath.getData().getSpeed();
		int[] o = unitPath.getData().getOrientation();
		int[] d = unitPath.getData().getDelay();
		
		if (path != null) {
			for (int i = 0; i < path.length; ++i) {
				if (path[i] != null) {
					tableModel.addRow(new Object[] {
									  info.changeToMobileX(new Integer(path[i].x)),
									  info.changeToMobileY(new Integer(path[i].y), 0),
									  new Integer(id == null || id[i] >= count ? 0 : id[i]),
									  new Integer(s == null ? 100 : s[i]),
									  new Integer(o == null ? 0 : o[i]),
									  new Integer(d == null ? 0 : d[i])});
				}
			}
		}
	}

	private void readStartPoint() {
	}

	private void readPathTable() {
		MapInfo info = MainFrame.self.getMapInfo();
		ArrayList tmp = new ArrayList();
		ArrayList tmp1 = new ArrayList();
		ArrayList tmp2 = new ArrayList();
		ArrayList tmp3 = new ArrayList();
		ArrayList tmp4 = new ArrayList();
	
		for (int i = 0; i < tableModel.getRowCount(); ++i) {
			int x = 0; //((Integer)(tableModel.getValueAt(i, 0))).intValue();
			int y = 0; //((Integer)(tableModel.getValueAt(i, 1))).intValue();
			int z = 0;
			int s = 0;
			int o = 0;
			int d = 0;
			
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

			//read z
			Object zObj = tableModel.getValueAt(i, 2);
			if (zObj == null) {
				continue;
			}
			if (! (zObj instanceof Integer)) {
				continue;
			}
			z = ( (Integer) zObj).intValue();
			
			Object zObj1 = tableModel.getValueAt(i, 3);
			if (zObj1 == null) {
				continue;
			}
			if (! (zObj1 instanceof Integer)) {
				continue;
			}
			s = ( (Integer) zObj1).intValue();
			
			Object zObj2 = tableModel.getValueAt(i, 4);
			if (zObj2 == null) {
				continue;
			}
			if (! (zObj2 instanceof Integer)) {
				continue;
			}
			o = ( (Integer) zObj2).intValue();
			
			Object zObj3 = tableModel.getValueAt(i, 5);
			if (zObj3 == null) {
				continue;
			}
			if (! (zObj3 instanceof Integer)) {
				continue;
			}
			d = ( (Integer) zObj3).intValue();
			
			tmp.add(new IntPair(info.changeToMapEditorX(x), info.changeToMapEditorY(y, 0)));
			tmp1.add(new Integer(z));
			tmp2.add(new Integer(s));
			tmp3.add(new Integer(o));
			tmp4.add(new Integer(d));
		}

		IntPair[] path = null;
		int[] id = null;
		int[] s = null;
		int[] o = null;
		int[] d = null;
		if (tmp != null) {
			path = new IntPair[tmp.size()];
			id = new int[tmp1.size()];
			s = new int[tmp2.size()];
			o = new int[tmp3.size()];
			d = new int[tmp4.size()];
			for (int i = 0; i < tmp.size(); ++i) {
				path[i] = (IntPair) (tmp.get(i));
				id[i] = (Integer)(tmp1.get(i));
				s[i] = (Integer)(tmp2.get(i));
				o[i] = (Integer)(tmp3.get(i));
				d[i] = (Integer)(tmp4.get(i));
			}
		}
		unitPath = new UnitPath(new UnitPathData(path, id, s, o, d));
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
		this.unitPath = new UnitPath(unitPath.data);
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
