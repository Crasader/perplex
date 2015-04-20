#ifndef __Turret_h__
#define __Turret_h__

#include "Unit.h"
#include "Rotor.h"
#include "Launcher.h"

class TurretOne : public Unit, public Rotor, public Launcher
{
public:
	TurretOne();
	virtual ~TurretOne();

	virtual void move(float y, float dt);
	virtual void startRotate(float dt);
	void update(float dt) override;
	bool init() override;
	bool init(const char* filename);
	virtual void rotating(float angle);
	virtual void rotated();
	float getTargetAngle();
	virtual void startShoot(float dt);
	virtual void shooting();
	virtual void shot();
	Vec2 getTurretPosition(Vec2 pos = Point(0.0, 0.0));
	Vec2 getTurretVector();
	CC_SYNTHESIZE(Unit*, _target, Target);
	static TurretOne* create(const char* filename);
	CREATE_FUNC(TurretOne);
	void startAction();
	void stopAction();
protected:
	bool _aiming;
	float _oldAngle;
	float _newAngle;
	float _speed;
	float _RoateTimeDelta;
	float _shootDelta;
	int _shootTimes;
};

class TurretTwo : public TurretOne
{
public:
	TurretTwo();
	virtual ~TurretTwo();
	bool init() override;
	virtual void shooting();
	CREATE_FUNC(TurretTwo);
};

#endif // __Turret_h__
