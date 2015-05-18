#ifndef __effectappear_h__
#define __effectappear_h__

#include "Effect.h"

class Unit;

class EffectUnitAppear : public Effect
{
public:
	EffectUnitAppear(EventManager* eventManager, GameScene* gameLayer, int unitID, int x, int y, bool show);

	virtual bool perform();

private:
	bool _show;
	int _unitID;
	int _x;
	int _y;
	Unit*	_unit;
};


#endif // __effectappear_h__
