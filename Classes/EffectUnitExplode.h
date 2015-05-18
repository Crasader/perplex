#ifndef __EffectUnitExplode_h__
#define __EffectUnitExplode_h__

#include "Effect.h"
#include "Unit.h"

class Unit;

class EffectUnitExplode : public Effect
{
public:
	EffectUnitExplode(EventManager* eventManager, GameScene* gameLayer, int unitID);

	virtual bool perform();


private:
	int _unitID;
	Unit* _unit;
};
#endif // __EffectUnitExplode_h__
