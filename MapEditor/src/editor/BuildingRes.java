package editor;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

abstract public class BuildingRes {
	public final static int TYPE_COUNT = 4;
	public final static int EXPLORE = 0;
	public final static int HOUSE = 1;
	public final static int BARRACK = 2;
	public final static int OILCAN = 3;
	public final static String[] DESCS = {
										 "爆炸物（沙包类）", 
										 "半毁型爆炸物（房屋类）", 
										 "兵营类", 
										 "油桶类"
	};
	
	private int id;
	private int type;
	private String name;
	protected IntPair origin;
	private Rect[] bodyRects; //被攻击范围，无方向性
	private boolean shake;
	private DropItemMode dim;

	public BuildingRes(int type) {
		init(0, type);
	}

	public BuildingRes(int id, int type) {
		init(id, type);
	}

	private void init(int id, int type) {
		this.id = id;
		this.type = type;
		this.name = "";
		this.shake = false;
		this.dim = new DropItemMode();
		this.origin = new IntPair();
		this.bodyRects = null;
	}

	public BuildingRes getCopy() {
		BuildingRes result = BuildingRes.createInstance(this.getID(), this.getType());
		result.copyFrom(this);
		return result;
	}

	public void copyFrom(BuildingRes source) {
		this.id = source.id;
		this.type = source.type;
		this.name = source.name;
		this.shake = source.shake;
		this.dim = source.dim.getCopy();
		this.origin.x = source.origin.x;
		this.origin.y = source.origin.y;
		this.bodyRects = XUtil.copyArray(source.bodyRects);
	}

	public String toString() {
		return name;
	}

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}
	
	public int getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isShake() {
		return shake;
	}
	
	public void setShake(boolean shake) {
		this.shake = shake;
	}
	
	public DropItemMode getDIM() {
		return dim.getCopy();
	}
	
	public void setDIM(DropItemMode dim) {
		this.dim = dim.getCopy();
	}
	
	public IntPair getOrigin() {
		return origin.getCopy();
	}
	
	public void setOrigin(IntPair origin) {
		this.origin.x = origin.x;
		this.origin.y = origin.y;
	}
	
	public int getLeft(int originX) {
		return originX - origin.x - getWidth() / 2;
	}
	
	public int getTop(int originY) {
		return originY - origin.y - getHeight() / 2;
	}

	public ArrayList getBodyRects() {
		ArrayList result = new ArrayList();
		if (bodyRects != null) {
			for (int i = 0; i < bodyRects.length; ++i) {
				Rect r = bodyRects[i];
				if (r != null) {
					result.add(r.getCopy());
				}
			}
		}
		return result;
	}

	public void setBodyRects(ArrayList bodyRects) {
		this.bodyRects = null;
		if (bodyRects != null) {
			if (bodyRects.size() > 0) {
				this.bodyRects = new Rect[bodyRects.size()];
				for (int i = 0; i < bodyRects.size(); ++i) {
					if (bodyRects.get(i) != null) {
						Rect r = (Rect) (bodyRects.get(i));
						this.bodyRects[i] = r.getCopy();
					}
				}
			}
		}
	}
	abstract public SingleImage[] getImages();
	
	abstract public int getWidth();
	
	abstract public int getHeight();
	
	abstract public void paintLeftTop(Graphics g, int left, int top, boolean flip);
	abstract public void paintOrigin(Graphics g, int originX, int originY, boolean flip);

	public void saveMobileData(DataOutputStream out) throws Exception {
		SL.writeInt(type, out);
		SL.writeInt(id, out);
		SL.writeByte(shake ? 1 : 0, out);
		SL.writeIntPairMobile(origin, out);
		SL.writeRectArrayMobile(bodyRects, out);
	}

	public void save(DataOutputStream out) throws Exception {
		out.writeInt(id);
		out.writeInt(type);
		SL.writeString(name, out);
		out.writeBoolean(shake);
		SL.writeIntPair(origin, out);
		SL.writeRectArray(bodyRects, out);
		dim.save(out);
	}
	
	public final static BuildingRes createInstance(int id, int type) {
		BuildingRes result = null;
		switch(type) {
			case EXPLORE:
				result = new ExploreBR(id);
				break;
			case HOUSE:
				result = new HouseBR(id);
				break;
		}
		return result;
	}

	public final static BuildingRes createViaFile(DataInputStream in, Object[] resManagers) throws Exception {
		int id = in.readInt();
		int type = in.readInt();
		BuildingRes result = BuildingRes.createInstance(id, type);
		result.load(in, resManagers);
		return result;
	}

	protected void load(DataInputStream in, Object[] resManagers) throws Exception {
		name = SL.readString(in);
		shake = in.readBoolean();
		origin = SL.readIntPair(in);
		bodyRects = SL.readRectArray(in);
		dim.load(in);
	}
}

class ExploreBR extends BuildingRes {
	public final static int STATE_COUNT = 3;
	public final static int NORMAL = 0;
	public final static int HALF_DEAD = 1;
	public final static int DEAD = 2;
	public final static String[] DESCS = {
										 "普通状态", 
										 "半毁状态", 
										 "全毁状态"
	};
	
	private SingleImage[] images;
	private int hp;
	private Explore[] explores;
	private int interval;
	
	public ExploreBR(int id) {
		super(id, EXPLORE);
		init();
	}
	
	public ExploreBR(int id, int type) {
		super(id, type);
		init();
	}
	
	private void init() {
		images = new SingleImage[STATE_COUNT];
		for(int i = 0; i < images.length; ++i) {
			images[i] = null;
		}
		hp = 100;
		explores = null;
		interval = 500;
	}
	
	public void copyFrom(BuildingRes br) {
		super.copyFrom(br);
		if(br instanceof ExploreBR) {
			ExploreBR source = (ExploreBR)br;
			this.images = source.images;
			this.hp = source.hp;
			this.explores = null;
			if(source.explores != null) {
				this.explores = new Explore[source.explores.length];
				for(int i = 0; i < source.explores.length; ++i) {
					this.explores[i] = source.explores[i].getCopy();
				}
			}
			this.interval = source.interval;
		}
	}
	
	public SingleImage[] getImages() {
		return images;
	}
	
	public SingleImage getImage(int state) {
		SingleImage result = null;
		if(images != null) {
			if(state >= 0 && state < images.length) {
				result = images[state];
			}
		}
		return result;
	}
	
	public void setImages(SingleImage[] images) {
		this.images = images;
	}
	
	public int getHP() {
		return hp;
	}
	
	public void setHP(int hp) {
		this.hp = hp;
	}
	
	public ArrayList getExplores() {
		ArrayList result = new ArrayList();
			if (this.explores != null) {
				for (int i = 0; i < this.explores.length; ++i) {
					if (this.explores[i] != null) {
						result.add(this.explores[i].getCopy());
					}
				}
			}
		return result;
	}

	public void setExplores(ArrayList explores) {
		this.explores = null;
		if (explores != null) {
			if (explores.size() > 0) {
				this.explores = new Explore[explores.size()];
				for (int i = 0; i < explores.size(); ++i) {
					this.explores[i] = ( (Explore) (explores.get(i))).getCopy();
				}
			}
		}
	}
	
	public int getInterval() {
		return interval;
	}
	
	public void setInterval(int interval) {
		this.interval = interval;
	}
	
	public int getWidth() {
		int result = 0;
		SingleImage image = getImage(NORMAL);
		if(image != null) {
			result = image.getWidth();
		}
		return result;
	}
	
	public int getHeight() {
		int result = 0;
		SingleImage image = getImage(NORMAL);
		if(image != null) {
			result = image.getHeight();
		}
		return result;
	}
	
	public void paintLeftTop(Graphics g, int left, int top, boolean flip) {
		paintLeftTop(NORMAL, g, left, top, flip);
	}
	
	public void paintLeftTop(int state, Graphics g, int left, int top, boolean flip) {
		SingleImage image = getImage(state);
		if(image != null) {
			image.paintLeftTop(g, left, top, flip);
		}
	}
	
	public void paintOrigin(Graphics g, int originX, int originY, boolean flip) {
		paintOrigin(NORMAL, g, originX, originY, flip);
	}
	
	public void paintOrigin(int state, Graphics g, int originX, int originY, boolean flip) {
		SingleImage image = getImage(state);
		if(image != null) {
			image.paintLeftTop(g, 
							   getLeft(originX), 
							   getTop(originY),
							   flip);
		}
	}
	
	public void saveMobileData(DataOutputStream out) throws Exception {
		super.saveMobileData(out);
		SL.writeInt(images[NORMAL].getID(), out);
		SingleImage image = getImage(HALF_DEAD);
		if(image == null) {
			SL.writeInt(-1, out);
		}
		else {
			SL.writeInt(image.getID(), out);
		}
		SL.writeInt(images[DEAD].getID(), out);
		
		SL.writeInt(hp, out);
		
		if(explores == null) {
			SL.writeInt(0, out);
		}
		else {
			SL.writeInt(explores.length, out);
			for(int i = 0; i < explores.length; ++i) {
				explores[i].saveMobileData(out);
			}
		}
		
		SL.writeInt(interval, out);
	}

	public void save(DataOutputStream out) throws Exception {
		super.save(out);
		
		if(images == null) {
			out.writeInt(0);
		}
		else {
			out.writeInt(images.length);
			for(int i = 0; i < images.length; ++i) {
				if(images[i] == null) {
					out.writeInt(-1);
				}
				else {
				    out.writeInt(images[i].getID());
				}
			}
		}
		
		out.writeInt(hp);
		
		if(explores == null) {
			out.writeInt(0);
		}
		else {
			out.writeInt(explores.length);
			for(int i = 0; i < explores.length; ++i) {
				explores[i].save(out);
			}
		}
		
		out.writeInt(interval);
	}

	protected void load(DataInputStream in, Object[] resManagers) throws Exception {
		super.load(in, resManagers);
		
		SIManager siManager = (SIManager)(resManagers[MainFrame.RES_SI]);
		int imageLength = in.readInt();
		if(imageLength <= 0) {
			images = null;
		}
		else {
			images = new SingleImage[imageLength];
			for(int i = 0; i < imageLength; ++i) {
				int imageID = in.readInt();
				images[i] = siManager.getSI(imageID);
			}
		}
		
		hp = in.readInt();
		
		ARManager arManager = (ARManager)(resManagers[MainFrame.RES_AR]);
		int exploreLength = in.readInt();
		if(exploreLength <= 0) {
			explores = null;
		}
		else {
			explores = new Explore[exploreLength];
			for(int i = 0; i < exploreLength; ++i) {
				explores[i] = Explore.createViaFile(in, arManager);
			}
		}
		
		interval = in.readInt();
	}
}

class HouseBR extends ExploreBR {
	
	public HouseBR(int id) {
		super(id, HOUSE);
		init();
	}
	
	public HouseBR(int id, int type) {
		super(id, type);
		init();
	} 

	private void init() {
	}

	public void copyFrom(BuildingRes br) {
		super.copyFrom(br);
		if(br instanceof HouseBR) {
		}
	}
}

class BRManager {
	private ArrayList brs;

	public BRManager() {
		brs = new ArrayList();
	}

	public BuildingRes[] getBRs() {
		BuildingRes[] result = null;
		if (brs.size() > 0) {
			result = new BuildingRes[brs.size()];
			for (int i = 0; i < brs.size(); ++i) {
				result[i] = (BuildingRes) (brs.get(i));
			}
		}
		return result;
	}

	public ArrayList getBRArrayList() {
		ArrayList result = new ArrayList(brs);
		return result;
	}

	public void setBRs(ArrayList brs) {
		this.brs = new ArrayList(brs);
	}

	public void setBRs(BuildingRes[] brs) {
		this.brs = new ArrayList();
		if (brs != null) {
			for (int i = 0; i < brs.length; ++i) {
				this.brs.add(brs[i]);
			}
		}
	}

	public BuildingRes getBR(int id) {
		for (int i = 0; i < brs.size(); ++i) {
			BuildingRes br = (BuildingRes) (brs.get(i));
			if (br.getID() == id) {
				return br;
			}
		}
		return null;
	}

	public boolean addBR(BuildingRes br) {
		for (int i = 0; i < brs.size(); ++i) {
			BuildingRes tmp = (BuildingRes) (brs.get(i));
			if (tmp.getID() == br.getID()) {
				return false;
			}
		}
		brs.add(br);
		return true;
	}

	public boolean removeBR(BuildingRes br) {
		if (br == null) {
			return false;
		}
		return removeBR(br.getID());
	}

	public boolean removeBR(int id) {
		for (int i = 0; i < brs.size(); ++i) {
			BuildingRes br = (BuildingRes) (brs.get(i));
			if (br.getID() == id) {
				brs.remove(br);
				return true;
			}
		}
		return false;
	}

	public void saveMobileData(String name) throws Exception {
		File f = new File(XUtil.getDefPropStr("MobilePath") + "\\buildingres_mobile.dat");
		DataOutputStream out =
			new DataOutputStream(
			new BufferedOutputStream(
			new FileOutputStream(f)));

		SL.writeInt(brs.size(), out);
		for (int i = 0; i < brs.size(); ++i) {
			BuildingRes br = (BuildingRes) (brs.get(i));
			br.saveMobileData(out);
		}
		out.flush();
		out.close();
	}

	public void save(String name) throws Exception {
		File f = new File(XUtil.getDefPropStr("BRFilePath") + "\\BuildingRes.dat");
		DataOutputStream out =
			new DataOutputStream(
			new BufferedOutputStream(
			new FileOutputStream(f)));

		out.writeInt(brs.size());
		for (int i = 0; i < brs.size(); ++i) {
			BuildingRes br = (BuildingRes) (brs.get(i));
			br.save(out);
		}
		out.flush();
		out.close();
	}

	public void load(String name, Object[] resManagers) throws Exception {
		brs.clear();
		File f = new File(XUtil.getDefPropStr("BRFilePath") + "\\BuildingRes.dat");
		if (f.exists()) {
			DataInputStream in =
				new DataInputStream(
				new BufferedInputStream(
				new FileInputStream(f)));
			int length = in.readInt();
			for (int i = 0; i < length; ++i) {
				brs.add(BuildingRes.createViaFile(in, resManagers));
			}
			in.close();
		}
	}
}
