#include "Turret.h"
#include "consts.h"
#include "GameControllers.h"
#include "GameLayer.h"
#include "Bullets.h"

TurretOne::TurretOne()
:Unit(kEnemyTurret, kShadowLand, 10, true, 10, 20)
, _aiming(false)
, _speed(80.0f)
, _oldAngle(0.0f)
, _newAngle(0.0f)
, _RoateTimeDelta(0)
, _shootTimes(1)
, _shootDelta(1.0f)
{

}

TurretOne::~TurretOne()
{

}

bool TurretOne::init()
{
	auto temp = Sprite::create("tank_turret0_0.png");
	if (temp)
	{
		_Model = temp;

		setMoveSize(temp->getTextureRect().size);
		addChild(_Model);
		return true;
	}
	return false;
}

TurretOne* TurretOne::create(const char* filename)
{
	auto ret = new TurretOne();
	if (ret && ret->init(filename))
	{
		ret->autorelease();
		return ret;
	}
	else
	{
		CC_SAFE_DELETE(ret);
		return nullptr;
	}
}

bool TurretOne::init(const char* filename)
{
	auto temp = Sprite::create(filename);
	if (temp)
	{
		_Model = temp;

		setMoveSize(temp->getTextureRect().size);
		addChild(_Model);
		/*this->scheduleUpdate();*/
		return true;
	}
	return false;
}

void TurretOne::move(float y, float dt)
{

}

float TurretOne::getTargetAngle()
{
	CC_ASSERT(_target);
	auto p = _target->getPosition();
	auto self = this->getParent()->getPosition();
	auto moveVector = self - p;
	auto moveAngle = moveVector.getAngle();
	auto parentAngle = this->getParent()->getRotation();
	return CC_RADIANS_TO_DEGREES(-1 * moveAngle) + 90.0f - parentAngle;
}

void TurretOne::startRotate(float dt)
{
	if (!_aiming)
	{
		_aiming = true;
		_oldAngle = getRotation();
		_newAngle = getTargetAngle();
		if (_newAngle > 180)
		{
			_newAngle -= 360;
		}
		if (_newAngle < -180)
		{
			_newAngle += 360;
		}
		auto delta = _newAngle - _oldAngle;
		if (delta > 180)
		{
			delta -= 360;
		}
		else if (delta < -180)
		{
			delta += 360;
		}
		_RoateTimeDelta = abs(delta / _speed);
		rotating(_RoateTimeDelta);
	}
}

void TurretOne::rotating(float dt)
{
	this->runAction(Sequence::create(
		RotateTo::create(_RoateTimeDelta, _newAngle),
		CallFunc::create(CC_CALLBACK_0(TurretOne::rotated, this)),
		nullptr));
}

void TurretOne::rotated()
{
	if (!GameLayer::isDie && _target != nullptr && _target->alive())
	{
		startShoot(0);
	}
}

void TurretOne::update(float dt)
{
	Unit::update(dt);
	startRotate(dt);
}

void TurretOne::startShoot(float dt)
{
	this->runAction(
		Sequence::create(
		Repeat::create(Sequence::create(DelayTime::create(_shootDelta),
		CallFunc::create(CC_CALLBACK_0(TurretOne::shooting, this)),
		nullptr), _shootTimes),
		CallFunc::create(CC_CALLBACK_0(TurretOne::shot, this)),
		nullptr));
}

void TurretOne::shooting()
{
	auto bullet1 = BulletController::spawnBullet(kEnemyBullet, getTurretPosition(Point(-5, 0)), getTurretVector());
	bullet1->setRotation(this->getRotation());
}

void TurretOne::shot()
{
	_aiming = false;
}

Vec2 TurretOne::getTurretPosition(Vec2 pos)
{
	Vec2 offset = this->getParent()->getPosition();
	float angle = CC_DEGREES_TO_RADIANS(-this->getRotation() + 90 - this->getParent()->getRotation());
	offset.x += (cosf(angle)* pos.x);
	offset.y += (sinf(angle)*(- getMoveRect().size.height / 2));
	return offset;
}

Vec2 TurretOne::getTurretVector()
{
	float angle = CC_DEGREES_TO_RADIANS(-this->getRotation() + 90 - this->getParent()->getRotation());
	return Vec2(cosf(angle)*-500, sinf(angle)*-500);
}

void TurretOne::startAction()
{
	if (!this->isScheduled(schedule_selector(TurretOne::update)))
	{
		this->scheduleUpdate();
	}
}

void TurretOne::stopAction()
{
	this->stopAllActions();
}

TurretTwo::TurretTwo()
:TurretOne()
{

}

TurretTwo::~TurretTwo()
{

}

bool TurretTwo::init()
{
	if (TurretOne::init("tank_turret0_1.png"))
	{
		return true;
	}
	return false;
}

void TurretTwo::shooting()
{
	auto bullet1 = BulletController::spawnBullet(kEnemyBullet, getTurretPosition(Point(-15, 0)), getTurretVector());
	bullet1->setRotation(-this->getRotation() - 180 - this->getParent()->getRotation());
	auto bullet2 = BulletController::spawnBullet(kEnemyBullet, getTurretPosition(Point(15, 0)), getTurretVector());
	bullet2->setRotation(-this->getRotation() - 180 - this->getParent()->getRotation());
}

