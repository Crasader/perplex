package editor;

import java.util.*;

import javax.swing.*;

/**
 显示和设置操作的List
 */
public class OperationList
	extends XList {
	public static ArrayList getOperationListItems(Operation[] operations) {
		ArrayList result = new ArrayList();
		if (operations != null) {
			for (int i = 0; i < operations.length; ++i) {
				if(operations[i] != null) {
					Operation operation = operations[i].getCopy();
					result.add(new XListItem(new Pair(operation, operation.getListItemDescription())));
				}
			}
		}
		return result;
	}

	protected MainFrame mainFrame;
	private int eventType;

	public OperationList(JDialog owner, MainFrame mainFrame) {
		super(owner);
		init(mainFrame);
	}

	public OperationList(ArrayList operationItems, JDialog owner, MainFrame mainFrame) {
		super(operationItems, owner);
		init(mainFrame);
	}

	public OperationList(Operation[] operations, JDialog owner, MainFrame mainFrame) {
		super(getOperationListItems(operations), owner);
		init(mainFrame);
	}

	private void init(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	public int getEventType() {
		return eventType;
	}

	public void setEventType(int eventType) {
		this.eventType = eventType;
	}

	protected XListItem insert(XListItem item) {
		OperationCreator creator = new OperationCreator(owner, mainFrame);
		creator.show();
		if (creator.getCloseType() == OKCancelDialog.OK_PERFORMED) {
			Operation operation = creator.getOperation();
			if (operation == null) {
				return item;
			}
			XListItem operationItem = new XListItem(
				new Pair(operation, operation.getListItemDescription()));
			insertItem(item, operationItem, null);
			return operationItem;
		}
		else {
			return item;
		}
	}

	protected XListItem modify(XListItem item) {
		Operation operation = Operation.getOperationViaListItem(item);
		if (operation == null) {
			return item;
		}
		OperationSetter setter = OperationSetter.createSetter(owner, mainFrame, operation);
		if (setter == null) {
			return item;
		}
		setter.show();
		if (setter.getCloseType() == OKCancelDialog.OK_PERFORMED) {
			operation = setter.getOperation();
			XListItem operationItem = new XListItem(
				new Pair(operation, operation.getListItemDescription()));
			replaceItem(item, operationItem, null);
			return operationItem;
		}
		else {
			return item;
		}
	}

	public Operation[] getOperations() {
		ArrayList tmp = new ArrayList();
		Object[] items = model.toArray();
		if (items == null) {
			return null;
		}
		for (int i = 0; i < items.length; ++i) {
			Object item = items[i];
			if (item == null) {
				continue;
			}
			if (! (item instanceof XListItem)) {
				continue;
			}
			XListItem listItem = (XListItem) item;
			if (listItem.getParent() != null) {
				continue;
			}
			Operation operation = Operation.getOperationViaListItem(listItem);
			if (operation != null) {
				tmp.add(operation);
			}
		}
		if (tmp.size() <= 0) {
			return null;
		}
		Operation[] result = new Operation[tmp.size()];
		for (int i = 0; i < tmp.size(); ++i) {
			result[i] = (Operation) (tmp.get(i));
		}
		return result;
	}
}