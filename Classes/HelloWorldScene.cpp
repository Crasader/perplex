#include "HelloWorldScene.h"
#include "cocostudio/CocoStudio.h"
#include "ui/CocosGUI.h"
#include "GameLayer.h"
#include "Configure.h"
#include "GameOverLayer.h"

USING_NS_CC;

using namespace cocostudio::timeline;

Scene* HelloWorld::createScene()
{
    // 'scene' is an autorelease object
    auto scene = Scene::create();
    
    // 'layer' is an autorelease object
    auto layer = HelloWorld::create();
	layer->setTag(LAYER_TAG_HELLOWORLD);

    // add layer as a child to scene
    scene->addChild(layer, 2);

	//add waring layer;
	/*auto warningLayer = LayerColor::create(Color4B(255, 0, 0, 60));
	warningLayer->setOpacity(255);
	warningLayer->setTag(LAYER_TAG_WARNING);
	scene->addChild(warningLayer, 7);*/
    // return the scene
    return scene;
}

// on "init" you need to initialize your instance
bool HelloWorld::init()
{
    //////////////////////////////
    // 1. super init first
    if ( !Layer::init() )
    {
        return false;
    }
    //GameScene
	auto visibleSize = Director::getInstance()->getVisibleSize();
	auto origin = Director::getInstance()->getVisibleOrigin();
	auto sb = GameLayer::create();
	sb->setTag(LAYER_TAG_GAME);
	sb->setPosition(origin);
	addChild(sb);
	
#ifdef COCOS2D_DEBUG
	auto d = DrawNode::create();
	d->drawRect(Vec2(0, 0), Vec2(visibleSize.width, visibleSize.height), Color4F::RED);
	d->setPosition(origin);
	addChild(d);
#endif // 

	//
	NotificationCenter::getInstance()->destroyInstance();
	NotificationCenter::getInstance()->addObserver(this, callfuncO_selector(HelloWorld::ShowGameOver), "ShowGameOver", nullptr);

	return true;
}

void HelloWorld::increaseScore(float dt)
{
	this->score++;
	std::stringstream ss;
	std::string str;
	ss << score;
	ss >> str;
	const char *p = str.c_str();
}

void HelloWorld::ShowGameOver(Ref* pObj)
{
	auto gameoverlayer = GameOverLayer::create(score);
	addChild(gameoverlayer, 10);
}

HelloWorld::HelloWorld()
:score(0)
, hpView(nullptr)
, scoreLabel(nullptr)
{

}
