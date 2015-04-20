/****************************************************************************
 Copyright (c) 2014 Chukong Technologies Inc.

 http://github.com/chukong/EarthWarrior3D

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 ****************************************************************************/

#include "MainMenuScene.h"
#include "LoadingScene.h"
#include "PublicApi.h"
#include "Airplane.h"
#include "GameLayer.h"
#include "HelloWorldScene.h"
#include "LicenseLayer.h"
USING_NS_CC;

Scene* MainMenuScene::createScene()
{
    // 'scene' is an autorelease object
    auto scene = Scene::create();
    
    // 'layer' is an autorelease object
    auto layer = MainMenuScene::create();
    
    // add layer as a child to scene
    scene->addChild(layer);
    
    // return the scene
    return scene;
}

// on "init" you need to initialize your instance
bool MainMenuScene::init()
{
    if ( !Layer::init() )
    {
        return false;
    }
	
	pRate = 3.1415926/2;

    //CocosDenshion::SimpleAudioEngine::getInstance()->playBackgroundMusic("Star_Chaser.mp3");
    
    
    Size visibleSize = Director::getInstance()->getVisibleSize();
    auto origin = Director::getInstance()->getVisibleOrigin();
    Size winSize = Director::getInstance()->getWinSize();

    
    //************* adds background ***********
	//startgame_callback();
    //************* adds start game ***********
    //auto start_normal=Sprite::createWithSpriteFrameName("start_game.png");
    //auto start_pressed=Sprite::createWithSpriteFrameName("start_game.png");
    //startgame_item = MenuItemSprite::create(start_normal, start_pressed, CC_CALLBACK_1(MainMenuScene::startgame, this));
    //startgame_item->setPosition(visibleSize.width/2,200);
    //startgame_item->setScale(1.3);
    
    ////************* Menu ******************
    //auto menu = Menu::create(startgame_item, NULL);
    //menu->setPosition(origin);
    //this->addChild(menu,3);
    
    //support controller
#if(CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID || CC_TARGET_PLATFORM == CC_PLATFORM_IOS)
    auto controllListener = EventListenerController::create();
    controllListener->onKeyUp = CC_CALLBACK_3(MainMenuScene::onKeyUp, this);
    controllListener->onConnected = CC_CALLBACK_2(MainMenuScene::onConnected,this);
    _eventDispatcher->addEventListenerWithSceneGraphPriority(controllListener, this);
    Controller::startDiscoveryController();
#endif
    
    return true;
}

//controller connect
void MainMenuScene::onConnected(Controller* controller, Event* event)
{
    auto size = Director::getInstance()->getWinSize();
    auto label = Label::createWithTTF("Controller Connected!","fonts/Marker Felt.ttf", 40);
    label->setPosition(Point(size.width/2,size.height*2/3));
    label->runAction(Sequence::create(FadeIn::create(1.0f),FadeOut::create(1.0f),NULL));
    
    this->addChild(label);
}

void MainMenuScene::onKeyUp(Controller *controller, int keyCode,Event *event)
{
    if(this->getChildByTag(20) != nullptr)
    {
        this->getChildByTag(20)->removeFromParent();
    }
    switch (keyCode)
    {
        case Controller::Key::BUTTON_START:
            this->startgame(this);
            break;
    }
}

void MainMenuScene::update(float dt){
  
}

void MainMenuScene::startgame(Ref* sender)
{
    startgame_item->runAction(Sequence::create(ScaleTo::create(0.1f, 1.4f),
                                                ScaleTo::create(0.1f, 1.2f),
                                                ScaleTo::create(0.1f, 1.3f),
                                               CallFunc::create(CC_CALLBACK_0(MainMenuScene::startgame_callback,this)),NULL));
}

void MainMenuScene::startgame_callback()
{
    //CocosDenshion::SimpleAudioEngine::getInstance()->stopBackgroundMusic();
    GameLayer::isDie=false;
    //auto scene = (LoadingScene::audioloaded) ? HelloWorld::createScene() :LoadingScene::createScene();
	auto scene = HelloWorld::createScene();
	Director::getInstance()->replaceScene(scene);
}

void MainMenuScene::onEnter()
{
	startgame_callback();
}


