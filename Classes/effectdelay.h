#ifndef __effectdelay_h__
#define __effectdelay_h__

#include "Effect.h"

class EffectDelay : public Effect
{
public:
	EffectDelay(EventManager* eventManager, GameScene* gameLayer, int second);

	virtual bool perform();

private:
	int _second;
	int _startTick;
};

#endif // __effectdelay_h__
