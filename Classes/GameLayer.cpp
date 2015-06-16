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

#include "GameLayer.h"
#include "Player.h"
#include "Enemies.h"
#include "PublicApi.h"
#include "GameControllers.h"
#include "consts.h"
#include "Bullets.h"
//#include "EnemyManager.h"
#include "Effects.h"
#include "GameEntity.h"
#include "SimpleAudioEngine.h"
#include "Effects.h"
#include "ParticleManager.h"
#include "CameraExt.h"
#include "LoadMap.h"
#include "ShadowController.h"
#include "MapManager.h"

USING_NS_CC;
using namespace std;

bool GameLayer::isDie=false;

bool GameLayer::init()
{    
	xScroll = 0.0f;
    speed = 60.0f;
	_elapsed = 20; //testing purpose, this was set to near boss timer
    _bossOut = false;

	ShadowController::init(this);

    _player = Player::create(nullptr, 0, 0, 0, 0);


    addChild(_player, kZOrderSky);
    EnemyController::init(this);

	auto mapmanager = new MapManager(nullptr);
	mapmanager->loadMapImg(0);
	if (!mapmanager->requestMap(1, true))
	{
		mapmanager->requestMap(1, true);
		mapmanager->createFloor();
		mapmanager->createUnits();
	}
	addChild(mapmanager->getFloor());

    //EffectManager::setLayer(this);

    this->schedule(schedule_selector(GameLayer::gameMaster) , 1.5f, -1, 2.0f);

    //BulletController::init(this);

    scheduleUpdate();
//     
//     _player->setPosition(Vec2(_camera->GetCameraCenter().x,-1000));
//     _player->runAction(Sequence::create(
//                                         DelayTime::create(0.75f),
// 					   EaseBackOut::create(MoveTo::create(1.7f, Vec2(_camera->GetCameraCenter().x, 100))),
// 					   Spawn::create(CallFunc::create(CC_CALLBACK_0(GameLayer::schedulePlayer, this)),
// 					   CallFunc::create([this](){
// 		_camera->StartCmdCamera(0, speed);
// 	}))
//                       ,nullptr));

	auto keyListener = EventListenerKeyboard::create();
	keyListener->onKeyPressed = CC_CALLBACK_2(GameLayer::onKeyPressed, this);
	_eventDispatcher->addEventListenerWithSceneGraphPriority(keyListener, this);
    return true;
}

void GameLayer::schedulePlayer()
{
    _player->scheduleUpdate();
}

//void GameLayer::gameMaster(float dt)
//{
//	if (isDie)
//	{
//		return;
//	}
//	_elapsed += dt;
//	int enemyCount = EnemyController::enemies.size();
//	//if(_elapsed < 10 && enemyCount < 5)
//	if (enemyCount < 5 && _elapsed < 60)
//	{
//		Vec2 random = Vec2(100 * CCRANDOM_MINUS1_1(), BOUND_RECT.size.height / 2 + 200);
//		for (int i = 0; i < 4; i++)
//		{
//			auto enemy1 = EnemyController::spawnEnemy(kEnemyFodder);
//			enemy1->setPosition(random + Vec2(60, 60)*(i + 1));
//			static_cast<Fodder*>(enemy1)->setMoveMode(moveMode::kDefault);
//			auto enemy2 = EnemyController::spawnEnemy(kEnemyFodder);
//			enemy2->setPosition(random + Vec2(-60, 60)*(i + 1));
//			static_cast<Fodder*>(enemy2)->setMoveMode(moveMode::kDefault);
//			enemy1->setRotation3D(Vec3::ZERO);
//			enemy2->setRotation3D(Vec3::ZERO);
//		}
//		auto leader = EnemyController::spawnEnemy(kEnemyFodderL);
//		leader->setPosition(random);
//		leader->setRotation3D(Vec3::ZERO);
//		static_cast<FodderLeader*>(leader)->setTarget(_player);
//		static_cast<FodderLeader*>(leader)->setMoveMode(moveMode::kDefault);
//	}
//	//else if(_elapsed < 20 && enemyCount <5)
//	if (_elapsed > 4 && enemyCount < 4 && _elapsed < 60)
//	{
//		Vec2 random = Vec2(-400, BOUND_RECT.size.height / 4 * CCRANDOM_MINUS1_1() + 350);
//		for (int i = 0; i < 3; i++)
//		{
//			float randomAngle = CCRANDOM_MINUS1_1() * 70;
//			auto enemy = EnemyController::spawnEnemy(kEnemyFodder);
//			enemy->setPosition(random + Vec2(60, 60)*(i + 1));
//			static_cast<Fodder*>(enemy)->setTurnRate(randomAngle*0.5);
//			enemy->setRotation(-randomAngle - 90);
//			auto enemy2 = EnemyController::spawnEnemy(kEnemyFodder);
//			enemy2->setPosition(random + Vec2(-60, 60)*(i + 1));
//			static_cast<Fodder*>(enemy2)->setTurnRate(randomAngle*0.5);
//			enemy2->setRotation(-randomAngle - 90);
//		}
//		auto leader = EnemyController::spawnEnemy(kEnemyFodderL);
//		leader->setPosition(random);
//		static_cast<FodderLeader*>(leader)->setTurnRate(45);
//		leader->setRotation(-45);
//		//enemy->runAction(EaseBackOut::create(MoveTo::create(2, _player->getPosition())));
//		static_cast<FodderLeader*>(leader)->setTarget(_player);
//		leader->schedule(schedule_selector(FodderLeader::shoot), CCRANDOM_0_1() * 1 + 1, 90, 0);
//
//	}
//	if (_elapsed > 10 && enemyCount < 4 && _elapsed < 60)
//	{
//		for (int q = 0; q < 2; q++)
//		{
//			//random if its from the top, left, or bottom
//			int direction = CCRANDOM_0_1() * 4;
//			float rX, rY;
//			switch (direction)
//			{
//			case 0://top
//				rY = BOUND_RECT.size.height / 2 + 200;
//				rX = ENEMY_BOUND_RECT.size.width*CCRANDOM_0_1();
//				break;
//			case 1://bottom
//				rY = -200;
//				rX = ENEMY_BOUND_RECT.size.width*CCRANDOM_0_1();
//				break;
//			case 2://left
//				rY = ENEMY_BOUND_RECT.size.height*CCRANDOM_0_1();
//				rX = ENEMY_BOUND_RECT.origin.x;
//				break;
//			case 3://right
//				rY = ENEMY_BOUND_RECT.size.height*CCRANDOM_0_1();
//				rX = ENEMY_BOUND_RECT.size.width;
//				break;
//			}
//			auto enemy = EnemyController::showCaseEnemy(kEnemyBigDude);
//			//enemy->setPosition(Vec2(100*CCRANDOM_MINUS1_1(), BOUND_RECT.size.height/2+200));
//			enemy->setPosition(rX, rY);
//			Vec2 targetPos = Vec2(BOUND_RECT.size.width / 3 * CCRANDOM_MINUS1_1(), BOUND_RECT.size.height / 3 * CCRANDOM_0_1());
//			enemy->setScale(2 * CCRANDOM_MINUS1_1() + 2);
//			float randomTime = CCRANDOM_0_1() * 1 + 1;
//			enemy->setRotation3D(Vec3(300, 0, -CC_RADIANS_TO_DEGREES((enemy->getPosition() - targetPos).getAngle()) + 90));
//			enemy->runAction(
//				Sequence::create(
//				Spawn::create(
//				EaseSineOut::create(MoveTo::create(randomTime, targetPos)),
//				EaseSineOut::create(ScaleTo::create(randomTime, 1)),//TODO: replace with move 3d when possible
//				EaseBackOut::create(RotateBy::create(randomTime + 0.2, Vec3(-300, 0, 0))),
//				nullptr
//				),
//				CallFunc::create(CC_CALLBACK_0(BigDude::showFinished, static_cast<BigDude*>(enemy))),
//				nullptr
//				));
//		}
//	}
//	if (_elapsed > 65 && !_bossOut)
//	{
//		//spawn boss
//		_bossOut = true;
//		auto boss = EnemyController::spawnEnemy(kEnemyBoss);
//		boss->setPosition(0, 800);
//		CocosDenshion::SimpleAudioEngine::getInstance()->stopBackgroundMusic();
//		// Music By Matthew Pable (http://www.matthewpablo.com/)
//		// Licensed under CC-BY 3.0 (http://creativecommons.org/licenses/by/3.0/)
//		CocosDenshion::SimpleAudioEngine::getInstance()->playBackgroundMusic("Orbital Colossus_0.mp3", true);
//	}
//}

void GameLayer::gameMaster(float dt)
{
    if(isDie)
    {
        return;
    }
    _elapsed+=dt;
    int enemyCount =EnemyController::enemies.size();
	
	if (enemyCount < 1)
	{
		Vec2 random = _camera->GetMapVisibleBound().origin + Vec2(0, 400 * CCRANDOM_MINUS1_1() + 10);

		
	}
	
}

void GameLayer::update(float dt)
{
    if (!isDie) {
		ShadowController::update(dt);
        GameController::update(dt, _player);
    }
    else
    {
        if (_player) {
			ShadowController::erase(_player);
            removeChild(_player);
            _player=NULL;
            removeChild(_streak);
            _streak=NULL;
            removeChild(_emissionPart);
            _emissionPart=NULL;
            stopAllActions();
            unscheduleAllCallbacks();
        }
    }
}

void GameLayer::removeBulletAndEnmeys(float dt)
{
    for(int i=EnemyController::enemies.size()-1;i>=0;i--)
    {
        EnemyController::erase(i);
    }
    for(int i=EnemyController::showCaseEnemies.size()-1;i>=0;i--)
    {
        //EnemyController::erase(i);
        EnemyController::showCaseEnemies.at(i)->removeFromParentAndCleanup(false);
        EnemyController::showCaseEnemies.erase(i);
    }
    for(int i=BulletController::bullets.size()-1;i>=0;i--)
    {
        BulletController::erase(i);
    }
}

void GameLayer::onKeyPressed(EventKeyboard::KeyCode keyCode, Event* event)
{
	switch (keyCode)
	{
	case EventKeyboard::KeyCode::KEY_1:
		if (!Director::getInstance()->isPaused())
		{
			Director::getInstance()->pause();
		}
		else {
			Director::getInstance()->resume();

		}
		break;
	case EventKeyboard::KeyCode::KEY_A:
		addEnemy(0);
		break;
	case EventKeyboard::KeyCode::KEY_C:
		if (EnemyController::enemies.size() > 0)
		{
			EnemyController::erase(0);
		}
		break;
	case EventKeyboard::KeyCode::KEY_X:
		removeBulletAndEnmeys(0);
		break;
	default:
		Director::getInstance()->resume();
		break;
	}
}

void GameLayer::addEnemy(int i)
{
	Vec2 random = _camera->GetMapVisibleBound().origin + Vec2(0, 400 * CCRANDOM_MINUS1_1() + 10);
	auto enemy1 = EnemyController::spawnEnemy(kEnemyTank);
	enemy1->setPosition(random + Vec2(160, 60)*(i + 1));
	/*enemy2->schedule(schedule_selector(Tank::shoot), 0, kRepeatForever, 0);*/
	/*static_cast<Tank*>(enemy1)->setTarget(_player);*/
}

MapManager* GameLayer::getManManger()
{
	//TODO

	return nullptr;
}

void GameLayer::setMapSwitch(int activeMapID, int eventID)
{
	//TODO
}

