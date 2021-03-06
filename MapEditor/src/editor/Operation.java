package editor;

//should modify
import java.io.*;

/**
 操作
 */
public abstract class Operation {
	public final static int SWITCH_CHANGE = 0;
	public final static int COUNTER_CHANGE = 1;
	public final static int COMMON_DIALOG = 2;
	public final static int DELAY = 3;
	public final static int UNIT_APPEAR = 4;
	public final static int UNIT_DISAPPEAR = 5;
	public final static int UNIT_PATH_MOVE = 6;
	public final static int UNIT_FIRE = 7;
	public final static int UNIT_EXPLORE = 8;
	public final static int UNIT_CHANGE_AI = 9;
	public final static int UNIT_CHANGE_HP = 10;
	public final static int PLAYER_RECEIVE_KEY = 11;
	public final static int PLAYER_OUT_SCREEN = 12;
	public final static int BUILDING_EXPLORE = 13;
	public final static int CAMERA_FADE_IN = 14;
	public final static int CAMERA_FADE_OUT = 15;
	public final static int CAMERA_MOVE = 16;
	public final static int CAMERA_INSTANT_MOVE = 17;
	public final static int CAMERA_FOCUS_UNIT = 18;
	public final static int CAMERA_SHAKE = 19;
	public final static int CAMERA_RECT = 20;
	public final static int CAMERA_EFFECT = 21;
	public final static int PLAY_MUSIC = 22;
	public final static int PLAY_ANIM = 23;
	public final static int CHANGE_MAP = 24;
	public final static int CHANGE_STATE = 25;

	public final static int[][] TYPES = { {
		SWITCH_CHANGE, //0
		COUNTER_CHANGE, 
		COMMON_DIALOG, 
		DELAY
	}
		, {
		UNIT_APPEAR, 
		UNIT_DISAPPEAR, 
		UNIT_PATH_MOVE, 
		UNIT_FIRE, 
		UNIT_EXPLORE, 
		UNIT_CHANGE_AI, 
		UNIT_CHANGE_HP, 
		PLAYER_RECEIVE_KEY, 
		PLAYER_OUT_SCREEN
	}
		,{
		BUILDING_EXPLORE
	}
		,{
		CAMERA_FADE_IN, 
		CAMERA_FADE_OUT, 
		CAMERA_MOVE, 
		CAMERA_INSTANT_MOVE, 
		CAMERA_FOCUS_UNIT,
		CAMERA_SHAKE, 
		CAMERA_RECT, 
		CAMERA_EFFECT
	}
		,{
		PLAY_MUSIC, 
		PLAY_ANIM, 
		CHANGE_MAP, 
		CHANGE_STATE
	}
	};

	public final static String[] KIND_DESCRIPTIONS = {
		"基本",
		"Unit",
		"建筑", 
		"镜头", 
		"其他"
	};

	public final static String[] TYPE_DESCRIPTIONS = {
		"设置开关量", //SWITCH_CHANGE
		"设置计数器", 
		"普通对话框", //COMMON_DIALOG
		"延迟",
		"Unit出现", 
		"Unit消失", 
		"Unit按固定路径移动", 
		"Unit用某种武器攻击", 
		"Unit爆炸", 
		"Unit改变AI", 
		"Unit改变HP", 
		"主角是否接收按键", 
		"主角是否必须位于屏幕内", 
		"建筑爆炸", 
		"淡入游戏", 
		"从游戏中淡出", 
		"镜头移动", 
		"镜头切换", 
		"镜头跟踪Unit", 
		"震屏（是否白屏）", 
		"设置镜头的矩形范围", 
		"打开/关闭剧情效果", 
		"播放音乐", 
		"播放动画",
		"切换地图",
		"切换游戏状态"
	};

	private static int maxID = 0;

	protected int type, id;

	protected Operation(int id, int type) {
		this.type = type;
		this.id = id;
	}

	public static Operation createInstance(int type) {
		return createInstance(maxID++, type);
	}

	public static Operation createInstance(int id, int type) {
		Operation result = null;
		switch (type) {
			case SWITCH_CHANGE:
				result = new SwitchChange(id);
				break;
			case COMMON_DIALOG:
				result = new CommonDialog(id);
				break;
			case DELAY:
				result = new Delay(id);
				break;
			case UNIT_PATH_MOVE:
				result = new UnitPathMove(id);
				break;
			case COUNTER_CHANGE:
				result = new CounterChange(id);
				break;
			case UNIT_APPEAR:
				result = new UnitAppear(id);
				break;
			case UNIT_DISAPPEAR:
				result = new UnitDisappear(id);
				break;
			case UNIT_FIRE:
				result = new UnitFire(id);
				break;
			case UNIT_EXPLORE:
				result = new UnitExplore(id);
				break;
			case UNIT_CHANGE_AI:
				result = new UnitChangeAI(id);
				break;
			case UNIT_CHANGE_HP:
				result = new UnitChangeProp(id, UNIT_CHANGE_HP);
				break;
			case PLAYER_RECEIVE_KEY:
				result = new PlayerReceiveKey(id);
				break;
			case PLAYER_OUT_SCREEN:
				result = new PlayerOutScreen(id);
				break;
			case BUILDING_EXPLORE:
				result = new BuildingExplore(id);
				break;
			case CAMERA_FADE_IN:
				result = new CameraFadeIn(id);
				break;
			case CAMERA_FADE_OUT:
				result = new CameraFadeOut(id);
				break;
			case CAMERA_MOVE:
			case CAMERA_INSTANT_MOVE:
				result = new CameraMove(id, type);
				break;
			case CAMERA_FOCUS_UNIT:
				result = new CameraFocusUnit(id);
				break;
			case CAMERA_SHAKE:
				result = new CameraShake(id);
				break;
			case CAMERA_RECT:
				result = new CameraRect(id);
				break;
			case CAMERA_EFFECT:
				result = new CameraEffect(id);
				break;
			case PLAY_MUSIC:
				result = new PlayMusic(id);
				break;
			case PLAY_ANIM:
				result = new PlayAnim(id);
				break;
			case CHANGE_MAP:
				result = new ChangeMap(id);
				break;
			case CHANGE_STATE:
				result = new ChangeState(id);
				break;
		}
		if (result != null) {
			if (maxID <= id) {
				maxID = id + 1;
			}
		}
		return result;
	}

	public static Operation createViaFile(DataInputStream in, StringManager stringManager) throws Exception {
		int id = in.readInt();
		int type = in.readInt();
		Operation result = createInstance(id, type);
		result.load(in, stringManager);
		return result;
	}

	public static Operation getOperationViaListItem(XListItem item) {
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
		if (! (result instanceof Operation)) {
			return null;
		}
		return ( (Operation) result);
	}

	public int getType() {
		return type;
	}

	public int getID() {
		return id;
	}

	public void save(DataOutputStream out, StringManager stringManager) throws Exception {
		out.writeInt(id);
		out.writeInt(type);
	}

	public void saveMobileData(DataOutputStream out, StringManager stringManager) throws Exception {
		//SL.writeInt(id, out);
		SL.writeInt(type, out);
	}

	protected void load(DataInputStream in, StringManager stringManager) throws Exception {
	}

	public abstract String getListItemDescription();

	public abstract Operation getCopy();
}