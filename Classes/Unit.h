/****************************************************************************
 Copyright (c) 2014 Chukong Technologies Inc.

 http://github.com/chukong/EarthWarrior3D

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 ****************************************************************************/

#ifndef __Moon3d__AirCraft__
#define __Moon3d__AirCraft__

#include "cocos2d.h"
#include "GameEntity.h"
#include "UnitRes.h"
#include "XDropTool.h"
#include "Weapon.h"

USING_NS_CC;

using namespace std;

class Unit : public GameEntity
{
public:
	Unit();
	Unit(bool alive, float hp, int score);
	Unit(int type, int shadowType, int radius, bool alive, float hp, int score);
	virtual ~Unit(){};
    virtual bool hurt(float damage);
    virtual void die();
    void shoot();
    //CC_SYNTHESIZE(float, _HP, HP);
    bool alive();
    virtual void move(float y, float dt);
    virtual void reset();
	int getUnitID() const { return _unitID; }
	void setUnitID(int aUnitID) { _unitID = aUnitID; }
	int getMoveType() const { return _moveType; }
	void setMoveType(int aMoveType) { _moveType = aMoveType; }
	int getMaxHP() const { return _maxHP; }
	void setMaxHP(int aMaxHP) { _maxHP = aMaxHP; }
	int getBodyLevel() const { return _BodyLevel; }
	void setBodyLevel(int aBodyLevel) { _BodyLevel = aBodyLevel; }
	int getMoveX() const { return _moveX; }
	void setMoveX(int aMoveX) { _moveX = aMoveX; }
	int getMoveY() const { return _moveY; }
	void setMoveY(int aMoveY) { _moveY = aMoveY; }
	int getMotion() const { return _motion; }
	void setMotion(int aMotion) { _motion = aMotion; }
	int getWalkDir() const { return _walkDir; }
	void setWalkDir(int aWalkDir) { _walkDir = aWalkDir; }
	int getAIType() const { return _AIType; }
	void setAIType(int aAIType) { _AIType = aAIType; }
	int getCampType() const { return _campType; }
	void setCampType(int aCampType) { _campType = aCampType; }
	vector<int> getMoveProb() const;
	void setMoveProb(int* aMoveProb, int len);
	vector<Vec2> getMoveItemData() const;
	void setMoveItemData(Vec2* aMoveItemData, int len);
	vector<Vec2> getPointItemData() const { return _pointItemData; }
	void setPointItemData(Vec2* aPointItemData, int len);
	vector<Vec2> getFireItemData() const { return _fireItemData; }
	void setFireItemData(Vec2* aFireItemData, int len);
	vector<Vec2> getDistItemData() const { return _distItemData; }
	void setDistItemData(Vec2* aDistItemData, int len);
	vector<XDropTool> getDropToolData() const { return _dropToolData; }
	void setDropToolData(XDropTool* aDropToolData, int len);
	vector<XUnitOrder> getUnitRecycleOrder() const { return _unitRecycleOrder; }
	void setUnitRecycleOrder(XUnitOrder* aUnitRecycleOrder, int len);
	Rect getWalkRect() const { return _walkRect; }
	void setWalkRect(Rect aWalkRect) { _walkRect = aWalkRect; }
	Rect getMoveRect() const { return _moveRect; }
	void setMoveRect(Rect aMoveRect) { _moveRect = aMoveRect; }
	UnitRes getUnitRes() const { return _unitRes; }
	void setUnitRes(UnitRes aUnitRes) { _unitRes = aUnitRes; }
protected:
    float		_HP;
    bool		_alive;
	bool		_active;//是否被激活
	bool		_eventUnit;//是否事件Unit;
    int			_score;
	int			_iDieExplodCount;//死亡爆炸动画个数
	int			_startExplodeTick;//爆炸开始时间
	int			_campType;
	int			_unitID;	//在事件系统的ID
	int			_moveType;	//运动方式
	int			_maxHP;		//血的最大值
	int			_BodyLevel;//身体的重量级数
	//移动速度
	int			_moveX;
	int			_moveY;
	//动作
	int			_motion;//当前的动作
	//死亡
	int			_beAttackTick;//被攻击延迟，用于蒙色
	int			_diaphaneity;//透明度
	int			_dieTick;
	bool		_die;//死亡闪烁状态
	//行动方向
	int        	 _walkDir;       //决定下半身方向，不可以上下转身的Unit由iWalkDir决定方向
	//AI
	int			_AIType;
	//fire
	bool		_unitFireRequire;
	int			_shotPosIndex;//发射点索引
	int			_shotLogicType;
	int			_weaponResID;
	int			_fireTick;//轻武器涉及延迟
	vector<bool>	 _dieExplode;//记录是否爆炸
	//vector<Weapon>	 _weaponList; //此Unit的武器表Weapon

	//8方向随机移动的概率
	vector<int>		_moveProb;
	//移动时间和概率
	vector<Vec2>	_moveItemData;
	//移动方式为按路径移动时的表示坐标
	vector<Vec2>	_pointItemData;
	//开火概率
	vector<Vec2>	_fireItemData;
	//根据距离，向目标移动的概率
	vector<Vec2>	_distItemData;
	//掉落道具
	vector<XDropTool> _dropToolData;
	//循环指令
	vector<XUnitOrder>	_unitRecycleOrder;
	//判断可行走范围
	bool		_endableMove;
	Rect		_walkRect;//行走碰撞判断矩形
	Rect		_moveRect;//有效行走矩形
	UnitRes		 _unitRes;
};

#endif /* defined(__Moon3d__AirCraft__) */
