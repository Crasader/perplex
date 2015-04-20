package editor;

import java.util.*;

/**
 包含某个对象而且使用特定方法显示其内容的逻辑对象，用以XList
 @see XList
 */
public class XListItem {
	public final static int CANNT_CHANGE = 0; //不能改变
	public final static int CAN_DELETE = 0x01; //删除
	public final static int CAN_MODIFY = 0x02; //修改
	public final static int CAN_INSERT = 0x04; //在前面插入
	public final static int CAN_CHANGE_ALL = 0xFF; //全部功能
	public final static int DEFAULT_CHANGE_TYPE = CAN_CHANGE_ALL; //默认为全部功能

	public final static String DEFAULT_STRING = "";

	private XListItem parent;
	private Object value;
	private int changeType;
	private boolean selected;

	public static XListItem[] getChildren(XListItem[] allItems, XListItem parent) {
		if (allItems == null || parent == null) {
			return null;
		}
		ArrayList tmp = new ArrayList();
		for (int i = 0; i < allItems.length; ++i) {
			XListItem item = allItems[i];
			if (item == null) {
				continue;
			}
			if (item.getParent() == parent) {
				tmp.add(item);
				XListItem[] children = XListItem.getChildren(allItems, item);
				if (children != null) {
					for (int j = 0; j < children.length; ++j) {
						tmp.add(children[j]);
					}
				}
			}
		}
		if (tmp.isEmpty()) {
			return null;
		}
		XListItem[] result = new XListItem[tmp.size()];
		for (int i = 0; i < tmp.size(); ++i) {
			result[i] = (XListItem) (tmp.get(i));
		}
		return result;
	}

	public XListItem(Object value) {
		init(null, value, DEFAULT_CHANGE_TYPE);
	}

	public XListItem(Object value, int changeType) {
		init(null, value, changeType);
	}

	public XListItem(XListItem parent, Object value) {
		init(parent, value, DEFAULT_CHANGE_TYPE);
	}

	public XListItem(XListItem parent, Object value, int changeType) {
		init(parent, value, changeType);
	}

	private void init(XListItem parent, Object value, int changeType) {
		this.parent = parent;
		this.value = value;
		this.changeType = changeType;
		this.selected = false;
	}

	public XListItem getParent() {
		return parent;
	}

	public Object getValue() {
		return value;
	}

	public int getChangeType() {
		return changeType;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setParent(XListItem parent) {
		this.parent = parent;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String toString() {
		if (value != null) {
			if (value instanceof Pair) { //意味着 pair (realObject,description)
				return ( (Pair) value).second.toString();
			}
			else {
				return value.toString();
			}
		}
		else {
			return DEFAULT_STRING;
		}
	}
}