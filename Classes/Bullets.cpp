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

#include "Bullets.h"
#include "consts.h"
#include "GameLayer.h"
#include "GameEntity.h"
#include "GameControllers.h"
#include "Enemies.h"
#include "ParticleManager.h"
#include "Sprite3DEffect.h"
#include "WeaponRes.h"
#include "GameScene.h"
#include "CameraExt.h"
#include "UnitManager.h"
#include "Explosion.h"

Bullet::Bullet()
	:GameEntity()
	, _hitRect(Rect::ZERO)
{

}

Bullet* Bullet::create(GameScene* gameScene, std::shared_ptr<WeaponRes> weaponRes, Vec2 pos, Vec2 move, int speed)
{
	auto bullet = new Bullet();
	if (bullet && bullet->init(gameScene, weaponRes, pos, move, speed))
	{
		bullet->autorelease();
		return bullet;
	}
	CC_SAFE_DELETE(bullet);
	return nullptr;
}


bool Bullet::init(GameScene* gameScene, std::shared_ptr<WeaponRes> weaponRes, Vec2 pos, Vec2 move, int speed)
{
	auto s = Sprite::create("b.png");
	_Model = s;
	if (_Model && GameEntity::init())
	{
		_hitRect = s->getBoundingBox();
		_gameScene = gameScene;
		_weaponRes = weaponRes;
		setPosition(pos);
		_vector = move;
		_id = kEnemyBullet;
		_owner = kEnemy;
		_power = 10;
		_liveTick = 4;
		_castoff = false;
		_castoffStage = 0;
		_bump = false;
		_speed = speed;
		addChild(_Model);
#if COCOS2D_DEBUG
		auto bound = DrawNode::create();
		bound->drawRect(_hitRect.origin, Vec2(_hitRect.getMaxX(), _hitRect.getMaxY()), Color4F::RED);
		addChild(bound);
#endif

		return true;
	}
	return false;
}

bool Bullet::init()
{
    _Model = Sprite::create("bullets.png", Rect(5,8,24,32));
    //_Model = ParticleSystemQuad::create("missileFlare.plist");
    if(_Model)
    {
        addChild(_Model);
        _id = kEnemyBullet;
        _owner = kEnemy;
        _damage = 0;
        
        auto part_frame=SpriteFrameCache::getInstance()->getSpriteFrameByName("toonFlare.png");
        ValueMap vm=ParticleManager::getInstance()->GetPlistData("missileFlare");
        ParticleSystemQuad *p = ParticleSystemQuad::create(vm);
        //p->setDisplayFrame(part_frame);
        p->setTextureWithRect(part_frame->getTexture(), part_frame->getRect());
        //p->setEndColor(Color4F(1,0,0,1));
        //p->setStartColor(Color4F(1,0,0,1));
        p->setPositionType(tPositionType::GROUPED);
        p->setScale(2.5);
        p->setTotalParticles(2);
        _Model->addChild(p,-1);
        p->setPosition(Vec2(_Model->getContentSize()/2));
        setScale(1.5);
        //static_cast<Sprite*>(_Model)->setFlippedY(true);
        return true;
    }
    return false;
}
bool PlayerBullet::init()
{
	//_Model = Sprite::create("bullets.png", Rect(54, 57, 36, 67));
	_Model = Sprite::create("001.png");
    if(_Model)
    {
		//_Model->setScale(0.5);
        addChild(_Model);
        _id = kPlayerBullet;
        _owner = kPlayer;
        _damage = 2;
        return true;
    }
    return false;
}

void Bullet::setVector(Vec2 vec)
{
    _vector = vec;
}

Vec2 Bullet::getVector()
{
    return _vector;
}
void Bullet::reset()
{
    setRotation(0);
}


void Bullet::setBump()
{
	_bump = true;
}

bool Bullet::isCastoff()
{
	return _castoff;
}


bool Bullet::isBump()
{
	return _bump;
}


Rect Bullet::getHitRect()
{
	return Rect(_hitRect.origin.x + getPositionX(), _hitRect.origin.y +getPositionY(), _hitRect.size.width, _hitRect.size.height);
}

int Bullet::getPower()
{
	return _power;
}


void Bullet::perform(float dt)
{
	if (_castoff)
	{
		_castoffStage++;
		return;
	}
	if (_bump)
	{
		auto callback = [&](){
		};
		_gameScene->getUnitManager().createExplode(this, 0, this->getPosition(), callback);
		_castoff = true;
		return;
	}
	setPosition(getPosition() + _vector * _speed * dt);
	cocos2d::Rect camerRect(
		_gameScene->getCamera()->getX() - 40,
		_gameScene->getCamera()->getY() - 40,
		_gameScene->getSceneWidth() + 80,
		_gameScene->getSceneHeight() + 80);
	if (_liveTick < 0 && !camerRect.containsPoint(getPosition()))
	{
		_castoff = true;
	}
	_liveTick -= dt;
}

bool Missile::init()
{
    //_Model = EffectSprite3D::createFromObjFileAndTexture("daodanv001.c3b", "daodan_32.png");
	_Model = Sprite::create("n3.png");
    if(_Model)
    {
		_accel = 15;
		_turnRate = 180;
		_yRotSpeed = 1400;
		_yRotation = 0;
		_left = false;
		_velocity = 0;

        addChild(_Model);
        _id = kPlayerMissiles;
        _owner = kPlayer;
		_Model->setRotation(180);
        _Model->setScale(0.3f);
        //_Model->setRotation3D(Vec3(0,0,180));
        _power = 20;
		_target = nullptr;
        //GameEntity::UseOutlineEffect(static_cast<Sprite3D*>(_Model), 0.01, Color3B(0,0,0));
        _left = (CCRANDOM_MINUS1_1()>0);
        if(_left)
            _yRotSpeed *= -1;
        
        
        // missile effects

        auto part2_frame=SpriteFrameCache::getInstance()->getSpriteFrameByName("toonSmoke.png");
        ValueMap vm2=ParticleManager::getInstance()->GetPlistData("emission");
        auto part2 = ParticleSystemQuad::create(vm2);
        //part2->setDisplayFrame(part2_frame);
        part2->setTextureWithRect(part2_frame->getTexture(), part2_frame->getRect());
        addChild(part2,1);
        part2->setPosition(0,-34);
        part2->setPositionType(tPositionType::GROUPED);
        //part2->setScale(2.5);
        
        
        auto part1_frame=SpriteFrameCache::getInstance()->getSpriteFrameByName("toonFlare.png");
        ValueMap vm1=ParticleManager::getInstance()->GetPlistData("missileFlare");
        auto part1 = ParticleSystemQuad::create(vm1);
        //part1->setDisplayFrame(part1_frame);
        part1->setTextureWithRect(part1_frame->getTexture(), part1_frame->getRect());
        addChild(part1,2);
        part1->setPosition(0,-30);
        part1->setPositionType(tPositionType::GROUPED);
        part1->setScale(2.5);
        return true;
    }
    return false;
}

void Missile::update(float dt)
{
    if(!_target)
    {
        //setTarget(static_cast<GameLayer*>(getParent())->_testDummy);//very hacky
        setTarget(static_cast<GameEntity*>(EnemyController::enemies.getRandomObject()));
    }
    if(_target){
        //turn towards the target
        float angle = -CC_RADIANS_TO_DEGREES((getPosition() - _target->getPosition()).getAngle());
        if(fabsf(angle-90)>70)
        {
            //too much angle to track, get another target instead
            _target = nullptr;
        }
        float curRot = getRotation();
        float angleDif = std::min(std::max((angle-90) - curRot, -_turnRate*dt), _turnRate*dt);
        
        float f = curRot + angleDif;
        setRotation(f);
        setPosition(getPosition()+Vec2(sinf(CC_DEGREES_TO_RADIANS(f))*_velocity,cosf(CC_DEGREES_TO_RADIANS(f))*_velocity) + _vector*dt);
        _vector = _vector * (1-dt);
        
    }
    else{
        float f = getRotation();
        setRotation(f);
        setPosition(getPosition()+Vec2(sinf(CC_DEGREES_TO_RADIANS(f))*_velocity,cosf(CC_DEGREES_TO_RADIANS(f))*_velocity) + _vector*dt);
        _vector = _vector * (1-dt*0.5);
    }
    // missiles need to rotate
    _yRotation += _yRotSpeed*dt;
   /* _Model->setRotation3D(Vec3(0,_yRotation, 0));*/
    
    
    
    
    _velocity += _accel*dt;
}

void Missile::reset()
{
    setTarget(nullptr);
    _velocity = 0;
}

