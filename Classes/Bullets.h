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

#ifndef __Moon3d__Bullet__
#define __Moon3d__Bullet__

#include "cocos2d.h"
#include "GameEntity.h"

class WeaponPos;
class GameScene;
class WeaponRes;

class Bullet : public GameEntity
{
public:
	Bullet();
    CREATE_FUNC(Bullet);
	static Bullet* create(GameScene* gameScene, std::shared_ptr<WeaponRes> weaponRes, Vec2 pos, Vec2 move, int speed);
	bool init(GameScene* gameScene, std::shared_ptr<WeaponRes> weaponRes, Vec2 pos, Vec2 move, int speed);
    bool init();
    void setVector(Vec2 vec);
    Vec2 getVector();
    virtual void reset();
    CC_SYNTHESIZE(float, _damage, Damage);
    CC_SYNTHESIZE(int, _owner, Owner)
	bool getDrawFlip() const { return _drawFlip; }
	void setDrawFlip(bool aDrawFlip) { _drawFlip = aDrawFlip; }
	bool getBump() const { return _bump; }
	void setBump();
	bool isCastoff();
	bool isBump();
	Rect getHitRect();
	int getPower();
	void perform(float dt) override;
protected:
	bool _drawFlip;
	bool _bump;
	bool _castoff;
	int _power;
	float _liveTick;
	float _speed;
	std::shared_ptr<WeaponRes> _weaponRes;
	GameScene* _gameScene;
    Vec2 _vector;
	Rect _hitRect;
};

class PlayerBullet : public Bullet
{
public:
    CREATE_FUNC(PlayerBullet);
    bool init();
};

class Missile : public Bullet
{
public:
    CREATE_FUNC(Missile);
    bool init();
    void update(float dt);
    CC_SYNTHESIZE(GameEntity*, _target, Target)
    virtual void reset();
protected:
    float _accel;
    float _turnRate;
    //float _maxSpeed = 100;
    float _yRotSpeed ;
    float _yRotation;
    bool _left;
    float _velocity;
};

#endif /* defined(__Moon3d__Bullet__) */
