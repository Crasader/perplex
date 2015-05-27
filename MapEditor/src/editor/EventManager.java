package editor;

import java.io.*;
import java.util.*;

/**
 某个地图所有的事件的管理者。负责保存、管理和加载事件。
 */
public class EventManager {
	private SwitchManager switchManager;
	private CounterManager counterManager;
	private HeadResManager headResManager;
	private ArrayList events;

	public EventManager() {
		init();
	}

	private void init() {
		switchManager = new SwitchManager();
		counterManager = new CounterManager();
		headResManager = new HeadResManager();
		try {
			headResManager.load();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		events = new ArrayList();
		reset();
	}

	public void reset() {
		switchManager.reset();
		counterManager.reset();
		events.clear();
	}
	
	public SwitchManager getSwitchManager() {
		return switchManager;
	}
	
	public CounterManager getCounterManager() {
		return counterManager;
	}
	
	public HeadResManager getHeadResManager() {
		return headResManager;
	}

	public Event[] getEvents() {
		Event[] result = new Event[events.size()];
		for (int i = 0; i < events.size(); ++i) {
			result[i] = ( (Event) (events.get(i))).getCopy();
		}
		return result;
	}

	public Event[] getEvents(int ownerType) {
		ArrayList tmp = new ArrayList();
		for (int i = 0; i < events.size(); ++i) {
			Event event = (Event) (events.get(i));
			if (event.getOwnerType() == ownerType) {
				tmp.add(event);
			}
		}
		Event[] result = new Event[tmp.size()];
		for (int i = 0; i < tmp.size(); ++i) {
			result[i] = (Event) (tmp.get(i));
		}
		return result;
	}

	public Event[] getEvents(int ownerType, int ownerID) {
		ArrayList tmp = new ArrayList();
		for (int i = 0; i < events.size(); ++i) {
			Event event = (Event) (events.get(i));
			if (event.getOwnerType() == ownerType && event.getOwnerID() == ownerID) {
				tmp.add(event);
			}
		}
		Event[] result = new Event[tmp.size()];
		for (int i = 0; i < tmp.size(); ++i) {
			result[i] = (Event) (tmp.get(i));
		}
		return result;
	}

	public boolean add(Event event) {
		for (int i = 0; i < events.size(); ++i) {
			if ( ( (Event) (events.get(i))).getID() == event.getID()) {
				return false;
			}
		}
		return events.add(event.getCopy());
	}

	public boolean remove(Event event) {
		for (int i = 0; i < events.size(); ++i) {
			if ( ( (Event) (events.get(i))).getID() == event.getID()) {
				events.remove(i);
				return true;
			}
		}
		return false;
	}

	public void replace(Event[] oldEvents, Event[] newEvents) {
		if (oldEvents != null) {
			for (int i = 0; i < oldEvents.length; ++i) {
				remove(oldEvents[i]);
			}
		}
		if (newEvents != null) {
			for (int i = 0; i < newEvents.length; ++i) {
				add(newEvents[i]);
			}
		}
	}

	public void saveMobileData(String name) throws Exception {
		try {
			String n = name.toLowerCase();
			File f = new File(XUtil.getDefPropStr("MobilePath") + "\\" + n + "_event_mobile.dat");
			DataOutputStream out =
				new DataOutputStream(
				new BufferedOutputStream(
				new FileOutputStream(f)));
			StringManager stringManager = new StringManager();
			SL.writeInt(events.size(), out);
			for (int i = 0; i < events.size(); ++i) {
				( (Event) (events.get(i))).saveMobileData(out, stringManager);
			}
			stringManager.save(name + "_String_Mobile");
			out.flush();
			out.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new Exception("保存事件失败");
		}
	}

	public void save(String name) throws Exception {
		try {
			switchManager.save(name);
			counterManager.save(name);
			File f = new File(XUtil.getDefPropStr("EventFilePath") + "\\" + name + "_Event.dat");
			DataOutputStream out =
				new DataOutputStream(
				new BufferedOutputStream(
				new FileOutputStream(f)));
			StringManager stringManager = new StringManager();
			out.writeInt(events.size());
			for (int i = 0; i < events.size(); ++i) {
				( (Event) (events.get(i))).save(out, stringManager);
			}
			stringManager.save(name + "_String");
			out.flush();
			out.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new Exception("保存事件失败");
		}
	}

	public void load(String name) throws Exception {
		try {
			reset();
			switchManager.load(name);
			counterManager.load(name);
			File f = new File(XUtil.getDefPropStr("EventFilePath") + "\\" + name + "_Event.dat");
			if (!f.exists()) {
				return;
			}
			DataInputStream in =
				new DataInputStream(
				new BufferedInputStream(
				new FileInputStream(f)));
			StringManager stringManager = new StringManager();
			stringManager.load(name + "_String");
			int size = in.readInt();
			for (int i = 0; i < size; ++i) {
				Event event = Event.createViaFile(in, stringManager);
				if (event != null) {
					add(event);
				}
			}
			in.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new Exception("读取事件失败");
		}
	}
}