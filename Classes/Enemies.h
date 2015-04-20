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

#ifndef __Moon3d__Fodder__
#define __Moon3d__Fodder__

#include "cocos2d.h"
#include "Unit.h"
#include "Rotor.h"
#include "Launcher.h"
#include "MoveMode.h"

class TurretOne;
class TurretTwo;

enum moveMode{
    kDefault,
    kTurn,
	kTrunAndRoll,
};

class Fodder : public Unit
{
public:
    CREATE_FUNC(Fodder);
    bool init();
    virtual void reset();
    virtual void move(float y, float dt);
    CC_SYNTHESIZE(int, _moveMode, MoveMode);
    CC_PROPERTY(float, _turn, TurnRate);
    CC_SYNTHESIZE(Unit*, _target, Target);
    virtual void shoot(float dt);
	Fodder();
	~Fodder();
protected:
	ActionInterval* action1;
	bool isRoll;
};

class FodderLeader : public Fodder
{
public:
    CREATE_FUNC(FodderLeader);
    bool init();
    virtual void reset();
};

class BigDude : public Unit
{
public:
    CREATE_FUNC(BigDude);
    bool init();
    virtual void reset();
    virtual void die();
    void update(float dt, Node* player);
    void showFinished();
    void showMuzzle();
    void dismissMuzzle(float dt);
    virtual void shoot(float dt);
    void fall();
protected:
    Sprite* muzzle1;
    Sprite* muzzle2;
    Vec2 _targetPos;
    float _turnRate;
};

class Boss : public Fodder
{
public:
    CREATE_FUNC(Boss);
    bool init();
    virtual void die();
    virtual void shoot(float dt);
    void update(float dt, Node* player);
protected:
    void createRandomExplosion();
    void dying();
    void dead();
    void enterTheBattle();
    void startShooting(float dt);
    void startShooting();
    void showMuzzle();
    void dismissMuzzle(float dt);
    void _turns();
    void _endGame(float dt);
    Vec2 _getCannon1Position();
    Vec2 _getCannon2Position();
    Vec2 _getCannon1Vector();
    Vec2 _getCannon2Vector();
    Node* _Cannon1;
    Node* _Cannon2;
    Sprite* muzzle1;
    Sprite* muzzle2;
    void _dash();
    void _next();
};

class Tank : public Unit, public Rotor, public Launcher
{
public:
	enum class TankAction
	{
		kIdle,
		kMove,
		kAim,
		kShoot,
	};

	Tank();
	virtual ~Tank();

	bool init() override;
	virtual void move(float y, float dt);
	virtual void reset();
	CC_SYNTHESIZE(int, _moveMode, MoveMode);
	CC_PROPERTY(float, _turn, TurnRate);
	Unit* getTarget() const;
	void update(float dt) override;
	void setTarget(Unit* aTarget);
	CREATE_FUNC(Tank);
	void setRoute(float dt);
	Vec2 getDistance(const Vec2& from, float distance, float direction);
	void linerRoute(Point from, Point to, float direction, float dt);
	void linerRoute(Point from, float distance, float direction, float dt);
	void bezierRoute(Point from, Point to, float direction, float dt);
	void bezierRoute(Point from, Point to, Point p1, Point p2, float direction, float dt);
	void bezierRoute(Point from, float distance, float direction, float dt);
	void moveActionEnd(Node* sender);

	virtual void startRotate(float dt);

	virtual void rotating(float dt);

	virtual void rotated();

	virtual void startShoot(float dt);

	virtual void shooting();

	virtual void shot();

protected:
	TurretTwo* _turret;
	TankAction _state;
	float updateInterval;
};

#endif /* defined(__Moon3d__Fodder__) */
