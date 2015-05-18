#ifndef __TriggerUnitToRect_h__
#define __TriggerUnitToRect_h__

#include "Trigger.h"

class Unit;

class TriggerUnitToRect : public Trigger
{
public:
	TriggerUnitToRect(EventManager* eventManager, GameScene* gameLayer, int unitID, cocos2d::Rect rect);
	~TriggerUnitToRect();

	virtual bool perform();

private:
	int _unitID;
	cocos2d::Rect _rect;
	Unit* _unit;
};

#endif // __TriggerUnitToRect_h__
