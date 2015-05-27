#ifndef __AIBase_h__
#define __AIBase_h__

#include "base\ccRandom.h"

class Unit;
class GameScene;

class AIBase
{
public:
	AIBase(GameScene* gameScene, Unit* unit)
		:_attack(false)
		, _AIFaceDesDir(0)
		, _patrolX(unit->getPositionX())
		, _patrolY(unit->getPositionY())
		, _pointIndex(-1)
		, _unit(unit)
		, _goalUnit(unit)
		, _gameScene(gameScene)
	{
		_AItick(rand_0_1() * 32);
	}
	virtual ~AIBase(){}
	virtual void perform();
	bool faceToEnemy()
	{
		auto angle = (_unit->getPosition() - _goalUnit->getPosition()).getAngle();
		if (_unit->getShotDir() == _AIFaceDesDir)
		{
			return true;
		}
		//向着目标转动或走动
		walkToGoal();
		return false;
	}
	void walkToGoal()
	{
	
	}

	void followPoint(bool cycle)
	{
		if (_unit->getPositionX() < _patrolX - 10)
		{

		}
	}
private:
	bool _attack;//受到攻击
	int _AIFaceDesDir;//面向的方向
	int _AItick;//延迟执行的计时器
	int _pointIndex;//当前跟踪点的索引
	float _patrolX;//巡逻出发点 X
	float _patrolY; //巡逻出发点 Y

	Unit* _goalUnit;
	Unit* _unit;
	GameScene* _gameScene;
};

#endif // __AIBase_h__
