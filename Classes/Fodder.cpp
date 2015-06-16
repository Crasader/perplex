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
#include "ShadowController.h"


Fodder::Fodder()
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
	if (foder && foder->init(gameScene, unitID, type, dir, campType))
	{
		foder->autorelease();
		return foder;
	}
	CC_SAFE_DELETE(foder);
	return nullptr;
}

bool Fodder::init(GameScene* gameScene, int unitID, int type, int dir, int campType)
{
	if (!Unit::init(gameScene, unitID, type, dir, campType))
	{
		return false;
	}
	_score = 10;
	_alive = true;
	_Model = AnimationLoader::getInstance().createAnimation("plane");
	if (_Model)
	{
		addChild(_Model);
		_id = kEnemyFodder;
		auto rect = _Model->getBoundingBox();
		setMoveRect(rect);
		setLocalZOrder(kZOrderSky);
		_shadowdata = ShadowSprite::create();
		_shadowdata->setShadowData(AnimationLoader::getInstance().createAnimation("plane"), this);
		_gameScene->addUnit(_shadowdata);
		CC_SAFE_RETAIN(_shadowdata);
		drawDebug(rect);
		return true;
	}
	return false;
}
