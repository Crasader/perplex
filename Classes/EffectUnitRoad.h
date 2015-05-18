#ifndef __EffectUnitRoad_h__
#define __EffectUnitRoad_h__

#include "Effect.h"
#include "Unit.h"

class EffectUnitRoad : public Effect
{
public:
	EffectUnitRoad(EventManager* eventManager, GameScene* gameLayer, int unitId, std::vector<cocos2d::Vec2> roads);

	virtual bool perform();

private:
	int _unitID;
	Unit* _unit;
	std::vector<cocos2d::Vec2> _roads;
};


#endif // __EffectUnitRoad_h__
