#ifndef __shotlogic_h__
#define __shotlogic_h__

#include "cocos2d.h"
#include "mathconst.h"

class Unit;

class ShotLogic
{
public:
	
public:
	ShotLogic(Unit* parent, cocos2d::Node* shot, int weaponResID, int posIndex);
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
	cocos2d::Node* _shot;
};
//////////////////////////////////////////////////////////////////////////
//Բ����
// //////////////////////////////////////////////////////////////////////////

static const float LOGICASPEED = 3.0f;

class ShotLogicA : public ShotLogic
{
public:
	ShotLogicA(Unit* parent, cocos2d::Node* shot, int weaponResID, int posIndex);

	void perform(float dt);
protected:
	float _delay;
	int _shotDirection;
	float _fireAngle; 
};

//////////////////////////////////////////////////////////////////////////
//ֱ��׷����
//////////////////////////////////////////////////////////////////////////

class ShotLogicB : public ShotLogic
{
public:
	ShotLogicB(Unit* parent, cocos2d::Node* shot, int weaponResID, int posIndex, int shotCount);

	~ShotLogicB();

	void perform(float dt);
private:
	short _delay;
	int _fireCount;
	int _shotCount;
	Unit* _goalUnit;
};

//////////////////////////////////////////////////////////////////////////
//������ָ������
//////////////////////////////////////////////////////////////////////////

class ShotLogicC : public ShotLogic
{
public:
	ShotLogicC(Unit* parent, cocos2d::Node* shot, int weaponResID, int posIndex);

	void perform(float dt);
};

//////////////////////////////////////////////////////////////////////////
//��������
//////////////////////////////////////////////////////////////////////////

class ShotLogicD : public ShotLogic
{
public:
	static const int LOGIC_D_MAXFIRECOUNT = 1;
public:
	
	ShotLogicD(Unit* parent, cocos2d::Node* shot, int weaponResID, int posIndex);

	void perform(float dt);
private:
	int _fireCount;
	float _delay;
};


//////////////////////////////////////////////////////////////////////////
//˫�浯
//////////////////////////////////////////////////////////////////////////

class ShotLogicE : public ShotLogic
{
public:
	ShotLogicE(Unit* parent, cocos2d::Node* shot, int weaponResID, int posIndex, bool dir);
	~ShotLogicE();

	void perform(float dt);
private:
	bool _dir;
	Unit* _goalUnit;
};


//////////////////////////////////////////////////////////////////////////
//���浯����浥����׼player��
//////////////////////////////////////////////////////////////////////////

class ShotLogicF : public ShotLogic
{
public:
	ShotLogicF(Unit* unit, cocos2d::Node* shot, int weaponResID, int posIndex, int shotCount, bool dir);
	~ShotLogicF();

	void perform(float dt);
private:
	bool _dir;
	int _shotCount;
	Unit* _goalUnit;
};

//////////////////////////////////////////////////////////////////////////
//˫��ָ������
//////////////////////////////////////////////////////////////////////////
class ShotLogicG : public ShotLogic
{
public:
	ShotLogicG(Unit* unit, cocos2d::Node* shot, int weaponResID, int posIndex);
	void perform(float dt);
private:
	int _count;
};

//////////////////////////////////////////////////////////////////////////
//�˷���ͬʱ����
//////////////////////////////////////////////////////////////////////////
class ShotLogicH : public ShotLogic
{
public:
	ShotLogicH(Unit* parent, cocos2d::Node* shot, int weaponResID, int posIndex);
	
	void perform(float dt);
};

//////////////////////////////////////////////////////////////////////////
//16����ͬʱ����
//////////////////////////////////////////////////////////////////////////
class ShotLogicI : public ShotLogic
{
public:
	ShotLogicI(Unit* parent, cocos2d::Node* shot, int weaponResID, int posIndex);

	void perform(float dt);
};
//////////////////////////////////////////////////////////////////////////
//������
//////////////////////////////////////////////////////////////////////////
class ShotLogicK : public ShotLogic
{
public:
	ShotLogicK(Unit* parent, cocos2d::Node* shot, int weaponResID, int posIndex);

	void perform(float dt);
};

//////////////////////////////////////////////////////////////////////////
//����̹����С��
//////////////////////////////////////////////////////////////////////////
class ShotLogicQ : public ShotLogic
{
public:
	ShotLogicQ(Unit* parent, cocos2d::Node* shot, int weaponResID, int posIndex, int shotCount);

	~ShotLogicQ();
	void perform(float dt);
private:
	short _delay;
	int _fireCount;
	int _shotCount;
	Unit* _goalUnit;
};
#endif // __shotlogic_h__
