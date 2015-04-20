package editor;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

class SingleDropItem {
	public final static int TYPE_HP = 0;
	public final static int TYPE_WEAPONA = 1;
	public final static int TYPE_WEAPONB = 2;
	public final static int TYPE_WEAPONC = 3;
	public final static int TYPE_ANYWAY = 4;
	
	public final static int[] TYPES = {TYPE_HP, 
									  TYPE_WEAPONA,
									  TYPE_WEAPONB,
									  TYPE_WEAPONC,
									  TYPE_ANYWAY
	};
	
	public final static String[] TYPE_DESCS = {
											  "散弹", 
											  "火炮", 
											  "导弹", 
											  "雷",
											  "血包"
	};
	
	public final static String[] TRIGGER_DESC = {
											  "散弹", 
											  "火炮", 
											  "导弹", 
											  "雷",
											  "血包"
	};
	
	
	public static SingleDropItem getDropItemViaListItem(XListItem item) {
		if (item == null) {
			return null;
		}
		if (item.getValue() == null) {
			return null;
		}
		if (! (item.getValue() instanceof Pair)) {
			return null;
		}
		Object result = ( (Pair) item.getValue()).first;
		if (! (result instanceof SingleDropItem)) {
			return null;
		}
		return ( (SingleDropItem) result);
	}

	
	public int type;
	public int min;
	public int max;
	public int prob;

	public SingleDropItem() {
		type = 0;
		min = max = 0;
		prob = 0;
	}

	public SingleDropItem(int type, int min, int max, int prob) {
		this.type = type;
		this.min = min;
		this.max = max;
		this.prob = prob;
	}

	public SingleDropItem getCopy() {
		SingleDropItem result = new SingleDropItem();
		result.copyFrom(this);
		return result;
	}
	
	public void copyFrom(SingleDropItem source) {
		this.type = source.type;
		this.min = source.min;
		this.max = source.max;
		this.prob = source.prob;
	}
	
	public String toString() {
		String triggerString = "当[" + TRIGGER_DESC[type] + "]位于范围[" + min + "," + max + ")的时候";
		if(type == TYPE_ANYWAY) {
			triggerString = "任意情况下";
		}		
		String result = triggerString + "以" + prob + "％的概率掉落[" + TYPE_DESCS[type] + "]";
		return result;
	}
	
	public void saveMobileData(DataOutputStream out) throws Exception {
		SL.writeInt(type, out);
		SL.writeInt(min, out);
		SL.writeInt(max, out);
		SL.writeInt(prob, out);
	}
	
	public void save(DataOutputStream out) throws Exception {
		out.writeInt(type);
		out.writeInt(min);
		out.writeInt(max);
		out.writeInt(prob);
	}
	
	public void load(DataInputStream in) throws Exception {
		type = in.readInt();
		min = in.readInt();
		max = in.readInt();
		prob = in.readInt();
	}
}

class SingleDropItemSetter extends OKCancelDialog {
	private SingleDropItem dropItem;
	private ValueChooser typeChooser;
	private NumberSpinner minSpinner;
	private NumberSpinner maxSpinner;
	private NumberSpinner probSpinner;
	
	public SingleDropItemSetter(JDialog owner, SingleDropItem dropItem) {
		super(owner);
		this.dropItem = dropItem;
		
		typeChooser = new ValueChooser(dropItem.type, SingleDropItem.TYPES, SingleDropItem.TRIGGER_DESC);
		typeChooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(typeChooser.getValue() == SingleDropItem.TYPE_ANYWAY) {
					minSpinner.setEnabled(false);
					maxSpinner.setEnabled(false);
				}
				else {
					minSpinner.setEnabled(true);
					maxSpinner.setEnabled(true);
				}
			}
		});
		minSpinner = new NumberSpinner();
		minSpinner.setIntValue(dropItem.min);
		maxSpinner = new NumberSpinner();
		maxSpinner.setIntValue(dropItem.max);
		probSpinner = new NumberSpinner();
		probSpinner.setIntValue(dropItem.prob);
		
		JPanel mmp = new JPanel();
		mmp.setLayout(new GridBagLayout());
		GridBagConstraints mmc = new GridBagConstraints();
		mmc.fill = GridBagConstraints.BOTH;
		mmc.insets = new Insets(0, 2, 0, 3);
					 
		mmc.gridx = 0;
		mmc.gridy = 0;
		mmc.weighty = 1;
		mmc.weightx = 0;
		mmp.add(new JLabel("从  "), mmc);
		
		mmc.gridx = 1;
		mmc.weightx = 1;
		mmp.add(minSpinner, mmc);
		
		mmc.gridx = 2;
		mmc.weightx = 0;
		mmp.add(new JLabel("  到  "), mmc);
		
		mmc.gridx = 3;
		mmc.weightx = 1;
		mmp.add(maxSpinner, mmc);
		
		JPanel p = new JPanel();
		p.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(2, 2, 3, 3);
		
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 1;
		p.add(new JLabel("选择类型："), c);
		
		c.gridx = 1;
		c.weightx = 1;
		p.add(typeChooser, c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0;
		p.add(new JLabel("设置对应属性的范围："), c);
		
		c.gridx = 1;
		c.weightx = 1;
		p.add(mmp, c);
		
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 0;
		p.add(new JLabel("设置概率："), c);
		
		c.gridx = 1;
		c.weightx = 1;
		p.add(probSpinner, c);
		
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
			
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(p, BorderLayout.CENTER);
		cp.add(buttonPanel, BorderLayout.SOUTH);
	}
	
	public SingleDropItem getDropItem() {
		return dropItem;
	}
	
	public void okPerformed() {
		this.closeType = OK_PERFORMED;
		dropItem.type = typeChooser.getValue();
		dropItem.min = minSpinner.getIntValue();
		dropItem.max = maxSpinner.getIntValue();
		dropItem.prob = probSpinner.getIntValue();
		dispose();
	}
	
	public void cancelPerformed() {
		dispose();
	}
}


public class DropItemMode {
	private ArrayList singleDropItems;

	public DropItemMode() {
		singleDropItems = new ArrayList();
	}

	public DropItemMode getCopy() {
		DropItemMode result = new DropItemMode();
		result.copyFrom(this);
		return result;
	}

	public void copyFrom(DropItemMode source) {
		this.singleDropItems.clear();
		for(int i = 0; i < source.singleDropItems.size(); ++i) {
			this.singleDropItems.add(((SingleDropItem)(source.singleDropItems.get(i))).getCopy());
		}
	}

	public SingleDropItem[] getDropItems() {
		SingleDropItem[] result = null;
		if(singleDropItems.size() > 0) {
			result = new SingleDropItem[singleDropItems.size()];
			for(int i = 0; i < singleDropItems.size(); ++i) {
				result[i] = ((SingleDropItem)(singleDropItems.get(i))).getCopy();
			}
		}
		return result;
	}

	public void clear() {
		singleDropItems.clear();
	}

	public void addDropItem(SingleDropItem dropItem) {
		if(dropItem != null) {
			singleDropItems.add(dropItem.getCopy());
		}
	}

	public void addDropItem(int type, int min, int max, int prob) {

		SingleDropItem tmp = new SingleDropItem(type, min, max, prob);
		singleDropItems.add(tmp);
	}

	public void saveMobileData(DataOutputStream out) throws Exception {
		SL.writeInt(singleDropItems.size(), out);
		for(int i = 0; i < singleDropItems.size(); ++i) {
			((SingleDropItem)(singleDropItems.get(i))).saveMobileData(out);
		}
	}

	public void save(DataOutputStream out) throws Exception {
		out.writeInt(singleDropItems.size());
		for(int i = 0; i < singleDropItems.size(); ++i) {
			((SingleDropItem)(singleDropItems.get(i))).save(out);
		}

	}

	public void load(DataInputStream in) throws Exception {
		singleDropItems.clear();
		int length = in.readInt();
		for(int i = 0; i < length; ++i) {
			SingleDropItem dropItem = new SingleDropItem();
			dropItem.load(in);
			singleDropItems.add(dropItem);
		}
	}
}

class DropItemModeList extends XList {
	public static ArrayList getDIMListItems(DropItemMode dim) {
		ArrayList result = new ArrayList();
		if (dim != null) {
			SingleDropItem[] dropItems = dim.getDropItems();
			if(dropItems != null) {
				for (int i = 0; i < dropItems.length; ++i) {
					if(dropItems[i] != null) {
						SingleDropItem dropItem = dropItems[i].getCopy();
						result.add(new XListItem(new Pair(dropItem, dropItem.toString())));
					}
				}
			}
		}
		return result;
	}

	public DropItemModeList(JDialog owner) {
		super(owner);
	}
	
	public DropItemModeList(JDialog owner, DropItemMode dim) {
		super(owner);
		setDIM(dim);
	}

	protected XListItem insert(XListItem item) {
		SingleDropItem dropItem = new SingleDropItem();

		SingleDropItemSetter setter = new SingleDropItemSetter(owner, dropItem);
		setter.show();
		if (setter.getCloseType() == OKCancelDialog.OK_PERFORMED) {
			dropItem = setter.getDropItem();			
			XListItem dropItemItem = new XListItem(new Pair(dropItem, dropItem.toString()));
			insertItem(item, dropItemItem, null);
			return dropItemItem;
		}
		else {
			return item;
		}
	}

	protected XListItem modify(XListItem item) {
		SingleDropItem dropItem = SingleDropItem.getDropItemViaListItem(item);
		if (dropItem == null) {
			return item;
		}
		SingleDropItemSetter setter = new SingleDropItemSetter(owner, dropItem);
		setter.show();
		if (setter.getCloseType() == OKCancelDialog.OK_PERFORMED) {
			dropItem = setter.getDropItem();
			XListItem listItem = new XListItem(new Pair(dropItem, dropItem.toString()));
			replaceItem(item, listItem, null);
			return listItem;
		}
		else {
			return item;
		}
	}

	public void setDIM(DropItemMode dim) {
		setItems(getDIMListItems(dim));
	}
	
	public DropItemMode getDIM() {
		DropItemMode result = new DropItemMode();

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
			SingleDropItem dropItem = SingleDropItem.getDropItemViaListItem(listItem);
			if (dropItem != null) {
				result.addDropItem(dropItem);
			}
		}
		return result;
	}
}



















