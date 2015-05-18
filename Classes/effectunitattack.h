#ifndef __effectunitattack_h__
#define __effectunitattack_h__

#include "Effect.h"

class Unit;

class EffectUnitAttack : public Effect
{
public:
	EffectUnitAttack(EventManager* eventmanager, GameScene* gamelayer, int unitID, int weaponType, int times);

	virtual bool perform();

private:
	int _unitID;
	int _weaponType;
	int _times;
	Unit* _unit;
};

#endif // __effectunitattack_h__
