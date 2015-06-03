#include "TestScene.h"
#include "AnimationLoader.h"
#include "ShadowController.h"
#include "Explosion.h"

using namespace cocostudio;

Scene* TestScene::createScene()
{
	auto scene = Scene::create();
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
	auto s = AnimationLoader::getInstance().createAnimation("plane");
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
	this->getEventDispatcher()->addEventListenerWithSceneGraphPriority(lister, this);
	return true;
}
