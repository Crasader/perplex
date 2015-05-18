#ifndef __effectcalculagraph_h__
#define __effectcalculagraph_h__

#include "Effect.h"

class EffectCalculaGraph : public Effect
{
public:
	EffectCalculaGraph(EventManager* eventManager, GameScene* gameLayer);

	bool perform();
private:
	bool _start;
};

#endif // __effectcalculagraph_h__
