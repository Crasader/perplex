#include "shotlogic.h"
#include "Unit.h"
#include "GameScene.h"
#include "Weapon.h"
#include "WeaponRes.h"
#include "weaponresmanager.h"
#include "mathconst.h"

ShotLogic::ShotLogic(Unit* parent, cocos2d::Node* shot, int weaponResID, int posIndex) 
: _end(false)
, _firetick(0)
, _posIndex(0)
, _weaponResID(weaponResID)
, _speed(200)
, _unit(parent)
, _shot(shot)
{
	if (_unit && _unit->getGameScene()->getDefficulty() > 1)
	{
		_speed = 300;
	}
		
}

ShotLogic::~ShotLogic()
{
}

void ShotLogic::perform( float dt )
{

}

bool ShotLogic::isEnd()
{
	return _end;
}

ShotLogicA::ShotLogicA(Unit* parent, cocos2d::Node* shot, int weaponResID, int posIndex)
	:ShotLogic(parent, shot, weaponResID, posIndex)
, _fireAngle(0.0f)
, _shotDirection(0)
, _delay(1)
{

}

void ShotLogicA::perform( float dt )
{
	float moveX = 0.0f;
	float moveY = 0.0f;
	if (_firetick < _delay)
	{
		_firetick ++;
		return;
	}
	_firetick = 0;
	moveX = COSVALUE32FP[_shotDirection];
	moveY = SINVALUE32FP[_shotDirection];
	/*auto angle = 11 * _shotDirection - _unit->getRotation() + 90;
	moveX = cosf(CC_DEGREES_TO_RADIANS(angle));
	moveY = sinf(CC_DEGREES_TO_RADIANS(angle));*/
	auto _pos = _unit->getShotPositionInWorld(_posIndex, _shot->getRotation(), _unit->getPosition());
	auto weapon = Weapon::create(_unit->getGameScene(),
		_unit->getGameScene()->getWeaponResManager()->getWeapResFromID(_weaponResID),
		_unit,
		_pos,
		Vec2(moveX, moveY),
		_speed,
		_posIndex,
		_unit->getCampType());
	_unit->getWeaponList().pushBack(weapon);
	_shotDirection++;
	if (_shotDirection >= 32)
	{
		_end = true;
	}
}
//////////////////////////////////////////////////////////////////////////
//直线追踪型
//////////////////////////////////////////////////////////////////////////
ShotLogicB::ShotLogicB(Unit* parent, cocos2d::Node* shot, int weaponResID, int posIndex, int shotCount)
:ShotLogic(parent, shot, weaponResID, posIndex)
, _fireCount(0)
, _delay(8)
, _shotCount(shotCount)
, _goalUnit(nullptr)
{
	_firetick = 8;
	_goalUnit = _unit->getGameScene()->getPlayer();
	CC_SAFE_RETAIN(_goalUnit);
	if (_goalUnit != nullptr && _goalUnit->getPower() <= 0)
	{
		CC_SAFE_RELEASE_NULL(_goalUnit);
	}
}

ShotLogicB::~ShotLogicB()
{
	CC_SAFE_RELEASE_NULL(_goalUnit);
}

void ShotLogicB::perform( float dt )
{
	if (_firetick < _delay)
	{
		_firetick++;
		return;
	}
	_firetick = 0;
	if (_fireCount >= _shotCount || _goalUnit == nullptr)
	{
		_end = true;
	}
	else
	{

		auto delt = _goalUnit->getPosition() - _unit->getPosition();
		auto _vec = delt.getNormalized();

		auto _pos = _unit->getShotPositionInWorld(_posIndex, _shot->getRotation(), _unit->getPosition());
		auto weapon = Weapon::create(_unit->getGameScene(),
			_unit->getGameScene()->getWeaponResManager()->getWeapResFromID(_weaponResID),
			_unit,
			_pos,
			_vec,
			_speed,
			_posIndex,
			_unit->getCampType());
		_unit->getWeaponList().pushBack(weapon);
		_fireCount++;
	}
}
//////////////////////////////////////////////////////////////////////////
//方向向下
//////////////////////////////////////////////////////////////////////////
ShotLogicD::ShotLogicD(Unit* parent, cocos2d::Node* shot, int weaponResID, int posIndex) 
:ShotLogic(parent, shot, weaponResID, posIndex)
, _fireCount(0)
, _delay(8)
{
	_firetick = 8;
}

void ShotLogicD::perform( float dt )
{
	if (_firetick < _delay)
	{
		_firetick++;
		return;
	}
	_firetick = 0;
	if (_fireCount >= LOGIC_D_MAXFIRECOUNT)
	{
		_end = true;
	}
	else
	{
		auto _vec = Vec2(0, -1);
		auto _pos = _unit->getShotPositionInWorld(_posIndex, _shot->getRotation(), _unit->getPosition());
		auto weapon = Weapon::create(_unit->getGameScene(),
			_unit->getGameScene()->getWeaponResManager()->getWeapResFromID(_weaponResID),
			_unit,
			_pos,
			_vec,
			_speed,
			_posIndex,
			_unit->getCampType());
		_unit->getWeaponList().pushBack(weapon);
		_fireCount++;
	}
}
//////////////////////////////////////////////////////////////////////////
//单发(指定方向)
//////////////////////////////////////////////////////////////////////////

ShotLogicC::ShotLogicC(Unit* parent, cocos2d::Node* shot, int weaponResID, int posIndex)
	:ShotLogic(parent, shot, weaponResID, posIndex)
{

}

void ShotLogicC::perform(float dt)
{
	_end = true;
	
	auto _pos = _unit->getShotPositionInWorld(_posIndex, _unit->getRotation(), _unit->getPosition());
	auto rad = CC_DEGREES_TO_RADIANS(-_shot->getRotation() + 90);
	auto _vec = Vec2(cosf(rad), sinf(rad)) * -1;
	auto weapon = Weapon::create(_unit->getGameScene(),
		_unit->getGameScene()->getWeaponResManager()->getWeapResFromID(_weaponResID),
		_unit,
		_pos,
		_vec,
		_speed,
		_posIndex,
		_unit->getCampType());
	_unit->getWeaponList().pushBack(weapon);
}
//========================================================================
//双叉弹
//========================================================================
ShotLogicE::ShotLogicE(Unit* parent, cocos2d::Node* shot, int weaponResID, int posIndex, bool dir)
:ShotLogic(parent, shot, weaponResID, posIndex)
, _dir(dir)
, _goalUnit(nullptr)
{
	_goalUnit = _unit->getGameScene()->getPlayer();
	
	if (_goalUnit != nullptr && _goalUnit->getPower() <= 0)
	{
		_goalUnit = nullptr;
	}
}

ShotLogicE::~ShotLogicE()
{
	
}

void ShotLogicE::perform( float dt )
{
	_goalUnit = _unit->getGameScene()->getPlayer();
	if (_goalUnit == nullptr)
	{
		_end = true;
		return;
	}
	auto _pos = _unit->getShotPositionInWorld(_posIndex, _shot->getRotation(), _unit->getPosition());
	auto delt = _goalUnit->getPosition() - _pos;
	auto _vec = delt.getNormalized();

	auto angle = delt.getAngle();
	auto x = cosf(angle - 0.1745);
	auto y = sinf(angle - 0.1745);
	_vec.x = x;
	_vec.y = y;
	auto weapon1 = Weapon::create(_unit->getGameScene(),
		_unit->getGameScene()->getWeaponResManager()->getWeapResFromID(_weaponResID),
		_unit,
		_pos,
		_vec,
		_speed,
		_posIndex,
		_unit->getCampType());

	x = cosf(angle + 0.1745);
	y = sinf(angle + 0.1745);
	_vec.x = x;
	_vec.y = y;
	auto weapon2 = Weapon::create(_unit->getGameScene(),
		_unit->getGameScene()->getWeaponResManager()->getWeapResFromID(_weaponResID),
		_unit,
		_pos,
		_vec,
		_speed,
		_posIndex,
		_unit->getCampType());

	_unit->getWeaponList().pushBack(weapon1);
	_unit->getWeaponList().pushBack(weapon2);
}
//------------------------------------------------------------------------
//三叉弹，五叉弹                                                                   
//------------------------------------------------------------------------
ShotLogicF::ShotLogicF(Unit* parent, cocos2d::Node* shot, int weaponResID, int posIndex, int shotCount, bool dir)
:ShotLogic(parent, shot, weaponResID, posIndex)
, _dir(dir)
, _shotCount(shotCount)
, _goalUnit(nullptr)
{

}

ShotLogicF::~ShotLogicF()
{
}

void ShotLogicF::perform( float dt )
{
	_goalUnit = _unit->getGameScene()->getPlayer();
	if (_goalUnit == nullptr)
	{
		_end = true;
		return;
	}
	auto _pos = _unit->getShotPositionInWorld(_posIndex, _shot->getRotation(), _unit->getPosition());
	auto delt = _goalUnit->getPosition() - _pos;
	auto _vec = delt.getNormalized();

	auto angle = delt.getAngle();
	auto weapon = Weapon::create(_unit->getGameScene(),
		_unit->getGameScene()->getWeaponResManager()->getWeapResFromID(_weaponResID),
		_unit,
		_pos,
		_vec,
		_speed,
		_posIndex,
		_unit->getCampType());

	_unit->getWeaponList().pushBack(weapon);

	for (int i = 1; i <= _shotCount; i++)
	{
		auto x = cosf(angle + i * 0.2618);
		auto y = sinf(angle + i * 0.2618);
		_vec.x = x;
		_vec.y = y;
		auto weapon1 = Weapon::create(_unit->getGameScene(),
			_unit->getGameScene()->getWeaponResManager()->getWeapResFromID(_weaponResID),
			_unit,
			_pos,
			_vec,
			_speed,
			_posIndex,
			_unit->getCampType());
		_unit->getWeaponList().pushBack(weapon1);

		x = cosf(angle - i * 0.2618);
		y = sinf(angle - i * 0.2618);
		_vec.x = x;
		_vec.y = y;
		auto weapon2 = Weapon::create(_unit->getGameScene(),
			_unit->getGameScene()->getWeaponResManager()->getWeapResFromID(_weaponResID),
			_unit,
			_pos,
			_vec,
			_speed,
			_posIndex,
			_unit->getCampType());
		_unit->getWeaponList().pushBack(weapon2);
	}

	_end = true;
}

//------------------------------------------------------------------------
//八方向同时发射                                                                   
//------------------------------------------------------------------------
ShotLogicH::ShotLogicH(Unit* parent, cocos2d::Node* shot, int weaponResID, int posIndex) 
:ShotLogic(parent, shot, weaponResID, posIndex)
{

}

void ShotLogicH::perform( float dt )
{

	for (int i = 0; i < 8; i++)
	{
		auto _pos = _unit->getShotPositionInWorld(_posIndex, _shot->getRotation(), _unit->getPosition());
		Vec2 _vec;
		_vec.x = COSVALUE8FP[i];
		_vec.y = SINVALUE8FP[i];
		auto weapon2 = Weapon::create(_unit->getGameScene(),
			_unit->getGameScene()->getWeaponResManager()->getWeapResFromID(_weaponResID),
			_unit,
			_pos,
			_vec,
			_speed,
			_posIndex,
			_unit->getCampType());
		_unit->getWeaponList().pushBack(weapon2);
	}
	_end = true;
}

//------------------------------------------------------------------------
//16方向同时发射                                                                   
//------------------------------------------------------------------------
ShotLogicI::ShotLogicI(Unit* parent, cocos2d::Node* shot, int weaponResID, int posIndex)
:ShotLogic(parent, shot, weaponResID, posIndex)
{

}

void ShotLogicI::perform( float dt )
{
	for (int i = 0; i < 16; i++)
	{
		auto _pos = _unit->getShotPositionInWorld(_posIndex, _shot->getRotation(), _unit->getPosition());
		Vec2 _vec;
		_vec.x = COSVALUE16FP[i];
		_vec.y = SINVALUE16FP[i];
		auto weapon2 = Weapon::create(_unit->getGameScene(),
			_unit->getGameScene()->getWeaponResManager()->getWeapResFromID(_weaponResID),
			_unit,
			_pos,
			_vec,
			_speed,
			_posIndex,
			_unit->getCampType());
		_unit->getWeaponList().pushBack(weapon2);
	}
	_end = true;
}

ShotLogicK::ShotLogicK(Unit* parent, cocos2d::Node* shot, int weaponResID, int posIndex) 
	:ShotLogic(parent, shot, weaponResID, posIndex)
{

}

void ShotLogicK::perform( float dt )
{
	_end = true;
}

ShotLogicQ::ShotLogicQ(Unit* parent, cocos2d::Node* shot, int weaponResID, int posIndex, int shotCount)
	:ShotLogic(parent, shot, weaponResID, posIndex)
, _fireCount(0)
, _shotCount(shotCount)
, _goalUnit(nullptr)
, _delay(8)
{
	_firetick = 8;
}

ShotLogicQ::~ShotLogicQ()
{
}

void ShotLogicQ::perform( float dt )
{
	_end = true;
}

ShotLogicG::ShotLogicG(Unit* unit, cocos2d::Node* shot, int weaponResID, int posIndex)
	:ShotLogic(unit, shot, weaponResID, posIndex)
{
	_count = 2;
}

void ShotLogicG::perform(float dt)
{
	if (_count > 0)
	{
		_count--;
		_end = true;
	}

	auto _pos = _unit->getShotPositionInWorld(_posIndex, _unit->getRotation(), _unit->getPosition());
	auto rad = CC_DEGREES_TO_RADIANS(-_shot->getRotation() + 90);
	auto _vec = Vec2(cosf(rad), sinf(rad)) * -1;
	auto weapon = Weapon::create(_unit->getGameScene(),
		_unit->getGameScene()->getWeaponResManager()->getWeapResFromID(_weaponResID),
		_unit,
		_pos,
		_vec,
		_speed,
		_posIndex,
		_unit->getCampType());
	_unit->getWeaponList().pushBack(weapon);
}
