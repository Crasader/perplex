#ifndef __TriggerBuildingHP_h__
#define __TriggerBuildingHP_h__

#include "Trigger.h"

class Building;

class TriggerBuildingHP : public Trigger
{
public:
	TriggerBuildingHP(EventManager* eventManager, GameScene* gameLayer, int buildingID, int minHP, int maxHP);

	virtual bool perform();

private:
	int _buidingID;
	int _minHP;
	int _maxHP;
	Building* _building;
};


#endif // __TriggerBuildingHP_h__
