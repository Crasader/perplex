#ifndef __Fodder_h__
#define __Fodder_h__

#include "Unit.h"

enum moveMode{
	kDefault,
	kTurn,
	kTrunAndRoll,
};

class FodderIdle;

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

#endif // __Fodder_h__
