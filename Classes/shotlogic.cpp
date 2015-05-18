#include "shotlogic.h"
#include "Unit.h"


ShotLogic::ShotLogic(Unit* unit, int weaponResID, int posIndex) : _end(false)
, _firetick(0)
, _posIndex(0)
, _weaponResID(weaponResID)
, _speed(4)
, _unit(unit)
{
	CC_SAFE_RETAIN(unit);
	//#TODO设置不同难度的发射速度
}

ShotLogic::~ShotLogic()
{
	CC_SAFE_RELEASE(_unit);
}

void ShotLogic::perform()
{

}

bool ShotLogic::isEnd()
{
	return _end;
}

ShotLogicA::ShotLogicA(Unit* unit, int weaponResID, int posIndex)
:ShotLogic(unit, weaponResID, posIndex)
, _fireAngle(0.0f)
, _delay(1)
{

}

void ShotLogicA::perform()
{
	float moveX = 0.0f;
	float moveY = 0.0f;
	if (_firetick < _delay)
	{
		_firetick++;
		return;
	}
	_firetick = 0;
	moveX = cos(CC_DEGREES_TO_RADIANS(11 * _shotDirection)) * LOGICASPEED;
	moveY = sin(CC_DEGREES_TO_RADIANS(11 * _shotDirection)) * LOGICASPEED;
	//#TODO添加武器

	_shotDirection++;
	if (_shotDirection >= 32)
	{
		_end = true;
	}
}

ShotLogicB::ShotLogicB(Unit* unit, int weaponResID, int posIndex, int shotCount) 
:ShotLogic(unit, weaponResID, posIndex)
, _fireCount(0)
, _delay(8)
, _shotCount(shotCount)
, _goalUnit(nullptr)
{
	_firetick = 8;
	//#toodo
}

ShotLogicB::~ShotLogicB()
{
	CC_SAFE_RELEASE(_goalUnit);
}

void ShotLogicB::perform()
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
		float movex = 0.0f;
		float movey = 0.0f;
		auto moveVector = _unit->getPosition() - _goalUnit->getPosition();
		auto moveAngle = moveVector.getAngle();
		auto parentAngle = _unit->getParent()->getRotation();
		auto radians = CC_DEGREES_TO_RADIANS(CC_RADIANS_TO_DEGREES(-1 * moveAngle) + 90.0f - parentAngle);
		movex = _speed * cos(radians);
		movey = _speed * sin(radians);
		//#toodo

		_fireCount++;
	}
}

ShotLogicD::ShotLogicD(Unit* unit, int weaponResID, int posIndex) 
:ShotLogic(unit, weaponResID, posIndex)
, _fireCount(0)
, _delay(8)
{
	_firetick = 8;
}

void ShotLogicD::perform()
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

	}
}

ShotLogicE::ShotLogicE(Unit* unit, int weaponResID, int posIndex, bool dir)
:ShotLogic(unit, weaponResID, posIndex)
, _dir(dir)
, _goalUnit(nullptr)
{

}

ShotLogicE::~ShotLogicE()
{
	CC_SAFE_RELEASE(_goalUnit);
}

void ShotLogicE::perform()
{
	if (_goalUnit == nullptr)
	{
		_end = true;
		return;
	}

	{
		auto radians = CC_DEGREES_TO_RADIANS((_unit->getPosition() - _goalUnit->getPosition()).getAngle() + 10);
		auto movex = cos(radians) * _speed;
		auto movey = sin(radians) * _speed;
		_end = true;
	}
	//#


	{
		auto radians = CC_DEGREES_TO_RADIANS((_unit->getPosition() - _goalUnit->getPosition()).getAngle() - 10);
		auto movex = cos(radians) * _speed;
		auto movey = sin(radians) * _speed;
	}
}

ShotLogicF::ShotLogicF(Unit* unit, int weaponResID, int posIndex, int shotCount, bool dir) 
:ShotLogic(unit, weaponResID, posIndex)
, _dir(dir)
, _shotCount(shotCount)
, _goalUnit(nullptr)
{

}

ShotLogicF::~ShotLogicF()
{
	CC_SAFE_RELEASE(_goalUnit);
}

void ShotLogicF::perform()
{
	if (_goalUnit == nullptr)
	{
		_end = true;
		return;
	}
	auto radians = CC_DEGREES_TO_RADIANS((_unit->getPosition() - _goalUnit->getPosition()).getAngle() + 10);
	auto movex = cos(radians) * _speed;
	auto movey = sin(radians) * _speed;

	//#

	for (int i = 1; i < _shotCount; i++)
	{
		auto radians = CC_DEGREES_TO_RADIANS((_unit->getPosition() - _goalUnit->getPosition()).getAngle() + i * 0.2589f);
		auto movex = cos(radians) * _speed;
		auto movey = sin(radians) * _speed;


		radians = CC_DEGREES_TO_RADIANS((_unit->getPosition() - _goalUnit->getPosition()).getAngle() - i * 0.2589f);
		movex = cos(radians) * _speed;
		movey = sin(radians) * _speed;
	}

	_end = true;
}

ShotLogicH::ShotLogicH(Unit* unit, int weaponResID, int posIndex) 
:ShotLogic(unit, weaponResID, posIndex)
{

}

void ShotLogicH::perform()
{

	for (int i = 0; i < 8; i++)
	{
		auto movex = _speed * COSVALUE8FP[i];
		auto movey = _speed * SINVALUE8FP[i];
		//#todo
	}
	_end = true;
}

ShotLogicI::ShotLogicI(Unit* unit, int weaponResID, int posIndex) 
:ShotLogic(unit, weaponResID, posIndex)
{

}

void ShotLogicI::perform()
{
	for (int i = 0; i < 8; i++)
	{
		auto movex = _speed * COSVALUE16FP[i];
		auto movey = _speed * SINVALUE16FP[i];
		//#todo
	}
	_end = true;
}

ShotLogicK::ShotLogicK(Unit* unit, int weaponResID, int posIndex) 
:ShotLogic(unit, weaponResID, posIndex)
{

}

void ShotLogicK::perform()
{
	_end = true;
}

ShotLogicQ::ShotLogicQ(Unit* unit, int weaponResID, int posIndex, int shotCount) 
:ShotLogic(unit, weaponResID, posIndex)
, _fireCount(0)
, _shotCount(shotCount)
, _goalUnit(nullptr)
, _delay(8)
{
	CC_SAFE_RETAIN(_goalUnit);
	_firetick = 8;
}

ShotLogicQ::~ShotLogicQ()
{
	CC_SAFE_RELEASE(_goalUnit);
}

void ShotLogicQ::perform()
{
	_end = true;
}

ShotLogicC::ShotLogicC(Unit* unit, int weaponResID, int posIndex)
:ShotLogic(unit, weaponResID, posIndex)
{

}

void ShotLogicC::perform()
{
	_end = true;
	auto movex = cos(CC_DEGREES_TO_RADIANS(45 * _unit->getShotDir())) * _speed;
	auto movey = sin(CC_DEGREES_TO_RADIANS(45 * _unit->getShotDir())) * _speed;
}
