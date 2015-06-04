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

#include "GameEntity.h"
#include "XDropTool.h"


USING_NS_CC;

using namespace std;

class UnitRes;
class XDropTool;
class Weapon;
class ShotLogic;
class ShotLogicManager;
class Player;
class XUnitOrder;
class GameScene;
class MotionImpl;
class AIBase;

enum UnitSDIR
{
	D_S_DOWN = 0,
	D_S_LEFT_DOWN = 1,
	D_S_LEFT = 2,
	D_S_LEFT_UP = 3,
	D_S_UP = 4,
	D_S_RIGHT_UP = 5,
	D_S_RIGHT = 6,
	D_S_RIGHT_DOWN = 7,
};

class Unit : public GameEntity
{
public:
	Unit();
	Unit(GameScene* gameScene, int unitID, int type, int walkdir, int camptype);
	Unit(bool alive, float hp, int score);
	Unit(int type, int shadowType, int radius, bool alive, float hp, int score);
	~Unit();
	bool init(GameScene* gameScene, int unitID, int type, int walkdir, int camptype);
	bool init();
	virtual bool hurt(float damage);
    virtual void die();
    void shoot();
    CC_SYNTHESIZE(float, _HP, HP);
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
	void setMoveProb(std::vector<int> aMoveProb);
	vector<Vec2> getMoveItemData() const;
	void setMoveItemData(vector<Vec2> aMoveItemData);
	vector<Vec2> getPointItemData() const { return _pointItemData; }
	void setPointItemData(vector<Vec2> aPointItemData);
	vector<Vec2> getFireItemData() const { return _fireItemData; }
	void setFireItemData(vector<Vec2> aFireItemData);
	vector<Vec2> getDistItemData() const { return _distItemData; }
	void setDistItemData(vector<Vec2> aDistItemData);
	vector<XDropTool> getDropToolData() const { return _dropToolData; }
	void setDropToolData(vector<XDropTool> aDropToolData);
	vector<XUnitOrder> getUnitRecycleOrder() const { return _unitRecycleOrder; }
	void setUnitRecycleOrder(vector<XUnitOrder> aUnitRecycleOrder);
	Rect getWalkRect() const { return _walkRect; }
	void setWalkRect(Rect aWalkRect) { _walkRect = aWalkRect; }
	Rect getMoveRect() const { return _moveRect; }
	void setMoveRect(Rect aMoveRect) { _moveRect = aMoveRect; }
	std::shared_ptr<UnitRes> getUnitRes() const { return _unitRes; }
	void setUnitRes(std::shared_ptr<UnitRes> aUnitRes);
	void perform(float dt);
	
	void castoffStage();

	void performWeapon(float dt);

	bool isNeedDelete();
	void AI(float dt);
	void analyzeOrder(float dt);
	void analyzeOrder(vector<XUnitOrder>& orders, float dt);
	void analyzeRecycleOrder(float dt);
	void setMotionAndDIR(int motion, int wdir);
	void fireRequire(int logicType, int weaponResID);
	void processDie(float dt);
	void processTool();
	void unitMove(float dt);
	bool enableMove(int newX, int newY);
	int getCastoffStage() { return _castoffStage; }
	void setEventUnit(bool eventUnit);
	int getPower();
	void setPower(int power) { _power = power; }
	void setHurt(int power);
	void setNewRoad(std::vector<cocos2d::Vec2> _roads);
	int beAttack(const cocos2d::Rect rect, int power);
	int getShotDir();
	std::vector<std::shared_ptr<MotionImpl>> getMotions() const { return _motions; }
	void setMotions(std::vector<std::shared_ptr<MotionImpl>> aMotions) { _motions = aMotions; }
	int calDirction(float angle);
	float calculeAngle();
	bool isActive(){ return _active; }
	bool isCastoff() { return _castoff; }
	void setUnitOrder(vector<XUnitOrder> aOrder);
	GameScene* getGameScene();
	Vector<Weapon*>& getWeaponList() { return _weapons; }
	float getShotX(int posIndex);
	float getShotY(int posIndex);
	Rect getHitRect() const;
	virtual Vec2 getPositionInCamera() { return getPosition(); };
// 	void setPosition(float x, float y) override;
// 	void setPosition(const Vec2& pos);
	void setPatrol(const Vec2& pos);
	float getSpeed();
	void setMotionData(std::vector<int> ids);
	std::vector<int> getMotionData();
	void changeMotion(int motion);
	int getCurrentMotionID() { return _curMotionID; }
private:
	bool isSceneTop();
	bool isSceneButton();
	bool isSceneCenter();
	bool isSceneUnder();
	bool isSceneAbove();
	bool isSceneTopAbove();
	void activateUnit();
	void fire(float dt);
protected:
    bool		_alive;
	bool		_active;//�Ƿ񱻼���
	bool		_eventUnit;//�Ƿ��¼�Unit;
	bool		_unitFireRequire;
	bool		_die;//������˸״̬
	bool		_endableMove;
	int			_power;
    int			_score;
	int			_iDieExplodCount;//������ը��������
	int			_startExplodeTick;//��ը��ʼʱ��
	int			_campType;
	int			_unitID;	//���¼�ϵͳ��ID
	int			_moveType;	//�˶���ʽ
	int			_maxHP;		//Ѫ�����ֵ
	int			_BodyLevel;//�������������
	//ָ�
	int			_orderType;//ָ������ 0 ���� ��ʼָ��    1 ���� ѭ��ָ��
	int			_currentOrderIndex;
	float		_orderDelay;
	int			_orderCount;

	//�ƶ��ٶ�
	int			_moveX;
	int			_moveY;
	//����
	int			_motion;//��ǰ�Ķ���
	//����
	int			_beAttackTick;//�������ӳ٣�������ɫ
	int			_diaphaneity;//͸����
	int			_dieTick;
	//�ж�����
	int        	 _walkDir;       //�����°����򣬲���������ת���Unit��iWalkDir��������
	//AI
	int			_AIType;
	//fire
	int			_shotPosIndex;//���������
	int			_shotLogicType;
	int			_weaponResID;
	int			_fireTick;//�������漰�ӳ�
	//�жϿ����߷�Χ
	Rect		_walkRect;//������ײ�жϾ���
	Rect		_moveRect;//��Ч���߾���
	GameScene* _gameScene;
	Player* _player;
	vector<bool>	 _dieExplode;//��¼�Ƿ�ը
	vector<XUnitOrder> _unitOrder;
	//8��������ƶ��ĸ���
	vector<int>		_moveProb;
	//�ƶ�ʱ��͸���
	vector<Vec2>	_moveItemData;
	//�ƶ���ʽΪ��·���ƶ�ʱ�ı�ʾ����
	vector<Vec2>	_pointItemData;
	//�������
	vector<Vec2>	_fireItemData;
	//���ݾ��룬��Ŀ���ƶ��ĸ���
	vector<Vec2>	_distItemData;
	//�������
	vector<XDropTool> _dropToolData;
	Vector<Weapon*> _weapons;//��Unit��������Weapon
	//ѭ��ָ��
	vector<XUnitOrder>	_unitRecycleOrder;
	std::shared_ptr<UnitRes>		 _unitRes;
	std::shared_ptr<ShotLogicManager> _shotLogicManager;
	std::vector<std::shared_ptr<MotionImpl>> _motions;
	std::shared_ptr<MotionImpl> _curMotion;
	std::shared_ptr<AIBase> _AI;
	std::vector<int> _motionIDs;
	int _curMotionID;
};
#endif /* defined(__Moon3d__AirCraft__) */
