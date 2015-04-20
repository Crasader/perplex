package editor;

import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 管理XListItem的List，拥有添加、删除、设置这三种操作。
 复制、剪切、粘贴的操作有待需求增加。
 因为比较特殊所以不加入到SwingUtil中
 */
public class XList
	extends JList {

	private JPopupMenu popup;
	private XMenuItem menuInsert;
	private XMenuItem menuModify;
	private XMenuItem menuDelete;
	protected JDialog owner;
	protected DefaultListModel model;
	protected XListItem selectedItem;
	protected boolean popupping;
	protected XListItem lastItem ;

	public XList(JDialog owner) {
		init(null, owner);
	}

	public XList(ArrayList items, JDialog owner) {
		init(items, owner);
	}

	public XList(XListItem[] items, JDialog owner) {
		ArrayList tmp = new ArrayList();
		if (items != null) {
			for (int i = 0; i < items.length; ++i) {
				tmp.add(items[i]);
			}
		}
		init(tmp, owner);
	}

	private void init(ArrayList items, JDialog owner) {
		this.owner = owner;
		model = new DefaultListModel();
		super.setModel(model);
		super.setCellRenderer(new XListCellRenderer());
		super.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		lastItem = new XListItem(null, XListItem.CAN_INSERT); //必然存在的一行
		
		popup = new JPopupMenu();
		//menuInsert
		menuInsert = new XMenuItem(null);
		menuInsert.setActionCommand("Insert");
		menuInsert.setText("插入");
		menuInsert.setMnemonic('I');
		popup.add(menuInsert);
		//menuModify
		menuModify = new XMenuItem(null);
		menuModify.setActionCommand("Modify");
		menuModify.setText("修改");
		menuModify.setMnemonic('M');
		popup.add(menuModify);
		//menuDelete
		menuDelete = new XMenuItem(null);
		menuDelete.setActionCommand("Delete");
		menuDelete.setText("删除");
		menuDelete.setMnemonic('D');
		popup.add(menuDelete);

		setItems(items);
		
		addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				selfSelectionChanged();
			}
		});
		addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				if (!popup.isVisible() && !popupping) {
					clearSelection(); //在非弹出菜单或者修改属性的时候，清空选项
				}
			}
		});
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == XUtil.LEFT_BUTTON && e.getClickCount() == 2) {
					dbClick(selectedItem);
				}
			}

			public void mousePressed(MouseEvent e) {
				if (e.getButton() == XUtil.RIGHT_BUTTON) {
					setSelectedIndex(locationToIndex(new Point(e.getX(), e.getY())));
					requestFocus();
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == XUtil.RIGHT_BUTTON) {
					popupMenu(selectedItem, e);
				}
			}
		});

		addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (selectedItem == null) {
					return;
				}
				switch (e.getKeyCode()) {
					case KeyEvent.VK_SPACE:
						startModify(selectedItem);
						break;
					case KeyEvent.VK_ENTER:
						startInsert(selectedItem);
						break;
					case KeyEvent.VK_DELETE:
						startDelete(selectedItem);
						break;
					default:
						break;
				}
			}
		});
	}
	
	protected final void setItems(ArrayList items) {
		model.clear();
		if (items != null) {
			for (int i = 0; i < items.size(); ++i) {
				model.addElement(items.get(i));
			}
		}

		model.addElement(lastItem);
		selectedItem = null;
		popupping = false;
	}

	private void selfSelectionChanged() {
		selectedItem = null;
		unselectAllItems(); //清除所有项的选择状态
		Object tmp = getSelectedValue();
		if (tmp != null) {
			if (tmp instanceof XListItem) {
				selectedItem = (XListItem) tmp;
				selectedItem.setSelected(true); //设置自己的选择状态
				XListItem[] children = getChildren(selectedItem);
				if (children != null) {
					for (int i = 0; i < children.length; ++i) {
						children[i].setSelected(true); //设置子项的选择状态
					}
				}
			}
		}
		repaint();
	}

	private class XMenuItem
		extends JMenuItem {
		private XListItem item;

		public XMenuItem(XListItem item) {
			super();
			this.item = item;
			addActionListener(new MenuActionListener());
		}

		public XListItem getItem() {
			return item;
		}

		public void setItem(XListItem item) {
			this.item = item;
		}
	}

	private void dbClick(XListItem item) {
		if (!startModify(item)) { //如果能修改则修改
			startInsert(item); //不能修改则插入
		}
	}

	private void popupMenu(XListItem item, MouseEvent e) {
		if (item == null) {
			return;
		}
		menuInsert.setItem(item);
		menuInsert.setEnabled( (item.getChangeType() & XListItem.CAN_INSERT) != 0);

		menuModify.setItem(item);
		menuModify.setEnabled( (item.getChangeType() & XListItem.CAN_MODIFY) != 0);

		menuDelete.setItem(item);
		menuDelete.setEnabled( (item.getChangeType() & XListItem.CAN_DELETE) != 0);

		popup.show(this, e.getX(), e.getY());
	}

	private boolean startInsert(XListItem item) {
		if (item == null) {
			return false;
		}
		if ( (item.getChangeType() & XListItem.CAN_INSERT) != 0) {
			popupping = true;
			XListItem newItem = insert(item);
			popupping = false;
			if (newItem != null) {
				setSelectedIndex(model.indexOf(newItem));
			}
			return true;
		}
		else {
			return false;
		}
	}

	protected XListItem insert(XListItem item) {
		return item;
	}

	private boolean startModify(XListItem item) {
		if (item == null) {
			return false;
		}
		if ( (item.getChangeType() & XListItem.CAN_MODIFY) != 0) {
			popupping = true;
			XListItem newItem = modify(item);
			popupping = false;
			if (newItem != null) {
				setSelectedIndex(model.indexOf(newItem));
			}
			return true;
		}
		else {
			return false;
		}
	}

	protected XListItem modify(XListItem item) {
		return item;
	}

	private boolean startDelete(XListItem item) {
		if (item == null) {
			return false;
		}
		if ( (item.getChangeType() & XListItem.CAN_DELETE) != 0) {
			popupping = true;
			delete(item);
			popupping = false;
			return true;
		}
		else {
			return false;
		}
	}

	protected void delete(XListItem item) {
		deleteItem(item);
	}

	public void unselectAllItems() {
		Object tmp;
		for (int i = 0; i < model.size(); ++i) {
			tmp = model.get(i);
			if (tmp != null) {
				if (tmp instanceof XListItem) {
					( (XListItem) tmp).setSelected(false);
				}
			}
		}
	}

	public XListItem[] getChildren(XListItem item) {
		if (item == null) {
			return null;
		}
		//获得全部的项目
		ArrayList tmp = new ArrayList();
		for (int i = 0; i < model.size(); ++i) {
			Object o = model.get(i);
			if (o == null) {
				continue;
			}
			if (! (o instanceof XListItem)) {
				continue;
			}
			tmp.add(o);
		}
		if (tmp.isEmpty()) {
			return null;
		}

		//获得参数的子项目
		XListItem[] allItems = new XListItem[tmp.size()];
		for (int i = 0; i < tmp.size(); ++i) {
			allItems[i] = (XListItem) (tmp.get(i));
		}
		return XListItem.getChildren(allItems, item);
	}

	public void insertItem(XListItem itemToInsertBefore, XListItem item, XListItem[] children) {
		if (item == null) {
			return;
		}
		if (itemToInsertBefore == null) {
			return;
		}
		int index = model.indexOf(itemToInsertBefore);
		if (index < 0) {
			return;
		}

		if (children != null) {
			for (int i = children.length - 1; i >= 0; --i) {
				if (children[i] != null) {
					model.add(index, children[i]);
				}
			}
		}

		item.setParent(itemToInsertBefore.getParent());
		model.add(index, item);
	}

	public void replaceItem(XListItem itemToBeReplaced, XListItem item, XListItem[] children) {
		insertItem(itemToBeReplaced, item, children);
		deleteItem(itemToBeReplaced);
	}

	public void deleteItem(XListItem item) {
		if (item == null) {
			return;
		}
		XListItem[] children = getChildren(item);
		if (children != null) {
			for (int i = 0; i < children.length; ++i) {
				if (children[i] != null) {
					model.removeElement(children[i]);
				}
			}
		}
		model.removeElement(item);
	}

	private class MenuActionListener
		implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (! (e.getSource()instanceof XMenuItem)) {
				return;
			}
			XListItem item = ( (XMenuItem) (e.getSource())).getItem();
			String menuName = (e.getActionCommand()).toUpperCase();
			if (menuName.equals("INSERT")) {
				startInsert(item);
			}
			else if (menuName.equals("MODIFY")) {
				startModify(item);
			}
			else if (menuName.equals("DELETE")) {
				startDelete(item);
			}
		}
	}

}

class XListCellRenderer
	extends DefaultListCellRenderer {
	private final static String NORMAL_HEAD = "◆";
	private final static String SPECIAL_HEAD = "◇";
	private final static String LEVEL_SPACE = "      ";
	public Component getListCellRendererComponent(
		JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		if (value != null) {
			if (value instanceof XListItem) {
				XListItem tmp = (XListItem) value;
				String newValue = "";
				XListItem parent = tmp.getParent();
				while (parent != null) {
					newValue += LEVEL_SPACE;
					parent = parent.getParent();
				}
				if (tmp.getChangeType() != XListItem.CANNT_CHANGE) {
					newValue += NORMAL_HEAD;
				}
				else {
					newValue += SPECIAL_HEAD;
				}
				newValue += tmp.toString();
				return super.getListCellRendererComponent(
					list, newValue, index, tmp.isSelected(), cellHasFocus);
			}
		}
		return super.getListCellRendererComponent(
			list, value, index, isSelected, cellHasFocus);
	}
}