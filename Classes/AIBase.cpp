#include "AIBase.h"
#include "Unit.h"
#include "UnitManager.h"
#include "UnitRes.h"
#include "GameScene.h"
#include "consts.h"
#include "GameGlobe.h"
USING_NS_CC;

const float Offset = 100;

AIBase::AIBase(GameScene* gameScene, Unit* unit) :_attack(false)
, _AIFaceDesDir(0)
, _patrol(unit->getPosition())
, _motion(0)
, _pointIndex(-1)
, _unit(unit)
, _goalUnit(unit)
, _gameScene(gameScene)
, _AItick(rand_0_1() * 32)
, _moveStart(false)
, _moveFinish(false)
, _nomalized(Vec2::ZERO)
, _time(0.0f)
, _moveRandom(nullptr)
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
 
 		break;
 	default:
		break;
 	}
		/*walkToGoal(dt);*/
}

bool AIBase::faceToEnemy( float dt )
{
	auto angle = (_unit->getPosition() - _goalUnit->getPosition()).getAngle();
	_goalUnit->setRotation(-CC_RADIANS_TO_DEGREES(angle) - 90);
// 	if (_unit->getShotDir() == _AIFaceDesDir)
// 	{
// 		return true;
// 	}
	//向着目标转动或走动
	walkToGoal(dt);
	return false;
}

void AIBase::walkToGoal( float dt )
{
	auto delta = _unit->getPosition() - _goalUnit->getPosition();
	auto normalized = delta.getNormalized();
	auto speed = _goalUnit->getSpeed();
	auto newPos = _goalUnit->getPosition() + normalized * speed * dt;
	_goalUnit->setPosition(newPos);
}

void AIBase::followPoint(float dt, bool cycle)
{
	if (_unit == nullptr)
	{
		return;
	}

	auto movefinish = false;
	auto oldPos = _unit->getPosition();
	if (!_moveStart)
	{
		_moveStart = true;
		auto delt = _patrol - oldPos;
		auto lenght = delt.getLength();
		_nomalized = delt.getNormalized();
		auto speed = _unit->getSpeed();
		if(_unit->getCurrentMotionID() != _motion)_unit->changeMotion(_motion);
		_time = lenght / speed;
		
		auto angle = delt.getAngle();
		_unit->setRotation(-CC_RADIANS_TO_DEGREES(angle) - 90);
	}
	auto movepos = _unit->getPosition() + _nomalized * _unit->getSpeed() * dt;
	
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
	_unit->setPosition(movepos);

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
			_moveRandom = [&](float dt){this->moveDown(dt); };
		}
		else if (prob < _unit->getMoveProb()[1])
		{
			_unit->setRotation(1 * 45);
			_moveRandom = [&](float dt){this->moveDownleft(dt); };
		}
		else if (prob < _unit->getMoveProb()[2])
		{
			_unit->setRotation(2 * 45);
			_moveRandom = [&](float dt){this->moveleft(dt); };
		}
		else if (prob < _unit->getMoveProb()[3])
		{
			_unit->setRotation(3 * 45);
			_moveRandom = [&](float dt){this->moveLeftup(dt); };
		}
		else if (prob < _unit->getMoveProb()[4])
		{
			_unit->setRotation(4 * 45);
			_moveRandom = [&](float dt){this->moveUp(dt); };
		}
		else if (prob < _unit->getMoveProb()[5])
		{
			_unit->setRotation(5 * 45);
			_moveRandom = [&](float dt){this->moveUpright(dt); };
		}
		else if (prob < _unit->getMoveProb()[6])
		{
			_unit->setRotation(6 * 45);
			_moveRandom = [&](float dt){this->moveRight(dt); };
		}
		else if (prob < _unit->getMoveProb()[7])
		{
			_unit->setRotation(7 * 45);
			_moveRandom = [&](float dt){this->moveDownRight(dt); };
		}
	}
	
	prob = rand() % 100;
// 	if (prob < 10)
// 	{
// 		if (_unit->getPositionX() > _patrol.x + Offset)
// 		{
// 			_unit->setRotation(2 * 45);
// 			_moveRandom = [&](float dt){this->moveleft(dt); };
// 		}
// 		else if (_unit->getPositionX() < _patrol.x - Offset)
// 		{
// 			_unit->setRotation(6 * 45);
// 			_moveRandom = [&](float dt){this->moveRight(dt); };
// 		}
// 		if (_unit->getPositionY() > _patrol.y + Offset)
// 		{
// 			_unit->setRotation(0 * 45);
// 			_moveRandom = [&](float dt){this->moveDown(dt); };
// 		}
// 		else if (_unit->getPositionY() < _patrol.y - Offset)
// 		{
// 			_unit->setRotation(4 * 45);
// 			_moveRandom = [&](float dt){this->moveUp(dt); };
// 		}
// 	}

	if (_moveRandom)
	{
		_moveRandom(dt);
	}
}

bool AIBase::findEnemyGoal(cocos2d::Rect& lookRect, float dt)
{
	cocos2d::Vector<Unit*> enemys;
	enemys = UnitManager::_enemies;
	//如果被攻击，可是范围扩大
	if (_attack)
	{
		_attack = false;
	}
	//查找视线内的敌人
	for (auto enemy : enemys)
	{
		if (enemy->getPower() > 0 && lookRect.containsPoint(enemy->getPosition()))
		{
			_goalUnit = enemy;
			CC_SAFE_RETAIN(_goalUnit);
			//追踪目标的方向
			faceToEnemy(dt);
			_patrol = _unit->getPosition();
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
