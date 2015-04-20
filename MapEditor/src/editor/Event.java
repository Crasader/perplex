package editor;

import java.io.*;
import java.awt.*;

/**
 事件。
 id：		在某个地图内事件之间唯一标志性的ID。不同地图之间可能有相同ID的事件。
 ownerType：	所有者类型。包括“无所有者”、“Unit所有”、“事件物体所有”
 ownerID：	所有者的ID。所有者类型为“无所有者”的时候没有意义。
 name：		名称
 triggers：	触发器集合。按照TRIGGER_TYPE_ORDER的顺序来保存之。
 operations：	操作集合。手机顺序执行各个操作。
 */

public class Event {
	public final static int OWNER_NONE = 0;
	public final static int OWNER_Unit = 1;
	public final static int OWNER_EVENTOBJECT = 2;

	public final static int[] TRIGGER_TYPE_ORDER = { //按照一定的顺序保存触发器，用以提高手机的执行速度
		Trigger.SWITCH,
		Trigger.RANDOM,
		Trigger.UNIT_IN_MAP_AREA
	};

	public static Event getEventViaListItem(XListItem item) {
		if (item == null) {
			return null;
		}
		if (item.getValue() == null) {
			return null;
		}
		if (! (item.getValue()instanceof Pair)) {
			return null;
		}
		Object result = ( (Pair) item.getValue()).first;
		if (! (result instanceof Event)) {
			return null;
		}
		return ( (Event) result);
	}

	public static String getUnitDescription(int unitID) {
		String unitName = "";
		if (unitID == UnitManager.PLAYER_ID) {
			unitName = UnitManager.PLAYER_NAME;
		}
		else {
			try {
				Sprite sprite = MainFrame.self.getPanels()[MainFrame.LAYER_UNIT].
								getManager().getSprite(unitID);
				if (sprite != null) {
					unitName = sprite.getName();
				}
			}
			catch (Exception e) {
			}
		}
		return "Unit[" + unitName + "]";
	}
	
	public static String getBuildingDescription(int buildingID) {
		String buildingName = "";
		try {
			Sprite sprite = MainFrame.self.getPanels()[MainFrame.LAYER_BUILDING].
							getManager().getSprite(buildingID);
			if (sprite != null) {
				buildingName = sprite.getName();
			}
		}
		catch (Exception e) {
		}
		return "Building[" + buildingName + "]";
	}

	
	public static String getAreaDescription(int left, int top, int width, int height) {
		return getAreaDescription(new int[] {left, top, width, height});
	}

	public static String getAreaDescription(Rect rect) {
		int[] data = null;
		if (rect != null) {
			data = new int[] {rect.x, rect.y, rect.width, rect.height};
		}
		return getAreaDescription(data);
	}

	public static String getAreaDescription(int[] data) {
		String result = "区域[";
		if (data != null) {
			if (data.length >= 1) {
				result = result + data[0];
			}
			if (data.length >= 2) {
				result = result + "," + data[1];
			}
			if (data.length >= 3) {
				result = result + "," + data[2];
			}
			if (data.length >= 4) {
				result = result + "," + data[3];
			}
		}
		result = result + "]";
		return result;
	}

	public static String getPointDescription(IntPair p) {
		int[] data = null;
		if (p != null) {
			data = new int[] {p.x, p.y};
		}
		return getPointDescription(data);
	}

	public static String getPointDescription(int[] data) {
		String result = "地点[";
		if (data != null) {
			if (data.length >= 1) {
				result = result + data[0];
			}
			if (data.length >= 2) {
				result = result + "," + data[1];
			}
		}
		result = result + "]";
		return result;
	}

	public static String getRangeDescription(IntPair r) {
		int[] data = null;
		if (r != null) {
			data = new int[] {r.x, r.y};
		}
		return getRangeDescription(data);
	}

	public static String getRangeDescription(int[] data) {
		String result = "范围[";
		if (data != null) {
			if (data.length >= 1) {
				result = result + data[0];
			}
			if (data.length >= 2) {
				result = result + "," + data[1];
			}
		}
		result = result + ")";
		return result;
	}

	public static String getSwitchDescription(int id) {
		String[] switchs = MainFrame.self.getEventManager().getSwitchManager().getSwitchs();
		return "开关量[" + getSwitchDescription(id, switchs) + "]";
	}

	public static String getSwitchDescription(int id, String[] switchs) {
		String result = "无";
		if (switchs != null) {
			if (id >= 0 && id < switchs.length) {
				result = XUtil.getNumberString(id,
											   XUtil.getDefPropInt("SwitchIDStringLength")) +
						 "：" + switchs[id];
			}
		}
		return result;
	}	

	public static String getCounterDescription(int id) {
		String[] counters = MainFrame.self.getEventManager().getCounterManager().getCounters();
		return "计数器[" + getCounterDescription(id, counters) + "]";
	}

	public static String getCounterDescription(int id, String[] counters) {
		String result = "无";
		if (counters != null) {
			if (id >= 0 && id < counters.length) {
				result = XUtil.getNumberString(id,
											   XUtil.getDefPropInt("CounterIDStringLength")) +
						 "：" + counters[id];
			}
		}
		return result;
	}
	
	public static int getColorValue(Color color) {
		return (int)(((color.getRed() & 0xFF) << 16) | 
					 ((color.getGreen() & 0xFF) << 8) |
					 (color.getBlue() & 0xFF));
	}
	
	public static String getColorDescription(Color color) {
		String result = "颜色[0x" + Integer.toHexString(Event.getColorValue(color)).toUpperCase() + "]";
		return result;
	}

	private static int maxID = 0;

	private int id, ownerType, ownerID;
	private String name;
	private Trigger[] triggers;
	private Operation[] operations;

	public static Event createInstance() {
		return createInstance(maxID++);
	}

	public static Event createInstance(int id) {
		Event result = new Event(id);
		if (result != null) {
			if (maxID <= id) {
				maxID = id + 1;
			}
		}
		return result;
	}

	public static Event createViaFile(DataInputStream in, StringManager stringManager) throws Exception {
		Event result = new Event();
		if (result != null) {
			result.load(in, stringManager);
		}
		return result;
	}

	protected Event() {
		init( -1, null, null);
	}

	protected Event(int id) {
		init(id, null, null);
	}

	protected Event(int id, Trigger[] triggers, Operation[] operations) {
		init(id, triggers, operations);
	}

	private void init(int id, Trigger[] triggers, Operation[] operations) {
		this.id = id;
		this.name = "事件" + id;
		this.triggers = triggers;
		this.operations = operations;
		this.ownerType = OWNER_NONE;
		this.ownerID = -1;
	}

	public int getID() {
		return id;
	}

	public int getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(int ownerType) {
		this.ownerType = ownerType;
	}

	public int getOwnerID() {
		return ownerID;
	}

	public void setOwnerID(int ownerID) {
		this.ownerID = ownerID;
	}

	public void setName(String name) {
		if (name == null) {
			this.name = "";
		}
		else {
			this.name = name;
		}
	}

	public String getName() {
		if (name == null) {
			return "";
		}
		else {
			return name;
		}
	}

	public void setTriggers(Trigger[] triggers) {
		this.triggers = triggers;
	}

	public Trigger[] getTriggers() {
		return triggers;
	}

	public void setOperations(Operation[] operations) {
		this.operations = operations;
	}

	public Operation[] getOperations() {
		return operations;
	}

	public String getListItemDescription() {
		return name;
	}

	public void saveMobileData(DataOutputStream out, StringManager stringManager) throws Exception {
		try {
//			SL.writeInt(id, out);

			if (triggers == null) {
				SL.writeInt(0, out);
			}
			else {
				SL.writeInt(triggers.length, out);
				//按照TRIGGER_TYPE_ORDER顺序保存触发器。
				for (int orderIndex = 0; orderIndex < TRIGGER_TYPE_ORDER.length; ++orderIndex) {
					for (int triggerIndex = 0; triggerIndex < triggers.length; ++triggerIndex) {
						if (triggers[triggerIndex].getType() == TRIGGER_TYPE_ORDER[orderIndex]) {
							triggers[triggerIndex].saveMobileData(out);
						}
					}
				}
				//按照加入时的顺序保存其类型不在TRIGGER_TYPE_ORDER里面的触发器。
				for (int triggerIndex = 0; triggerIndex < triggers.length; ++triggerIndex) {
					boolean hasSaved = false;
					for (int orderIndex = 0; orderIndex < TRIGGER_TYPE_ORDER.length; ++orderIndex) {
						if (triggers[triggerIndex].getType() == TRIGGER_TYPE_ORDER[orderIndex]) {
							hasSaved = true;
							break;
						}
					}
					if (!hasSaved) {
						triggers[triggerIndex].saveMobileData(out);
					}
				}
			}

			if (operations == null) {
				SL.writeInt(0, out);
			}
			else {
				SL.writeInt(operations.length, out);
				for (int i = 0; i < operations.length; ++i) {
					operations[i].saveMobileData(out, stringManager);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new Exception("保存事件失败");
		}
	}

	public void save(DataOutputStream out, StringManager stringManager) throws Exception {
		try {
			out.writeInt(id);
			out.writeInt(ownerType);
			out.writeInt(ownerID);
			SL.writeString(name, out);

			if (triggers == null) {
				out.writeInt(0);
			}
			else {
				out.writeInt(triggers.length);
				for (int i = 0; i < triggers.length; ++i) {
					triggers[i].save(out);
				}
			}

			if (operations == null) {
				out.writeInt(0);
			}
			else {
				out.writeInt(operations.length);
				for (int i = 0; i < operations.length; ++i) {
					operations[i].save(out, stringManager);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new Exception("保存事件失败");
		}
	}

	protected void load(DataInputStream in, StringManager stringManager) throws Exception {
		init( -1, null, null);
		try {
			id = in.readInt();
			if (maxID <= id) {
				maxID = id + 1;
			}
			ownerType = in.readInt();
			ownerID = in.readInt();
			name = SL.readString(in);

			int triggerSize = in.readInt();
			if (triggerSize > 0) {
				triggers = new Trigger[triggerSize];
				for (int i = 0; i < triggerSize; ++i) {
					triggers[i] = Trigger.createViaFile(in);
				}
			}

			int operationSize = in.readInt();
			if (operationSize > 0) {
				operations = new Operation[operationSize];
				for (int i = 0; i < operationSize; ++i) {
					operations[i] = Operation.createViaFile(in, stringManager);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new Exception("读取事件失败");
		}
	}

	public Event getCopy() {
		Event result = new Event(this.id);
		result.ownerType = this.ownerType;
		result.ownerID = this.ownerID;
		result.name = this.name;
		if (this.triggers == null) {
			result.triggers = null;
		}
		else {
			result.triggers = new Trigger[this.triggers.length];
			for (int i = 0; i < this.triggers.length; ++i) {
				result.triggers[i] = this.triggers[i].getCopy();
			}
		}
		if (this.operations == null) {
			result.operations = null;
		}
		else {
			result.operations = new Operation[this.operations.length];
			for (int i = 0; i < this.operations.length; ++i) {
				result.operations[i] = this.operations[i].getCopy();
			}
		}
		return result;
	}
}