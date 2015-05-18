#ifndef __effectunitproperty_h__
#define __effectunitproperty_h__

#include "Effect.h"

class Unit;

class EffectUnitProperty : public Effect
{
public:
	EffectUnitProperty(EventManager* eventManager, GameScene* gameLayer, int unitID, int property, int operate, int count);
	virtual bool perform();
private:
	int _unitID;
	int _property;
	int _operate;
	int _count;
	Unit* _unit;
};

#endif // __effectunitproperty_h__
