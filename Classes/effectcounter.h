#ifndef __effectcounter_h__
#define __effectcounter_h__

#include "Effect.h"

class EffectCounter : public Effect
{
public:
	EffectCounter(EventManager* eventManager, GameScene* gameLayer, int counterID, int operate, int count);

	virtual bool perform();


private:
	int _counterID;
	int _operate;
	int _count;
};

#endif // __effectcounter_h__
