#include "tank.h"
#include "AnimationLoader.h"
#include "GameScene.h"
#include "ShadowController.h"
#include "consts.h"

const std::string SkinName[] = { "tank_turret0_0.png", "tank_turret0_1.png" };

Tank::Tank(GameScene* gameScene, int unitID, int type, int dir, int campType)
	:Unit(gameScene, unitID, type, dir, campType)
	, _turrent(nullptr)
	, rad(0.0f)
	, _shot1(nullptr)
	, _shot2(nullptr)
{

}

Tank::~Tank()
{

}


Tank* Tank::create(GameScene* gameScene, int unitID, int type, int dir, int campType)
{
	auto ret = new Tank(gameScene, unitID, type, dir, campType);
	if (ret && ret->init(gameScene, unitID, type, dir, campType))
	{
		ret->autorelease();
		return ret;
	}
	CC_SAFE_DELETE(ret);
	return nullptr;
}

bool Tank::init(GameScene* gameScene, int unitID, int type, int dir, int campType)
{
	if (!Unit::init(gameScene, unitID, type, dir, campType))
	{
		return false;
	}
	auto temp = AnimationLoader::getInstance().createAnimation("tank");
	if (temp)
	{
		_Model = temp;
		_turrent = temp->getBone("t");
		_shot1 = temp->getBone("r");
		/*auto skin = Skin::create("b.png");
		_shot1->addDisplay(skin, 0);
		_shot1->changeDisplayWithIndex(0, true);*/
		_shot2 = temp->getBone("l");
		/*auto skin = Skin::create(SkinName[1]);
		_turrent->addDisplay(skin, 1);*/
		_turrent->changeDisplayWithIndex(1, true);
		addChild(_Model);
		auto rect = _Model->getBoundingBox();
		setMoveRect(rect);
		setLocalZOrder(kZOrderLand);
		_shadowdata = ShadowSprite::create();
		auto shadwo = AnimationLoader::getInstance().createAnimation("tank");
		shadwo->getBone("t")->setVisible(false);
		_shadowdata->setShadowData(shadwo, this);
		_gameScene->addUnit(_shadowdata);
		_shadowdata->setOffset(Vec2(15, -15));
		CC_SAFE_RETAIN(_shadowdata);
		drawDebug(rect);
		schedule(CC_SCHEDULE_SELECTOR(Tank::followGoald) , 0.1f, CC_REPEAT_FOREVER, 0);
	}
	return true;
}



void Tank::followGoald(float dt)
{
	if (_gameScene->getPlayer() == nullptr || _turrent == nullptr)
	{
		log("player is null");
		return;
	}

	auto delt = _gameScene->getPlayer()->getPositionInCamera() - getPositionInCamera();
	auto r = CC_RADIANS_TO_DEGREES(delt.getAngle());
	rad = r;
	log("angle: %f", rad);
	auto time = 0.1f * abs(rad / 15);
	/*_turrent->setRotation(-rad -getRotation() - 90);*/
	_turrent->stopAllActions();

	_turrent->runAction(Sequence::create(RotateTo::create(time, -rad - getRotation() - 90),
		CallFunc::create([&](){
	}), nullptr));

}

Vec2 Tank::getTurrentVector()
{
	auto rad = CC_DEGREES_TO_RADIANS(-_turrent->getRotation() - 90 - getRotation());
	return Vec2(cosf(rad), sinf(rad));
}

void Tank::setTurrentRotation(float angle)
{
	auto a = rad - angle;
	_turrent->setRotation(a);
}

void Tank::setRotation(float rotation)
{
	Node::setRotation(rotation);
	//setTurrentRotation(rotation);
}

