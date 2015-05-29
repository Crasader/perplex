package editor;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

class SingleBarrack {
	public static SingleBarrack getBarrackViaListItem(XListItem item) {
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
		if (! (result instanceof SingleBarrack)) {
			return null;
		}
		return ( (SingleBarrack) result);
	}

	public int unitType;
	public int left, top, width, height;
	public int unitCount;
	public int interval;
	
	public SingleBarrack() {
		unitType = 0;
		left = top = width = height = 0;
		unitCount = 0;
		interval = 0;
	}
	
	public SingleBarrack(int unitType, 
						 int x, int y, int width, int height, 
						 int unitCount, 
						 int interval) {
		
		this.unitType = unitType; 
		this.left = left; this.top = top; this.width = width; this.height = height;
		this.unitCount = unitCount;
		this.interval = interval;
	}
	
	public void copyFrom(SingleBarrack source) {
		this.unitType = source.unitType; 
		this.left = source.left; 
		this.top = source.top; 
		this.width = source.width; 
		this.height = source.height;
		this.unitCount = source.unitCount;
		this.interval = source.interval;
	}
	
	public SingleBarrack getCopy() {
		SingleBarrack result = new SingleBarrack();
		result.copyFrom(this);
		return result; 
	}
	
	public String toString() {
		String typeName = "未知兵种";
		UnitRes ur = ((URManager)(MainFrame.self.getResManagers()[MainFrame.RES_UR])).getUR(unitType);
		if(ur != null) {
			typeName = ur.getName();
		}
		String result = "[" + unitCount + "]个[" + typeName + "]出现于" + 
						Event.getAreaDescription(left, top, width, height) + "，冷却[" + interval + "]Tick"; 
		return result;
	}
	
	public void saveMobileData(DataOutputStream out) throws Exception {
		MapInfo info = MainFrame.self.getMapInfo();
		SL.writeInt(unitType, out);
		SL.writeInt(info.changeToMobileX(left), out);
		SL.writeInt(info.changeToMobileY(top, height), out);
		SL.writeInt(width, out);
		SL.writeInt(height, out);
		SL.writeInt(unitCount, out);
		SL.writeInt(interval, out);
	}
	
	public void save(DataOutputStream out) throws Exception {
		out.writeInt(unitType);
		out.writeInt(left);
		out.writeInt(top);
		out.writeInt(width);
		out.writeInt(height);
		out.writeInt(unitCount);
		out.writeInt(interval);
	}
	
	public void load(DataInputStream in) throws Exception {
		unitType = in.readInt();
		left = in.readInt();
		top = in.readInt();
		width = in.readInt();
		height = in.readInt();
		unitCount = in.readInt();
		interval = in.readInt();
	}
}

class SingleBarrackSetter extends OKCancelDialog {
	
	private SingleBarrack barrack;
	
	private ValueChooser typeChooser;
	private MapAreaSetPanel areaPanel;
	private NumberSpinner countSpinner;
	private NumberSpinner intervalSpinner;
	
	public SingleBarrackSetter(JDialog owner, MainFrame mainFrame, SingleBarrack barrack) {
		super(owner);
		this.barrack = barrack;
		
		URManager urManager = (URManager)(mainFrame.getResManagers()[MainFrame.RES_UR]);
		UnitRes[] urs = urManager.getURs();
		if(urs != null) {
			int[] ids = new int[urs.length];
			int maxID = 0;
			for(int i = 0; i < urs.length; ++i) {
				ids[i] = urs[i].getID();
				maxID = Math.max(maxID, ids[i]);
			}
			String[] names = new String[maxID + 1];
			for(int i = 0; i < names.length; ++i) {
				names[i] = "无效";
			}
			for(int i = 0; i < urs.length; ++i) {
				names[urs[i].getID()] = urs[i].getName();
			}
			typeChooser = new ValueChooser(barrack.unitType, ids, names);
		}
		else {
			typeChooser = new ValueChooser(barrack.unitType, new int[] {0}, new String[] {"无效"});
		}
		
		areaPanel = new MapAreaSetPanel(owner, mainFrame, 
										barrack.left, barrack.top, barrack.width, barrack.height); 
		
		countSpinner = new NumberSpinner();
		countSpinner.setIntValue(barrack.unitCount);
		
		intervalSpinner = new NumberSpinner();
		intervalSpinner.setIntValue(barrack.interval);
		
		JPanel p = new JPanel();
		p.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(2, 2, 3, 3);

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		p.add(new JLabel("选择一种作战单位："), c);
		
		c.gridx = 1;
		c.weightx = 1;
		p.add(typeChooser, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 0;
		p.add(new JLabel("出兵的数量："), c);

		c.gridx = 1;
		c.weightx = 1;
		p.add(countSpinner, c);

		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 0;
		p.add(new JLabel("冷却时间（Tick）："), c);

		c.gridx = 1;
		c.weightx = 1;
		p.add(intervalSpinner, c);
		
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		c.weightx = 1;
		p.add(areaPanel, c);
		
		c.gridy = 4;
		c.weighty = 1;
		p.add(new JPanel(), c);
		
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(p, BorderLayout.CENTER);
		cp.add(buttonPanel, BorderLayout.SOUTH);
	}
	
	public SingleBarrack getBarrack() {
		return barrack;
	}
	
	protected void okPerformed() {
		this.closeType = OK_PERFORMED;
		barrack.unitType = typeChooser.getValue();
		barrack.left = areaPanel.getAreaLeft();
		barrack.top = areaPanel.getAreaTop();
		barrack.width = areaPanel.getAreaWidth();
		barrack.height = areaPanel.getAreaHeight();
		barrack.unitCount = countSpinner.getIntValue();
		barrack.interval = intervalSpinner.getIntValue();
		dispose();
	}
	
	protected void cancelPerformed() {
		dispose();
	}
}

public class BarrackMode {
	private Building owner;
	private ArrayList singleBarracks;
	
    public BarrackMode() {
		this.owner = null;
		init();
    }
	
	public BarrackMode(Building owner) {
		this.owner = owner;
		init();
	}
	
	private void init() {
		singleBarracks = new ArrayList();		
	}
	
	public BarrackMode getCopy() {
		BarrackMode result = new BarrackMode();
		result.copyFrom(this);
		return result;
	}
	
	public void copyFrom(BarrackMode source) {
		this.owner = source.owner;
		this.singleBarracks.clear();
		for(int i = 0; i < source.singleBarracks.size(); ++i) {
			this.singleBarracks.add(((SingleBarrack)(source.singleBarracks.get(i))).getCopy());
		}
	}
	
	public Building getOwner() {
		return owner;
	}
	
	public void setOwner(Building owner) {
		this.owner = owner;
	}
	
	public SingleBarrack[] getBarracks() {
		SingleBarrack[] result = null;
		if(singleBarracks.size() > 0) {
			result = new SingleBarrack[singleBarracks.size()];
			for(int i = 0; i < singleBarracks.size(); ++i) {
				result[i] = ((SingleBarrack)(singleBarracks.get(i))).getCopy();
			}
		}
		return result;
	}
	
	public void clear() {
		singleBarracks.clear();
	}
	
	public void addBarrack(SingleBarrack barrack) {
		if(barrack != null) {
			singleBarracks.add(barrack.getCopy());
		}
	}
	
	public void addBarrack(int unitType, 
						   int x, int y, int width, int height, 
						   int unitCount, 
						   int interval) {
		
		SingleBarrack tmp = new SingleBarrack(unitType, 
											  x, y, width, height, 
											  unitCount, 
											  interval);
		singleBarracks.add(tmp);
	}
	
	public void saveMobileData(DataOutputStream out) throws Exception {
		SL.writeInt(singleBarracks.size(), out);
		for(int i = 0; i < singleBarracks.size(); ++i) {
			((SingleBarrack)(singleBarracks.get(i))).saveMobileData(out);
		}
	}
	
	public void save(DataOutputStream out) throws Exception {
		out.writeInt(singleBarracks.size());
		for(int i = 0; i < singleBarracks.size(); ++i) {
			((SingleBarrack)(singleBarracks.get(i))).save(out);
		}

	}
	
	public void load(DataInputStream in) throws Exception {
		singleBarracks.clear();
		int length = in.readInt();
		for(int i = 0; i < length; ++i) {
			SingleBarrack barrack = new SingleBarrack();
			barrack.load(in);
			singleBarracks.add(barrack);
		}
	}
}

class BarrackModeList extends XList {
	public static ArrayList getBMListItems(BarrackMode bm) {
		ArrayList result = new ArrayList();
		if (bm != null) {
			SingleBarrack[] barracks = bm.getBarracks();
			if(barracks != null) {
				for (int i = 0; i < barracks.length; ++i) {
					if(barracks[i] != null) {
						SingleBarrack barrack = barracks[i].getCopy();
						result.add(new XListItem(new Pair(barrack, barrack.toString())));
					}
				}
			}
		}
		return result;
	}
	
	private MainFrame mainFrame;
	private BarrackMode bm;
	
	public BarrackModeList(JDialog owner, MainFrame mainFrame, BarrackMode bm) {
		super(getBMListItems(bm), owner);
		this.mainFrame = mainFrame;
		this.bm = bm;
	}

	protected XListItem insert(XListItem item) {
		SingleBarrack barrack = new SingleBarrack();
		Building building = bm.getOwner();
		if(building != null) {
			barrack.left = building.getX();
			barrack.top = building.getY();
		}

		SingleBarrackSetter setter = new SingleBarrackSetter(owner, mainFrame, barrack);
		setter.show();
		if (setter.getCloseType() == OKCancelDialog.OK_PERFORMED) {
			barrack = setter.getBarrack();			
			XListItem barrackItem = new XListItem(new Pair(barrack, barrack.toString()));
			insertItem(item, barrackItem, null);
			return barrackItem;
		}
		else {
			return item;
		}
	}

	protected XListItem modify(XListItem item) {
		SingleBarrack barrack = SingleBarrack.getBarrackViaListItem(item);
		if (barrack == null) {
			return item;
		}
		SingleBarrackSetter setter = new SingleBarrackSetter(owner, mainFrame, barrack);
		setter.show();
		if (setter.getCloseType() == OKCancelDialog.OK_PERFORMED) {
			barrack = setter.getBarrack();
			XListItem barrackItem = new XListItem(new Pair(barrack, barrack.toString()));
			replaceItem(item, barrackItem, null);
			return barrackItem;
		}
		else {
			return item;
		}
	}
	
	public BarrackMode getBM() {
		BarrackMode result = new BarrackMode();
		
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
			SingleBarrack barrack = SingleBarrack.getBarrackViaListItem(listItem);
			if (barrack != null) {
				result.addBarrack(barrack);
			}
		}
		return result;
	}
}




















