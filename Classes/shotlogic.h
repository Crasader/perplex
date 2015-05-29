#ifndef __shotlogic_h__
#define __shotlogic_h__

#include "cocos2d.h"
#include "mathconst.h"

class Unit;

class ShotLogic
{
public:
	
public:
	ShotLogic(Unit* unit, int weaponResID, int posIndex);
	virtual ~ShotLogic();
	virtual void perform(float dt);;
	bool isEnd();
protected:
	bool _end;
	float _firetick;
	float _performTick;
	int _posIndex;
	int _weaponResID;
	float _speed;
	Unit* _unit;
};
//////////////////////////////////////////////////////////////////////////
//圆弧形
// //////////////////////////////////////////////////////////////////////////

static const float LOGICASPEED = 3.0f;

class ShotLogicA : public ShotLogic
{
public:
	ShotLogicA(Unit* unit, int weaponResID, int posIndex);

	void perform(float dt);
protected:
	short _delay;
	int _shotDirection;
	float _fireAngle; 
};

//////////////////////////////////////////////////////////////////////////
//直线追踪型
//////////////////////////////////////////////////////////////////////////

class ShotLogicB : public ShotLogic
{
public:
	ShotLogicB(Unit* unit, int weaponResID, int posIndex, int shotCount);

	~ShotLogicB();

	void perform(float dt);
private:
	short _delay;
	int _fireCount;
	int _shotCount;
	Unit* _goalUnit;
};

//////////////////////////////////////////////////////////////////////////
//单发（指定方向）
//////////////////////////////////////////////////////////////////////////

class ShotLogicC : public ShotLogic
{
public:
	ShotLogicC(Unit* unit, int weaponResID, int posIndex);

	void perform(float dt);
};

//////////////////////////////////////////////////////////////////////////
//方向向下
//////////////////////////////////////////////////////////////////////////

class ShotLogicD : public ShotLogic
{
public:
	static const int LOGIC_D_MAXFIRECOUNT = 1;
public:
	ShotLogicD(Unit* unit, int weaponResID, int posIndex);

	void perform(float dt);
private:
	int _fireCount;
	float _delay;
};


//////////////////////////////////////////////////////////////////////////
//双叉弹
//////////////////////////////////////////////////////////////////////////

class ShotLogicE : public ShotLogic
{
public:
	ShotLogicE(Unit* unit, int weaponResID, int posIndex, bool dir);
	~ShotLogicE();

	void perform(float dt);
private:
	bool _dir;
	Unit* _goalUnit;
};


//////////////////////////////////////////////////////////////////////////
//三叉弹，五叉单（对准player）
//////////////////////////////////////////////////////////////////////////

class ShotLogicF : public ShotLogic
{
public:
	ShotLogicF(Unit* unit, int weaponResID, int posIndex, int shotCount, bool dir);
	~ShotLogicF();

	void perform(float dt);
private:
	bool _dir;
	int _shotCount;
	Unit* _goalUnit;
};

//////////////////////////////////////////////////////////////////////////
//双发指定方向
//////////////////////////////////////////////////////////////////////////
class ShotLogicG : public ShotLogic
{
public:
	

private:

};

//////////////////////////////////////////////////////////////////////////
//八方向同时发射
//////////////////////////////////////////////////////////////////////////
class ShotLogicH : public ShotLogic
{
public:
	ShotLogicH(Unit* unit, int weaponResID, int posIndex);
	
	void perform(float dt);
};

//////////////////////////////////////////////////////////////////////////
//16方向同时发射
//////////////////////////////////////////////////////////////////////////
class ShotLogicI : public ShotLogic
{
public:
	ShotLogicI(Unit* unit, int weaponResID, int posIndex);

	void perform(float dt);
};
//////////////////////////////////////////////////////////////////////////
//轰天炮
//////////////////////////////////////////////////////////////////////////
class ShotLogicK : public ShotLogic
{
public:
	ShotLogicK(Unit* unit, int weaponResID, int posIndex);

	void perform(float dt);
};

//////////////////////////////////////////////////////////////////////////
//重型坦克右小炮
//////////////////////////////////////////////////////////////////////////
class ShotLogicQ : public ShotLogic
{
public:
	ShotLogicQ(Unit* unit, int weaponResID, int posIndex, int shotCount);

	~ShotLogicQ();
	void perform(float dt);
private:
	short _delay;
	int _fireCount;
	int _shotCount;
	Unit* _goalUnit;
};
#endif // __shotlogic_h__
