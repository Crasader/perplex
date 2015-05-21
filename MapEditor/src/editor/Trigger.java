package editor;

//should modify
import java.io.*;

/**
 触发器
 */
public class Trigger {
	public final static int SWITCH = 0;
	public final static int COUNTER = 1;
	public final static int RANDOM = 2;
	public final static int UNIT_IN_MAP_AREA = 3;
	public final static int UNIT_HP = 4;
	public final static int BUILDING_HP = 5;

	public final static int[] TYPES = {
									  SWITCH,
									  COUNTER, 
									  RANDOM,
									  UNIT_IN_MAP_AREA, 
									  UNIT_HP,
									  BUILDING_HP
	};

	public final static String[] TYPE_DESCRIPTIONS = {
		"某个开关量满足条件", 
		"某个计数器满足条件", 
		"某个随机数在一定范围内",
		"某个Unit在地图的固定范围内",
		"作战单位的HP满足条件", 
		"建筑的HP满足条件"
	};

	private static int maxID = 0;

	private int id, type, targetID;
	private int[] data;

	public static Trigger getTriggerViaListItem(XListItem item) {
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
		if (! (result instanceof Trigger)) {
			return null;
		}
		return ( (Trigger) result);
	}

	protected Trigger(int id, int type, int targetID, int[] data) {
		this.id = id;
		this.type = type;
		this.targetID = targetID;
		this.data = data;
	}

	public static Trigger createInstance(int type) {
		return createInstance(maxID++, type);
	}

	public static Trigger createInstance(int id, int type) {
		Trigger result = new Trigger(id, type, -1, null);
		if (result != null) {
			if (maxID <= id) {
				maxID = id + 1;
			}
		}
		return result;
	}

	public static Trigger createViaFile(DataInputStream in) throws Exception {
		int id = in.readInt();
		int type = in.readInt();
		Trigger result = createInstance(id, type);
		result.load(in);
		return result;
	}

	public int getID() {
		return id;
	}

	public int getType() {
		return type;
	}

	public int getTargetID() {
		return targetID;
	}

	public int[] getData() {
		return data;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setTargetID(int targetID) {
		this.targetID = targetID;
	}

	public void setData(int[] data) {
		this.data = data;
	}

	public Trigger getCopy() {
		Trigger result = Trigger.createInstance(this.id, this.type);
		result.targetID = this.targetID;
		result.data = XUtil.copyArray(this.data);
		return result;
	}

	public void saveMobileData(DataOutputStream out) throws Exception {
		//SL.writeInt(id, out);
		SL.writeInt(type, out);
		SL.writeInt(targetID, out);
		if(data != null) {
			for(int i = 0; i < data.length; ++i) {
				if (data.length == 4) {
					if (i == 0) {
						SL.writeInt(MainFrame.self.getMapInfo().changeToMobileX(data[0]), out);
					}
					else if(i == 1)
					{
						SL.writeInt(MainFrame.self.getMapInfo().changeToMobileY(data[1], data[3]), out);
					}
					else
					{
						SL.writeInt(data[i], out);
					}
				}
				else
				{
					SL.writeInt(data[i], out);
				}
			}
		}
	}

	public void save(DataOutputStream out) throws Exception {
		out.writeInt(id);
		out.writeInt(type);
		out.writeInt(targetID);
		SL.writeIntArray(data, out);
	}

	protected void load(DataInputStream in) throws Exception {
		this.targetID = in.readInt();
		data = SL.readIntArray(in);
	}

	public String getListItemDescription() {
		switch (type) {
			case SWITCH:
				return getSwitchDescription();
			case RANDOM:
				return getRandomDescirption();
			case UNIT_IN_MAP_AREA:
				return getUnitInMapAreaDescription();
			case COUNTER:
				return getCounterDescription();
			case UNIT_HP:
				return getUnitPropDescription(type);
			case BUILDING_HP:
				return getBuildingPropDescription(type);
		}
		return "";
	}

	private String getSwitchDescription() {
		String result = Event.getSwitchDescription(targetID) + "为";
		if (data == null) {
			result = result + "On";
		}
		else {
			if (data.length < 1) {
				result = result + "On";
			}
			else {
				if (data[0] == 0) {
					result = result + "Off";
				}
				else {
					result = result + "On";
				}
			}
		}
		return result;
	}
	
	private String getCounterDescription() {
		String result = Event.getCounterDescription(targetID);
		if (data != null) {
			if (data.length > 0) {
				result = result + Relation.DESCRIPTIONS[data[0]];
			}
			if(data.length > 1) {
				result = result + data[1];
			}
		}
		return result;
	}


	private String getUnitInMapAreaDescription() {
		String result = Event.getUnitDescription(targetID) + "位于" + Event.getAreaDescription(data) + "内";
		return result;
	}

	private String getRandomDescirption() {
		String result = "以" + targetID + "为基数的随机数在" + Event.getRangeDescription(data) + "之内";
		return result;
	}
	
	private String getUnitPropDescription(int type) {
		String result = Event.getUnitDescription(targetID) + "的" + 
						UnitPropTrigger.getPropDescription(type) + "在" + 
						Event.getRangeDescription(data) + "之内";
		return result;
	}
	
	private String getBuildingPropDescription(int type) {
		String result = Event.getBuildingDescription(targetID) + "的" + 
						BuildingPropTrigger.getPropDescription(type) + "在" + 
						Event.getRangeDescription(data) + "之内";
		return result;
	}
}
