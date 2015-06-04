package editor;

import java.io.*;
import java.util.*;

class WeaponPos { //单个武器挂接点的位置，分16方向
	public final static int COVER_Y = 0;
	public final static int COVER_N = 1;
	public final static int[] COVERS = {COVER_Y, COVER_N};
	public final static String[] COVER_DESCRIPTIONS = {"遮挡Unit", "被Unit遮挡"};

	private int id;
	private String name;
	private int weapon;
	private IntPair[] pos;
	private int[] covers;

	public WeaponPos() {
		init(0, null);
	}

	public WeaponPos(int id) {
		init(id, null);
	}

	public WeaponPos(int id, String name) {
		init(id, name);
	}

	private void init(int id, String name) {
		this.id = id;
		if (name == null) {
			this.name = "位置" + id;
		}
		else {
			this.name = name;
		}
		weapon = Weapon.MACHINE_GUN;

		covers = new int[Dir.LENGTH];
		pos = new IntPair[Dir.LENGTH];
		for (int i = 0; i < Dir.FULL_STAND_DIRS.length; ++i) {
			pos[i] = new IntPair();
		}
	}

	public WeaponPos getCopy() {
		WeaponPos result = new WeaponPos();
		result.copyFrom(this);
		return result;
	}

	public void copyFrom(WeaponPos source) {
		this.id = source.id;
		this.name = source.name;
		this.weapon = source.weapon;
		for (int i = 0; i < Dir.FULL_STAND_DIRS.length; ++i) {
			this.covers[i] = source.covers[i];
			this.pos[i].x = source.pos[i].x;
			this.pos[i].y = source.pos[i].y;
		}
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getWeapon() {
		return weapon;
	}
	
	public void setWeapon(int weapon) {
		this.weapon = weapon;;
	}

	public int getCover(int dir) {
		int result = COVER_Y;
		if (dir >= 0 && dir < pos.length) {
			result = covers[dir];
		}
		return result;
	}

	public void setCover(int dir, int cover) {
		if (dir >= 0 && dir < this.pos.length) {
			this.covers[dir] = cover;
		}
	}

	public IntPair getPos(int dir) {
		IntPair result = new IntPair();
		if (dir >= 0 && dir < pos.length) {
			result.x = pos[dir].x;
			result.y = pos[dir].y;
		}
		return result;
	}

	public void setPos(int dir, IntPair pos) {
		if (dir >= 0 && dir < this.pos.length) {
			this.pos[dir].x = pos.x;
			this.pos[dir].y = pos.y;
		}
	}

	public void saveMobileData(DataOutputStream out) throws Exception {
		SL.writeInt(id, out);
		SL.writeInt(weapon, out);
		for (int i = 0; i < Dir.FULL_STAND_DIRS.length; ++i) {
			SL.writeInt(covers[Dir.FULL_STAND_DIRS[i]], out);
			SL.writeIntPairMobile(pos[Dir.FULL_STAND_DIRS[i]], out);
		}
	}

	public void save(DataOutputStream out) throws Exception {
		out.writeInt(id);
		SL.writeString(name, out);
		out.writeInt(weapon);
		for (int i = 0; i < Dir.FULL_STAND_DIRS.length; ++i) {
			out.writeInt(covers[Dir.FULL_STAND_DIRS[i]]);
			SL.writeIntPair(pos[Dir.FULL_STAND_DIRS[i]], out);
		}
	}

	public final static WeaponPos createViaFile(DataInputStream in) throws Exception {
		WeaponPos result = new WeaponPos();
		result.load(in);
		return result;
	}

	private void load(DataInputStream in) throws Exception {
		id = in.readInt();
		name = SL.readString(in);
		weapon = in.readInt();
		for (int i = 0; i < Dir.FULL_STAND_DIRS.length; ++i) {
			covers[Dir.FULL_STAND_DIRS[i]] = in.readInt();
			pos[Dir.FULL_STAND_DIRS[i]] = SL.readIntPair(in);
		}
	}
}

public class UnitRes {
	private int id;
	private String name;
	private int hp;
	private UnitMoveMode um;
	private UnitFireMode uf;
	private Animation anim; //该单位全部的动画、图片资源
	private IntPair[] moveAnimRanges; //移动的动画，分8方向
	private DoublePair[] moveSpeeds; //移动的速度，分8方向
	private IntPair[] moveGrids; //占用的格子，各方向相同
	private IntPair[] standAnimRanges; //站立的动画，分16方向
	private IntPair[] bodyCenters; //人物的中心点
	private Rect[][] bodyRects; //被攻击范围，分16方向，[方向][每方向的矩形个数]
	private IntPair[] deadAnimRanges; //从死亡开始直到结束的全部动画范围，分16方向
	private Explore[][] deadExplores; //死亡的爆炸效果，分16方向
	private WeaponPos[] weaponPos; //武器挂接点的位置，分16方向
	
	private DropItemMode dim;


	public UnitRes() {
		init(0);
	}

	public UnitRes(int id) {
		init(id);
	}

	private void init(int id) {
		this.id = id;
		this.name = "";
		this.hp = 10;
		this.um = new UnitMoveMode();
		this.uf = new UnitFireMode();
		this.anim = null;

		moveAnimRanges = new IntPair[Dir.LENGTH];
		for (int i = 0; i < Dir.LENGTH; ++i) {
			moveAnimRanges[i] = new IntPair();
		}

		moveSpeeds = new DoublePair[Dir.LENGTH];
		for (int i = 0; i < Dir.LENGTH; ++i) {
			moveSpeeds[i] = new DoublePair();
		}

		moveGrids = null;

		standAnimRanges = new IntPair[Dir.LENGTH];
		for (int i = 0; i < Dir.LENGTH; ++i) {
			standAnimRanges[i] = new IntPair();
		}

		bodyCenters = new IntPair[Dir.LENGTH];
		for (int i = 0; i < Dir.LENGTH; ++i) {
			bodyCenters[i] = new IntPair();
		}

		bodyRects = new Rect[Dir.LENGTH][];
		for (int i = 0; i < bodyRects.length; ++i) {
			bodyRects[i] = null;
		}

		deadAnimRanges = new IntPair[Dir.LENGTH];
		for (int i = 0; i < Dir.LENGTH; ++i) {
			deadAnimRanges[i] = new IntPair();
		}

		deadExplores = new Explore[Dir.LENGTH][];
		for (int i = 0; i < Dir.LENGTH; ++i) {
			deadExplores[i] = null;
		}

		weaponPos = null;
		
		dim = new DropItemMode();
	}

	public UnitRes getCopy() {
		UnitRes result = new UnitRes();
		result.copyFrom(this);
		return result;
	}

	public void copyFrom(UnitRes source) {
		this.id = source.id;
		this.name = source.name;
		this.hp = source.hp;
		this.um = source.um.getCopy();
		this.uf = source.uf.getCopy();
		
		this.anim = source.anim;
		um.setAnimaCount(anim != null ? anim.getAnimaCount() : 0);
		for (int i = 0; i < Dir.LENGTH; ++i) {
			this.moveAnimRanges[i].x = source.moveAnimRanges[i].x;
			this.moveAnimRanges[i].y = source.moveAnimRanges[i].y;
		}

		for (int i = 0; i < Dir.LENGTH; ++i) {
			this.moveSpeeds[i].x = source.moveSpeeds[i].x;
			this.moveSpeeds[i].y = source.moveSpeeds[i].y;
		}

		this.moveGrids = XUtil.copyArray(source.moveGrids);

		for (int i = 0; i < Dir.LENGTH; ++i) {
			this.standAnimRanges[i].x = source.standAnimRanges[i].x;
			this.standAnimRanges[i].y = source.standAnimRanges[i].y;
		}

		for (int i = 0; i < Dir.LENGTH; ++i) {
			this.bodyCenters[i].x = source.bodyCenters[i].x;
			this.bodyCenters[i].y = source.bodyCenters[i].y;
		}

		for (int i = 0; i < Dir.LENGTH; ++i) {
			this.bodyRects[i] = XUtil.copyArray(source.bodyRects[i]);
		}

		for (int i = 0; i < Dir.LENGTH; ++i) {
			this.deadAnimRanges[i].x = source.deadAnimRanges[i].x;
			this.deadAnimRanges[i].y = source.deadAnimRanges[i].y;
		}

		for (int i = 0; i < Dir.LENGTH; ++i) {
			if (source.deadExplores[i] != null) {
				this.deadExplores[i] = new Explore[source.deadExplores[i].length];
				for (int j = 0; j < source.deadExplores[i].length; ++j) {
					this.deadExplores[i][j] = source.deadExplores[i][j].getCopy();
				}
			}
		}

		if (source.weaponPos != null) {
			this.weaponPos = new WeaponPos[source.weaponPos.length];
			for (int i = 0; i < source.weaponPos.length; ++i) {
				this.weaponPos[i] = source.weaponPos[i].getCopy();
			}
		}
		
		this.dim = source.dim.getCopy();
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getHP() {
		return hp;
	}
	
	public void setHP(int hp) {
		this.hp = hp;
	}
	
	public UnitMoveMode getUM() {
		um.getCopy();
		um.setAnimaCount(anim != null ? anim.getAnimaCount() : 0);
		return um;
	}
	
	public void SetUM(UnitMoveMode um) {
		if(um == null) {
			this.um = new UnitMoveMode();
		}
		else {
			this.um = um.getCopy();
		}
	}
	
	public UnitFireMode getUF() {
		return uf.getCopy();
	}
	
	public void SetUF(UnitFireMode uf) {
		if(uf == null) {
			this.uf = new UnitFireMode();
		}
		else {
			this.uf = uf.getCopy();
		}
	}

	public Animation getAnim() {
		return anim;
	}

	public void setAnim(Animation anim) {
		if (name.trim().equals("") && anim != null) {
			name = anim.getName();
		}
		this.anim = anim;
	}

	public IntPair getMoveAnimRange(int dir) {
		IntPair result = new IntPair();
		if (dir >= 0 && dir < moveAnimRanges.length) {
			result.x = moveAnimRanges[dir].x;
			result.y = moveAnimRanges[dir].y;
		}
		return result;
	}

	public void setMoveAnimRange(int dir, IntPair moveAnimRange) {
		if (dir >= 0 && dir < moveAnimRanges.length) {
			moveAnimRanges[dir].x = moveAnimRange.x;
			moveAnimRanges[dir].y = moveAnimRange.y;
		}
	}

	public DoublePair getMoveSpeed(int dir) {
		DoublePair result = new DoublePair();
		if (dir >= 0 && dir < moveSpeeds.length) {
			result.x = moveSpeeds[dir].x;
			result.y = moveSpeeds[dir].y;
		}
		return result;
	}

	public void setMoveSpeed(int dir, DoublePair moveSpeed) {
		if (dir >= 0 && dir < moveSpeeds.length) {
			moveSpeeds[dir].x = moveSpeed.x;
			moveSpeeds[dir].y = moveSpeed.y;
		}
	}

	public ArrayList getMoveGrids() {
		ArrayList result = new ArrayList();
		if (moveGrids != null) {
			for (int i = 0; i < moveGrids.length; ++i) {
				IntPair p = moveGrids[i];
				if (p != null) {
					result.add(p.getCopy());
				}
			}
		}
		return result;
	}

	public void setMoveGrids(ArrayList moveGrids) {
		this.moveGrids = null;
		if (moveGrids != null) {
			if (moveGrids.size() > 0) {
				this.moveGrids = new IntPair[moveGrids.size()];
				for (int i = 0; i < moveGrids.size(); ++i) {
					if (moveGrids.get(i) != null) {
						this.moveGrids[i] = ( (IntPair) (moveGrids.get(i))).getCopy();
					}
				}
			}
		}
	}

	public IntPair getStandAnimRange(int dir) {
		IntPair result = new IntPair();
		if (dir >= 0 && dir < standAnimRanges.length) {
			result.x = standAnimRanges[dir].x;
			result.y = standAnimRanges[dir].y;
		}
		return result;
	}

	public void setStandAnimRange(int dir, IntPair standAnimRange) {
		if (dir >= 0 && dir < standAnimRanges.length) {
			standAnimRanges[dir].x = standAnimRange.x;
			standAnimRanges[dir].y = standAnimRange.y;
		}
	}

	public IntPair getBodyCenter(int dir) {
		IntPair result = new IntPair();
		if (dir >= 0 && dir < bodyCenters.length) {
			result.x = bodyCenters[dir].x;
			result.y = bodyCenters[dir].y;
		}
		return result;
	}

	public void setBodyCenter(int dir, IntPair center) {
		if (dir >= 0 && dir < bodyCenters.length) {
			bodyCenters[dir].x = center.x;
			bodyCenters[dir].y = center.y;
		}
	}

	public ArrayList getBodyRect(int dir) {
		ArrayList result = new ArrayList();
		if (dir >= 0 && dir < bodyRects.length) {
			if (bodyRects[dir] != null) {
				for (int i = 0; i < bodyRects[dir].length; ++i) {
					Rect r = bodyRects[dir][i];
					if (r != null) {
						result.add(r.getCopy());
					}
				}
			}
		}
		return result;
	}

	public void setBodyRect(int dir, ArrayList bodyRect) {
		if (dir >= 0 && dir < this.bodyRects.length) {
			this.bodyRects[dir] = null;
			if (bodyRect != null) {
				if (bodyRect.size() > 0) {
					this.bodyRects[dir] = new Rect[bodyRect.size()];
					for (int i = 0; i < bodyRect.size(); ++i) {
						if (bodyRect.get(i) != null) {
							Rect r = (Rect) (bodyRect.get(i));
							this.bodyRects[dir][i] = r.getCopy();
						}
					}
				}
			}
		}
	}

	public IntPair getDeadAnimRange(int dir) {
		IntPair result = new IntPair();
		if (dir >= 0 && dir < deadAnimRanges.length) {
			result.x = deadAnimRanges[dir].x;
			result.y = deadAnimRanges[dir].y;
		}
		return result;
	}

	public void setDeadAnimRange(int dir, IntPair deadAnimRange) {
		if (dir >= 0 && dir < deadAnimRanges.length) {
			deadAnimRanges[dir].x = deadAnimRange.x;
			deadAnimRanges[dir].y = deadAnimRange.y;
		}
	}

	public ArrayList getDeadExplores(int dir) {
		ArrayList result = new ArrayList();
		if (dir >= 0 && dir < deadExplores.length) {
			if (deadExplores[dir] != null) {
				for (int i = 0; i < deadExplores[dir].length; ++i) {
					if (deadExplores[dir][i] != null) {
						result.add(deadExplores[dir][i].getCopy());
					}
				}
			}
		}
		return result;
	}

	public void setDeadExplores(int dir, ArrayList deadExplore) {
		if (dir >= 0 && dir < deadExplores.length) {
			deadExplores[dir] = null;
			if (deadExplore != null) {
				if (deadExplore.size() > 0) {
					deadExplores[dir] = new Explore[deadExplore.size()];
					for (int i = 0; i < deadExplore.size(); ++i) {
						this.deadExplores[dir][i] = ( (Explore) (deadExplore.get(i))).getCopy();
					}
				}
			}
		}
	}

	public ArrayList getWeaponPos() {
		ArrayList result = new ArrayList();
		if (weaponPos != null) {
			for (int i = 0; i < weaponPos.length; ++i) {
				result.add(weaponPos[i].getCopy());
			}
		}
		return result;
	}

	public void setWeaponPos(ArrayList weaponPos) {
		this.weaponPos = null;
		if (weaponPos != null) {
			if (weaponPos.size() > 0) {
				this.weaponPos = new WeaponPos[weaponPos.size()];
				for (int i = 0; i < weaponPos.size(); ++i) {
					this.weaponPos[i] = ( (WeaponPos) (weaponPos.get(i))).getCopy();
				}
			}
		}
	}
	
	public DropItemMode getDIM() {
		return dim.getCopy();
	}
	
	public void setDIM(DropItemMode dim) {
		this.dim = dim.getCopy();
	}

	public void saveMobileData(DataOutputStream out) throws Exception {
		SL.writeInt(id, out);
		SL.writeInt(hp, out);
		
		if (anim == null) {
			SL.writeInt( -1, out);
		}
		else {
			SL.writeInt(anim.getID(), out);
		}

		for (int i = 0; i < Dir.MOVE_DIRS.length; ++i) {
			SL.writeIntPairMobile(moveAnimRanges[Dir.MOVE_DIRS[i]], out);
		}

		for (int i = 0; i < Dir.FULL_MOVE_DIRS.length; ++i) {
			SL.writeDoublePairMobile(moveSpeeds[Dir.FULL_MOVE_DIRS[i]], out);
		}

		SL.writeIntPairArrayMobile(moveGrids, out);

		for (int i = 0; i < Dir.STAND_DIRS.length; ++i) {
			SL.writeIntPairMobile(standAnimRanges[Dir.STAND_DIRS[i]], out);
		}

		for (int i = 0; i < Dir.FULL_STAND_DIRS.length; ++i) {
			SL.writeIntPairMobile(bodyCenters[Dir.FULL_STAND_DIRS[i]], out);
		}

		for (int i = 0; i < Dir.FULL_STAND_DIRS.length; ++i) {
			SL.writeRectArrayMobile(bodyRects[Dir.FULL_STAND_DIRS[i]], out);
		}

		for (int i = 0; i < Dir.STAND_DIRS.length; ++i) {
			SL.writeIntPairMobile(deadAnimRanges[Dir.STAND_DIRS[i]], out);
		}

		for (int i = 0; i < Dir.FULL_STAND_DIRS.length; ++i) {
			if (deadExplores[Dir.FULL_STAND_DIRS[i]] == null) {
				SL.writeInt(0, out);
			}
			else {
				SL.writeInt(deadExplores[Dir.FULL_STAND_DIRS[i]].length, out);
				for (int j = 0; j < deadExplores[Dir.FULL_STAND_DIRS[i]].length; ++j) {
					deadExplores[Dir.FULL_STAND_DIRS[i]][j].saveMobileData(out);
				}
			}
		}

		if (weaponPos == null) {
			SL.writeInt(0, out);
		}
		else {
			SL.writeInt(weaponPos.length, out);
			for (int i = 0; i < weaponPos.length; ++i) {
				WeaponPos p = weaponPos[i];
				p.saveMobileData(out);
			}
		}
		SL.writeShort(anim != null ? anim.getAnimaCount() : 1, out);
	}

	public void save(DataOutputStream out) throws Exception {
		out.writeInt(id);
		SL.writeString(name, out);
		out.writeInt(hp);
		um.save(out);
		uf.save(out);
		dim.save(out);
		
		if (anim == null) {
			out.writeInt( -1);
		}
		else {
			out.writeInt(anim.getID());
		}

		for (int i = 0; i < Dir.MOVE_DIRS.length; ++i) {
			SL.writeIntPair(moveAnimRanges[Dir.MOVE_DIRS[i]], out);
		}

		for (int i = 0; i < Dir.FULL_MOVE_DIRS.length; ++i) {
			SL.writeDoublePair(moveSpeeds[Dir.FULL_MOVE_DIRS[i]], out);
		}

		SL.writeIntPairArray(moveGrids, out);

		for (int i = 0; i < Dir.STAND_DIRS.length; ++i) {
			SL.writeIntPair(standAnimRanges[Dir.STAND_DIRS[i]], out);
		}

		for (int i = 0; i < Dir.FULL_STAND_DIRS.length; ++i) {
			SL.writeIntPair(bodyCenters[Dir.FULL_STAND_DIRS[i]], out);
		}

		for (int i = 0; i < Dir.FULL_STAND_DIRS.length; ++i) {
			SL.writeRectArray(bodyRects[Dir.FULL_STAND_DIRS[i]], out);
		}

		for (int i = 0; i < Dir.STAND_DIRS.length; ++i) {
			SL.writeIntPair(deadAnimRanges[Dir.STAND_DIRS[i]], out);
		}

		for (int i = 0; i < Dir.FULL_STAND_DIRS.length; ++i) {
			if (deadExplores[Dir.FULL_STAND_DIRS[i]] == null) {
				out.writeInt(0);
			}
			else {
				out.writeInt(deadExplores[Dir.FULL_STAND_DIRS[i]].length);
				for (int j = 0; j < deadExplores[Dir.FULL_STAND_DIRS[i]].length; ++j) {
					deadExplores[Dir.FULL_STAND_DIRS[i]][j].save(out);
				}
			}
		}

		if (weaponPos == null) {
			out.writeInt(0);
		}
		else {
			out.writeInt(weaponPos.length);
			for (int i = 0; i < weaponPos.length; ++i) {
				WeaponPos p = weaponPos[i];
				p.save(out);
			}
		}
	}

	public final static UnitRes createViaFile(DataInputStream in, ARManager arManager) throws Exception {
		UnitRes result = new UnitRes();
		result.load(in, arManager);
		return result;
	}

	private void load(DataInputStream in, ARManager arManager) throws Exception {
		id = in.readInt();
		name = SL.readString(in);
		hp = in.readInt();
		um = UnitMoveMode.createViaFile(in);
		uf = UnitFireMode.createViaFile(in);
		dim.load(in);

		
		int animID = in.readInt();
		anim = arManager.getAnim(animID);

		for (int i = 0; i < Dir.MOVE_DIRS.length; ++i) {
			moveAnimRanges[Dir.MOVE_DIRS[i]] = SL.readIntPair(in);
		}

		for (int i = 0; i < Dir.FULL_MOVE_DIRS.length; ++i) {
			moveSpeeds[Dir.FULL_MOVE_DIRS[i]] = SL.readDoublePair(in);
		}

		moveGrids = SL.readIntPairArray(in);

		for (int i = 0; i < Dir.STAND_DIRS.length; ++i) {
			standAnimRanges[Dir.STAND_DIRS[i]] = SL.readIntPair(in);
		}

		for (int i = 0; i < Dir.FULL_STAND_DIRS.length; ++i) {
			bodyCenters[Dir.FULL_STAND_DIRS[i]] = SL.readIntPair(in);
		}

		for (int i = 0; i < Dir.FULL_STAND_DIRS.length; ++i) {
			bodyRects[Dir.FULL_STAND_DIRS[i]] = SL.readRectArray(in);
		}

		for (int i = 0; i < Dir.STAND_DIRS.length; ++i) {
			deadAnimRanges[Dir.STAND_DIRS[i]] = SL.readIntPair(in);
		}

		for (int i = 0; i < Dir.FULL_STAND_DIRS.length; ++i) {
			int deadExploreLength = in.readInt();
			if (deadExploreLength > 0) {
				deadExplores[Dir.FULL_STAND_DIRS[i]] = new Explore[deadExploreLength];
				for (int j = 0; j < deadExploreLength; ++j) {
					deadExplores[Dir.FULL_STAND_DIRS[i]][j] = Explore.createViaFile(in, arManager);
				}
			}
		}

		int posLength = in.readInt();
		if (posLength > 0) {
			weaponPos = new WeaponPos[posLength];
			for (int i = 0; i < posLength; ++i) {
				weaponPos[i] = WeaponPos.createViaFile(in);
			}
		}
	}
}

class URManager {
	private ArrayList urs;

	public URManager() {
		urs = new ArrayList();
	}

	public UnitRes[] getURs() {
		UnitRes[] result = null;
		if (urs.size() > 0) {
			result = new UnitRes[urs.size()];
			for (int i = 0; i < urs.size(); ++i) {
				result[i] = (UnitRes) (urs.get(i));
			}
		}
		return result;
	}

	public ArrayList getURArrayList() {
		ArrayList result = new ArrayList(urs);
		return result;
	}

	public void setURs(ArrayList urs) {
		this.urs = new ArrayList(urs);
	}

	public void setURs(UnitRes[] urs) {
		this.urs = new ArrayList();
		if (urs != null) {
			for (int i = 0; i < urs.length; ++i) {
				this.urs.add(urs[i]);
			}
		}
	}

	public UnitRes getUR(int id) {
		for (int i = 0; i < urs.size(); ++i) {
			UnitRes ur = (UnitRes) (urs.get(i));
			if (ur.getID() == id) {
				return ur;
			}
		}
		return null;
	}

	public boolean addUR(UnitRes ur) {
		for (int i = 0; i < urs.size(); ++i) {
			UnitRes tmp = (UnitRes) (urs.get(i));
			if (tmp.getID() == ur.getID()) {
				return false;
			}
		}
		urs.add(ur);
		return true;
	}

	public boolean removeUR(UnitRes ur) {
		if (ur == null) {
			return false;
		}
		return removeUR(ur.getID());
	}

	public boolean removeUR(int id) {
		for (int i = 0; i < urs.size(); ++i) {
			UnitRes ur = (UnitRes) (urs.get(i));
			if (ur.getID() == id) {
				urs.remove(ur);
				return true;
			}
		}
		return false;
	}

	public void saveMobileData(String name) throws Exception {
		File f = new File(XUtil.getDefPropStr("MobilePath") + "\\unitres_mobile.dat");
		DataOutputStream out =
			new DataOutputStream(
			new BufferedOutputStream(
			new FileOutputStream(f)));

		SL.writeInt(urs.size(), out);
		for (int i = 0; i < urs.size(); ++i) {
			UnitRes ur = (UnitRes) (urs.get(i));
			ur.saveMobileData(out);
		}
		out.flush();
		out.close();
	}

	public void save(String name) throws Exception {
		File f = new File(XUtil.getDefPropStr("URFilePath") + "\\UnitRes.dat");
		DataOutputStream out =
			new DataOutputStream(
			new BufferedOutputStream(
			new FileOutputStream(f)));

		out.writeInt(urs.size());
		for (int i = 0; i < urs.size(); ++i) {
			UnitRes ur = (UnitRes) (urs.get(i));
			ur.save(out);
		}
		out.flush();
		out.close();
	}

	public void load(String name, ARManager arManager) throws Exception {
		urs.clear();
		File f = new File(XUtil.getDefPropStr("URFilePath") + "\\UnitRes.dat");
		if (f.exists()) {
			DataInputStream in =
				new DataInputStream(
				new BufferedInputStream(
				new FileInputStream(f)));
			int length = in.readInt();
			for (int i = 0; i < length; ++i) {
				urs.add(UnitRes.createViaFile(in, arManager));
			}
			in.close();
		}
	}
}