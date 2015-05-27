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
		//����Ŀ��ת�����߶�
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
	bool _attack;//�ܵ�����
	int _AIFaceDesDir;//����ķ���
	int _AItick;//�ӳ�ִ�еļ�ʱ��
	int _pointIndex;//��ǰ���ٵ������
	float _patrolX;//Ѳ�߳����� X
	float _patrolY; //Ѳ�߳����� Y

	Unit* _goalUnit;
	Unit* _unit;
	GameScene* _gameScene;
};

#endif // __AIBase_h__
