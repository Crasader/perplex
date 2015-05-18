#ifndef __effectunitmovetype_h__
#define __effectunitmovetype_h__

#include "Effect.h"

class Unit;

class EffectUnitMoveType : public Effect
{
public:
	EffectUnitMoveType(EventManager* eventManager, GameScene* gameLayer, int unitID, int aitype);

	virtual bool perform();

private:
	int _unitID;
	int _AIType;
	Unit* _unit;
};

#endif // __effectunitmovetype_h__
