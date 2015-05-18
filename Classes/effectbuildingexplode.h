#ifndef __effectbuildingexplode_h__
#define __effectbuildingexplode_h__

#include "Effect.h"

class Building;

class EffectBuildingExplode : public Effect
{
public:
	EffectBuildingExplode(EventManager* eventManager, GameScene* gameLayer, int buildingID);
	virtual bool perform();
private:
	int _buildingID;
	Building* _building;
};


#endif // __effectbuildingexplode_h__
