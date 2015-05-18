#ifndef __TriggerUnitProperty_h__
#define __TriggerUnitProperty_h__

#include "Trigger.h"

class Unit;

class TriggerUnitProperty : public Trigger
{
public:
	TriggerUnitProperty(EventManager* eventManager, GameScene* gameLayer, int unitID, int property, int min, int max);

	virtual bool perform();

private:
	int _unitID;
	int _property;
	int _min;
	int _max;
	Unit* _unit;
};

#endif // __TriggerUnitProperty_h__
