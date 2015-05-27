#include "TestScene.h"
#include "AnimationLoader.h"
#include "cocostudio\CCArmature.h"
#include "ShadowController.h"

using namespace cocostudio;

Scene* TestScene::createScene()
{
	auto scene = Scene::create();
	auto layer = TestScene::create();
	scene->addChild(layer);
	return scene;
}

bool TestScene::init()
{
	if (!Layer::init())
	{
		return false;
	}
	auto colorLayer = LayerColor::create(Color4B::BLUE);
	addChild(colorLayer);
	auto s = AnimationLoader::getInstance().createAnimation("plane");
	s->setColor(Color3B::BLACK);
	s->setOpacity(127);
	s->getAnimation()->play("run");
	s->setScale(0.75);
	auto s1 = AnimationLoader::getInstance().createAnimation("plane");
	s1->getAnimation()->play("run");
	s1->setPosition(200, 200);
	auto shadow = ShadowSprite::create();
	shadow->setShadowData(s, s1);

	addChild(shadow);
	addChild(s1);
	return true;
}
