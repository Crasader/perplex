#include "Fodder.h"
#include "GameControllers.h"
#include "Bullets.h"
#include "consts.h"
#include "SimpleAudioEngine.h"
#include "Effects.h"
#include "GameScene.h"
#include "GameLayer.h"
#include "Sprite3DEffect.h"
#include "Turret.h"
#include "RotateWithAction.h"
#include "FodderIdle.h"
#include "AnimationLoader.h"


Fodder::Fodder()
:action1(nullptr)
, isRoll(false)
{

}


Fodder::Fodder(GameScene* gameScene, int unitID, int type, int dir, int campType)
:Unit(gameScene, unitID, type, dir, campType)
{

}

Fodder::~Fodder()
{
	
}


Fodder* Fodder::create(GameScene* gameScene, int unitID, int type, int dir, int campType)
{
	auto foder = new Fodder(gameScene, unitID, type, dir, campType);
	if (foder && foder->init())
	{
		foder->autorelease();
		return foder;
	}
	CC_SAFE_DELETE(foder);
	return nullptr;
}

bool Fodder::init()
{
	if (!Unit::init())
	{
		return false;
	}
	_score = 10;
	_alive = true;
	_Model = AnimationLoader::getInstance().createAnimation("plane");
	if (_Model)
	{
		addChild(_Model);
		_type = kEnemyFodder;
		_HP = 10;
		_radius = 30;
		_shadowType = kZOrderSky;

		_curState = std::shared_ptr<MotionImpl>(new FodderIdle(this));
		_curState->execute();
		auto rect = _Model->getBoundingBox();
		setMoveRect(rect);

#if COCOS2D_DEBUG
		auto bound = DrawNode::create();
		bound->drawRect(rect.origin, Vec2(rect.getMaxX(), rect.getMaxY()), Color4F::RED);
		addChild(bound);
#endif
		return true;
	}
	return false;
}
void Fodder::reset()
{
	Unit::reset();
	_target = nullptr;
	_HP = 10;
}
void Fodder::setTurnRate(float turn)
{
	setMoveMode(moveMode::kTrunAndRoll);
	//setRotation3D(Vec3(fabsf(turn)*0.15, turn, 0));
	setRotation(fabsf(turn)*0.15);
	_turn = turn;
}
float Fodder::getTurnRate()
{
	return _turn;
}
void Fodder::move(float y, float dt)
{
	switch (_moveMode)
	{
	case moveMode::kTurn:
		forward(y, getTurnRate()*dt);
		break;
	case kTrunAndRoll:
		forward(y, getTurnRate()*dt);
		if (!isRoll) {
			isRoll = true;
			if (action1){
				_Model->runAction(action1);
			}
		}
		break;
	default:
		//setPosition(getPosition()+pos);
		forward(y);

	}

}
void Fodder::shoot(float dt)
{
	if (!GameLayer::isDie && _target->alive())
	{
		//get angle to player;
		float angle = (getPosition() - _target->getPosition()).getAngle();
		auto bullet = BulletController::spawnBullet(kEnemyBullet, getPosition(), Vec2(cosf(angle)*-500, sinf(angle)*-500));
		//auto bullet =BulletController::spawnBullet(kEnemyBullet, getPosition(), Vec2(0,-500));
		bullet->setRotation(-CC_RADIANS_TO_DEGREES(angle) - 90);
		//log("aaaaaaa");
	}
	else{
		//log("player is dead,hahahaha");
	}
}

