#ifndef __AIBase_h__
#define __AIBase_h__

#include "cocos2d.h"

class Unit;
class GameScene;

class AIBase
{
public:
	AIBase(GameScene* gameScene, Unit* unit);
	virtual ~AIBase();
	virtual void perform(float dt);;
	bool faceToEnemy(float dt);
	void walkToGoal(float dt);
	void followPoint(float dt, bool cycle = false);
	void moveRandom(float dt);
	bool findEnemyGoal(cocos2d::Rect& lookRect, float dt);
	//Ŀ���Ƿ���Ч
	bool isEnemyGoalValid(cocos2d::Rect looRect);
	void resetRaod();
	void setBeAttack();
	float getMoveTick();
	void fire();
	void setPatrolX(float x) { _patrol.x = x; }
	void setPatrolY(float y) { _patrol.y = y; }
	cocos2d::Vec2 getPatorl(){ return _patrol; }
	void moveDown(float dt);

	void move(const  cocos2d::Vec2& normalized, float dt);

	void moveDownleft(float dt);
	void moveleft(float dt);
	void moveLeftup(float dt);
	void moveUp(float dt);
	void moveUpright(float dt);
	void moveRight(float dt);
	void moveDownRight(float dt);
private:
	bool _attack;//�ܵ�����
	bool _lock;
	int _AIFaceDesDir;//����ķ���
	int _AItick;//�ӳ�ִ�еļ�ʱ��
	int _pointIndex;//��ǰ���ٵ������
	int _motion;
	int _speed;
	int _dir;
	float _delay;
	cocos2d::Vec2 _patrol;//Ѳ�߳�����
	bool _moveStart;
	bool _moveFinish;
	cocos2d::Vec2 _nomalized;
	float _time;
	Unit* _goalUnit;
	Unit* _unit;
	GameScene* _gameScene;
	std::function<void(float)> _moveRandom;
	std::function<void()> _complete;
};

#endif // __AIBase_h__
