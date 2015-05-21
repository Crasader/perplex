#include "TestScene.h"
#include "AnimationLoader.h"
#include "cocostudio\CCArmature.h"

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

	auto loader = AnimationLoader::getInstance();
	auto s = loader.createAnimation("tank");
	s->getAnimation()->play("idle");
	s->setPosition(Vec2(100, 100));
	addChild(s);
	return true;
}
