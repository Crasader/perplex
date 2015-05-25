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

#include "GameControllers.h"
#include "GameEntity.h"
#include "Bullets.h"
#include "consts.h"
#include "Unit.h"
#include "Effects.h"
#include "SimpleAudioEngine.h"
#include "Enemies.h"
#include "Player.h"
#include "GameScene.h"
#include "CameraExt.h"
#include "ShadowController.h"
#include "Turret.h"

Node* BulletController::_bulletLayer = nullptr;
bool BulletController::_inited = false;
Vector<Bullet*> BulletController::bullets;
Vector<Missile*> BulletController::_missilePool;


void BulletController::reset(){
    _inited = false;
    _bulletLayer = nullptr;
    bullets.clear();
}
bool BulletController::init(Node *bulletLayer){
    if(bulletLayer)
    {
        reset();
        _bulletLayer = bulletLayer;
        _inited = false;
        return true;
    }
    return false;
}
Bullet* BulletController::spawnBullet(int type, Vec2 pos, Vec2 vec)
{
    Bullet *bullet = nullptr;
    switch(type)
    {
        case kPlayerBullet:
            bullet = PlayerBullet::create();
            //bullet->retain();
            //bullet->setType(kPlayerBullet);
            break;
        case kPlayerMissiles:
            if(!_missilePool.empty())
            {
                // if the pool is not empty, we don't need to create, just return that, and reset its data
                bullet = _missilePool.back();
                //bullet->retain();
                _missilePool.popBack();
                
                //bullet->reset();
            }
            else
            {
                bullet = Missile::create();
                bullet->retain();
            }
            //bullet->setType
            break;
        case kEnemyBullet:
            bullet = Bullet::create();
            bullet->setType(kEnemyBullet);
            break;
    }
    if(bullet)
    {
        bullets.pushBack(bullet);
		_bulletLayer->addChild(bullet, kZOrderBullet);
        //bullet->release();
        bullet->setPosition(pos);
        bullet->setVector(vec);
        return bullet;
    }
    return nullptr;
}
void BulletController::erase(Bullet* b)
{
    if(b->getType() == kPlayerMissiles)
    {
        _missilePool.pushBack(static_cast<Missile*>(b));
        bullets.eraseObject(b);
        b->removeFromParentAndCleanup(false);
        b->reset();
    }
    else
    {
        b->removeFromParentAndCleanup(true);
        bullets.eraseObject(b);
    }
}
void BulletController::erase(int i)
{
    auto b = bullets.at(i);
    if(b->getType() == kPlayerMissiles)
    {
        _missilePool.pushBack(static_cast<Missile*>(b));
        bullets.erase(i);
        b->removeFromParentAndCleanup(false);
        b->reset();
    }
    else
    {
        bullets.erase(i);
        b->removeFromParentAndCleanup(true);
    }
}


Node* EnemyController::_enemyLayer = nullptr;
bool EnemyController::_inited = false;
Vector<Unit*> EnemyController::enemies;
Vector<Unit*> EnemyController::showCaseEnemies;
Vector<Fodder*> EnemyController::_fodderPool;
Vector<FodderLeader*> EnemyController::_fodderLPool;
Vector<BigDude*> EnemyController::_bigDudePool;
Vector<Boss*> EnemyController::_bossPool;
Vector<Tank*> EnemyController::_tankPool;
Vector<TurretOne*> EnemyController::_turretPool;

//const float EnemyController::EnemyMoveDist = -400;
const float EnemyController::EnemyMoveDist = -100;

bool EnemyController::init(Node* enemyLayer)
{
    _enemyLayer = enemyLayer;
    _inited = true;
    return true;
}

void EnemyController::reset()
{
    _inited = false;
    _enemyLayer = nullptr;
    enemies.clear();
}
Unit* EnemyController::createOrGet(int type)
{
    Unit *enemy = nullptr;
    switch(type)
    {
        case kEnemyFodder:
            if(!_fodderPool.empty())
            {
				auto size = _fodderPool.size();
                enemy = _fodderPool.back();
                _fodderPool.popBack();
            }
            else
            {
                enemy = Fodder::create();
                enemy->retain();
            }
            break;
        case kEnemyFodderL:
            if(!_fodderLPool.empty())
            {
                enemy = _fodderLPool.back();
                _fodderLPool.popBack();
            }
            else
            {
                enemy = FodderLeader::create();
                enemy->retain();
            }
            break;
        case kEnemyBigDude:
            if(!_bigDudePool.empty())
            {
                enemy = _bigDudePool.back();
                _bigDudePool.popBack();
            }
            else
            {
                enemy = BigDude::create();
                enemy->retain();
            }
            break;
		case kEnemyBoss:
			if (!_bossPool.empty())
			{
				enemy = _bossPool.back();
				_bossPool.popBack();
			}
			else
			{
				enemy = Boss::create();
				enemy->retain();
			}
			break;
		case kEnemyTank:
			if (!_tankPool.empty())
			{
				enemy = _tankPool.back();
				_tankPool.popBack();
			}
			else
			{
				enemy = Tank::create();
				enemy->retain();
			}
			break;
		case kEnemyTurret:
			if (!_turretPool.empty())
			{
				enemy = _turretPool.back();
				_turretPool.popBack();
			}
			else
			{
				enemy = TurretOne::create();
				enemy->retain();
			}
			break;
    }
    return enemy;
}

Unit* EnemyController::spawnEnemy(int type)
{
    CC_ASSERT(_enemyLayer);
    Unit *enemy = createOrGet(type);
    if(enemy)
    {
        enemies.pushBack(enemy);
        _enemyLayer->addChild(enemy, enemy->getShadowType() == kShadowLand? kZOrderLand : kZOrderSky);
		//ShadowController::createShadow(enemy, Point(10, -10));
        return enemy;
    }
    return nullptr;
}
Unit* EnemyController::showCaseEnemy(int type)
{
    CC_ASSERT(_enemyLayer);
    Unit *enemy = createOrGet(type);
    if(enemy)
    {
        showCaseEnemies.pushBack(enemy);
		_enemyLayer->addChild(enemy, type == kShadowLand ? kZOrderLand : kZOrderSky);
        return enemy;
    }
    return nullptr;
}
void EnemyController::erase(int i)
{
	if (i >= 0 && i < enemies.size())
	{
		auto e = enemies.at(i);
		int type = e->getType();
		switch (type)
		{
		case kEnemyFodder:
			_fodderPool.pushBack(static_cast<Fodder*>(e));
			break;
		case kEnemyFodderL:
			_fodderLPool.pushBack(static_cast<FodderLeader*>(e));
			break;
		case kEnemyBigDude:
			_bigDudePool.pushBack(static_cast<BigDude*>(e));
			break;
		case kEnemyBoss:
			_bossPool.pushBack(static_cast<Boss*>(e));
			break;
		case kEnemyTank:
			_tankPool.pushBack(static_cast<Tank*>(e));
			break;
		case kEnemyTurret:
			_turretPool.pushBack(static_cast<TurretOne*>(e));
			break;
		}
		//ShadowController::erase(e);
		enemies.erase(i);
		e->removeFromParentAndCleanup(false);
		e->reset();
	}
  
}


void GameController::update(float dt, Player* player)
{
    Vec2 temp;
	Bullet* b = nullptr;
	auto list = BulletController::bullets;
    float enemyMoveDist =EnemyController::EnemyMoveDist*dt;
	for (int i = list.size() - 1; i >= 0; i--)
    {
		b = list.at(i);
        temp =b->getPosition();
        if(true)
        {
            if(b->getOwner() == kPlayer)
            {
                //********* Enemy Loop **********
                for(int j = EnemyController::enemies.size()-1; j >= 0; j--)
                {
                    auto e = EnemyController::enemies.at(j);
                    if(b->getPosition().getDistance(e->getPosition()) <(b->getRadius() + e->getRadius()))
                    {
                        //collision happened
                        bool dead =  e->hurt(b->getDamage());
                        if(!dead)
                        {
                            switch(b->getType())
                            {
                                case kPlayerMissiles:
									EffectManager::createExplosion(b->getPosition());

                                    CocosDenshion::SimpleAudioEngine::getInstance()->playEffect("boom2.mp3");
                                    break;
                                default:
                                    CocosDenshion::SimpleAudioEngine::getInstance()->playEffect("hit.mp3");
                                    break;
                            }
                        }
                        BulletController::erase(i);
                    }
                    
                }
                //*********** Enemy Loop ***************
                if(b->getType() == kPlayerMissiles)
                {
                    b->update(dt);
                }
                else{
                    b->setPosition(temp+(b->getVector()*dt));
                }
            }
            // loop all enemy bullets against player
            else if(b->getPosition().getDistance(player->getPosition()) < b->getRadius()+player->getRadius())
            {
                player->hurt(b->getDamage());
                BulletController::erase(i);
				EffectManager::createTestExplosion(player->getPosition());
                break;
            }
            else // nothing happens to the bullet, move along..
            {
                
                b->setPosition(temp+(b->getVector()*dt));
            }
        }
        else
        {
            BulletController::erase(i);
        }
    }
    // Enemies update
    for(int k = EnemyController::enemies.size()-1; k>=0; k--)
    {
        auto enemy =EnemyController::enemies.at(k);
        if(!enemy->alive())
        {
            EnemyController::erase(k);
            //enemy->reset();
            //break;
        }
        switch(enemy->getType())
        {
            case kEnemyBigDude:
                static_cast<BigDude*>(enemy)->update(dt, player);
                break;
            case kEnemyBoss:
                static_cast<Boss*>(enemy)->update(dt, player);
                break;
            default:
                //enemy->move(enemyMoveDist, dt);
                break;
        }
		auto enemyMoveRect = enemy->getMoveRect();
		if (enemy->getType() != kEnemyBoss)
        {
            //enemy went out side, kill it
            //EnemyController::erase(k);
        }
        //if colliding with player
        else if(enemy->getPosition().getDistance(player->getPosition()) <(enemy->getRadius() + player->getRadius()) && enemy->getShadowType() != kShadowLand)
        {
            player->hurt(50);
            enemy->hurt(50);
			/*if (enemy->getType() != kEnemyBoss && enemy->getType() != kEnemyBigDude)
			{
			EnemyController::erase(k);
			}*/
        }
        //TODO: if enemy collide with player
        //if(enemy->getPosition().getDistance(<#const cocos2d::Point &other#>))
    }
}