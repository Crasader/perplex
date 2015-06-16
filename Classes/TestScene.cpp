#include "TestScene.h"
#include "AnimationLoader.h"
#include "ShadowController.h"
#include "Explosion.h"
#include <vector>
#include "cocostudio\CCSkin.h"
#include <iostream>


using namespace cocostudio;

Scene* TestScene::createScene()
{
	auto scene = Scene::createWithPhysics();
	scene->getPhysicsWorld()->setDebugDrawMask(PhysicsWorld::DEBUGDRAW_ALL);

	auto layer = TestScene::create();

	scene->addChild(layer);
	return scene;
}

void TestScene::draw(Renderer *renderer, const Mat4& transform, uint32_t flags)
{

}

bool TestScene::init()
{
	if (!Layer::init())
	{
		return false;
	}
	auto colorLayer = LayerColor::create(Color4B::BLUE);
	addChild(colorLayer);
	/*	auto s = AnimationLoader::getInstance().createAnimation("plane");
	s->setColor(Color3B::BLACK);
	s->setOpacity(127);
	s->getAnimation()->play("run");
	s->setScale(0.75);
	s1 = AnimationLoader::getInstance().createAnimation("plane");
	s1->getAnimation()->play("idle");
	s1->setPosition(200, 200);
	auto shadow = ShadowSprite::create();
	shadow->setShadowData(s, s1);
	s1->setRotation(90);
	s1->setScale(0.5f);
	addChild(shadow);
	addChild(s1);
	auto b = s1->getBone("l");
	auto p = b->getDisplayRenderNode()->convertToWorldSpace(Vec2::ZERO);
	auto d = DrawNode::create();
	d->drawDot(p, 10, Color4F::RED);
	addChild(d);
	auto m = MoveTo::create(1, Vec2(0, 0));
	auto end = [&](){
		d->setPosition(p);
		runAction(Sequence::create(m, nullptr));
	};
	d->runAction(Sequence::create(m,nullptr));
	auto dict = s1->getBoneDic();
	for (auto element : dict)
	{
		auto bone = element.second;
		auto detector = bone->getColliderDetector();
		if (detector == nullptr) continue;
		auto bodylist = detector->getColliderBodyList();
		for (auto body : bodylist)
		{
			auto vertexList = body->getCalculatedVertexList();
			float minx, miny, maxx, maxy = 0;
			int i = 0;
			for (auto vertex : vertexList)
			{
				if (i == 0)
				{
					minx = maxx = vertex.x;
					miny = maxy = vertex.y;
				}
				else
				{
					minx = vertex.x < minx ? vertex.x : minx;
					miny = vertex.y < miny ? vertex.y : miny;
					maxx = vertex.x > maxx ? vertex.x : maxx;
					maxy = vertex.y > maxy ? vertex.y : maxy;
				}
				i++;
			}
			Rect temp(minx, miny, maxx - minx, maxy - miny);
			if (temp.intersectsRect(Rect::ZERO))
			{

			}
		}
	}
	
	auto lister = EventListenerTouchOneByOne::create();
	lister->onTouchBegan = [&](Touch* t, Event* e)
	{
		auto p = t->getLocation();
		auto ex = Explosion::create(p, nullptr);
		addChild(ex);
		return true;
	};
	this->getEventDispatcher()->addEventListenerWithSceneGraphPriority(lister, this);*/
	auto size = Director::getInstance()->getVisibleSize();
	auto oringin = Director::getInstance()->getVisibleOrigin();

	s1 = AnimationLoader::getInstance().createAnimation("tank");
	s1->getAnimation()->play("000");
	s1->setPosition(size.width / 2, size.height / 2);
	s1->getBone("t")->changeDisplayByIndex(1, true);
	addChild(s1);
	auto move = MoveBy::create(3, Vec2(100, 100));
	auto r = RotateTo::create(1, 45);

	/*s1->runAction(RepeatForever::create(Sequence::create(move, move->reverse(), r, r->reverse(), nullptr)));*/

	auto _shot1 = s1->getBone("r");
	auto skin = cocostudio::Skin::create();
	_shot1->addDisplay(skin, 0);
	_shot1->changeDisplayWithIndex(0, true);
	pos = _shot1->getDisplayRenderNode()->getPosition();


	drawnode = Sprite::create("b.png");
	drawnode->setPosition(pos);
	addChild(drawnode, 10, 100);

	auto listern = EventListenerTouchOneByOne::create();
	listern->onTouchBegan = [&](Touch* touch, Event* event)
	{
		auto p = touch->getLocation();
		auto p1 = s1->getPosition();
		auto angle = (p - p1).getAngle();
		auto t = s1->getBone("t");
		s1->setRotation(-CC_RADIANS_TO_DEGREES(angle) - s1->getRotation() - 90);
		return true;
	};

	listern->onTouchMoved = [&](Touch* touch, Event* event)
	{
		auto p = touch->getLocation();
		auto p1 = s1->getPosition();
		auto angle = (p - p1).getAngle();
		auto t = s1->getBone("t");
		t->setRotation(-CC_RADIANS_TO_DEGREES(angle) - s1->getRotation() - 90);
		pos = s1->getBone("r")->getDisplayRenderNode()->convertToWorldSpaceAR(Vec2::ZERO);
		/*auto p3 = pos.rotateByAngle(Vec2::ZERO, -CC_DEGREES_TO_RADIANS(t->getRotation() + s1->getRotation()));*/

		ve = (p - p1).getNormalized();

		drawnode->setPosition(pos);
		schedule(schedule_selector(TestScene::shot), 0.1f, CC_REPEAT_FOREVER, 0);
	};
	getEventDispatcher()->addEventListenerWithSceneGraphPriority(listern, this);
	return true;
}

void TestScene::shot(float dt)
{
	drawnode->setPosition(drawnode->getPosition() + ve * 100 *  dt);
}
VisibleRect::VisibleRect()
{
	auto size = Director::getInstance()->getVisibleSize();
	auto oringin = Director::getInstance()->getVisibleOrigin();
	_rect.origin = oringin;
	_rect.size = size;
}

void VisibleRect::crazyInit()
{
	auto size = Director::getInstance()->getVisibleSize();
	auto oringin = Director::getInstance()->getVisibleOrigin();
	_rect.origin = oringin;
	_rect.size = size;
}
