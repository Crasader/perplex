#include "AIBase.h"
#include "Unit.h"
#include "UnitManager.h"
#include "UnitRes.h"
#include "GameScene.h"
#include "consts.h"
#include "GameGlobe.h"
#include "CameraExt.h"

USING_NS_CC;

const float Offset = 100;

AIBase::AIBase(GameScene* gameScene, Unit* unit)
:_attack(false)
, _lock(false)
, _AIFaceDesDir(0)
, _patrol(unit->getPosition())
, _motion(0)
, _speed(_unit->getSpeed())
, _dir(0)
, _delay(0)
, _pointIndex(-1)
, _unit(unit)
, _goalUnit(nullptr)
, _gameScene(gameScene)
, _AItick(rand_0_1() * 32)
, _moveStart(false)
, _moveFinish(false)
, _nomalized(Vec2::ZERO)
, _time(0.0f)
, _moveRandom(nullptr)
, _complete(nullptr)
{
	_unit->changeMotion(_motion);
}

AIBase::~AIBase()
{

}

void AIBase::perform( float dt )
{
  	switch (_unit->getMoveType())
  	{
  	case STAND_MOVE:
  
  		break;
  	case RANDOM_MOVE:
  		moveRandom(dt);
  		break;
  	case PATH_MOVE:
  		followPoint(dt, true);
  		break;
  	case ORDER_MOVE:
		_unit->AI(dt);
		_unit->unitMoveTo(_unit->getPosition() + _unit->getVec() * dt);
  		break;
  	default:
		break;
  	}
		/*walkToGoal(dt);*/
		//findEnemyGoal(Rect(_gameScene->getCamera()->getX(), _gameScene->getCamera()->getY(), _gameScene->getSceneWidth(), _gameScene->getSceneHeight()), dt);
}

bool AIBase::faceToEnemy( float dt )
{
	auto angle = (_goalUnit->getPosition() - _unit->getPosition()).getAngle();
	_unit->setRotation(-CC_RADIANS_TO_DEGREES(angle) - 90);
// 	if (_unit->getShotDir() == _AIFaceDesDir)
// 	{
// 		return true;
// 	}
	return false;
}

void AIBase::walkToGoal( float dt )
{
	auto delta = _patrol - _unit->getPosition();
	auto normalized = delta.getNormalized();
	auto speed = _unit->getSpeed();
	if (!_lock)
	{
		_lock = true;
		_time = delta.getLength() / speed;
	}
	auto newPos = _unit->getPosition() + normalized * speed * dt;
	if (_time > 0)
	{
		_time -= dt;
	}
	else
	{
		_time = 0.0f;
		newPos = _patrol;
		_lock = false;
	}

	_unit->setPosition(newPos);
}

void AIBase::followPoint(float dt, bool cycle)
{
	if (_unit == nullptr)
	{
		return;
	}

	if (_delay > 0)
	{
		_delay -= dt;
		return;
	}
	else
	{
		_delay = 0.0f;
	}
	auto movefinish = false;
	auto oldPos = _unit->getPositionInCamera();
	if (!_moveStart)
	{
		_moveStart = true;
		auto delt = _patrol - oldPos;
		auto lenght = delt.getLength();
		_nomalized = delt.getNormalized();
		_time = lenght / _speed;
		
		auto angle = delt.getAngle();
		auto rotateTime = 0.16f * abs(angle);
		/*_unit->setRotation(-CC_RADIANS_TO_DEGREES(angle) - 90);*/
		log("angle: %f, time %f", angle, rotateTime);
		if (_dir == 1)_unit->runAction(RotateTo::create(rotateTime, -CC_RADIANS_TO_DEGREES(angle) - 90));
		_unit->changeMotion(_motion);
	}
	auto movepos = _unit->getPositionInCamera() + _nomalized * _speed * dt;
	
	if (_time > 0)
	{
		_time -= dt;
	}
	else
	{
		_moveStart = false;
		_time = 0.0f;
		movepos = _patrol;
		movefinish = true;
	}
	_unit->unitMoveTo(movepos);
	/*if (_dir == 1)
	{
		auto oldAngle = _unit->getRotation();
		auto angle = (_unit->getPositionInCamera() - oldPos).getAngle();
		angle = -CC_RADIANS_TO_DEGREES(angle);
		if (oldAngle != angle)_unit->setRotation( angle - 90);
	}*/
	if (movefinish && !_unit->getPointItemData().empty())
	{
		_pointIndex++;
		if (_pointIndex >= _unit->getPointItemData().size())
		{
			if (!cycle)
			{
				_pointIndex = _unit->getPointItemData().size() - 1;
				return;
			}
			else
			{
				_pointIndex = 0;
			}
		}
		_patrol = _unit->getPointItemData()[_pointIndex];
		_motion = _unit->getMotionData()[_pointIndex];
		_speed = _unit->getMoveSpeed()[_pointIndex];
		_dir = _unit->getMoveDir()[_pointIndex];
		_delay = _unit->getMoveDelay()[_pointIndex];
	}
}

void AIBase::moveRandom( float dt )
{
	
	auto prob = rand() % 100;
	if (_moveRandom == nullptr)
	{
		if (prob < _unit->getMoveProb()[0])
		{
			_unit->setRotation(0 * 45);
			_moveRandom = CC_CALLBACK_1(AIBase::moveDown, this);
		}
		else if (prob < _unit->getMoveProb()[1])
		{
			_unit->setRotation(1 * 45);
			_moveRandom = CC_CALLBACK_1(AIBase::moveDownleft, this);
		}
		else if (prob < _unit->getMoveProb()[2])
		{
			_unit->setRotation(2 * 45);
			_moveRandom = CC_CALLBACK_1(AIBase::moveleft, this);
		}
		else if (prob < _unit->getMoveProb()[3])
		{
			_unit->setRotation(3 * 45);
			_moveRandom = CC_CALLBACK_1(AIBase::moveLeftup, this);
		}
		else if (prob < _unit->getMoveProb()[4])
		{
			_unit->setRotation(4 * 45);
			_moveRandom = CC_CALLBACK_1(AIBase::moveUp, this);
		}
		else if (prob < _unit->getMoveProb()[5])
		{
			_unit->setRotation(5 * 45);
			_moveRandom = CC_CALLBACK_1(AIBase::moveUpright, this);
		}
		else if (prob < _unit->getMoveProb()[6])
		{
			_unit->setRotation(6 * 45);
			_moveRandom = CC_CALLBACK_1(AIBase::moveRight, this);
		}
		else if (prob < _unit->getMoveProb()[7])
		{
			_unit->setRotation(7 * 45);
			_moveRandom = CC_CALLBACK_1(AIBase::moveDownRight, this);
		}
	}
	
	prob = rand() % 100;
// 	if (prob < 10)
// 	{
// 		if (_unit->getPositionX() > _patrol.x + Offset)
// 		{
// 			_unit->setRotation(2 * 45);
// 			_moveRandom = [&](float dt){this->moveleft, this);
// 		}
// 		else if (_unit->getPositionX() < _patrol.x - Offset)
// 		{
// 			_unit->setRotation(6 * 45);
// 			_moveRandom = [&](float dt){this->moveRight, this);
// 		}
// 		if (_unit->getPositionY() > _patrol.y + Offset)
// 		{
// 			_unit->setRotation(0 * 45);
// 			_moveRandom = [&](float dt){this->moveDown, this);
// 		}
// 		else if (_unit->getPositionY() < _patrol.y - Offset)
// 		{
// 			_unit->setRotation(4 * 45);
// 			_moveRandom = [&](float dt){this->moveUp, this);
// 		}
// 	}

	if (_moveRandom)
	{
		_moveRandom(dt);
	}
}

bool AIBase::findEnemyGoal(cocos2d::Rect& lookRect, float dt)
{
	//如果被攻击，可是范围扩大
	if (_attack)
	{
		_attack = false;
	}
	//查找视线内的敌人
	for (auto a : UnitManager::_allys)
	{
		_goalUnit = a;
		if (_unit != _goalUnit && _unit->getPower() > 0 && lookRect.containsPoint(_unit->getPositionInCamera()))
		{
			//追踪目标的方向
			if (!_lock)
			{
				faceToEnemy(dt);
				_patrol = _goalUnit->getPositionInCamera();
			}
			//向着目标转动或走动
			walkToGoal(dt);
			return true;
		}
	}
	return false;
}

bool AIBase::isEnemyGoalValid(cocos2d::Rect looRect)
{
	if (_goalUnit != nullptr)
	{
		if (_goalUnit->getPower() <= 0)
		{
			CC_SAFE_RELEASE_NULL(_goalUnit);
		}
		else if (!looRect.containsPoint(_goalUnit->getPosition()))
		{
			CC_SAFE_RELEASE_NULL(_goalUnit);
		}
		else
		{
			return true;
		}
	}
	return false;
}

void AIBase::resetRaod()
{
	_patrol = _unit->getPosition();
	_pointIndex = 0;
}

void AIBase::setBeAttack()
{
	_attack = true;
}

float AIBase::getMoveTick()
{
	auto tem = rand_0_1() * 99;
	auto prob = 0;
	auto tick = 0.0f;
	for (auto item : _unit->getMoveItemData())
	{
		prob += item.y;
		if (tem <= prob)
		{
			tick = item.x;
			break;
		}
	}
	return tick;
}

void AIBase::fire()
{
	auto tem = 0;
	auto exist = false;
	size_t i = 0;
	for (auto item : _unit->getFireItemData())
	{
		tem = rand() % 100;
		if (tem <= item.y)
		{
			break;
		}
		i++;
	}
	if (i >= _unit->getFireItemData().size())
	{
		return;
	}
	for (auto weapon : _unit->getUnitRes()->_weaponTypeList)
	{
		if (weapon == _unit->getFireItemData()[i].x)
		{
			exist = true;
			break;
		}
	}
	if (!exist)
	{
		return;
	}
}

void AIBase::moveDown( float dt )
{
	move(Vec2(0, -1),dt);

}

void AIBase::move(const cocos2d::Vec2& normalized, float dt)
{
	auto oldpos = _unit->getPosition();
	auto movpos = oldpos + normalized * _unit->getSpeed() * dt;
	_unit->setPosition(movpos);
}

void AIBase::moveDownleft(float dt)
{
	move(Vec2(-1, -1), dt);
}

void AIBase::moveleft( float dt )
{
	move(Vec2(-1, 0), dt);
}

void AIBase::moveLeftup( float dt )
{
	move(Vec2(-1, 1), dt);
}

void AIBase::moveUp( float dt )
{
	move(Vec2(0, 1), dt);
}

void AIBase::moveUpright( float dt )
{
	move(Vec2(1, 1), dt);
}

void AIBase::moveRight( float dt )
{
	move(Vec2(1, 0), dt);
}

void AIBase::moveDownRight( float dt )
{
	move(Vec2(1, -1), dt);
}
