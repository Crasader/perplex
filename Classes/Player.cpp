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

#include "Player.h"
#include "Bullets.h"
#include "consts.h"
#include "GameScene.h"
#include "PublicApi.h"
#include "GameLayer.h"
#include "Effects.h"
#include "CameraExt.h"
#include "ShadowController.h"
#include "cocostudio/CCArmature.h"
#include "AnimationLoader.h"
#include "Weapon.h"
#include "shotlogic.h"
#include "shotlogicmanager.h"
#include "WeaponRes.h"
#include "weaponresmanager.h"

bool Player::init()
{
	_Model = AnimationLoader::getInstance().createAnimation("lordplane");
	if (_Model && Unit::init(_gameScene, _id, _type, _walkDir, _campType))
    {
        addChild(_Model);
        _alive = true;
		_shotTick = 0;
		_shotDelay = 0.5;
		auto rect = _Model->getBoundingBox();
		setMoveRect(rect);

		_shadowdata = ShadowSprite::create();
		_shadowdata->setShadowData(AnimationLoader::getInstance().createAnimation("lordplane"), this);
		_gameScene->addUnit(_shadowdata);
		CC_SAFE_RETAIN(_shadowdata);
#if COCOS2D_DEBUG
		auto bound = DrawNode::create();
		bound->drawRect(rect.origin, Vec2(rect.getMaxX(), rect.getMaxY()), Color4F::RED);
		addChild(bound);
#endif

        auto listener = EventListenerTouchOneByOne::create();
        listener->setSwallowTouches(true);
        
        listener->onTouchBegan = CC_CALLBACK_2(Player::onTouchBegan, this);
        listener->onTouchMoved = CC_CALLBACK_2(Player::onTouchMoved, this);
        listener->onTouchEnded = CC_CALLBACK_2(Player::onTouchEnded, this);
        
        _eventDispatcher->addEventListenerWithSceneGraphPriority(listener, this);
        //controller support ios and android
#if(CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID || CC_TARGET_PLATFORM == CC_PLATFORM_IOS)
        
        //need include base/CCEventListenerController.h and base/CCController.h文件
        auto controlListener = EventListenerController::create();
        
        controlListener->onKeyDown = CC_CALLBACK_3(Player::onKeyDown,this);
        
        controlListener->onKeyUp = CC_CALLBACK_3(Player::onKeyUp,this);
        
        controlListener->onAxisEvent = CC_CALLBACK_3(Player::onAxisEvent,this);
        
        
        _eventDispatcher->addEventListenerWithSceneGraphPriority(controlListener,this);
        
        Controller::startDiscoveryController();

        //init
        this->axisX = 0;
        this->axisY = 0;
        this->keyX = 0;
        this->keyY = 0;
#endif
        
        return true;
    }
    return false;
}

#if(CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID || CC_TARGET_PLATFORM == CC_PLATFORM_IOS)
void Player::onKeyDown(Controller *controller, int keyCode,Event *event)
{
    const auto & keyStatus = controller->getKeyStatus(keyCode);
    switch(keyCode)
    {
        case Controller::Key::BUTTON_DPAD_UP:
            keyY = keyStatus.value;
            break;
        case Controller::Key::BUTTON_DPAD_DOWN:
            keyY = -keyStatus.value;
            break;
        case Controller::Key::BUTTON_DPAD_LEFT:
            keyX = -keyStatus.value;
            break;
        case Controller::Key::BUTTON_DPAD_RIGHT:
            keyX = keyStatus.value;
            break;
    }
}

void Player::onKeyUp(Controller *controller, int keyCode,Event *event)
{
    switch(keyCode)
    {
        case Controller::Key::BUTTON_DPAD_UP:
        case Controller::Key::BUTTON_DPAD_DOWN:
            keyY = 0;
            break;
        case Controller::Key::BUTTON_DPAD_LEFT:
        case Controller::Key::BUTTON_DPAD_RIGHT:
            keyX = 0;
            break;
    }
}

void Player::onKeyRepeat()
{
    Vec2 prev = this->getPosition();
    Vec2 delta =Vec2(15*keyX,15*keyY);
    
    Vec2 shiftPosition = delta+prev;
    
	auto camera = _gameScene->getCamera();
	setPosition(shiftPosition.getClampPoint(camera->GetCameraOriginToGL(), camera->GetCameraOriginToGL()+Vec2(camera->getCameraSize().width, camera->getMapSize().height)));
}

void Player::onAxisEvent(Controller* controller, int keyCode, Event* event)
{
    const auto & keyStatus = controller->getKeyStatus(keyCode);
#if(CC_TARGET_PLATFORM == CC_TARGET_OS_MAC)
    switch(keyCode)
    {
        case Controller::Key::JOYSTICK_LEFT_X:
        case Controller::Key::JOYSTICK_RIGHT_X:
            this->axisX = keyStatus.value;
            break;
        case Controller::Key::JOYSTICK_LEFT_Y:
        case Controller::Key::JOYSTICK_RIGHT_Y:
            this->axisY = keyStatus.value;
            break;
    }
#else
    //ios
    switch(keyCode)
    {
//        case Controller::Key::JOYSTICK_LEFT_X:
        case Controller::Key::JOYSTICK_RIGHT_X:
            this->axisY = keyStatus.value;
            break;
//        case Controller::Key::JOYSTICK_LEFT_Y:
        case Controller::Key::JOYSTICK_RIGHT_Y:
            this->axisX = -keyStatus.value;
            break;
    }
#endif
}

void Player::onAxisRepeat()
{
    Vec2 prev = this->getPosition();
    Vec2 delta =Vec2(15*axisX,-15*axisY);
    
    Vec2 shiftPosition = delta+prev;
    
	auto camera = _gameScene->getCamera();
	setPosition(shiftPosition.getClampPoint(camera->GetCameraOriginToGL(), camera->GetCameraOriginToGL()+Vec2(camera->getCameraSize().width, camera->getMapSize().height)));
}
#endif

void Player::update(float dt)
{
    
#if(CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID || CC_TARGET_PLATFORM == CC_PLATFORM_IOS)
    this->onAxisRepeat();
    this->onKeyRepeat();
#endif
    
}

void Player::perform(float dt)
{
	if (_shadowdata)
	{
		_shadowdata->update(dt);
	}
	perfromWeapon(dt);

	switch (_state)
	{
	case UniteState::INVALID:
	case UniteState::ACTIVATE:
		fire(dt);
		break;
	case UniteState::EXPLOSION:
		Unit::processExplosion(dt);
		break;
	case UniteState::DEATH:
		Unit::processDie(dt);
		break;
	case UniteState::CASTOFF:
		if (_castoff)
		{
			_castoffStage++;
		}
		break;
	case UniteState::BEATTAK:
		break;
	case UniteState::RELIVE:
		_state = UniteState::ACTIVATE;
		setVisible(true);
		break;
	default:
		break;
	}
	processBump(dt);

}


void Player::perfromWeapon(float dt)
{
	for (auto weapon : _weapons)
	{
		if (weapon->getCastoffStage() > 0)
		{
			continue;
		}
		weapon->perform(dt);
	}

	for (int i = 0; i < _weapons.size(); i++)
	{
		if (_weapons.at(i)->getCastoffStage() > 0)
		{
			_weapons.eraseObject(_weapons.at(i));
			--i;
		}
	}
}

void Player::fire(float dt)
{
	if (_shotTick < _shotDelay)
	{
		_shotTick += dt;
		return;
	}
	_shotTick = 0;
	auto weaponres = _gameScene->getWeaponResManager()->getWeapResFromID(_weaponResID);
	auto speed = 200;
	auto pos = getPositionInCamera();
	auto vec = Vec2(0, 1);
	auto weapon = Weapon::create(_gameScene, weaponres, this, pos, vec, speed, 0, Ally);
	_weapons.pushBack(weapon);
}

void Player::deleteCastoffShot()
{
	auto shots = _shotLogicManager->getShotLogics();
	auto b = find_if(shots.begin(), shots.end(), [](shared_ptr<ShotLogic> a){return (a->isEnd()); });
	for (auto iter = b; iter != shots.end(); ++iter)
	{
		shots.erase(iter);
	}
}

void Player::processBump(float dt)
{
	for (auto shot : _shotLogicManager->getShotLogics())
	{
		if (shot->isEnd())
		{
			continue;
		}
		shot->perform(dt);
	}
	deleteCastoffShot();
}

bool Player::onTouchBegan(Touch *touch, Event *event)
{
    return true;
}

void Player::onTouchMoved(Touch *touch, Event *event)
{
    Vec2 prev = event->getCurrentTarget()->getPosition();
    Vec2 delta =touch->getDelta();
    
    Vec2 shiftPosition = delta+prev;
	
	auto camera = _gameScene->getCamera();
	setPosition(shiftPosition.getClampPoint(camera->GetCameraOriginToGL(), Vec2(camera->GetCameraOriginToGL().x + camera->getCameraSize().width, camera->getMapSize().height)));
}

void Player::onTouchEnded(Touch *touch, Event *event)
{
}

void Player::hideWarningLayer(Node* node)
{
    if(node)
        node->setVisible(false);
}

cocos2d::Vec2 Player::getPositionInCamera()
{
	return Vec2(getPositionX() + _gameScene->getCamera()->getX(), getPositionY() + _gameScene->getCamera()->getY());
}

Player::Player(GameScene* gameScene, int unitID, int type, int walkdir, int camptype)
:Unit(gameScene,unitID,type,walkdir,camptype)
{

}

Player::~Player()
{
}

Player* Player::create(GameScene* gameScene, int unitID, int type, int walkdir, int camptype)
{
	auto player = new Player(gameScene, unitID, type, walkdir, camptype);
	if (player && player->init())
	{
		player->autorelease();
		return player;
	}
	CC_SAFE_DELETE(player);
	return nullptr;
}
